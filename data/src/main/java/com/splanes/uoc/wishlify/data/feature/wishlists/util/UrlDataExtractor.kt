package com.splanes.uoc.wishlify.data.feature.wishlists.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Handler
import android.os.Looper
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.milliseconds

/**
 * Local webpage metadata extractor backed by an off-screen [WebView].
 *
 * It acts as a fallback strategy when remote extraction is incomplete, letting
 * the app inspect the rendered page and its JSON-LD metadata directly.
 */
class UrlDataExtractor(private val context: Context) {

  /**
   * Loads the target page, executes the extraction script and returns the
   * resolved metadata, or `null` when the page cannot be processed in time.
   */
  @SuppressLint("SetJavaScriptEnabled")
  suspend fun extract(url: String): UrlMetadata? = withContext(Dispatchers.Main) {
    withTimeoutOrNull(15_000.milliseconds) {
      suspendCancellableCoroutine { continuation ->

        Timber.d("UrlDataExtractor -> start loading url=%s", url)

        var extractionStarted = false
        var finishedSuccessfully = false

        val webView = WebView(context).apply {
          settings.javaScriptEnabled = true
          settings.domStorageEnabled = true
          settings.loadsImagesAutomatically = true
          settings.blockNetworkImage = false
          settings.javaScriptCanOpenWindowsAutomatically = false
          settings.userAgentString =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/124.0.0.0 Safari/537.36"

          webChromeClient = object : WebChromeClient() {}

          webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
              Timber.d("UrlDataExtractor -> onPageStarted url=%s", url)
            }

            override fun onPageFinished(webView: WebView, url: String) {
              Timber.d("UrlDataExtractor -> onPageFinished url=%s title=%s", url, webView.title)
              if (extractionStarted || finishedSuccessfully) return
              extractionStarted = true

              Handler(Looper.getMainLooper()).postDelayed({
                Timber.d("UrlDataExtractor -> evaluating javascript")
                if (finishedSuccessfully) return@postDelayed

                webView.evaluateJavascript(EXTRACTION_SCRIPT) { raw ->
                  Timber.d("UrlDataExtractor -> js result raw=%s", raw)

                  val result = runCatching {
                    val decoded = JSONArray("[$raw]").getString(0)
                    val json = JSONObject(decoded)

                    UrlMetadata(
                      title = json.optString("title").takeIf { it.isNotBlank() },
                      description = json.optString("description").takeIf { it.isNotBlank() },
                      image = json.optString("image").takeIf { it.isNotBlank() },
                      siteName = json.optString("siteName").takeIf { it.isNotBlank() },
                      jsonLd = json.optJSONArray("jsonLd")?.toJsonObjectList()
                    )
                  }.onFailure {
                    Timber.e(it, "UrlDataExtractor -> parsing js result failed")
                  }.getOrNull()

                  finishedSuccessfully = true
                  if (continuation.isActive) {
                    continuation.resume(result)
                  }

                  webView.destroy()
                }
              }, 1200)
            }

            override fun onReceivedError(
              webView: WebView?,
              request: WebResourceRequest?,
              error: WebResourceError?
            ) {
              Timber.e(
                "UrlDataExtractor -> onReceivedError url=%s code=%s desc=%s isMainFrame=%s",
                request?.url,
                error?.errorCode,
                error?.description,
                request?.isForMainFrame
              )

              if (request?.isForMainFrame == true && continuation.isActive) {
                continuation.resume(null)
                webView?.destroy()
              }
            }

            override fun onReceivedHttpError(
              webView: WebView?,
              request: WebResourceRequest?,
              errorResponse: WebResourceResponse?
            ) {
              Timber.e(
                "UrlDataExtractor -> onReceivedHttpError url=%s status=%s isMainFrame=%s",
                request?.url,
                errorResponse?.statusCode,
                request?.isForMainFrame
              )

              if (request?.isForMainFrame == true && continuation.isActive) {
                continuation.resume(null)
                webView?.destroy()
              }
            }

            override fun onReceivedSslError(
              webView: WebView?,
              handler: SslErrorHandler?,
              error: SslError?
            ) {
              Timber.e("UrlDataExtractor -> onReceivedSslError error=%s", error)
              handler?.cancel()

              if (continuation.isActive) {
                continuation.resume(null)
                webView?.destroy()
              }
            }
          }
        }

        continuation.invokeOnCancellation {
          Timber.d("UrlDataExtractor -> cancelled")
          webView.destroy()
        }

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

        val headers = mapOf(
          "Accept-Language" to "es-ES,es;q=0.9,en;q=0.8",
          "Cache-Control" to "no-cache",
          "Pragma" to "no-cache",
          "Upgrade-Insecure-Requests" to "1"
        )

        webView.loadUrl(url,headers)
      }
    }
  }
}

private const val EXTRACTION_SCRIPT = """
(function() {
  function getMeta(name) {
    var el = document.querySelector('meta[property="' + name + '"]') ||
             document.querySelector('meta[name="' + name + '"]');
    return el ? el.content : null;
  }

  var jsonLd = [];
  var scripts = document.querySelectorAll('script[type="application/ld+json"]');

  scripts.forEach(function(s) {
    try {
      if (s.innerText) {
        jsonLd.push(JSON.parse(s.innerText));
      }
    } catch(e) {}
  });

  return JSON.stringify({
    title: document.title || null,
    description: getMeta("og:description") || getMeta("twitter:description") || getMeta("description"),
    image: getMeta("og:image") || getMeta("twitter:image"),
    siteName: getMeta("og:site_name"),
    jsonLd: jsonLd
  });
})();
"""

/** Lightweight in-memory representation of metadata extracted from a webpage. */
data class UrlMetadata(
  val title: String?,
  val description: String?,
  val image: String?,
  val siteName: String?,
  val jsonLd: List<JSONObject>?
)

private fun JSONArray.toJsonObjectList(): List<JSONObject> =
  buildList {
    for (i in 0 until length()) {
      val item = opt(i)
      if (item is JSONObject) add(item)
    }
  }
