package ir.the_code.guardify.di

import ir.the_code.guardify.viewmodels.web_browser.WebBrowserViewModel
import ir.the_code.guardify.viewmodels.messages.MessagesViewModel
import ir.the_code.guardify.viewmodels.messages_inbox.MessagesInboxViewModel
import ir.the_code.guardify.viewmodels.login.LoginViewModel
import ir.the_code.guardify.viewmodels.register.RegisterViewModel
import ir.the_code.guardify.viewmodels.validation_link.ValidationLinkViewModel
import ir.the_code.guardify.viewmodels.settings.SettingsViewModel
import ir.the_code.guardify.viewmodels.notifications.MyNotificationsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModules = module {
    viewModelOf(::WebBrowserViewModel)
    viewModelOf(::MessagesViewModel)
    viewModelOf(::MessagesInboxViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::ValidationLinkViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::MyNotificationsViewModel)
}