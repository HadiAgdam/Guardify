package ir.the_code.guardify.di

import ir.the_code.guardify.data.workers.InternetDependentWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val workerModule = module {
    workerOf(::InternetDependentWorker)
}