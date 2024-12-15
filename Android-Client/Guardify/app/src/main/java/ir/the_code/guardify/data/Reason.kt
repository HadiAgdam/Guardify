package ir.the_code.guardify.data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import ir.the_code.guardify.ui.theme.DarkBlue
import ir.the_code.guardify.ui.theme.Green
import ir.the_code.guardify.ui.theme.Orange
import ir.the_code.guardify.ui.theme.Red
import ir.the_code.guardify.ui.theme.Yellow

@Immutable
enum class MessageReason(val color: Color) {
    SAFE(Green),
    PHISHING(Red),
    SPAM(Color(0xFF5E35B1)),
    UNSAFE(Orange),
    SUSPICIOUS(Yellow),
    ADVERTISE(DarkBlue)
}

@Immutable
enum class NotificationReason(val color: Color) {
    PHISHING(Red),
    SPAM(Color(0xFF5E35B1)),
    UNSAFE(Orange),
    SUSPICIOUS(Yellow),
    ADVERTISE(DarkBlue)
}