package com.example.apigateway;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final SecretKey signingKey;
    private static final String ADMIN = "ADMIN";
    private static final String FINANCE_MANAGER = "FINANCE_MANAGER";
    private static final String ACCOUNT_HOLDER = "ACCOUNT_HOLDER";
    private static final Set<String> STAFF_ROLES = Set.of(ADMIN, FINANCE_MANAGER);

    public JwtAuthenticationFilter(@Value("${jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(authorization.substring(7))
                    .getPayload();

            String userId = claims.getSubject();
            String role = String.valueOf(claims.get("role"));
            if (!isAuthorized(path, exchange.getRequest().getMethod(), role, userId)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .header("X-Username", String.valueOf(claims.get("username")))
                    .header("X-User-Role", role)
                    .build();

            return chain.filter(exchange.mutate().request(request).build());
        } catch (JwtException | IllegalArgumentException exception) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/api/auth/")
                || path.startsWith("/actuator/")
                || path.equals("/actuator/health");
    }

    private boolean isAuthorized(String path, HttpMethod method, String role, String userId) {
        if (ADMIN.equals(role)) {
            return true;
        }

        if (FINANCE_MANAGER.equals(role)) {
            return isFinanceManagerAllowed(path, method);
        }

        if (ACCOUNT_HOLDER.equals(role)) {
            return isAccountHolderAllowed(path, method, userId);
        }

        return false;
    }

    private boolean isFinanceManagerAllowed(String path, HttpMethod method) {
        if (path.matches("/api/users/\\d+/role")
                || path.matches("/api/users/\\d+/activate")
                || (HttpMethod.DELETE.equals(method) && path.matches("/api/users/\\d+"))) {
            return false;
        }

        return path.startsWith("/api/accounts/")
                || path.equals("/api/accounts")
                || path.startsWith("/api/account-requests/")
                || path.equals("/api/account-requests")
                || path.startsWith("/api/payments/")
                || path.equals("/api/payments")
                || path.startsWith("/api/transactions/")
                || path.equals("/api/transactions")
                || path.startsWith("/api/users/")
                || path.equals("/api/users")
                || path.startsWith("/api/chat/")
                || path.startsWith("/api/notifications/");
    }

    private boolean isAccountHolderAllowed(String path, HttpMethod method, String userId) {
        if (HttpMethod.GET.equals(method) && path.equals("/api/users/" + userId)) {
            return true;
        }
        if (HttpMethod.PUT.equals(method) && path.equals("/api/users/" + userId + "/profile")) {
            return true;
        }

        if (HttpMethod.POST.equals(method) && path.equals("/api/accounts/self")) {
            return true;
        }
        if (HttpMethod.GET.equals(method) && path.equals("/api/accounts/user/" + userId)) {
            return true;
        }
        if (HttpMethod.GET.equals(method) && path.equals("/api/accounts/user/" + userId + "/active")) {
            return true;
        }
        if (HttpMethod.GET.equals(method) && path.matches("/api/accounts/\\d+/balance")) {
            return true;
        }
        if (HttpMethod.POST.equals(method) && path.matches("/api/accounts/\\d+/(set-pin|verify-pin|reset-pin)")) {
            return true;
        }

        if (path.equals("/api/account-requests") || path.startsWith("/api/accounts/requests")) {
            return !path.matches(".*/\\d+/(approve|reject)");
        }

        if (path.startsWith("/api/payments")) {
            return HttpMethod.POST.equals(method)
                    || path.equals("/api/payments/user/" + userId)
                    || path.matches("/api/payments/\\d+")
                    || path.matches("/api/payments/account/\\d+");
        }

        if (path.startsWith("/api/transactions")) {
            return HttpMethod.POST.equals(method)
                    || path.equals("/api/transactions/user/" + userId)
                    || path.matches("/api/transactions/\\d+")
                    || path.matches("/api/transactions/account/\\d+");
        }

        return path.startsWith("/api/chat/")
                || path.startsWith("/api/notifications/");
    }
}
