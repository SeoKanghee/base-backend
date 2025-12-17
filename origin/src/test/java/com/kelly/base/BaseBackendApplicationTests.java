package com.kelly.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.core.VerificationOptions;
import org.springframework.modulith.docs.Documenter;

@DisplayName("BaseBackendApplicationTests")
class BaseBackendApplicationTests {

    @Nested
    @DisplayName("MainApplicationTests")
    class MainApplicationTests {
        @Test
        @DisplayName("main application test")
        void bootRunTest() {
            Assertions.assertDoesNotThrow(
                    () -> BaseBackendApplication.main(new String[] {})
            );
        }
    }

    @Nested
    @DisplayName("ModularTests")
    class ModularTests {
        private final ApplicationModules modules = ApplicationModules.of(BaseBackendApplication.class);

        @Test
        @DisplayName("verify modular test - module dependency 가 잘 지켜지고 있는지 확인")
        void modularVerifyTest() {
            modules.verify();
        }

        @Test
        @DisplayName("create module doc test - module document 생성")
        void createModuleDocumentation() {
            // when, then
            Assertions.assertDoesNotThrow(
                    () -> new Documenter(modules).writeDocumentation()
                                                 .writeIndividualModulesAsPlantUml()
                                                 .writeModulesAsPlantUml()
            );
            listAllModules();
        }

        private void listAllModules() {
            System.out.println("----------------------------------------");
            modules.forEach(module -> {
                System.out.println("Module: " + module);
                System.out.println("----------------------------------------");
            });
        }
    }
}
