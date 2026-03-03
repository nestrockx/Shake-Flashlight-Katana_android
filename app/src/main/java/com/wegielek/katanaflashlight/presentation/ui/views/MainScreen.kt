package com.wegielek.katanaflashlight.presentation.ui.views

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wegielek.katanaflashlight.presentation.ui.views.ad.BannerAd
import com.wegielek.katanaflashlight.presentation.ui.views.screen.AboutScreen
import com.wegielek.katanaflashlight.presentation.ui.views.screen.LandingScreen

private object Route {
    const val LANDING_SCREEN = "landingScreen"
    const val ABOUT_SCREEN = "aboutScreen"
}

@Composable
fun MainScreen(
    innerPadding: PaddingValues,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Route.LANDING_SCREEN,
        modifier =
            Modifier.fillMaxSize().background(Color.Black).padding(innerPadding),
    ) {
        composable(route = Route.LANDING_SCREEN) {
            LandingScreen(navigateToAbout = {
                navController.navigate(Route.ABOUT_SCREEN) {
                    launchSingleTop = true
                }
            })
        }
        composable(
            route = Route.ABOUT_SCREEN,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(durationMillis = 200, easing = FastOutLinearInEasing),
                )
            },
        ) {
            AboutScreen(
                navigateToLanding = {
                    navController.navigate(Route.LANDING_SCREEN) {
                        popUpTo(Route.LANDING_SCREEN) { inclusive = false }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
