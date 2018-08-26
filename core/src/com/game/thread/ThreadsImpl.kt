package com.main.threading

import com.game.diag.Log
import java.util.concurrent.Executors

internal class ThreadsImpl: ThreadsInterface {

    private var jobsExecutor = Executors.newWorkStealingPool()

    init {
        Log.info("allocating " + Runtime.getRuntime().availableProcessors() + " threads ..")
    }

    @Synchronized
    override fun runJobs(jobs: List<JobDesc>, counter: AtomicCounter?) {
        jobs.forEach{
            it.threadsInterface = this
            if (counter != null) it.counter = counter
            jobsExecutor.submit(it)
        }
    }

    override fun waitForDone(counter: AtomicCounter) {
        counter.setWaiting()
    }
}