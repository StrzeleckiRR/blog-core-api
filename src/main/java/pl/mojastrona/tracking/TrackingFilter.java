package pl.mojastrona.tracking;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class TrackingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            MultiReadHttpServletRequest request = new MultiReadHttpServletRequest(req);
            MDC.put("requestId", UUID.randomUUID().toString());

            log.info(prepareRequestLog(request));
            log.info(new String(request.getInputStream().readAllBytes()));

            long start = System.currentTimeMillis();

            filterChain.doFilter(request, response);

            long end = System.currentTimeMillis();
            log.info("Stop request:  {} {},status: {}, took {} ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    (end - start));
        }finally {
            MDC.clear();
        }
    }


    private static String prepareRequestLog(HttpServletRequest request) {
        StringBuilder requestLog = new StringBuilder("Start request ");

        requestLog.append(request.getMethod())
                .append(" ")
                .append(request.getRequestURI());

        if(request.getQueryString()!=null){
            requestLog.append("?").append(request.getQueryString());
        }

        return requestLog.toString();
    }

}
