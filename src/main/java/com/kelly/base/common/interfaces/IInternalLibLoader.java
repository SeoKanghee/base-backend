package com.kelly.base.common.interfaces;

import java.io.InputStream;

public interface IInternalLibLoader {
    InputStream getLibInputStream(final String libPath, final String libName);
}
