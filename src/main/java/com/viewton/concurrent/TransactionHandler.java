package com.viewton.concurrent;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Callable;

@Component
public class TransactionHandler {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void doInNewTransaction(Runnable runnable) {
        runnable.run();
    }

    @SneakyThrows
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> T doInNewTransaction(Callable<T> callable) {
        return callable.call();
    }
}
