package com.kelly.base.common.config;

import com.kelly.base.common.interfaces.IVault;
import com.kelly.base.common.jni.NativeVault;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

@DisplayName("VaultConfigTests")
class VaultConfigTests {
    private final ApplicationContextRunner contextRunner
            = new ApplicationContextRunner().withUserConfiguration(VaultConfig.class);

    @Nested
    @DisplayName("InjectionTests")
    class InjectionTests {
        private final String configPropertyPrefix = "config.options.vault-type=";

        @Test
        @DisplayName("injection test - default")
        void injectionDefaultTest() {
            // when
            contextRunner.run(
                    context -> {
                        // then - config.options.vault-type 에 정의된 값이 없으므로 default 로 randomVault 생성
                        Assertions.assertTrue(context.containsBean("randomVault"));

                        // then - nativeVault, cloudVault 는 생성되지 않았는지 확인
                        Assertions.assertFalse(context.containsBean("nativeVault"));
                        Assertions.assertFalse(context.containsBean("cloudVault"));

                        // then - vault 가 injection 이 되어야 함
                        IVault vault = context.getBean(IVault.class);
                        Assertions.assertNotNull(vault);
                        Assertions.assertNotNull(vault.getJasyptSeed());
                    }
            );
        }

        @Test
        @DisplayName("injection test - native")
        void injectionNativeTest() {
            // when
            contextRunner.withPropertyValues(configPropertyPrefix + "native").run(
                    context -> {
                        // then - config.options.vault-type 에 정의된 값에 따라 nativeVault 생성
                        Assertions.assertTrue(context.containsBean("nativeVault"));

                        // then - randomVault, cloudVault 는 생성되지 않았는지 확인
                        Assertions.assertFalse(context.containsBean("randomVault"));
                        Assertions.assertFalse(context.containsBean("cloudVault"));

                        // then - vault 가 injection 이 되어야 함
                        IVault vault = context.getBean(IVault.class);
                        Assertions.assertNotNull(vault);
                        Assertions.assertNotNull(vault.getJasyptSeed());
                        Assertions.assertInstanceOf(NativeVault.class, vault);
                    }
            );
        }

        @ParameterizedTest
        @CsvSource({
                "wrong, true, false, false",    // 정의되지 않은 값이 들어가 있는 경우
                "cloud, false, true, false",    // cloud
        })
        @DisplayName("injection test - not native")
        void injectionNotNativeTest(
                final String propertyValue,
                final boolean hasRandomBean, final boolean hasCloudBean, final boolean hasNativeBean
        ) {
            // when
            contextRunner.withPropertyValues(configPropertyPrefix + propertyValue).run(
                    context -> {
                        // then - config.options.vault-type 에 정의된 값에 따라 생성된 bean 검증
                        Assertions.assertEquals(hasRandomBean, context.containsBean("randomVault"));
                        Assertions.assertEquals(hasCloudBean, context.containsBean("cloudVault"));
                        Assertions.assertEquals(hasNativeBean, context.containsBean("nativeVault"));

                        // then - vault 가 injection 이 되어야 함
                        IVault vault = context.getBean(IVault.class);
                        Assertions.assertNotNull(vault);
                        Assertions.assertNotNull(vault.getJasyptSeed());
                    }
            );
        }
    }
}
