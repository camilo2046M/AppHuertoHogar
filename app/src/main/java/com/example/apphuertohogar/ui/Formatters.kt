package com.example.apphuertohogar.ui

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/**
 * Convierte un valor numérico (Int, Double, Long) a formato de moneda chilena.
 * Acepta 'Number' para evitar errores de tipo entre Int y Double.
 * Ejemplo: 25000 -> "$ 25.000"
 */
fun formatPrice(price: Number): String {
    val chileLocale = Locale("es", "CL")
    val format = NumberFormat.getCurrencyInstance(chileLocale)
    format.currency = Currency.getInstance("CLP")
    format.maximumFractionDigits = 0
    return format.format(price)
}

// NOTA: He eliminado 'extractPriceValue' porque tu API ya devuelve
// el precio como un número (Int), así que no hace falta limpiar texto.

// Asegúrate de que esta URL sea la misma que usas en RetrofitClient
// Si estás en AWS, pon la IP de AWS. Si es local, 10.0.2.2.
const val BASE_IMAGE_URL = "http://52.44.157.216:9090"

/**
 * Convierte una ruta relativa en una URL completa.
 */
fun buildImageUrl(imagePath: String?): String {
    if (imagePath.isNullOrBlank()) return "" // Manejo de nulos seguro

    return if (imagePath.startsWith("http")) {
        imagePath
    } else {
        val cleanPath = if (imagePath.startsWith("/")) imagePath else "/$imagePath"
        "$BASE_IMAGE_URL$cleanPath"
    }
}