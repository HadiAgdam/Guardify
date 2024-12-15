package ir.the_code.guardify.di

import ir.the_code.guardify.data.repositories.messages.MessagesRepository
import ir.the_code.guardify.data.repositories.messages.MessagesRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::MessagesRepositoryImpl) bind MessagesRepository::class
}