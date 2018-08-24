package com.game.core

import com.main.threading.JobDesc

internal class GameInitJob(
        engineCore: EngineCore
): JobDesc(
        {
            engineCore.coreThreads.runJobs(
                    listOf(
                            GameLogicJob(engineCore),
                            RenderLogicJob(engineCore)
                    )
            )
        }
)