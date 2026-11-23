package com.example.proyecto_integrador_moviles_g7.presentation.notificaciones

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.proyecto_integrador_moviles_g7.R
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.Notificacion
import com.example.proyecto_integrador_moviles_g7.presentation.viewmodel.TipoNotificacion

/**
 * Publica las alertas de la app (presupuesto, meta y saldo) en la barra de notificaciones
 * de Android. Cada alerta se envía una sola vez por usuario: las claves ya enviadas se
 * guardan en SharedPreferences para no repetirlas al reabrir la app.
 */
class NotificadorSistema(context: Context) {

    private val context = context.applicationContext
    private val preferencias = this.context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE)

    init {
        crearCanal()
    }

    /** true si el usuario dio permiso (en Android 12 o anterior no hace falta pedirlo). */
    fun tienePermiso(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    /** Publica las alertas de [usuarioId] que todavía no se habían enviado. */
    fun publicarNuevas(usuarioId: String, notificaciones: List<Notificacion>) {
        if (!tienePermiso() || !NotificationManagerCompat.from(context).areNotificationsEnabled()) return
        val claveUsuario = "$ENVIADAS-$usuarioId"
        val enviadas = preferencias.getStringSet(claveUsuario, emptySet()).orEmpty()
        val nuevas = notificaciones.filter { it.clave !in enviadas }
        if (nuevas.isEmpty()) return

        nuevas.forEach { publicar(it) }
        preferencias.edit().putStringSet(claveUsuario, enviadas + nuevas.map { it.clave }).apply()
    }

    @Suppress("MissingPermission") // El permiso se verifica en publicarNuevas()
    private fun publicar(notificacion: Notificacion) {
        // Abre la app tal como estaba (no crea una nueva pantalla de inicio)
        val abrirApp = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        }
        val pendiente = abrirApp?.let {
            PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val color = when (notificacion.tipo) {
            TipoNotificacion.PRESUPUESTO_EXCEDIDO -> 0xFFE11D48.toInt()
            TipoNotificacion.PRESUPUESTO_CERCANO, TipoNotificacion.SALDO_BAJO -> 0xFFF59E0B.toInt()
            TipoNotificacion.META_ALCANZADA -> 0xFF059669.toInt()
        }
        val aviso = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setColor(color)
            .setContentTitle(notificacion.titulo)
            .setContentText(notificacion.mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificacion.mensaje))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .apply { pendiente?.let { setContentIntent(it) } }
            .build()
        NotificationManagerCompat.from(context).notify(notificacion.clave.hashCode(), aviso)
    }

    private fun crearCanal() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val canal = NotificationChannel(CANAL, "Alertas de finanzas", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Presupuesto cerca del límite o superado, meta de ahorro cumplida y saldo bajo"
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    private companion object {
        const val CANAL = "alertas_finanzas"
        const val PREFERENCIAS = "finanzas_g7_notificaciones"
        const val ENVIADAS = "enviadas"
    }
}
