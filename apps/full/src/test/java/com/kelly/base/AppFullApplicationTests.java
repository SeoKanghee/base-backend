package com.kelly.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@DisplayName("AppFullApplicationTests")
class AppFullApplicationTests {
    private final ApplicationModules modules = ApplicationModules.of(AppFullApplication.class);

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
