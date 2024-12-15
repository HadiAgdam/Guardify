package ir.the_code.guardify.viewmodels.web_browser

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Patterns
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebContent
import com.google.accompanist.web.WebViewNavigator
import com.google.accompanist.web.WebViewState
import ir.the_code.guardify.components.bottom_navigation.bottomNavigationHeight
import ir.the_code.guardify.data.models.url.IsUrlBlocked
import ir.the_code.guardify.data.network.response.NetworkErrors
import ir.the_code.guardify.data.network.response.Response
import ir.the_code.guardify.data.network.response.onSuccess
import ir.the_code.guardify.data.network.services.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class WebBrowserViewModel(private val apiService: ApiService) : ViewModel() {
    var currentUrl by mutableStateOf("https://google.com/")
    val navigator = WebViewNavigator(viewModelScope)
    var favicon by mutableStateOf<ImageBitmap?>(null)
    var progress by mutableFloatStateOf(0f)

    private val _response = MutableStateFlow<Response<IsUrlBlocked, NetworkErrors>>(Response.Idle)
    val response = _response.asStateFlow()


    val client = object : AccompanistWebViewClient() {
        override fun onPageFinished(view: WebView, url: String?) {
            url?.let {
                currentUrl = it
            }
            super.onPageFinished(view, url)
            // Apply Margin
//            view.evaluateJavascript(
//                """
//            (function() {
//                var body = document.body;
//                body.style.marginBottom = '${bottomNavigationHeight.value + 32 + navigationBarsHeight}px';
//            })();
//            """.trimIndent()
//            ) {}
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            if (request != null) {
                runBlocking {
                    checkUrlIsValid(request.url.toString())
                }
            }
            return super.shouldOverrideUrlLoading(view, request)
        }
    }

    val chromeClient = object : AccompanistWebChromeClient() {
        override fun onReceivedIcon(view: WebView, icon: Bitmap?) {
            favicon = icon?.asImageBitmap()
            super.onReceivedIcon(view, icon)
        }

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            progress = (newProgress / 100f).coerceIn(0f, 1f)
            super.onProgressChanged(view, newProgress)
        }
    }

    fun loadUrl(url: String) = viewModelScope.launch(Dispatchers.IO) {
        currentUrl = url.getActualUrl()
        if (checkUrlIsValid(currentUrl)) {
            navigator.loadUrl(currentUrl)
        }
    }

    private fun String.getActualUrl() =
        if (isUrl()) this else "https://www.google.com/search?q=$this"

    suspend fun checkUrlIsValid(link: String): Boolean {
        _response.update { Response.Loading }
        val newResponse = apiService.isLinkBlocked(link)
        _response.update { newResponse }
        newResponse.onSuccess {
            return true
        }
        return false
    }

    private val urlRegex =
        Regex("^(https?:\\/\\/)?(www\\.)?[a-zA-Z0-9]+\\.[a-zA-Z]{2,}(:[0-9]+)?(/[\\w-]+)*/?")

    private fun String.isUrl(): Boolean {
        return urlRegex.matches(this)
    }
}