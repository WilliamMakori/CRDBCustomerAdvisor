package com.crdb.advisor;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.crdb.advisor.handler.GetProfileHandler;
import com.crdb.advisor.handler.GetRecommendationsHandler;
import com.crdb.advisor.handler.UpdateProfileHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LocalServer {

    private static final GetRecommendationsHandler recommendationsHandler =
        new GetRecommendationsHandler();
    private static final UpdateProfileHandler updateProfileHandler =
        new UpdateProfileHandler();
    private static final GetProfileHandler getProfileHandler =
        new GetProfileHandler();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(
            new InetSocketAddress(3000), 0);

        // GET /recommendations/{customerId}
        server.createContext("/recommendations", exchange -> {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                handleCors(exchange);
                return;
            }
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
                return;
            }
            String customerId = extractLastPathSegment(
                exchange.getRequestURI().getPath());
            APIGatewayProxyRequestEvent event = buildEvent(
                "GET", customerId, null);
            APIGatewayProxyResponseEvent response =
                recommendationsHandler.handleRequest(event, new LocalContext());
            sendResponse(exchange, response.getStatusCode(), response.getBody());
        });

        // GET and POST /customers/{customerId}/profile
        server.createContext("/customers", exchange -> {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                handleCors(exchange);
                return;
            }
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            String customerId = parts.length > 2 ? parts[2] : "";

            if ("GET".equals(exchange.getRequestMethod())) {
                APIGatewayProxyRequestEvent event = buildEvent(
                    "GET", customerId, null);
                APIGatewayProxyResponseEvent response =
                    getProfileHandler.handleRequest(event, new LocalContext());
                sendResponse(exchange, response.getStatusCode(), response.getBody());

            } else if ("POST".equals(exchange.getRequestMethod()) ||
                       "PUT".equals(exchange.getRequestMethod())) {
                String body = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8);
                APIGatewayProxyRequestEvent event = buildEvent(
                    exchange.getRequestMethod(), customerId, body);
                APIGatewayProxyResponseEvent response =
                    updateProfileHandler.handleRequest(event, new LocalContext());
                sendResponse(exchange, response.getStatusCode(), response.getBody());
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("Local server running on http://localhost:3000");
        System.out.println("Press Ctrl+C to stop");
    }

    private static APIGatewayProxyRequestEvent buildEvent(
            String method, String customerId, String body) {
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("customerId", customerId);

        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setHttpMethod(method);
        event.setPathParameters(pathParams);
        event.setBody(body);
        return event;
    }

    private static String extractLastPathSegment(String path) {
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }

    private static void handleCors(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add(
            "Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add(
            "Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        exchange.getResponseHeaders().add(
            "Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(204, -1);
    }

    private static void sendResponse(
            HttpExchange exchange, int statusCode, String body) throws IOException {
        exchange.getResponseHeaders().add(
            "Content-Type", "application/json");
        exchange.getResponseHeaders().add(
            "Access-Control-Allow-Origin", "*");
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}