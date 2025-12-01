package com.example.apphuertohogar.ui

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FormattersTest {

    @Test
    fun formatPrice_montoNormal_formateaCorrectamente() {
        val price = 25000.0
        val formattedPrice = formatPrice(price)
        assertEquals("$25.000", formattedPrice)
    }

    @Test
    fun formatPrice_montoGrande_formateaCorrectamenteMillones() {
        val price = 1500000.0
        val formattedPrice = formatPrice(price)
        assertEquals("$1.500.000", formattedPrice)
    }

    @Test
    fun formatPrice_montoCero_retornaCeroFormateado() {
        val price = 0.0
        val formattedPrice = formatPrice(price)
        assertEquals("$0", formattedPrice)
    }

    @Test
    fun formatPrice_conDecimales_eliminaDecimales() {
        val price = 1234.56
        val formattedPrice = formatPrice(price)
        assertEquals("$1.235", formattedPrice)
    }
}