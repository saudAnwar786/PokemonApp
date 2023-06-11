package com.sacoding.pokedox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sacoding.pokedox.PokemonDetail.PokemonDetailScreen
import com.sacoding.pokedox.PokemonList.PokemonListScreen
import com.sacoding.pokedox.ui.theme.PokedoxTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokedoxTheme {
                // A surface container using the 'background' color from the theme
                val navHost = rememberNavController()
                NavHost(navController = navHost, startDestination = "pokemon_list_screen"){
                    composable(route = "pokemon_list_screen"){
                           PokemonListScreen(navController = navHost)
                    }

                    composable(route = "pokemon_detail_screen/{dominantColor}/{name}",
                               arguments = listOf(
                                   navArgument("dominantColor"){
                                        type = NavType.IntType
                                   },
                                   navArgument("name"){
                                       type = NavType.StringType
                                   }
                               )
                    ){
                        val dominantColor = remember{
                            val color  = it.arguments?.getInt("dominantColor")
                            color?.let {
                                Color(it)
                            }?:Color.White
                        }

                        val pokemonName = remember{
                            it.arguments?.getString("name")
                        }
                        PokemonDetailScreen(
                            navController = navHost,
                            dominantColor = dominantColor,
                            pokemonName = pokemonName?.lowercase()?:""
                        )


                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PokedoxTheme {
        Greeting("Android")
    }
}