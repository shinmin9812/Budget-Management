package com.wanted.teamV.component;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

public class AuthorizationExtractor {

    private static final String BEARER_TYPE = "Bearer ";

    public static String extract(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            System.out.println("No Token Header");
        }

        validateAuthorizationFormat(authorizationHeader);
        return authorizationHeader.substring(BEARER_TYPE.length()).trim();
    }

    public static void validateAuthorizationFormat(String authorizationHeader) {
        if (!authorizationHeader.toLowerCase().startsWith(BEARER_TYPE.toLowerCase())) {
            System.out.println("Invalid Token");
        }
    }
}
