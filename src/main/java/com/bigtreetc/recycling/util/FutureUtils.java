package com.bigtreetc.recycling.util;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.val;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestContextHolder;

public class FutureUtils {

  public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) {
    return CompletableFuture.supplyAsync(withContext(supplier));
  }

  public static Runnable withContext(Runnable runnable) {
    val requestAttributes = RequestContextHolder.currentRequestAttributes();
    val contextMap = MDC.getCopyOfContextMap();
    return () -> {
      RequestContextHolder.setRequestAttributes(requestAttributes);
      MDC.setContextMap(contextMap);
      try {
        runnable.run();
      } finally {
        MDC.clear();
        RequestContextHolder.resetRequestAttributes();
      }
    };
  }

  public static <U> Supplier<U> withContext(Supplier<U> supplier) {
    val requestAttributes = RequestContextHolder.currentRequestAttributes();
    val contextMap = MDC.getCopyOfContextMap();
    return () -> {
      RequestContextHolder.setRequestAttributes(requestAttributes);
      MDC.setContextMap(contextMap);
      try {
        return supplier.get();
      } finally {
        MDC.clear();
      }
    };
  }

  public static <T, U> Function<T, U> withContext(Function<? super T, ? extends U> fn) {
    val requestAttributes = RequestContextHolder.currentRequestAttributes();
    val contextMap = MDC.getCopyOfContextMap();
    return t -> {
      RequestContextHolder.setRequestAttributes(requestAttributes);
      MDC.setContextMap(contextMap);
      try {
        return fn.apply(t);
      } finally {
        MDC.clear();
        RequestContextHolder.resetRequestAttributes();
      }
    };
  }

  public static <T> BiConsumer<? super T, ? super Throwable> withContext(
      BiConsumer<? super T, ? super Throwable> action) {
    val requestAttributes = RequestContextHolder.currentRequestAttributes();
    val contextMap = MDC.getCopyOfContextMap();
    return (t, ex) -> {
      RequestContextHolder.setRequestAttributes(requestAttributes);
      MDC.setContextMap(contextMap);
      try {
        action.accept(t, ex);
      } finally {
        MDC.clear();
        RequestContextHolder.resetRequestAttributes();
      }
    };
  }
}
