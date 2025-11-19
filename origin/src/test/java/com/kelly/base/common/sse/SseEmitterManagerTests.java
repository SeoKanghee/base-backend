package com.kelly.base.common.sse;

import com.kelly.base.common.config.CommonPropertiesConfig;
import com.kelly.base.common.sse.dto.SseEvent;
import com.kelly.base.common.sse.dto.SseEventType;
import com.kelly.base.common.utils.DateTimeUtil;
import com.kelly.base.common.utils.ValueGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

@DisplayName("SseEmitterManagerTests")
class SseEmitterManagerTests {
    private CommonPropertiesConfig mockCommonPropertiesConfig;

    private SseEmitterManager sseEmitterManager;

    @BeforeEach
    void init() {
        mockCommonPropertiesConfig = mock(CommonPropertiesConfig.class);
        sseEmitterManager = new SseEmitterManager(mockCommonPropertiesConfig);
    }

    @Nested
    @DisplayName("InitTests")
    class InitTests {
        @Test
        @DisplayName("init test - properties 에 정의된 timeout 값을 사용하는지 확인")
        void initPropertiesTimeoutTest() {
            // given - commonPropertiesConfig mocking
            final long sseTimeoutFromProp = 10L;
            when(mockCommonPropertiesConfig.getSseEmitterTimeoutMs()).thenReturn(sseTimeoutFromProp);

            // when
            Assertions.assertDoesNotThrow(
                    () -> sseEmitterManager.init()
            );

            // then -> 설정된 값이 있으므로 해당 값 사용
            final Long sseTimeout = (Long) ReflectionTestUtils.getField(sseEmitterManager, "sseTimeout");
            Assertions.assertEquals(sseTimeoutFromProp, sseTimeout);
        }

        @Test
        @DisplayName("init test - timeout 기본값 확인")
        void initDefaultTimeoutTest() {
            // given - commonPropertiesConfig mocking
            when(mockCommonPropertiesConfig.getSseEmitterTimeoutMs()).thenReturn(null);

            // when
            Assertions.assertDoesNotThrow(
                    () -> sseEmitterManager.init()
            );

            // then -> null 이므로 default 값 사용
            final Long sseTimeout = (Long) ReflectionTestUtils.getField(sseEmitterManager, "sseTimeout");
            Assertions.assertEquals(60000L, sseTimeout);
        }
    }

    @Nested
    @DisplayName("CreateEmitterTests")
    class CreateEmitterTests {
        @Test
        @DisplayName("createEmitter test - 신규 emitter 등록")
        void createEmitterNewOneTest() {
            // when
            final SseEmitter result = Assertions.assertDoesNotThrow(
                    () -> sseEmitterManager.createEmitter(1L)
            );

            // then - map 에 등록됐는지 확인
            @SuppressWarnings("unchecked")  // warning 제거
            final Map<Long, SseEmitter> emitters
                    = (Map<Long, SseEmitter>) ReflectionTestUtils.getField(sseEmitterManager, "emitters");
            Assertions.assertNotNull(emitters);
            final SseEmitter expectedResult = emitters.get(1L);
            Assertions.assertEquals(expectedResult, result);
        }

        @Test
        @DisplayName("createEmitter test - 동일한 userId 로 emitter 등록 시도")
        void createEmitterDuplicatedTest() {
            // 이미 SseEmitter 가 등록되어 있을 경우, 기존 값을 삭제하고 등록하는지 확인
            // given
            final SseEmitter firstSseEmitter = Assertions.assertDoesNotThrow(
                    () -> sseEmitterManager.createEmitter(1L)
            );

            // when
            final SseEmitter secondSseEmitter = Assertions.assertDoesNotThrow(
                    () -> sseEmitterManager.createEmitter(1L)
            );

            // then - secondSseEmitter 가 map 에 등록됐는지 확인
            @SuppressWarnings("unchecked")  // warning 제거
            final Map<Long, SseEmitter> emitters
                    = (Map<Long, SseEmitter>) ReflectionTestUtils.getField(sseEmitterManager, "emitters");
            Assertions.assertNotNull(emitters);
            final SseEmitter expectedResult = emitters.get(1L);
            Assertions.assertEquals(expectedResult, secondSseEmitter);

            // then - firstSseEmitter 는 complete 처리됐으므로 send 시 exception 발생
            Assertions.assertThrows(
                    IllegalStateException.class,
                    () -> firstSseEmitter.send("impossible to complete the sending")
            );
        }
    }

    @Nested
    @DisplayName("SendToUserTests")
    class SendToAccountTests {
        @Test
        @DisplayName("sendToUser - emitter 가 없는 경우")
        void sendToUserNoEmitterTest() {
            // given
            final SseEvent sseEvent = sseEmitterManager.createConnectEvent(1L);

            // when - SseEmitter 생성 없이 send
            final boolean result = Assertions.assertDoesNotThrow(
                    () -> sseEmitterManager.sendToUser(1L, sseEvent)
            );

            // then
            Assertions.assertFalse(result);
        }

        @Test
        @DisplayName("sendToUser - emitter 가 있는 경우")
        void sendToUserSuccessTest() {
            // given - SseEmitter 등록
            sseEmitterManager.createEmitter(1L);
            final SseEvent sseEvent = sseEmitterManager.createConnectEvent(1L);

            // when
            final boolean result = Assertions.assertDoesNotThrow(
                    () -> sseEmitterManager.sendToUser(1L, sseEvent)
            );

            // then
            Assertions.assertTrue(result);
        }

        @Test
        @DisplayName("sendToUser - emitter 처리 중 exception 이 발생하는 경우")
        void sendToUserExceptionTest() throws IOException {
            // given - SseEmitter mocking
            final SseEmitter mockSseEmitter = mock(SseEmitter.class);
            doThrow(IOException.class).when(mockSseEmitter).send(any(SseEmitter.SseEventBuilder.class));

            // given - SseEmitter 강제 등록
            @SuppressWarnings("unchecked") final Map<Long, SseEmitter> emitters =
                    (Map<Long, SseEmitter>) ReflectionTestUtils.getField(sseEmitterManager, "emitters");
            Assertions.assertNotNull(emitters);
            emitters.put(1L, mockSseEmitter);

            // given - SseEvent 생성
            final SseEvent sseEvent = sseEmitterManager.createConnectEvent(1L);

            // when
            final boolean result = Assertions.assertDoesNotThrow(
                    () -> sseEmitterManager.sendToUser(1L, sseEvent)
            );

            // then - IOException 으로 인한 전송 실패
            Assertions.assertFalse(result);
        }
    }

    @Nested
    @DisplayName("DisconnectTests")
    class DisconnectTests {
        @Test
        @DisplayName("disconnect test - 등록된 emitter 해제 성공")
        void disconnectSuccessTest() {
            // disconnect 시에 특별한 문제가 없는 경우
            // given - 등록
            sseEmitterManager.createEmitter(1L);
            Assertions.assertEquals(1, sseEmitterManager.getConnectionCount());

            // when
            Assertions.assertDoesNotThrow(
                    () -> {
                        final SseEvent sseEvent = sseEmitterManager.createDisconnectEvent(1L);
                        sseEmitterManager.disconnect(1L, sseEvent);
                    }
            );

            // then - 연결된 connection 이 없어야 됨
            Assertions.assertEquals(0, sseEmitterManager.getConnectionCount());
        }

        @Test
        @DisplayName("disconnect test - emitter 가 없는 경우")
        void disconnectNoEmitterTest() {
            // disconnect 시에 등록된 SseEmitter 가 없는 경우
            // when
            Assertions.assertDoesNotThrow(
                    () -> {
                        final SseEvent sseEvent = sseEmitterManager.createDisconnectEvent(1L);
                        sseEmitterManager.disconnect(1L, sseEvent);
                    }
            );

            // then - 연결된 connection 이 없어야 됨
            Assertions.assertEquals(0, sseEmitterManager.getConnectionCount());
        }

        @Test
        @DisplayName("disconnect test - emitter 해제 중 exception 발생 : IllegalStateException")
        void disconnectExceptionTest() {
            // disconnect 시에 등록된 SseEmitter 에서 Exception 이 발생하는 경우

            // given - 등록 후 complete 처리
            final SseEmitter sseEmitter = sseEmitterManager.createEmitter(1L);
            sseEmitter.complete();
            Assertions.assertEquals(1, sseEmitterManager.getConnectionCount());

            // when
            Assertions.assertDoesNotThrow(
                    () -> {
                        final SseEvent sseEvent = sseEmitterManager.createDisconnectEvent(1L);
                        sseEmitterManager.disconnect(1L, sseEvent);
                    }
            );

            // then - 연결된 connection 이 없어야 됨
            Assertions.assertEquals(0, sseEmitterManager.getConnectionCount());
        }

        @Test
        @DisplayName("disconnect - emitter 해제 중 exception 발생 : RuntimeException(mock)")
        void disconnectMockExceptionTest() {
            // given - SseEmitter mocking
            final SseEmitter mockSseEmitter = mock(SseEmitter.class);
            doThrow(RuntimeException.class).when(mockSseEmitter).complete();

            // given - SseEmitter 강제 등록
            @SuppressWarnings("unchecked") final Map<Long, SseEmitter> emitters =
                    (Map<Long, SseEmitter>) ReflectionTestUtils.getField(sseEmitterManager, "emitters");
            Assertions.assertNotNull(emitters);
            emitters.put(1L, mockSseEmitter);

            // when
            Assertions.assertDoesNotThrow(
                    () -> {
                        final SseEvent sseEvent = sseEmitterManager.createDisconnectEvent(1L);
                        sseEmitterManager.disconnect(1L, sseEvent);
                    }
            );

            // then - 연결된 connection 이 없어야 됨
            Assertions.assertEquals(0, sseEmitterManager.getConnectionCount());
        }
    }

    @Nested
    @DisplayName("IntegrationTests")
    class IntegrationTests {
        @Test
        @DisplayName("sendToAll, getConnectionCount, isConnected test - 전체 통합 검증")
        void broadcastTest() {
            // when - 등록 #1
            sseEmitterManager.createEmitter(1L);

            // then - 등록 여부 확인 #1
            Assertions.assertEquals(1, sseEmitterManager.getConnectionCount());
            Assertions.assertTrue(sseEmitterManager.isConnected(1L));

            // when - 등록 #2
            sseEmitterManager.createEmitter(2L);

            // then - 등록 여부 확인 #2
            Assertions.assertEquals(2, sseEmitterManager.getConnectionCount());
            Assertions.assertTrue(sseEmitterManager.isConnected(2L));

            // then - sendToAll
            final SseEvent sseEvent = new SseEvent(
                    SseEventType.REFRESH_ANALYSIS, ValueGenerator.getNanoId(), DateTimeUtil.nowUtc(), Map.of()
            );
            Assertions.assertDoesNotThrow(
                    () -> sseEmitterManager.sendToAll(sseEvent)
            );
        }
    }

    @Nested
    @DisplayName("CallbackMethodTests")
    class CallbackMethodTests {
        @ParameterizedTest
        @ValueSource(strings = {
                // org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter 참조
                "timeoutCallback",      // timeoutCallback.addDelegate(callback)
                "completionCallback",   // completionCallback.addDelegate(callback)
        })
        @DisplayName("runnable callback test")
        void runnableCallbackTest(final String methodName) {
            // given - 등록
            final SseEmitter sseEmitter = sseEmitterManager.createEmitter(1L);
            Assertions.assertTrue(sseEmitterManager.isConnected(1L));

            // given - callback method 가져오기
            final var runnableCallback = ReflectionTestUtils.getField(sseEmitter, methodName);
            Assertions.assertNotNull(runnableCallback);

            // when - 실행
            final Runnable castedCallback = ((Runnable) runnableCallback);
            Assertions.assertDoesNotThrow(castedCallback::run);

            // then - callback 이 동작했으므로 userId 1L의 emitter가 제거되었는지 확인
            Assertions.assertFalse(sseEmitterManager.isConnected(1L));
        }

        @Test
        @DisplayName("onError callback test")
        void onErrorTest() {
            // given - 등록
            final SseEmitter sseEmitter = sseEmitterManager.createEmitter(1L);
            Assertions.assertTrue(sseEmitterManager.isConnected(1L));

            // given - callback method 가져오기 : errorCallback.addDelegate(callback)
            final var errorCallback = ReflectionTestUtils.getField(sseEmitter, "errorCallback");
            Assertions.assertNotNull(errorCallback);

            // when - 에러 발생
            @SuppressWarnings("unchecked")  // warning 제거
            final Consumer<Throwable> consumer = (Consumer<Throwable>) errorCallback;
            consumer.accept(new IOException("Test error"));

            // then - userId 1L의 emitter가 제거되었는지 확인
            Assertions.assertFalse(sseEmitterManager.isConnected(1L));
        }
    }

    @Nested
    @DisplayName("RemoveEmitterTests")
    class RemoveEmitterTests {
        @Test
        @DisplayName("removeEmitter test - 등록된 emitter 가 없는 경우")
        void removeEmitterNullTest() {
            // removeEmitter 가 호출될 때 대부분의 경우 emitter 존재 여부를 확인하고 처리함
            // runtime 에 타이밍상 문제가 될 부분도 존재하므로 테스트 코드로 확인

            // when, then - exception 이 발생하지 않아야 함
            Assertions.assertDoesNotThrow(
                    () -> ReflectionTestUtils.invokeMethod(sseEmitterManager, "removeEmitter", 999L)
            );
        }
    }
}
