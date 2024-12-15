package ir.the_code.guardify.di

import ir.the_code.guardify.data.helpers.MessagesHelper
import ir.the_code.guardify.data.helpers.MessagesHelperImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val otherModules = module {
    singleOf(::MessagesHelperImpl) bind MessagesHelper::class
}