package com.example.proyecto_integrador_moviles_g7.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyecto_integrador_moviles_g7.presentation.screens.SeccionesScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.AccesoScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.DashboardScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.DetalleCategoriaScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.SaldoInicialScreen
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.AuthViewModel
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.DashboardViewModel
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.DetalleCategoriaViewModel
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.FinanzasSessionViewModel
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.SaldoInicialViewModel

object Rutas {
    const val ACCESO = "acceso"
    const val SALDO_INICIAL = "saldo_inicial"
    const val DASHBOARD = "dashboard"
    const val MOVIMIENTOS = "movimientos"
    const val INFORMES = "informes"
    const val AHORRO = "ahorro"
    const val PERFIL = "perfil"
    const val NOTIFICACIONES = "notificaciones"

    val secciones = mapOf("Inicio" to DASHBOARD, "Movimientos" to MOVIMIENTOS,
        "Informes" to INFORMES, "Ahorro" to AHORRO)
    const val ARG_CATEGORIA_ID = "categoriaId"
    const val DETALLE_CATEGORIA = "detalle_categoria/{$ARG_CATEGORIA_ID}"

    fun detalleCategoria(categoriaId: Int) = "detalle_categoria/$categoriaId"
}

/**
 * Flujo: Acceso -> Dashboard -> Detalle de Categoría.
 * Un usuario nuevo pasa por Saldo Inicial ("Regístrate") antes del Dashboard.
 */
@Composable
fun NavegacionApp(navController: NavHostController = rememberNavController()) {
    val sessionViewModel: FinanzasSessionViewModel = viewModel()
    val repository = sessionViewModel.repository

    val onDestino: (String) -> Unit = { nombre ->
        Rutas.secciones[nombre]?.let { ruta ->
            navController.navigate(ruta) {
                popUpTo(Rutas.DASHBOARD) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    NavHost(navController = navController, startDestination = Rutas.ACCESO) {
        composable(Rutas.ACCESO) {
            val authViewModel: AuthViewModel = viewModel()
            AccesoScreen(
                viewModel = authViewModel,
                onIniciarSesion = { navController.irAlDashboard() },
                onRegistrarse = { navController.navigate(Rutas.SALDO_INICIAL) }
            )
        }
        composable(Rutas.SALDO_INICIAL) {
            val saldoViewModel: SaldoInicialViewModel = viewModel(
                factory = SaldoInicialViewModel.factory(repository)
            )
            SaldoInicialScreen(
                viewModel = saldoViewModel,
                onAtras = { navController.popBackStack() },
                onGuardado = { navController.irAlDashboard() }
            )
        }
        composable(Rutas.DASHBOARD) {
            val dashboardViewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModel.factory(repository)
            )
            DashboardScreen(
                viewModel = dashboardViewModel,
                onDestino = onDestino,
                onPerfil = { navController.navigate(Rutas.PERFIL) { launchSingleTop = true } },
                onNotificaciones = { navController.navigate(Rutas.NOTIFICACIONES) { launchSingleTop = true } },
                onCategoriaClick = { categoriaId ->
                    navController.navigate(Rutas.detalleCategoria(categoriaId))
                }
            )
        }
        mapOf(
            Rutas.MOVIMIENTOS to "Movimientos", Rutas.INFORMES to "Informes",
            Rutas.AHORRO to "Ahorro", Rutas.PERFIL to "Perfil",
            Rutas.NOTIFICACIONES to "Notificaciones"
        ).forEach { (ruta, titulo) ->
            composable(ruta) {
                val dashboardViewModel: DashboardViewModel = viewModel(
                    factory = DashboardViewModel.factory(repository)
                )
                SeccionesScreen(titulo, dashboardViewModel, onDestino,
                    onAtras = { navController.popBackStack() },
                    onCategoria = { navController.navigate(Rutas.detalleCategoria(it)) },
                    onSalir = {
                        navController.navigate(Rutas.ACCESO) {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    })
            }
        }
        composable(
            route = Rutas.DETALLE_CATEGORIA,
            arguments = listOf(navArgument(Rutas.ARG_CATEGORIA_ID) { type = NavType.IntType })
        ) { entrada ->
            val categoriaId = entrada.arguments?.getInt(Rutas.ARG_CATEGORIA_ID) ?: 0
            val detalleViewModel: DetalleCategoriaViewModel = viewModel(
                factory = DetalleCategoriaViewModel.factory(categoriaId, repository)
            )
            DetalleCategoriaScreen(
                viewModel = detalleViewModel,
                onAtras = { navController.popBackStack() }
            )
        }
    }
}

// Al entrar al Dashboard se descarta Acceso/Saldo Inicial para que "atrás" no regrese a ellas
private fun NavHostController.irAlDashboard() {
    navigate(Rutas.DASHBOARD) {
        popUpTo(Rutas.ACCESO) { inclusive = true }
        launchSingleTop = true
    }
}
