package az.iktex.organization.structure.testders.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import org.slf4j.MDC;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter extends OncePerRequestFilter {
    private static final Pattern TRACE_ID_PATTERN =
            Pattern.compile("[A-Za-z0-9-]{8,64}");

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String traceId = request.getHeader("X-Trace-Id");

        if (traceId == null || !TRACE_ID_PATTERN.matcher(traceId).matches()) {
            traceId = UUID.randomUUID().toString();
        }

        MDC.put("traceId", traceId);

        try {
            response.setHeader("X-Trace-Id", traceId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("traceId");
        }
    }
}