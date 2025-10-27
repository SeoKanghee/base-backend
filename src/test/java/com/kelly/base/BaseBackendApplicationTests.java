package com.kelly.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@DisplayName("BaseBackendApplicationTests")
class BaseBackendApplicationTests {
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void init() {
        outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);
    }

    @Test
    @DisplayName("main application test")
    void bootRunTest() {
        BaseBackendApplication.main(new String[] {});

        String output = outputStream.toString().trim();
        Assertions.assertFalse(output.isBlank());
    }
}
