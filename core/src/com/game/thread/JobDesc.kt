package com.main.threading

import java.util.concurrent.Callable

internal abstract class JobDesc(
        private val block: (threadsInterface: ThreadsInterface) -> Unit

): Callable<Int> {

    var counter: AtomicCounter? = null
    lateinit var threadsInterface: ThreadsInterface

    override fun call(): Int {
        block.invoke(threadsInterface)
        counter?.afterJob()
        return 1
    }
}