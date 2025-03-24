package com.viewton.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public interface ViewtonExecutorService {

    <T> Future<T> supply(Supplier<T> supplier);
}
