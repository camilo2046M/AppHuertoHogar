package com.example.apphuertohogar.ui


import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/**
 * Convierte un valor Double a un string con formato de moneda chilena.
 * Ejemplo: 25000.0 -> "$ 25.000"
 */
fun formatPrice(price: Double): String {
    // 1. Crear el Locale para Chile (Español, Chile)
    val chileLocale = Locale("es", "CL")

    // 2. Obtener el formateador de moneda para ese Locale
    val format = NumberFormat.getCurrencyInstance(chileLocale)

    // 3. Asignar la moneda CLP para que use el símbolo $ y no "CLP"
    format.currency = Currency.getInstance("CLP")

    // 4. Quitar los decimales (ej. ,00)
    format.maximumFractionDigits = 0

    // 5. Formatear el número
    return format.format(price)
}