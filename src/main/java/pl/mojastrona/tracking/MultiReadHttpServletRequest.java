package pl.mojastrona.tracking;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;

public class MultiReadHttpServletRequest extends HttpServletRequestWrapper {

    private byte[] body;

    public MultiReadHttpServletRequest(HttpServletRequest request) {
        super(request);
        try {
            body = request.getInputStream().readAllBytes();
        } catch (IOException e) {
            body = new byte[0];
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedServletInputStream(body);
    }

    public byte[] getBody() {
        return body;
    }
}