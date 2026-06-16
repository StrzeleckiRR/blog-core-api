package pl.mojastrona.tracking;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CachedServletInputStream extends ServletInputStream {

    private ByteArrayInputStream inputStream;

    public CachedServletInputStream(byte[] body) {
        this.inputStream = new ByteArrayInputStream(body);
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    @Override
    public boolean isFinished() {
        return inputStream.available() == 0;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        throw new UnsupportedOperationException();
    }
}