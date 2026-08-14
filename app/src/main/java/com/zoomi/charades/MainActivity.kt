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
import androidx.navigation.compose.dialog
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
import com.zoomi.charades.ui.screens.CreateCustomDeckScreen
import com.zoomi.charades.ui.screens.DeckDetailScreen
import com.zoomi.charades.ui.screens.DeckListScreen
import com.zoomi.charades.ui.screens.GameScreen
import com.zoomi.charades.ui.screens.LoadingScreen
import com.zoomi.charades.ui.screens.SettingsScreen
import com.zoomi.charades.ui.screens.StatsScreen
import com.zoomi.charades.ui.screens.TeamSetupScreen
import com.zoomi.charades.ui.theme.CharadesTheme
import com.zoomi.charades.ui.viewmodel.CreateCustomDeckViewModel
import com.zoomi.charades.ui.viewmodel.DeckListViewModel
import com.zoomi.charades.ui.viewmodel.SettingsViewModel
import com.zoomi.charades.ui.viewmodel.StatsViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

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
                viewModel(factory = DeckListViewModel.Factory(deckRepository, favoritesRepository))
            DeckListScreen(
                viewModel = viewModel,
                onSelectDeck = { deck -> navController.navigate("deckDetail/${deck.id}") },
                onOpenSettings = { navController.navigate("settings") },
                onOpenStats = { navController.navigate("stats") },
                onCreateCustomDeck = { navController.navigate("createCustomDeck") },
            )
        }
        dialog("createCustomDeck") {
            val viewModel: CreateCustomDeckViewModel =
                viewModel(factory = CreateCustomDeckViewModel.Factory(deckRepository, customCategoryRepository))
            CreateCustomDeckScreen(viewModel = viewModel, onClose = { navController.popBackStack() })
        }
        dialog("settings") {
            val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(settingsRepository))
            SettingsScreen(viewModel = viewModel, onClose = { navController.popBackStack() })
        }
        dialog("stats") {
            val viewModel: StatsViewModel = viewModel(factory = StatsViewModel.Factory(statsRepository))
            StatsScreen(viewModel = viewModel, onClose = { navController.popBackStack() })
        }
        dialog(
            route = "deckDetail/{deckId}",
            arguments = listOf(navArgument("deckId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val deck by deckRepository.deckById(backStackEntry.deckIdArg()).collectAsState(initial = null)
            val settings by settingsRepository.settings.collectAsState(initial = GameSettings())
            deck?.let {
                DeckDetailScreen(
                    deck = it,
                    defaultTimerSeconds = settings.defaultRoundDurationSeconds,
                    onStart = { timerSeconds, isPartyMode ->
                        if (isPartyMode) {
                            navController.navigate("teamSetup/${it.id}/$timerSeconds")
                        } else {
                            navController.navigate("game/${it.id}/$timerSeconds")
                        }
                    },
                    onBack = { navController.popBackStack() },
                )
            }
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
