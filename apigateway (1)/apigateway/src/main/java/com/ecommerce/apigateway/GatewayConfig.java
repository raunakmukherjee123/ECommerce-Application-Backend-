package com.ecommerce.apigateway;

import com.ecommerce.apigateway.filter.AuthenticationFilter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    private final AuthenticationFilter authenticationFilter;

    public GatewayConfig(AuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }
// for redis, download dependency spring data reactive redis
    @Bean
    public RedisRateLimiter redisRateLimiter()
    {
        return new RedisRateLimiter(1,1,1);
    }

    @Bean
    public KeyResolver hostNameKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest().getRemoteAddress().getHostName());
    }

     @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder)
    {
        return routeLocatorBuilder.routes()
                .route("product",r->r.path("/api/products/**")
//                        .filters(f ->f.circuitBreaker(config -> config
//                                .setName("ecomBreaker")
//
//                                .setFallbackUri("forward:/fallback/products")))
                        .filters(f -> f
                                .retry(retryConfig -> retryConfig
                                        .setRetries(10)
                                        .setMethods(HttpMethod.GET)
                                )
                                .filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(hostNameKeyResolver()))
                                .circuitBreaker(config -> config
                                        .setName("ecomBreaker")
                                        .setFallbackUri("forward:/fallback/products")))
                                                     .uri("lb://product"))
                // altering path of product ms
//                .route("product",r->r.path("/products/**")
//                        .filters(f-> f.rewritePath("/products(?<segment>/?.*)",
//                                "/api/products${segment}"))
//                        .uri("lb://product"))
                .route("user",r->r.path("/api/users/**","/auth/**")
                        .filters(f -> f
                                .filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                        )
                        .uri("lb://user"))
                .route("order",r->r.path("/api/order/**","/api/cart/**")
                        .filters(f -> f
                                .filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                        )
                        .uri("lb://order"))
                 .build();
    }
}
