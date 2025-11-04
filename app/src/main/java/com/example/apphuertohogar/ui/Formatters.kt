package com.example.apphuertohogar.ui


import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/**
 * Convierte un valor Double a un string con formato de moneda chilena.
 * Ejemplo: 25000.0 -> "$ 25.000"
 */
fun formatPrice(price: Double): String {
    val chileLocale = Locale("es", "CL")

    val format = NumberFormat.getCurrencyInstance(chileLocale)

    format.currency = Currency.getInstance("CLP")

    format.maximumFractionDigits = 0

    return format.format(price)
}