package com.main.threading

import java.util.concurrent.atomic.AtomicInteger

internal class AtomicCounter(
        limit: Int
) {

    private val counter = AtomicInteger(limit)
    private val lock = java.lang.Object()

    fun setWaiting() {
        synchronized(lock) { lock.wait() }
    }

    fun afterJob() {
        if (counter.decrementAndGet() <= 0)
            synchronized(lock) { lock.notifyAll() }
    }
}