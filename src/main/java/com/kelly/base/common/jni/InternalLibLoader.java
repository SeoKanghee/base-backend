package com.kelly.base.common.jni;

import java.io.InputStream;
import java.nio.file.Paths;

public interface InternalLibLoader {
    static InputStream getLibInputStream(final String libNameExt) {
        final String libPath = Paths.get("/", "native", libNameExt).toString();
        return InternalLibLoader.class.getResourceAsStream(libPath);
    }
}
