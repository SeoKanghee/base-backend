package com.kelly.base.common.jni;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

@Slf4j
public class NativeVault {
    private static final String LIB_NAME = "native_vault";

    static {
        loadLibrary();
    }

    private static void loadLibrary() {
        final String libNameExt = System.mapLibraryName(LIB_NAME);
        try {
            File targetFile = File.createTempFile("temp_", "_" + libNameExt);
            targetFile.deleteOnExit();

            try (InputStream is = InternalLibLoader.getLibInputStream(libNameExt)) {
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
