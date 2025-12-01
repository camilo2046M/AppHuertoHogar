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

/**
 * --- NUEVA FUNCIÓN ---
 * Toma el string del backend (ej. "$2.500 / kg" o "$ 1.000")
 * y extrae solo el valor numérico (ej. 2500.0).
 * Útil para calcular totales.
 */
fun extractPriceValue(priceString: String): Double {
    // 1. Reemplaza todo lo que NO sea un número (0-9) por vacío.
    //    Esto elimina '$', '.', ' ', '/ kg', etc.
    //    En Chile el punto es separador de miles, así que eliminarlo está bien para obtener el entero.
    val cleanString = priceString.replace(Regex("[^0-9]"), "")

    // 2. Convierte a Double. Si falla, devuelve 0.0
    return cleanString.toDoubleOrNull() ?: 0.0
}