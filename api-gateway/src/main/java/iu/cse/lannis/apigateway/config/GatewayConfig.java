package iu.cse.lannis.apigateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Set;

@Configuration
public class GatewayConfig {
    @Autowired
    private AuthFilter authFilter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder rlb) {
        return rlb.routes()
                .route("auth-server", r -> r.path("/auth/**")
                        .filters(f -> f
                                .filter(authFilter) // authorizing
                                .circuitBreaker(config -> config // handle fault tolerance
                                        .setName("authServerCircuitBreaker")
                                        .setFallbackUri("forward:/fallback")
                                        .setStatusCodes(Set.of("INTERNAL_SERVER_ERROR")
                                )
                        )
                )
                        .uri("lb://auth-server"))
                .route("service-student", r -> r.path("/students/**")
                        .filters(f -> f
                                .filter(authFilter)
                                .circuitBreaker(config -> config
                                        .setName("serviceStudentCircuitBreaker")
                                        .setFallbackUri("forward:/fallback")
                                        .setStatusCodes(Set.of("INTERNAL_SERVER_ERROR"))
                                )
                        )
                        .uri("lb://service-student")
                )
                .route("service-retention", r -> r.path("/retentions/**")
                        .filters(f -> f
                                .filter(authFilter)
                                .circuitBreaker(config -> config
                                        .setName("serviceRetentionCircuitBreaker")
                                        .setFallbackUri("forward:/fallback")
                                        .setStatusCodes(Set.of("INTERNAL_SERVER_ERROR"))
                                )
                        )
                        .uri("lb://service-retention")
                )
                .route("service-course", r -> r.path("/courses/**")
                        .filters(f ->
                                f
                                .filter(authFilter)
                                .circuitBreaker(config -> config
                                        .setName("serviceCourseCircuitBreaker")
                                        .setFallbackUri("forward:/fallback")
                                        .setStatusCodes(Set.of("INTERNAL_SERVER_ERROR"))
                                )
                        )
                        .uri("lb://service-course"))
                .build();
    }
}
