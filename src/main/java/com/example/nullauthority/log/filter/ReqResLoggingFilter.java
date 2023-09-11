package com.example.nullauthority.log.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // security 필터보다 먼저 적용하기 위해서
public class ReqResLoggingFilter extends OncePerRequestFilter {
    public static final String REQUEST_ID = "request_id";
    private final Logger log = LoggerFactory.getLogger(ReqResLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper cachingRequestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper cachingResponseWrapper = new ContentCachingResponseWrapper(response);
        long start = System.currentTimeMillis();
        filterChain.doFilter(cachingRequestWrapper, cachingResponseWrapper);
        long end = System.currentTimeMillis();
        try {
            String requestId = UUID.randomUUID().toString().substring(0, 8);

            MDC.put(REQUEST_ID, requestId);
            log.info("""
                            [REQUEST] {} - {} {} - {}
                            Headers : {}
                            Request : {}
                            Response : {}
                            """,
                    request.getMethod(),
                    request.getRequestURI(),
                    cachingResponseWrapper.getStatus(),
                    (end - start) / 1000.0,
                    getHeaders(request),
                    getRequestBody(cachingRequestWrapper),
                    getResponseBody(cachingResponseWrapper));

            cachingResponseWrapper.copyBodyToResponse();
        } catch (Exception e) {
            logger.error("[${this::class.simpleName}] Logging 실패");
        }
        MDC.remove(REQUEST_ID);
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headerMap = new HashMap<>();

        Enumeration<String> headerArray = request.getHeaderNames();
        while (headerArray.hasMoreElements()) {
            String headerName = headerArray.nextElement();
            headerMap.put(headerName, request.getHeader(headerName));
        }
        return headerMap;
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                try {
                    return new String(buf, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException e) {
                    return " - ";
                }
            }
        }
        return " - ";
    }

    private String getResponseBody(final HttpServletResponse response) throws IOException {
        String payload = null;
        ContentCachingResponseWrapper wrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                payload = new String(buf, wrapper.getCharacterEncoding());
            }
        }
        return null == payload ? " - " : payload;
    }
}
