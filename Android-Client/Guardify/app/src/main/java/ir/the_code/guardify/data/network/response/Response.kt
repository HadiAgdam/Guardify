package ir.the_code.guardify.data.network.response

sealed class Response<out D, out E : ErrorI> {
    data class Success<out D>(val data: D) : Response<D, Nothing>()
    data class Error<out E : ErrorI>(val error: E) : Response<Nothing, E>()
    data object Loading : Response<Nothing, Nothing>()
    data object Idle : Response<Nothing, Nothing>()
}


inline fun <D, E : ErrorI, R> Response<D, E>.map(action: (D) -> R): Response<R, E> {
    return when (this) {
        is Response.Success -> Response.Success(action(data))
        is Response.Error -> Response.Error(error)
        Response.Loading -> Response.Loading
        else -> Response.Idle
    }
}

inline fun <D, E : ErrorI> Response<D, E>.onSuccess(action: (D) -> Unit): Response<D, E> {
    return when (this) {
        is Response.Success -> {
            action(data)
            this
        }

        else -> this
    }
}

inline fun <D, E : ErrorI> Response<D, E>.onError(action: (E) -> Unit): Response<D, E> {
    return when (this) {
        is Response.Error -> {
            action(error)
            this
        }

        else -> this
    }
}

inline fun <D, E : ErrorI> Response<D, E>.onLoading(action: () -> Unit): Response<D, E> {
    return when (this) {
        is Response.Loading -> {
            action()
            this
        }

        else -> this
    }
}