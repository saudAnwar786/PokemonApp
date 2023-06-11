package com.sacoding.pokedox.PokemonList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.sacoding.pokedox.R
import com.sacoding.pokedox.data.models.PokemonListEntry

@Composable
fun PokemonListScreen(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
){
    Surface(color = MaterialTheme.colorScheme.background,modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(16.dp))
            Image(painter = painterResource(id = R.drawable.img), contentDescription = "PokemonLogo",
                  modifier = Modifier
                      .fillMaxWidth()
                      .align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(16.dp))
            SearchBar(hint = "Search...",
            modifier = Modifier

                .fillMaxWidth()
                .padding(16.dp)){

                viewModel.searchPokemon(it)
            }
            Spacer(modifier = Modifier.height(20.dp))
            PokemonListRv(navController = navController)

        }
    }
}

@Composable
fun SearchBar(
    modifier:Modifier = Modifier,
    hint:String = "",
    onSearch:(String)->Unit =
        {}
){

    var text by remember{
        mutableStateOf("")
    }

    var isHintDisplayed by remember{
        mutableStateOf(hint != "")
    }
    Box(modifier = modifier.fillMaxWidth()){
       BasicTextField(value = text, onValueChange =
       { text = it
           onSearch(it) }  ,
       maxLines = 1,
       singleLine = true,
       textStyle = TextStyle(color = Color.Black),
       modifier = Modifier
           .fillMaxWidth()
           .shadow(5.dp, CircleShape)
           .background(Color.White, CircleShape)
           .padding(20.dp)
           .onFocusChanged {
               isHintDisplayed = it.isFocused == false && text.isEmpty()
           }
       )
        if(isHintDisplayed) {
            Text(text = hint,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp))
        }




    }
}

@Composable
fun PokemonListRv(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
){
    val isLoading  by remember{ viewModel.isLoading }
    val pokemonList by remember { viewModel.pokemonList }
    val endReached by remember { viewModel.endReached }
    val loadError by remember { viewModel.loadError }
    val isSearching by remember{viewModel.isSearching}
    LazyColumn(contentPadding = PaddingValues(16.dp)) {

        val itemCount = if (pokemonList.size % 2 == 0) {
            pokemonList.size / 2
        } else {
            pokemonList.size / 2 + 1
        }
        items(itemCount) {
            if(it >= itemCount - 1 && !endReached && !isLoading && !isSearching) {
                viewModel.loadPokemonpaginatedList()
            }
            PokemonRow(rowIndex = it, entries = pokemonList, navController = navController)
            Spacer(modifier = Modifier.height(12.dp))

        }
    }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if(isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            if(loadError.isNotEmpty()) {
                RetrySection(error = loadError) {
                    viewModel.loadPokemonpaginatedList()
                }
            }
        }


}

@Composable
fun RetrySection(
    error: String,
    onRetry: () -> Unit
) {
    Column {
        Text(error, color = Color.Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onRetry() },
            modifier = Modifier.align(CenterHorizontally)
        ) {
            Text(text = "Retry")
        }
    }
}

@Composable
fun PokemonList(
    entry:PokemonListEntry,
    navController: NavController,
    modifier:Modifier = Modifier,
    viewModel: PokemonListViewModel = hiltViewModel()
){

    val defaultDominantColor = MaterialTheme.colorScheme.surface
    var dominantColor  by remember{
        mutableStateOf(defaultDominantColor)
    }
    Box(modifier = modifier
        .shadow(5.dp)
        .clip(
            RoundedCornerShape(8.dp)
        )
        .aspectRatio(1f)
        .background(
            Brush.verticalGradient(
                listOf(
                    dominantColor,
                    defaultDominantColor

                )
            )
        )
        .clickable {
            navController.navigate("pokemon_detail_screen/${dominantColor.toArgb()}/${entry.pokemonName}")
        }
        ){
        Column(modifier = Modifier.fillMaxSize(.9f)) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(entry.url)
                    .crossfade(true).build(), contentDescription = entry.pokemonName,
                onSuccess = {
                    viewModel.getDominantColor(it.result.drawable) { color ->
                        dominantColor = color
                    }
                },
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = entry.pokemonName,
                textAlign = TextAlign.Center, color = Color.Black,
                fontSize = 20.sp
            )
        }
        }
    }


@Composable
fun PokemonRow(
    rowIndex: Int,
    entries: List<PokemonListEntry>,
    navController: NavController
){
    Column {
        Row{
            PokemonList(entry = entries[rowIndex*2], navController = navController,
            modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(16.dp))
            if(entries.size >= rowIndex* 2 +2){
                PokemonList(entry = entries[rowIndex*2 + 1], navController = navController,
                    modifier = Modifier.weight(1f))
            }
            else{
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}