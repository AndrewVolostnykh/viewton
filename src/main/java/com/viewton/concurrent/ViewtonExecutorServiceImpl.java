package com.viewton.concurrent;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class ViewtonExecutorServiceImpl implements ViewtonExecutorService {

    private final ExecutorService executor = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors() * 2);

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    @Override
    public <T> Future<T> supply(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, executor);
    }
}
