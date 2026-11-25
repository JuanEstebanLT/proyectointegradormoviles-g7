package com.example.proyecto_integrador_moviles_g7.presentation.navigation

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.example.proyecto_integrador_moviles_g7.data.network.SesionActual
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyecto_integrador_moviles_g7.data.model.TipoMovimiento
import com.example.proyecto_integrador_moviles_g7.presentation.components.SeccionPrincipal
import com.example.proyecto_integrador_moviles_g7.presentation.screens.AccesoScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.AdminScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.AhorroScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.CategoriasScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.DashboardScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.DetalleCategoriaScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.EditarPerfilScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.HistorialScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.InformesScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.NotificacionesScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.OnboardingScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.PerfilScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.RegistrarMovimientoScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.RegistroScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.SaldoInicialScreen
import com.example.proyecto_integrador_moviles_g7.presentation.screens.SplashScreen
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.AdminViewModel
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.AhorroViewModel
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.AuthViewModel
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.DashboardViewModel
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.DetalleCategoriaViewModel
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.FinanzasSessionViewModel
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.InformesViewModel
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.NotificacionesViewModel
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.PerfilViewModel
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.SaldoInicialViewModel

object Rutas {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val ACCESO = "acceso"
    const val REGISTRO = "registro"
    const val SALDO_INICIAL = "saldo_inicial"
    const val DASHBOARD = "dashboard"
    const val MOVIMIENTOS = "movimientos"
    const val INFORMES = "informes"
    const val AHORRO = "ahorro"
    const val PERFIL = "perfil"
    const val EDITAR_PERFIL = "editar_perfil"
    const val NOTIFICACIONES = "notificaciones"
    const val CATEGORIAS = "categorias"
    const val ADMIN = "admin"

    const val ARG_CATEGORIA_ID = "categoriaId"
    const val DETALLE_CATEGORIA = "detalle_categoria/{$ARG_CATEGORIA_ID}"

    const val ARG_TIPO = "tipo"
    const val REGISTRAR_MOVIMIENTO = "registrar/{$ARG_TIPO}?$ARG_CATEGORIA_ID={$ARG_CATEGORIA_ID}"

    fun detalleCategoria(categoriaId: Int) = "detalle_categoria/$categoriaId"

    fun registrarMovimiento(tipo: TipoMovimiento, categoriaId: Int? = null) =
        "registrar/${tipo.name}" + (categoriaId?.let { "?$ARG_CATEGORIA_ID=$it" } ?: "")

    fun seccion(seccion: SeccionPrincipal) = when (seccion) {
        SeccionPrincipal.INICIO -> DASHBOARD
        SeccionPrincipal.MOVIMIENTOS -> MOVIMIENTOS
        SeccionPrincipal.INFORMES -> INFORMES
        SeccionPrincipal.AHORRO -> AHORRO
    }
}

private const val PREFERENCIAS = "finanzas_g7"
private const val ONBOARDING_VISTO = "onboarding_visto"

/**
 * Flujo: Splash -> (Onboarding la primera vez) -> Acceso -> Dashboard.
 * Un usuario nuevo pasa por Registro y Saldo Inicial antes del Dashboard.
 * Las pestañas Inicio, Movimientos, Informes y Ahorro comparten la barra inferior.
 */
@Composable
fun NavegacionApp(
    navController: NavHostController = rememberNavController(),
    inicio: String = Rutas.SPLASH
) {
    val sessionViewModel: FinanzasSessionViewModel = viewModel()
    val repository = sessionViewModel.repository
    val cuenta = sessionViewModel.cuenta
    val context = LocalContext.current
    val cargando by repository.cargando.collectAsStateWithLifecycle()
    val sinConexion by repository.sinConexion.collectAsStateWithLifecycle()
    val ultimaSincronizacion by repository.ultimaSincronizacion.collectAsStateWithLifecycle()
    val usuario by cuenta.usuario.collectAsStateWithLifecycle()

    // Los fallos de la API en segundo plano se informan sin bloquear la pantalla
    LaunchedEffect(repository) {
        repository.errores.collect { mensaje ->
            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
        }
    }

    // Android 13+ pide permiso para mostrar notificaciones; se solicita al iniciar sesión
    val lanzadorPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) sessionViewModel.revisarNotificaciones()
    }
    LaunchedEffect(usuario?.id) {
        if (usuario != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !sessionViewModel.notificador.tienePermiso()
        ) {
            lanzadorPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val irASeccion: (SeccionPrincipal) -> Unit = { seccion ->
        if (seccion == SeccionPrincipal.INICIO) {
            navController.popBackStack(Rutas.DASHBOARD, inclusive = false)
        } else {
            navController.navigate(Rutas.seccion(seccion)) {
                popUpTo(Rutas.DASHBOARD) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
    val irA: (String) -> Unit = { ruta -> navController.navigate(ruta) { launchSingleTop = true } }
    val alIniciarSesion = {
        sessionViewModel.alIniciarSesion()
        navController.irAlDashboard()
    }
    val cerrarSesion = {
        sessionViewModel.cerrarSesion()
        navController.navigate(Rutas.ACCESO) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }
    val iniciales = usuario?.iniciales ?: "G7"

    // Token vencido o inválido (401): se cierra la sesión guardada y se vuelve a Acceso
    LaunchedEffect(Unit) {
        SesionActual.expirada.collect {
            Toast.makeText(context, "Tu sesión expiró. Vuelve a iniciar sesión.", Toast.LENGTH_LONG).show()
            cerrarSesion()
        }
    }

    NavHost(navController = navController, startDestination = inicio) {
        composable(Rutas.SPLASH) {
            val scope = rememberCoroutineScope()
            SplashScreen(onTerminar = {
                scope.launch {
                    // Con sesión guardada en el teléfono se entra directo al Dashboard
                    if (sessionViewModel.restaurarSesion()) {
                        navController.irAlDashboard()
                    } else {
                        val destino = if (onboardingVisto(context)) Rutas.ACCESO else Rutas.ONBOARDING
                        navController.navigate(destino) { popUpTo(Rutas.SPLASH) { inclusive = true } }
                    }
                }
            })
        }
        composable(Rutas.ONBOARDING) {
            val terminar = { ruta: String ->
                marcarOnboardingVisto(context)
                navController.navigate(Rutas.ACCESO) { popUpTo(Rutas.ONBOARDING) { inclusive = true } }
                if (ruta != Rutas.ACCESO) navController.navigate(ruta)
            }
            OnboardingScreen(
                onCrearCuenta = { terminar(Rutas.REGISTRO) },
                onIniciarSesion = { terminar(Rutas.ACCESO) }
            )
        }
        composable(Rutas.ACCESO) {
            val authViewModel: AuthViewModel = viewModel()
            AccesoScreen(
                viewModel = authViewModel,
                onIniciarSesion = alIniciarSesion,
                onRegistrarse = { irA(Rutas.REGISTRO) }
            )
        }
        composable(Rutas.REGISTRO) {
            val authViewModel: AuthViewModel = viewModel()
            RegistroScreen(
                viewModel = authViewModel,
                onAtras = { navController.popBackStack() },
                onRegistrado = {
                    sessionViewModel.alIniciarSesion()
                    navController.navigate(Rutas.SALDO_INICIAL) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
        composable(Rutas.SALDO_INICIAL) {
            val saldoViewModel: SaldoInicialViewModel = viewModel(factory = SaldoInicialViewModel.factory(repository))
            // Tras registrarse no hay pantalla previa: "atrás" omite el saldo y va al Dashboard
            val volver = {
                if (navController.previousBackStackEntry != null) navController.popBackStack()
                else navController.irAlDashboard()
            }
            SaldoInicialScreen(viewModel = saldoViewModel, onAtras = { volver() }, onGuardado = { volver() })
        }
        composable(Rutas.DASHBOARD) {
            val dashboardViewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.factory(repository, cuenta))
            DashboardScreen(
                viewModel = dashboardViewModel,
                cargando = cargando,
                sinConexion = sinConexion,
                ultimaSincronizacion = ultimaSincronizacion,
                onReintentar = { repository.recargar() },
                onSeccion = irASeccion,
                onPerfil = { irA(Rutas.PERFIL) },
                onNotificaciones = { irA(Rutas.NOTIFICACIONES) },
                onCategoriaClick = { irA(Rutas.detalleCategoria(it)) },
                onCategorias = { irA(Rutas.CATEGORIAS) },
                onRegistrar = { irA(Rutas.registrarMovimiento(it)) }
            )
        }
        composable(Rutas.MOVIMIENTOS) {
            val dashboardViewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.factory(repository, cuenta))
            HistorialScreen(
                viewModel = dashboardViewModel,
                onSeccion = irASeccion,
                onPerfil = { irA(Rutas.PERFIL) },
                onNotificaciones = { irA(Rutas.NOTIFICACIONES) }
            )
        }
        composable(Rutas.INFORMES) {
            val informesViewModel: InformesViewModel = viewModel(factory = InformesViewModel.factory(repository))
            val notificaciones: NotificacionesViewModel = viewModel(factory = NotificacionesViewModel.factory(repository, cuenta))
            val estadoNotificaciones by notificaciones.uiState.collectAsStateWithLifecycle()
            InformesScreen(
                viewModel = informesViewModel,
                iniciales = iniciales,
                notificacionesSinLeer = estadoNotificaciones.sinLeer,
                onSeccion = irASeccion,
                onPerfil = { irA(Rutas.PERFIL) },
                onNotificaciones = { irA(Rutas.NOTIFICACIONES) },
                onCategoria = { irA(Rutas.detalleCategoria(it)) }
            )
        }
        composable(Rutas.AHORRO) {
            val ahorroViewModel: AhorroViewModel = viewModel(factory = AhorroViewModel.factory(repository, cuenta))
            val notificaciones: NotificacionesViewModel = viewModel(factory = NotificacionesViewModel.factory(repository, cuenta))
            val estadoNotificaciones by notificaciones.uiState.collectAsStateWithLifecycle()
            AhorroScreen(
                viewModel = ahorroViewModel,
                iniciales = iniciales,
                notificacionesSinLeer = estadoNotificaciones.sinLeer,
                onSeccion = irASeccion,
                onPerfil = { irA(Rutas.PERFIL) },
                onNotificaciones = { irA(Rutas.NOTIFICACIONES) }
            )
        }
        composable(
            route = Rutas.REGISTRAR_MOVIMIENTO,
            arguments = listOf(
                navArgument(Rutas.ARG_TIPO) { type = NavType.StringType },
                navArgument(Rutas.ARG_CATEGORIA_ID) { type = NavType.IntType; defaultValue = -1 }
            )
        ) { entrada ->
            val tipo = runCatching { TipoMovimiento.valueOf(entrada.arguments?.getString(Rutas.ARG_TIPO).orEmpty()) }
                .getOrDefault(TipoMovimiento.GASTO)
            val categoriaId = entrada.arguments?.getInt(Rutas.ARG_CATEGORIA_ID)?.takeIf { it > 0 }
            val dashboardViewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.factory(repository, cuenta))
            RegistrarMovimientoScreen(
                tipo = tipo,
                viewModel = dashboardViewModel,
                categoriaInicial = categoriaId,
                onAtras = { navController.popBackStack() },
                onGuardado = { navController.popBackStack() }
            )
        }
        composable(Rutas.CATEGORIAS) {
            val dashboardViewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.factory(repository, cuenta))
            CategoriasScreen(
                viewModel = dashboardViewModel,
                onAtras = { navController.popBackStack() },
                onCategoria = { irA(Rutas.detalleCategoria(it)) }
            )
        }
        composable(
            route = Rutas.DETALLE_CATEGORIA,
            arguments = listOf(navArgument(Rutas.ARG_CATEGORIA_ID) { type = NavType.IntType })
        ) { entrada ->
            val categoriaId = entrada.arguments?.getInt(Rutas.ARG_CATEGORIA_ID) ?: 0
            val detalleViewModel: DetalleCategoriaViewModel = viewModel(
                factory = DetalleCategoriaViewModel.factory(categoriaId, repository)
            )
            val estado by repository.state.collectAsStateWithLifecycle()
            DetalleCategoriaScreen(
                viewModel = detalleViewModel,
                categorias = estado.categorias,
                onAtras = { navController.popBackStack() },
                onRegistrar = { tipo, id -> irA(Rutas.registrarMovimiento(tipo, id)) }
            )
        }
        composable(Rutas.PERFIL) {
            val perfilViewModel: PerfilViewModel = viewModel(factory = PerfilViewModel.factory(repository, cuenta))
            PerfilScreen(
                viewModel = perfilViewModel,
                onAtras = { navController.popBackStack() },
                onEditarPerfil = { irA(Rutas.EDITAR_PERFIL) },
                onCategorias = { irA(Rutas.CATEGORIAS) },
                onSaldoInicial = { irA(Rutas.SALDO_INICIAL) },
                onMetaAhorro = { irASeccion(SeccionPrincipal.AHORRO) },
                onNotificaciones = { irA(Rutas.NOTIFICACIONES) },
                onAdmin = { irA(Rutas.ADMIN) },
                onCerrarSesion = cerrarSesion
            )
        }
        composable(Rutas.EDITAR_PERFIL) {
            val perfilViewModel: PerfilViewModel = viewModel(factory = PerfilViewModel.factory(repository, cuenta))
            EditarPerfilScreen(
                viewModel = perfilViewModel,
                onAtras = { navController.popBackStack() },
                onGuardado = { navController.popBackStack() }
            )
        }
        composable(Rutas.NOTIFICACIONES) {
            val notificaciones: NotificacionesViewModel = viewModel(factory = NotificacionesViewModel.factory(repository, cuenta))
            NotificacionesScreen(
                viewModel = notificaciones,
                onAtras = { navController.popBackStack() },
                onCategoria = { irA(Rutas.detalleCategoria(it)) },
                onAhorro = { irASeccion(SeccionPrincipal.AHORRO) }
            )
        }
        composable(Rutas.ADMIN) {
            val adminViewModel: AdminViewModel = viewModel(factory = AdminViewModel.factory(cuenta))
            AdminScreen(
                viewModel = adminViewModel,
                idUsuarioActual = usuario?.id,
                onAtras = { navController.popBackStack() }
            )
        }
    }
}

// Al entrar al Dashboard se descarta Acceso/Saldo Inicial para que "atrás" no regrese a ellas
private fun NavHostController.irAlDashboard() {
    navigate(Rutas.DASHBOARD) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

private fun onboardingVisto(context: Context): Boolean =
    context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE).getBoolean(ONBOARDING_VISTO, false)

private fun marcarOnboardingVisto(context: Context) {
    context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE).edit().putBoolean(ONBOARDING_VISTO, true).apply()
}
