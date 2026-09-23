package com.youlai.system.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Set;

/** Rejects cross-site simple requests as well as preflight requests. */
public final class LocalRequestFilter extends OncePerRequestFilter {
    private static final Set<String> LOCAL_HOSTS = Set.of("127.0.0.1", "localhost", "::1", "[::1]");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String origin = request.getHeader("Origin");
        // Host checking also prevents a public DNS name being rebound to loopback.
        boolean localHost = LOCAL_HOSTS.contains(request.getServerName().toLowerCase(java.util.Locale.ROOT));
        boolean trustedOrigin = origin == null || CorsConfig.LOCAL_ORIGINS.contains(origin);
        boolean remoteBrowser = origin == null && "cross-site".equals(request.getHeader("Sec-Fetch-Site"));
        if (!localHost || !trustedOrigin || remoteBrowser) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only the local application may access this API");
            return;
        }
        chain.doFilter(request, response);
    }
}
