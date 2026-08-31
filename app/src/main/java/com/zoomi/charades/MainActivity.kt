package com.zoomi.charades

import android.os.Bundle
import java.net.URLDecoder
import java.net.URLEncoder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zoomi.charades.ads.AdMobAdProvider
import com.zoomi.charades.ads.AdProvider
import com.zoomi.charades.ads.ConsentManager
import com.zoomi.charades.data.AdsRepository
import com.zoomi.charades.data.CustomCategoryRepository
import com.zoomi.charades.data.CustomDeckRepository
import com.zoomi.charades.data.DeckRepository
import com.zoomi.charades.data.FavoritesRepository
import com.zoomi.charades.data.GameSettings
import com.zoomi.charades.data.SettingsRepository
import com.zoomi.charades.data.StatsRepository
import com.zoomi.charades.data.appDataStore
import com.zoomi.charades.ui.screens.DeckListScreen
import com.zoomi.charades.ui.screens.GameScreen
import com.zoomi.charades.ui.screens.LoadingScreen
import com.zoomi.charades.ui.screens.TeamSetupScreen
import com.zoomi.charades.ui.theme.CharadesTheme
import com.zoomi.charades.ui.theme.LocalHapticsEnabled
import com.zoomi.charades.ui.viewmodel.DeckListViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val customDeckRepository = CustomDeckRepository(applicationContext.appDataStore)
        val deckRepository = DeckRepository(customDeckRepository)
        val settingsRepository = SettingsRepository(applicationContext.appDataStore)
        val statsRepository = StatsRepository(applicationContext.appDataStore)
        val favoritesRepository = FavoritesRepository(applicationContext.appDataStore)
        val customCategoryRepository = CustomCategoryRepository(applicationContext.appDataStore)
        val adsRepository = AdsRepository(applicationContext.appDataStore, roundsBetweenAds = PLACEHOLDER_ROUNDS_BETWEEN_ADS)
        val adProvider: AdProvider = AdMobAdProvider(PLACEHOLDER_AD_UNIT_ID)
        val consentManager = ConsentManager()
        lifecycleScope.launch {
            consentManager.requestConsentIfNeeded(this@MainActivity)
            adProvider.initialize(applicationContext)
        }

        setContent {
            val settings by settingsRepository.settings.collectAsState(initial = GameSettings())
            LaunchedEffect(settings.fullscreenModeEnabled) {
                val controller = WindowInsetsControllerCompat(window, window.decorView)
                if (settings.fullscreenModeEnabled) {
                    controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    controller.hide(WindowInsetsCompat.Type.systemBars())
                } else {
                    controller.show(WindowInsetsCompat.Type.systemBars())
                }
            }
            CompositionLocalProvider(LocalHapticsEnabled provides settings.hapticsEnabled) {
                CharadesTheme(theme = settings.theme) {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        CharadesNavHost(
                            deckRepository,
                            settingsRepository,
                            statsRepository,
                            favoritesRepository,
                            customCategoryRepository,
                            adsRepository,
                            adProvider,
                        )
                    }
                }
            }
        }
    }
}

// U3 (exact ad cadence) is unresolved in PRODUCT_REQUIREMENTS.md. This is a placeholder so the
// gating structure is exercisable end to end — it is not a tuned product decision and should be
// revisited once U3 resolves.
private const val PLACEHOLDER_ROUNDS_BETWEEN_ADS = 5

// Google's public sample interstitial ad unit ID — always fills with a test ad, safe for
// development, but MUST be replaced with a real ad unit ID from the developer's own AdMob
// account (alongside AndroidManifest.xml's APPLICATION_ID) before any public release build.
private const val PLACEHOLDER_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

@Composable
private fun CharadesNavHost(
    deckRepository: DeckRepository,
    settingsRepository: SettingsRepository,
    statsRepository: StatsRepository,
    favoritesRepository: FavoritesRepository,
    customCategoryRepository: CustomCategoryRepository,
    adsRepository: AdsRepository,
    adProvider: AdProvider,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "loading") {
        composable("loading") {
            LoadingScreen(
                onLoaded = {
                    navController.navigate("deckList") {
                        popUpTo("loading") { inclusive = true }
                    }
                },
            )
        }
        composable("deckList") {
            val viewModel: DeckListViewModel =
                viewModel(factory = DeckListViewModel.Factory(deckRepository, favoritesRepository, customCategoryRepository))
            DeckListScreen(
                viewModel = viewModel,
                deckRepository = deckRepository,
                customCategoryRepository = customCategoryRepository,
                settingsRepository = settingsRepository,
                statsRepository = statsRepository,
                onStartGame = { deck, timerSeconds -> navController.navigate("game/${deck.id}/$timerSeconds") },
                onStartPartyGame = { deck, timerSeconds -> navController.navigate("teamSetup/${deck.id}/$timerSeconds") },
            )
        }
        composable(
            route = "teamSetup/{deckId}/{timerSeconds}",
            arguments = listOf(
                navArgument("deckId") { type = NavType.StringType },
                navArgument("timerSeconds") { type = NavType.IntType },
            ),
        ) { backStackEntry ->
            val deckId = backStackEntry.deckIdArg()
            val timerSeconds = backStackEntry.arguments?.getInt("timerSeconds") ?: 60
            TeamSetupScreen(
                onStartMatch = { teamNames ->
                    val encoded = teamNames.joinToString(",") { URLEncoder.encode(it, "UTF-8") }
                    navController.navigate("partyGame/$deckId/$timerSeconds/$encoded")
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = "game/{deckId}/{timerSeconds}",
            arguments = listOf(
                navArgument("deckId") { type = NavType.StringType },
                navArgument("timerSeconds") { type = NavType.IntType },
            ),
        ) { backStackEntry ->
            val deck by deckRepository.deckById(backStackEntry.deckIdArg()).collectAsState(initial = null)
            val timerSeconds = backStackEntry.arguments?.getInt("timerSeconds") ?: 60
            deck?.let {
                GameScreen(
                    deck = it,
                    roundDurationSeconds = timerSeconds,
                    settingsRepository = settingsRepository,
                    statsRepository = statsRepository,
                    adsRepository = adsRepository,
                    adProvider = adProvider,
                    onExit = { navController.popBackStack("deckList", inclusive = false) },
                )
            }
        }
        composable(
            route = "partyGame/{deckId}/{timerSeconds}/{teamNames}",
            arguments = listOf(
                navArgument("deckId") { type = NavType.StringType },
                navArgument("timerSeconds") { type = NavType.IntType },
                navArgument("teamNames") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val deck by deckRepository.deckById(backStackEntry.deckIdArg()).collectAsState(initial = null)
            val timerSeconds = backStackEntry.arguments?.getInt("timerSeconds") ?: 60
            val teamNames = backStackEntry.arguments?.getString("teamNames").orEmpty()
                .split(",")
                .filter { it.isNotBlank() }
                .map { URLDecoder.decode(it, "UTF-8") }
            deck?.let {
                GameScreen(
                    deck = it,
                    roundDurationSeconds = timerSeconds,
                    settingsRepository = settingsRepository,
                    statsRepository = statsRepository,
                    adsRepository = adsRepository,
                    adProvider = adProvider,
                    partyTeams = teamNames,
                    onExit = { navController.popBackStack("deckList", inclusive = false) },
                )
            }
        }
    }
}

private fun NavBackStackEntry.deckIdArg(): String = arguments?.getString("deckId").orEmpty()
