package com.kelly.base.common.jni;

import com.kelly.base.common.interfaces.ILibLoader;

import java.io.InputStream;
import java.nio.file.Paths;

public class InternalLibLoader implements ILibLoader {
    public InputStream getLibInputStream(final String libPath, final String libName) {
        // jar 내 resource 에 있는 library 를 stream 으로 읽기 위한 path 정의
        final String resourcePath = Paths.get("/", libPath, libName).toString();
        return InternalLibLoader.class.getResourceAsStream(resourcePath);
    }
}
