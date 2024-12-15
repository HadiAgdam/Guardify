package ir.the_code.guardify.data.network.response

import android.content.Context
import ir.the_code.guardify.R

enum class NetworkErrors : ErrorI {
    REQUEST_TIMEOUT,
    PAY_LOAD_TOO_LARGE,
    UNAUTHORIZED,
    SERIALIZE,
    NO_INTERNET,
    UNKNOWN,
    SERVER_ERROR,
    CLIENT_ERROR,
    BAD_REQUEST,
    INVALID_DATA,
}


fun NetworkErrors.getSuitableMessage(context: Context) = when(this){
    NetworkErrors.REQUEST_TIMEOUT, NetworkErrors.NO_INTERNET -> context.getString(R.string.problem_in_connect)
    NetworkErrors.UNAUTHORIZED -> context.getString(R.string.unauthorize_error)
    NetworkErrors.BAD_REQUEST -> context.getString(R.string.bad_request_error)
    NetworkErrors.UNKNOWN -> context.getString(R.string.unknown_error)
    NetworkErrors.INVALID_DATA -> context.getString(R.string.invalid_data)
    NetworkErrors.SERIALIZE -> context.getString(R.string.serialize_error)
    NetworkErrors.SERVER_ERROR -> context.getString(R.string.server_error)
    NetworkErrors.CLIENT_ERROR -> context.getString(R.string.client_error)
    NetworkErrors.PAY_LOAD_TOO_LARGE -> context.getString(R.string.pay_load_too_large_error)
}