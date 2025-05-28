package net.bytle.exec;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Execute a boolean action (such as a ping)
 * for a certain duration at a certain interval
 */
public class PingTaskExecutor {

  /**
   * Result class to hold execution statistics
   */
  public static class ExecutionResult {
    private final int successCount;
    private final int totalExecutions;
    private final boolean stoppedEarly;

    public ExecutionResult(int successCount, int totalExecutions, boolean stoppedEarly) {
      this.successCount = successCount;
      this.totalExecutions = totalExecutions;
      this.stoppedEarly = stoppedEarly;
    }

    public int getSuccessCount() {
      return successCount;
    }

    public int getTotalExecutions() {
      return totalExecutions;
    }

    public int getFailureCount() {
      return totalExecutions - successCount;
    }

    public boolean wasStoppedEarly() {
      return stoppedEarly;
    }

    public double getSuccessRate() {
      return totalExecutions > 0 ? (double) successCount / totalExecutions : 0.0;
    }

    @Override
    public String toString() {
      return String.format("ExecutionResult{successes=%d, total=%d, failures=%d, successRate=%.2f%%, stoppedEarly=%s}",
        successCount, totalExecutions, getFailureCount(), getSuccessRate() * 100, stoppedEarly);
    }
  }

  /**
   * Executes a task repeatedly at specified intervals for a given duration
   *
   * @param task            The task to execute (Runnable)
   * @param intervalSeconds Interval between executions in seconds
   * @param durationSeconds Total duration to run in seconds
   */
  public static void executeAtInterval(Runnable task, long intervalSeconds, long durationSeconds) {
    executeAtInterval(() -> {
      task.run();
      return false;
    }, intervalSeconds, durationSeconds, false);
  }

  /**
   * Executes a task repeatedly at specified intervals for a given duration
   *
   * @param task            The task to execute that returns true if successful
   * @param intervalSeconds Interval between executions in seconds
   * @param durationSeconds Total duration to run in seconds
   * @param stopOnSuccess   If true, stops execution when task returns true
   * @return ExecutionResult containing success count and execution statistics
   */
  public static ExecutionResult executeAtInterval(Supplier<Boolean> task, long intervalSeconds, long durationSeconds, boolean stopOnSuccess) {
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    AtomicBoolean shouldStop = new AtomicBoolean(false);
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger totalExecutions = new AtomicInteger(0);
    AtomicBoolean stoppedEarly = new AtomicBoolean(false);

    // Schedule the task to run at fixed intervals
    scheduler.scheduleAtFixedRate(() -> {
      if (!shouldStop.get()) {
        totalExecutions.incrementAndGet();
        boolean success = task.get();
        if (success) {
          successCount.incrementAndGet();
          if (stopOnSuccess) {
            stoppedEarly.set(true);
            shouldStop.set(true);
            scheduler.shutdownNow();
          }
        }
      }
    }, 0, intervalSeconds, TimeUnit.SECONDS);


    // Wait for completion and return results
    try {
      // Always wait for clean shutdown
      //noinspection ResultOfMethodCallIgnored
      scheduler.awaitTermination(durationSeconds, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    return new ExecutionResult(successCount.get(), totalExecutions.get(), stoppedEarly.get());
  }

  // Example usage
  public static void main(String[] args) {
    // Example 1: Simple ping without stopping on success (void return)
    System.out.println("Example 1: Ping every second for 5 seconds (no tracking)");
    executeAtInterval(() -> {
      System.out.println("Simple ping at " + System.currentTimeMillis());
    }, 1, 5);

    try {
      Thread.sleep(6000);
    } catch (InterruptedException e) {
    }

    // Example 2: Ping with result tracking, stop on first success
    System.out.println("\nExample 2: Ping every second, stop on first success");
    ExecutionResult result1 = executeAtInterval(() -> {
      System.out.println("Attempting to ping server...");
      // Simulate ping logic - return true if successful
      boolean success = Math.random() > 0.7; // 30% chance of success
      if (success) {
        System.out.println("Ping successful!");
        return true;
      } else {
        System.out.println("Ping failed, will retry...");
        return false;
      }
    }, 1, 10, true); // Stop on success, max 10 seconds

    System.out.println("Result: " + result1);

    // Example 3: Run for full duration and collect statistics
    System.out.println("\nExample 3: Ping every second for 5 seconds, collect all results");
    ExecutionResult result2 = executeAtInterval(() -> {
      System.out.println("Testing connection...");
      boolean success = Math.random() > 0.5; // 50% chance of success
      System.out.println(success ? "✓ Success" : "✗ Failed");
      return success;
    }, 1, 5, false); // Don't stop on success

    System.out.println("Final Result: " + result2);
    System.out.println("Success rate: " + String.format("%.1f%%", result2.getSuccessRate() * 100));
  }
}
