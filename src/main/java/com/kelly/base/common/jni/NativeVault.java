package com.kelly.base.common.jni;

import com.kelly.base.common.interfaces.IInternalLibLoader;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class NativeVault {
    private final IInternalLibLoader internalLibLoader;

    private static final String LIB_PATH = "native";
    private static final String LIB_NAME = "native_vault";

    @PostConstruct
    void init() {
        loadLibrary();
    }

    private void loadLibrary() {
        final String libNameWithExt = System.mapLibraryName(LIB_NAME);
        try {
            File targetFile = File.createTempFile("temp_", "_" + libNameWithExt);
            targetFile.deleteOnExit();

            try (InputStream is = internalLibLoader.getLibInputStream(LIB_PATH, libNameWithExt)) {
                try (OutputStream os = new FileOutputStream(targetFile)) {
                    byte[] buf = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = Objects.requireNonNull(is).read(buf)) != -1) {
                        os.write(buf, 0, bytesRead);
                    }
                }

                System.load(targetFile.getAbsolutePath());
            }
            log.info("internal library has been loaded successfully");
        } catch (Exception e) {
            log.error("cannot load library - cause : {}", e.getCause(), e);
        }
    }

    public native byte[] getJasyptSeed();
}
