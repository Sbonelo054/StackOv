package com.apps.stackoverflow

import androidx.compose.ui.window.ComposeUIViewController
import com.apps.stackoverflow.presentation.navigation.StackOverflowNavGraph
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {

    return ComposeUIViewController {
        StackOverflowNavGraph()
    }
}