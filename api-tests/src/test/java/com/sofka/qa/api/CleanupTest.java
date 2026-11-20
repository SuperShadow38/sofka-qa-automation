package com.sofka.qa.api;

import com.intuit.karate.Runner;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CleanupTest {
    @Test
    void removesTheUserAfterAnIntermediateAssertionFails() throws Exception {
        verifyCleanup(true);
    }

    @Test
    void preservesTheOriginalAssertionWhenCleanupAlsoFails() throws Exception {
        verifyCleanup(false);
    }

    private void verifyCleanup(boolean cleanupWorks) throws Exception {
        var exists = new AtomicBoolean(false);
        var deleteAttempts = new AtomicInteger();
        var payload = new AtomicReference<>("");
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/v2/user", exchange -> {
            String method = exchange.getRequestMethod();
            int status = 200;
            String body = "{\"code\":200,\"type\":\"unknown\",\"message\":\"ok\"}";
            if (method.equals("POST")) {
                payload.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
                exists.set(true);
                String id = payload.get().replaceFirst("(?s).*\"id\"\\s*:\\s*(\\d+).*", "$1");
                body = "{\"code\":200,\"type\":\"unknown\",\"message\":\"" + id + "\"}";
            } else if (method.equals("DELETE")) {
                deleteAttempts.incrementAndGet();
                if (cleanupWorks) exists.set(false);
                else { status = 503; body = "{\"message\":\"cleanup unavailable\"}"; }
            } else if (exists.get()) {
                body = payload.get().replace("\"Ana\"", "\"Nombre incorrecto\"");
            } else {
                status = 404;
                body = "{\"code\":1,\"type\":\"error\",\"message\":\"User not found\"}";
            }
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            byte[] response = body.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, response.length);
            try (var output = exchange.getResponseBody()) { output.write(response); }
        });
        server.start();
        String previous = System.getProperty("api.base.url");
        try {
            System.setProperty("api.base.url", "http://127.0.0.1:" + server.getAddress().getPort() + "/v2");
            var result = Runner.path("classpath:users/lifecycle.feature")
                    .reportDir("target/cleanup-verification/" + (cleanupWorks ? "removed" : "unavailable"))
                    .parallel(1);
            assertEquals(1, result.getFailCount(), "La aserción de nombre debe fallar intencionalmente");
            assertTrue(result.getErrorMessages().contains("firstName"), result.getErrorMessages());
            assertFalse(result.getErrorMessages().contains("No se confirmó la limpieza"), "Debe conservarse el error original");
            assertEquals(1, deleteAttempts.get(), "Debe intentarse la limpieza una vez, sin recursión");
            assertEquals(!cleanupWorks, exists.get(), "El usuario se elimina si el servicio responde");
        } finally {
            if (previous == null) System.clearProperty("api.base.url");
            else System.setProperty("api.base.url", previous);
            server.stop(0);
        }
    }
}
