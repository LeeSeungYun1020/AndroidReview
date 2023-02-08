package com.leeseungyun1020.sub1

import androidx.compose.runtime.Composable
import com.google.accompanist.web.WebView
import com.google.accompanist.web.WebViewState
import com.google.accompanist.web.rememberWebViewState

@Composable
fun Wiki(state:WebViewState = rememberWebViewState("https://wikipedia.org")) = WebView(
    state = state,
    onCreated = { it.settings.javaScriptEnabled = true }
)