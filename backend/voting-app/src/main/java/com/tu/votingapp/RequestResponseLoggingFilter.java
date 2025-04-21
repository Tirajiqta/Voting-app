package com.tu.votingapp;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

@Component
@Order(1) // Ensure it runs early, adjust order as needed relative to other filters like Spring Security
public class RequestResponseLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    // Define maximum payload size to log to prevent OutOfMemory errors
    private static final int MAX_PAYLOAD_LENGTH = 10240; // Log up to 10KB

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Use Spring's wrappers to cache request/response bodies
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);

        long startTime = System.currentTimeMillis();
        String requestDetails = formatRequest(requestWrapper); // Format request details before processing

        // Log request details *before* processing the chain
        log.info("Incoming Request: {}", requestDetails);

        try {
            // Let the rest of the chain (including controllers) process the request
            chain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            String responseDetails = formatResponse(responseWrapper); // Format response details after processing

            // Log response details *after* processing
            log.info("Outgoing Response ({} ms): {}", duration, responseDetails);

            // IMPORTANT: Copy the cached response content back to the original response
            // This ensures the client actually receives the response body
            responseWrapper.copyBodyToResponse();
        }
    }

    private String formatRequest(ContentCachingRequestWrapper request) {
        StringBuilder builder = new StringBuilder();
        builder.append("Method=").append(request.getMethod());
        builder.append(", URI=").append(request.getRequestURI());
        String queryString = request.getQueryString();
        if (queryString != null) {
            builder.append('?').append(queryString);
        }

        // Log Headers
        builder.append(", Headers=[");
        builder.append(formatHeaders(Collections.list(request.getHeaderNames()), request::getHeader));
        builder.append("]");

        // Log Request Body (if present and within size limit)
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            builder.append(", Body=");
            builder.append(formatPayload(content, request.getCharacterEncoding()));
        }

        return builder.toString();
    }

    private String formatResponse(ContentCachingResponseWrapper response) {
        StringBuilder builder = new StringBuilder();
        builder.append("Status=").append(response.getStatus());

        // Log Headers
        builder.append(", Headers=[");
        builder.append(formatHeaders(response.getHeaderNames(), response::getHeader));
        builder.append("]");

        // Log Response Body (if present and within size limit)
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            builder.append(", Body=");
            builder.append(formatPayload(content, response.getCharacterEncoding()));
        }

        return builder.toString();
    }

    private String formatHeaders(Collection<String> headerNames, Function<String, String> headerValueResolver) {
        StringBuilder headerBuilder = new StringBuilder();
        boolean first = true;
        for (String headerName : headerNames) {
            // Optionally filter sensitive headers like Authorization, Cookie, etc.
            // if (headerName.equalsIgnoreCase("Authorization") || headerName.equalsIgnoreCase("Cookie")) {
            //     headerValue = "***";
            // }
            if (!first) {
                headerBuilder.append(", ");
            }
            headerBuilder.append(headerName).append("=").append(headerValueResolver.apply(headerName));
            first = false;
        }
        return headerBuilder.toString();
    }

    private String formatPayload(byte[] payload, String characterEncoding) {
        if (payload == null || payload.length == 0) {
            return "";
        }
        int length = Math.min(payload.length, MAX_PAYLOAD_LENGTH);
        try {
            // Use response character encoding if available, otherwise default
            String encoding = (characterEncoding != null) ? characterEncoding : StandardCharsets.UTF_8.name();
            String payloadStr = new String(payload, 0, length, encoding);
            // Replace non-printable characters for cleaner logs, optional
            payloadStr = payloadStr.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "?");
            if (payload.length > MAX_PAYLOAD_LENGTH) {
                payloadStr += "... (truncated)";
            }
            return payloadStr;
        } catch (UnsupportedEncodingException e) {
            log.warn("Unsupported encoding '{}' for logging payload.", characterEncoding, e);
            return "[Unsupported Encoding: " + characterEncoding + ", length=" + length + "]";
        } catch (Exception e) {
            log.warn("Error formatting payload for logging", e);
            return "[Error formatting payload, length=" + length + "]";
        }
    }

    // init() and destroy() methods from Filter interface (can be left empty)
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}
