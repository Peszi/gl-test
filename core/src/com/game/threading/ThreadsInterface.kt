package com.main.threading

internal interface ThreadsInterface {
    fun runJobs(jobs: List<JobDesc>, counter: AtomicCounter? = null)
    fun waitForDone(counter: AtomicCounter)
}