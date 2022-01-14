package org.example.main.connectivity;

import java.io.IOException;
import java.io.InputStream;

public interface ConnectionService {

    InputStream get(String url) throws IOException;

}
