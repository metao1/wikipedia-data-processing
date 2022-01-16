package com.top.wiki.connectivity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MockedBasicInputService implements InputStreamConnectionService {

    @Override
    public InputStream get(String url) throws IOException {
        return new FileInputStream(url);
    }
}
