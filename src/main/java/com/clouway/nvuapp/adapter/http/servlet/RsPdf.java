package com.clouway.nvuapp.adapter.http.servlet;

import com.clouway.nvuapp.core.Response;
import com.google.common.collect.ImmutableMap;

import javax.servlet.http.Cookie;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Map;

/**
 * @author Petar Nedelchev (peter.krasimirov@gmail.com)
 */
public class RsPdf implements Response {
    private final byte[] pdf;

    public RsPdf(byte[] pdf) {
        this.pdf = pdf;
    }

    @Override
    public InputStream body() throws IOException {
        return new ByteArrayInputStream(pdf);
    }

    @Override
    public Map<String, String> headers() {
        return ImmutableMap.of("Content-type", "application/pdf", "Content-Disposition", "attachment;filename='questionnaire.pdf'");
    }

    @Override
    public int status() {
        return HttpURLConnection.HTTP_OK;
    }

    @Override
    public Iterable<Cookie> cookies() {
        return Collections.emptyList();
    }
}
