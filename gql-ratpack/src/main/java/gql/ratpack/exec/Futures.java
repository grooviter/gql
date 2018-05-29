package gql.ratpack.exec;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import ratpack.exec.Execution;

/**
 * Creates instances of {@link CompletableFuture} honoring Ratpack's executors
 *
 * @since 0.1.0
 */
public class Futures {

  /**
   * Creates a {@link CompletableFuture} using the blocking executor
   *
   * @param supplier code providing a value
   * @return a blocking instance of {@link CompletableFuture}
   * @since 0.1.0
   */
  public static <T> CompletableFuture<T> blocking(Supplier<T> supplier) {
    return CompletableFuture.supplyAsync(supplier, Execution.current().getController().getBlockingExecutor());
  }

  /**
   * Creates a {@link CompletableFuture} using the non blocking executor (event loop)
   *
   * @param supplier code providing a value
   * @return a non blocking instance of {@link CompletableFuture}
   * @since 0.1.0
   */
  public static <T> CompletableFuture<T> async(Supplier<T> supplier) {
    return CompletableFuture.supplyAsync(supplier, Execution.current().getController().getExecutor());
  }
}

