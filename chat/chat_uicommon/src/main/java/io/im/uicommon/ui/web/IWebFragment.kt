package io.im.uicommon.ui.web

import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import io.im.uicommon.base.ChatBaseFragment
import io.im.uicommon.databinding.CommonChatFragmentWebBinding
import io.im.uicommon.widgets.IMBasicWebView


/**
 * by DAD FZ
 * 2026/5/23
 * desc：
 **/
class IWebFragment : ChatBaseFragment() {

    private val binding: CommonChatFragmentWebBinding by lazy {
        CommonChatFragmentWebBinding.inflate(layoutInflater)
    }

    private var webUrl = ""
    private lateinit var webView: IMBasicWebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        webUrl = arguments?.getString("url") ?: ""
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        webView = IMBasicWebView(mActivity)
        binding.webParent.addView(
            webView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        initWeb()
        loadUrl(webUrl)
    }

    fun loadUrl(url: String) {
        webUrl = url
        if (!TextUtils.isEmpty(url)) {
            webView.loadUrl(webUrl)
        }
    }

    fun onBack(): Boolean {
        if (webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        try {
            webView.onResume()
        } catch (_: Exception) {
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            webView.onPause()
        } catch (_: Exception) {
        }
    }

    override fun onDestroy() {
        try {
            webView.clearHistory()
            webView.freeMemory()
            val parent = webView.parent as? ViewGroup
            parent?.removeView(webView)
            binding.webParent.removeAllViews()
            binding.webParent.visibility = View.GONE
            webView.visibility = View.GONE
            webView.removeAllViews()
            webView.destroy()
        } catch (_: Exception) {
        }
        super.onDestroy()
    }


    private fun initWeb() {
        webView.getSettings().cacheMode = WebSettings.LOAD_NO_CACHE
        webView.clearCache(true)
        webView.setWebChromeClient(object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                mActivity.runOnUiThread {
                    if (newProgress < 100) {
                        binding.progress.visibility = View.VISIBLE
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            binding.progress.setProgress(newProgress, true)
                        } else {
                            binding.progress.progress = newProgress
                        }
                    } else {
                        view?.postDelayed({
                            if (binding.progress.visibility == View.VISIBLE) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    binding.progress.setProgress(100, true)
                                } else {
                                    binding.progress.progress = 100
                                }
                                binding.progress.visibility = View.GONE
                                binding.progress.progress = 0
                            }
                        }, 150);
                    }
                }
            }
        })
        webView.setWebViewClient(object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return true
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                handler?.proceed()
            }

        })
    }

    companion object {
        @JvmStatic
        fun instance(url: String): IWebFragment {
            return IWebFragment().apply {
                arguments = Bundle().apply {
                    putString("url", url)
                }
            }
        }
    }

}