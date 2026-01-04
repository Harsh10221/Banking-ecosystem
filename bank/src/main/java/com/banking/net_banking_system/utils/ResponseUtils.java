package com.banking.net_banking_system.utils;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseUtils {



    public static void writeErrorResponse(HttpServletResponse response, int status, String message) throws IOException{

        response.setStatus(status);
        response.setContentType("application/json");
        String json = String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\"}", message);
        response.getWriter().write(json);
    }

}
