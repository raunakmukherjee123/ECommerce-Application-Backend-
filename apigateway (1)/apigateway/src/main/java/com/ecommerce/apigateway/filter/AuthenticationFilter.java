package com.ecommerce.apigateway.filter;

import com.ecommerce.apigateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator routeValidator;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;


    public AuthenticationFilter() {
        super(Config.class);
    }

//    @Override
//    public GatewayFilter apply(Config config) {
//        return ((exchange,chain)->{
//            ServerHttpRequest loggedInUser;
//            if(routeValidator.isSecured.test(exchange.getRequest()))
//
//                //header contains token or not
//                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
//                    throw new RuntimeException("missing authorization header");
//                }
//
//            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
//
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                authHeader = authHeader.substring(7);
//            }
//
//            try {
//                jwtUtil.validateToken(authHeader);
//
//                 loggedInUser = exchange.getRequest()
//                        .mutate()
//                        .header("loggedInUser", jwtUtil.extractUserName(authHeader))
//                        .build();
//            } catch (Exception e) {
//                System.out.println("invalid access...!");
//                throw new RuntimeException("un authorized access to application");
//            }
//
//            return chain.filter(exchange.mutate().request(loggedInUser).build());
//        });
//    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {

            ServerHttpRequest request = exchange.getRequest();

            if (routeValidator.isSecured.test(request)) {

                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("Missing authorization header");
                }

                String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                try {
                    jwtUtil.validateToken(authHeader);

                    request = request.mutate()
                            .header("loggedInUser", jwtUtil.extractUserName(authHeader))
                            .build();

                } catch (Exception e) {
                    throw new RuntimeException("Unauthorized access");
                }
            }

            return chain.filter(exchange.mutate().request(request).build());
       //     return chain.filter(exchange);
        });
    }

    public static class Config{

    }
}
