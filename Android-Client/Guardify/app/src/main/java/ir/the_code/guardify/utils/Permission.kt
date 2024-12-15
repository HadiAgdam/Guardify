package ir.the_code.guardify.utils

import android.content.Context
import android.content.pm.PackageManager

fun Context.hasPermissions(
    permissions: List<String>
) = permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }