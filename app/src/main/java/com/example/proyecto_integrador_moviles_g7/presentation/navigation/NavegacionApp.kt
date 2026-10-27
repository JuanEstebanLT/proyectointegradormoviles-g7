package com.example.proyecto_integrador_moviles_g7.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyecto_integrador_moviles_g7.presentation.screens.AccesoScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.DashboardScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.DetalleCategoriaScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.SaldoInicialScreen

object Rutas {
    const val ACCESO = "acceso"
    const val SALDO_INICIAL = "saldo_inicial"
    const val DASHBOARD = "dashboard"
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
    NavHost(navController = navController, startDestination = Rutas.ACCESO) {
        composable(Rutas.ACCESO) {
            AccesoScreen(
                onIniciarSesion = { navController.irAlDashboard() },
                onRegistrarse = { navController.navigate(Rutas.SALDO_INICIAL) }
            )
        }
        composable(Rutas.SALDO_INICIAL) {
            SaldoInicialScreen(
                onAtras = { navController.popBackStack() },
                onGuardar = { navController.irAlDashboard() }
            )
        }
        composable(Rutas.DASHBOARD) {
            DashboardScreen(
                onCategoriaClick = { categoriaId ->
                    navController.navigate(Rutas.detalleCategoria(categoriaId))
                }
            )
        }
        composable(
            route = Rutas.DETALLE_CATEGORIA,
            arguments = listOf(navArgument(Rutas.ARG_CATEGORIA_ID) { type = NavType.IntType })
        ) { entrada ->
            DetalleCategoriaScreen(
                categoriaId = entrada.arguments?.getInt(Rutas.ARG_CATEGORIA_ID) ?: 0,
                onAtras = { navController.popBackStack() }
            )
        }
    }
}

// Al entrar al Dashboard se descarta Acceso/Saldo Inicial para que "atrás" no regrese a ellas
private fun NavHostController.irAlDashboard() {
    navigate(Rutas.DASHBOARD) {
        popUpTo(Rutas.ACCESO) { inclusive = true }
    }
}
