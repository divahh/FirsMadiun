package com.example.firsmadiun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.firsmadiun.data.models.TipsType
import com.example.firsmadiun.data.repository.AuthRepository
import com.example.firsmadiun.ui.screen.auth.login.LoginScreen
import com.example.firsmadiun.ui.screen.auth.signup.RegisterScreen
import com.example.firsmadiun.ui.screen.feature.HomeScreen
import com.example.firsmadiun.ui.screen.feature.NoPentingScreen
import com.example.firsmadiun.ui.screen.feature.ReportDetailScreen
import com.example.firsmadiun.ui.screen.feature.ReportListScreen
import com.example.firsmadiun.ui.screen.feature.SplashScreen
import com.example.firsmadiun.ui.screen.feature.ReportScreen
import com.example.firsmadiun.ui.screen.feature.TipsScreen
import com.example.firsmadiun.ui.theme.DamkarTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DamkarTheme {
                DamkarApp()
            }
        }
    }
}

/**
 * Root navigasi aplikasi DAMKAR.
 *
 * Alur:  splash → login → home → laporan/{kategori}
 */
@Composable
fun DamkarApp() {
    val navController = rememberNavController()
    val authRepository = AuthRepository()

    NavHost(
        navController = navController,
        startDestination = DamkarRoutes.SPLASH
    ) {

        // ── Splash ──────────────────────────────────────────
        composable(DamkarRoutes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    // cek apakah user sudah login
                    val destination = if (authRepository.isLoggedIn)
                        DamkarRoutes.HOME else DamkarRoutes.LOGIN

                    navController.navigate(destination) {
                        popUpTo(DamkarRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // ── Login ────────────────────────────────────────────
        composable(DamkarRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(DamkarRoutes.HOME) {
                        popUpTo(DamkarRoutes.LOGIN) { inclusive = true }
                    }
                },
                onSignUp = {
                    navController.navigate(DamkarRoutes.REGISTER) {
                        popUpTo(DamkarRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // ── Register ──────────────────────────────────────────
        composable(DamkarRoutes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(DamkarRoutes.HOME) {
                        popUpTo(DamkarRoutes.REGISTER) { inclusive = true }
                    }
                },
                onLogin = {
                    navController.navigate(DamkarRoutes.LOGIN) {
                        popUpTo(DamkarRoutes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        // ── Home ─────────────────────────────────────────────
        composable(DamkarRoutes.HOME) {
            HomeScreen(
                onLogout = {
                    navController.navigate(DamkarRoutes.LOGIN) {
                        popUpTo(DamkarRoutes.HOME) { inclusive = true }
                    }
                },
                onKategoriClick = { kategori ->
                    navController.navigate(DamkarRoutes.laporan(kategori))
                },
                onRiwayatClick = {
                    navController.navigate(DamkarRoutes.DAFTAR_LAPORAN)
                },
                onBannerClick = { type ->
                    if (type == "tips" || type == "tipsedu" || type == DamkarRoutes.TIPSEDU) {
                        navController.navigate(DamkarRoutes.TIPSEDU)
                    } else {
                        navController.navigate(DamkarRoutes.NOPENTING)
                    }
                }
            )
        }

        // ── Tips Edu & No Penting ─────────────────────────────────────────────
        composable(DamkarRoutes.TIPSEDU) {
            TipsScreen(type = TipsType.TIPSEDU, onBack = { navController.popBackStack() })
        }
        composable(DamkarRoutes.NOPENTING) {
            NoPentingScreen(type = TipsType.NOPENTING, onBack = { navController.popBackStack() })
        }

        // ── Lapor Kejadian ───────────────────────────────────
        composable(
            route = DamkarRoutes.LAPORAN,
            arguments = listOf(
                navArgument("kategori") {
                    type = NavType.StringType
                    defaultValue = "Kebakaran"
                }
            )
        ) { backStack ->
            val kategori = backStack.arguments?.getString("kategori") ?: "Kebakaran"
            ReportScreen(
                selectedKategori = kategori,
                onBack = { navController.popBackStack() },
                onLaporanTerkirim = {
                    navController.navigate(DamkarRoutes.HOME) {
                        popUpTo(DamkarRoutes.HOME)
                        launchSingleTop = true
                    }
                }
            )
        }

        // ── Daftar Laporan ───────────────────────────────────
        composable(DamkarRoutes.DAFTAR_LAPORAN) {
            ReportListScreen(
                onBack = { navController.popBackStack() },
                onTambahLaporan = {
                    navController.navigate(DamkarRoutes.laporan("Kebakaran"))
                },
                onItemClick = { id ->
                    navController.navigate(DamkarRoutes.detailLaporan(id))
                }
            )
        }

        // ── Detail Laporan ──────────────────────────────────
        composable(
            DamkarRoutes.DETAIL_LAPORAN,
            arguments = listOf(navArgument("laporanId") {type = NavType.StringType })
            ) { backStack ->
            val id = backStack.arguments?.getString("laporanId") ?: ""
            ReportDetailScreen(
                laporanId = id,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Konstanta rute navigasi terpusat.
 */
object DamkarRoutes {
    const val SPLASH  = "splash"
    const val LOGIN   = "login"
    const val REGISTER = "register"
    const val HOME    = "home"
    const val TIPSEDU = "tips&edu"
    const val NOPENTING = "no_penting"
    const val LAPORAN = "laporan/{kategori}"
    const val DAFTAR_LAPORAN = "daftar_laporan"
    const val DETAIL_LAPORAN = "detail_laporan/{laporanId}"

    fun laporan(kategori: String) = "laporan/$kategori"
    fun detailLaporan(id: String) = "detail_laporan/$id"
}

@Preview
@Composable
fun DamkarAppPreview() {
    DamkarTheme { DamkarApp() }
}