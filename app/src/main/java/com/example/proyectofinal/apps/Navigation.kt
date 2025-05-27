package com.example.proyectofinal.apps

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appproyecto.apps.MenuPantalla
import com.example.proyectofinal.ViewModel.ResumenProgresoViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginPantalla(navController) }
        composable("registro") { RegistroPantalla(navController) }

        composable(
            route = "menu/{idUsuario}",
            arguments = listOf(navArgument("idUsuario") { type = NavType.IntType })
        ) { backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getInt("idUsuario") ?: -1
            MenuPantalla(navController, idUsuario)
        }

        composable(
            route = "anotaciones_personal/{idUsuario}",
            arguments = listOf(navArgument("idUsuario") { type = NavType.IntType })
        ) { backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getInt("idUsuario") ?: -1
            AnotacionesPersonalesPantalla(navController, idUsuario)
        }

        composable(
            route = "notas_rapidas/{idUsuario}",
            arguments = listOf(navArgument("idUsuario") { type = NavType.IntType })
        ) { backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getInt("idUsuario") ?: -1
            NotasRapidasPantalla(navController, idUsuario)
        }

        composable(
            route = "objetivos/{idUsuario}",
            arguments = listOf(navArgument("idUsuario") { type = NavType.IntType })
        ) { backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getInt("idUsuario") ?: -1
            ObjetivosPantalla(navController, idUsuario)
        }

        composable(
            route = "progreso_objetivos/{idUsuario}",
            arguments = listOf(navArgument("idUsuario") { type = NavType.IntType })
        ) { backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getInt("idUsuario") ?: -1
            ProgresoObjetivosPantalla(navController, idUsuario)
        }

        composable(
            route = "resumen_progreso/{idUsuario}",
            arguments = listOf(navArgument("idUsuario") { type = NavType.IntType })
        ) { backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getInt("idUsuario") ?: -1
            val viewModel: ResumenProgresoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            ResumenProgresoPantalla(navController, viewModel)
        }


        composable(
            route = "agregar_anotacion/{idUsuario}",
            arguments = listOf(navArgument("idUsuario") { type = NavType.IntType })
        ) { backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getInt("idUsuario") ?: -1
            EditarAnotacionPantalla(navController, idUsuario, idAnotacion = null)
        }


        composable(
            route = "agregar_anotacion/{idUsuario}/{idAnotacion}",
            arguments = listOf(
                navArgument("idUsuario") { type = NavType.IntType },
                navArgument("idAnotacion") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getInt("idUsuario") ?: -1
            val idAnotacion = backStackEntry.arguments?.getInt("idAnotacion")
            EditarAnotacionPantalla(navController, idUsuario, idAnotacion = idAnotacion)
        }
    }
}