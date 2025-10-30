package com.example.apphuertohogar.navigation

sealed class NavigationEvent {

    data class NavigateTo(
        val route: Screen,
        val popUpToRoute: Screen?= null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false,
        val productoId: Int? = null
    ) : NavigationEvent()

    data object PopBackStack: NavigationEvent()
    data object NavigateUp: NavigationEvent()

}