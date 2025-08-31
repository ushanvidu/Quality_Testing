package com.QA.demo.performance;

import com.QA.demo.model.User;
import com.QA.demo.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@ActiveProfiles("test")
public class PerformanceTest {

    @Autowired
    private UserService userService;

    private List<Long> createdUserIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        createdUserIds.clear();
    }

    @AfterEach
    void tearDown() {
        // Clean up all created users
        for (Long userId : createdUserIds) {
            try {
                userService.deleteUser(userId);
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
        createdUserIds.clear();
    }

    @Test
    void testSingleUserCreationPerformance() {
        System.out.println("=== SINGLE USER CREATION PERFORMANCE TEST ===");

        User user = new User();
        user.setName("Performance Test User");
        user.setEmail("performance" + System.currentTimeMillis() + "@test.com"); // Unique email
        user.setAge(30);

        // Measure performance
        long totalTime = 0;
        int iterations = 5;

        for (int i = 0; i < iterations; i++) {
            // Create unique user each time
            User testUser = new User();
            testUser.setName("User " + i);
            testUser.setEmail("test" + System.currentTimeMillis() + i + "@example.com");
            testUser.setAge(25 + i);

            long startTime = System.nanoTime();
            User createdUser = userService.createUser(testUser);
            long endTime = System.nanoTime();

            long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
            totalTime += duration;

            // Store for cleanup
            createdUserIds.add(createdUser.getId());

            System.out.println("Iteration " + (i + 1) + ": " + duration + "ms");
        }

        double averageTime = (double) totalTime / iterations;
        System.out.println("Average response time: " + averageTime + "ms");
        System.out.println("Total time for " + iterations + " iterations: " + totalTime + "ms");
    }

    @Test
    void testConcurrentUserCreation() throws InterruptedException, ExecutionException {
        System.out.println("=== CONCURRENT USER CREATION PERFORMANCE TEST ===");

        int threadCount = 3; // Reduced for stability
        int requestsPerThread = 5; // Reduced for stability
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Callable<PerformanceResult>> tasks = new ArrayList<>();

        AtomicInteger userCounter = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> {
                long totalTime = 0;
                int successCount = 0;
                int errorCount = 0;

                for (int j = 0; j < requestsPerThread; j++) {
                    int userId = userCounter.incrementAndGet();
                    User user = new User();
                    user.setName("User-" + System.currentTimeMillis() + "-" + userId);
                    user.setEmail("user" + System.currentTimeMillis() + "-" + userId + "@test.com");
                    user.setAge(20 + (userId % 30));

                    try {
                        long startTime = System.nanoTime();
                        User createdUser = userService.createUser(user);
                        long endTime = System.nanoTime();

                        long duration = (endTime - startTime) / 1_000_000;
                        totalTime += duration;
                        successCount++;

                        // Store for cleanup
                        synchronized (createdUserIds) {
                            createdUserIds.add(createdUser.getId());
                        }

                    } catch (Exception e) {
                        errorCount++;
                        System.out.println("Error creating user: " + e.getMessage());
                    }
                }

                return new PerformanceResult(successCount, errorCount, totalTime);
            });
        }

        long testStartTime = System.currentTimeMillis();
        List<Future<PerformanceResult>> results = executor.invokeAll(tasks);

        int totalSuccess = 0;
        int totalErrors = 0;
        long totalTime = 0;

        for (Future<PerformanceResult> result : results) {
            PerformanceResult perfResult = result.get();
            totalSuccess += perfResult.successCount;
            totalErrors += perfResult.errorCount;
            totalTime += perfResult.totalTime;
        }

        long testEndTime = System.currentTimeMillis();
        long totalTestTime = testEndTime - testStartTime;

        int totalRequests = totalSuccess + totalErrors;
        double avgResponseTime = totalSuccess > 0 ? (double) totalTime / totalSuccess : 0;
        double throughput = totalTestTime > 0 ? (totalSuccess * 1000.0) / totalTestTime : 0;

        System.out.println("=== PERFORMANCE TEST RESULTS ===");
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Successful: " + totalSuccess);
        System.out.println("Errors: " + totalErrors);
        System.out.println("Success Rate: " + String.format("%.2f", (totalSuccess * 100.0 / totalRequests)) + "%");
        System.out.println("Average Response Time: " + String.format("%.2f", avgResponseTime) + "ms");
        System.out.println("Throughput: " + String.format("%.2f", throughput) + " requests/second");
        System.out.println("Total Test Time: " + totalTestTime + "ms");
        System.out.println("Concurrency Level: " + threadCount + " threads");

        executor.shutdown();
    }

    private static class PerformanceResult {
        int successCount;
        int errorCount;
        long totalTime;

        PerformanceResult(int successCount, int errorCount, long totalTime) {
            this.successCount = successCount;
            this.errorCount = errorCount;
            this.totalTime = totalTime;
        }
    }
}