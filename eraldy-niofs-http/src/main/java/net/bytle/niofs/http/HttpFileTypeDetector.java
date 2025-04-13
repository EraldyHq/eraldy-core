package net.bytle.niofs.http;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;

public class HttpFileTypeDetector extends FileTypeDetector {

    @Override
    public String probeContentType(Path path) throws IOException {

        boolean b = path instanceof HttpRequestPath;
        if (!b) {
            return null;
        }
        HttpRequestPath httpPath = (HttpRequestPath) path;
        URL url = httpPath.toUri().toURL();
        URLConnection urlConnection = url.openConnection();
        return urlConnection.getContentType();

    }
}
