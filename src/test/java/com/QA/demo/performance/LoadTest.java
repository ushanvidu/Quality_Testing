package com.QA.demo.performance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoadTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api";
    }

    @Test
    void testApiLoadPerformance() throws InterruptedException, ExecutionException {
        System.out.println("=== API LOAD PERFORMANCE TEST ===");

        int threadCount = 10;
        int requestsPerThread = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Callable<LoadResult>> tasks = new ArrayList<>();

        AtomicInteger requestCounter = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> {
                int successCount = 0;
                int errorCount = 0;
                long totalTime = 0;

                for (int j = 0; j < requestsPerThread; j++) {
                    int requestId = requestCounter.incrementAndGet();
                    String userJson = String.format(
                            "{\"name\": \"LoadUser-%d\", \"email\": \"loaduser%d@test.com\", \"age\": %d}",
                            requestId, requestId, 20 + (requestId % 30)
                    );

                    try {
                        long startTime = System.nanoTime();

                        given()
                                .contentType(ContentType.JSON)
                                .body(userJson)
                                .when()
                                .post("/users")
                                .then()
                                .statusCode(201);

                        long endTime = System.nanoTime();
                        long duration = (endTime - startTime) / 1_000_000;

                        totalTime += duration;
                        successCount++;

                    } catch (Exception e) {
                        errorCount++;
                    }
                }

                return new LoadResult(successCount, errorCount, totalTime);
            });
        }

        long testStartTime = System.currentTimeMillis();
        List<Future<LoadResult>> results = executor.invokeAll(tasks);

        int totalSuccess = 0;
        int totalErrors = 0;
        long totalTime = 0;

        for (Future<LoadResult> result : results) {
            LoadResult loadResult = result.get();
            totalSuccess += loadResult.successCount;
            totalErrors += loadResult.errorCount;
            totalTime += loadResult.totalTime;
        }

        long testEndTime = System.currentTimeMillis();
        long totalTestTime = testEndTime - testStartTime;

        double avgResponseTime = totalSuccess > 0 ? (double) totalTime / totalSuccess : 0;
        double throughput = totalTestTime > 0 ? (totalSuccess * 1000.0) / totalTestTime : 0;
        int totalRequests = totalSuccess + totalErrors;

        System.out.println("=== LOAD TEST RESULTS ===");
        System.out.println("Total API Requests: " + totalRequests);
        System.out.println("Successful: " + totalSuccess);
        System.out.println("Errors: " + totalErrors);
        System.out.println("Success Rate: " + String.format("%.2f", (totalSuccess * 100.0 / totalRequests)) + "%");
        System.out.println("Average Response Time: " + String.format("%.2f", avgResponseTime) + "ms");
        System.out.println("Throughput: " + String.format("%.2f", throughput) + " requests/second");
        System.out.println("Total Test Duration: " + totalTestTime + "ms");
        System.out.println("Concurrency Level: " + threadCount + " threads");

        executor.shutdown();
    }

    private static class LoadResult {
        int successCount;
        int errorCount;
        long totalTime;

        LoadResult(int successCount, int errorCount, long totalTime) {
            this.successCount = successCount;
            this.errorCount = errorCount;
            this.totalTime = totalTime;
        }
    }
}