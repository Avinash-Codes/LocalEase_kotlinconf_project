package com.avinash.localease

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun MainViewController(
    mapUIViewController: () -> UIViewController
) = ComposeUIViewController {
    mapViewController = mapUIViewController
    mapUIViewController()
}

lateinit var mapViewController: () -> UIViewController
