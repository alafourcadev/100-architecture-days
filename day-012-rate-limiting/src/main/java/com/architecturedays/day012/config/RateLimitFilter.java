package com.architecturedays.day012.config;

import com.architecturedays.day012.service.TiendaService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("after")
@Order(1)
public class RateLimitFilter implements Filter {

    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final TiendaService tiendaService;

    public RateLimitFilter(TiendaService tiendaService) {
        this.tiendaService = tiendaService;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String clientIp = request.getRemoteAddr();
        String path = request.getRequestURI();
        boolean isCompra = path.contains("comprar");

        String bucketKey = clientIp + ":" + (isCompra ? "comprar" : "general");
        TokenBucket bucket = buckets.computeIfAbsent(bucketKey,
                k -> isCompra ? new TokenBucket(10, 10) : new TokenBucket(30, 30));

        if (bucket.tryConsume()) {
            response.setHeader("X-Rate-Limit-Remaining",
                    String.valueOf(bucket.getAvailableTokens()));
            chain.doFilter(req, res);
        } else {
            tiendaService.registrarRechazo();
            response.setStatus(429);
            response.setHeader("Retry-After", "10");
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\":\"Too Many Requests\",\"mensaje\":\"Demasiadas solicitudes. Intenta en unos segundos.\",\"retryAfter\":10}");
            System.out.println("RATE LIMIT: Rechazada " + clientIp + " -> " + path);
        }
    }
}
