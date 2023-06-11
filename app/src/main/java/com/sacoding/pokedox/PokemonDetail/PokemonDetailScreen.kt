package com.sacoding.pokedox.PokemonDetail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.sacoding.pokedox.R
import com.sacoding.pokedox.data.remote.responses.Pokemon
import com.sacoding.pokedox.data.remote.responses.Type
import com.sacoding.pokedox.util.Resource
import com.sacoding.pokedox.util.parseStatToAbbr
import com.sacoding.pokedox.util.parseStatToColor
import com.sacoding.pokedox.util.parseTypeToColor
import java.lang.Math.round
import java.util.Locale


@Composable
fun PokemonDetailScreen(
    navController: NavController,
    viewModel: PokemonDetailViewModel = hiltViewModel(),
    dominantColor: Color,
    topPadding: Dp = 20.dp,
    pokemonImageSize: Dp = 200.dp,
    pokemonName:String

    ) {

    val pokemonInfo = produceState<Resource<Pokemon>>(initialValue = Resource.Loading()){
        value = viewModel.getPokemonDetail(pokemonName)
    }.value

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = dominantColor)
        .padding(bottom = 16.dp)){

        PokemonDetailTopScreen(
            navController = navController,
            modifier = Modifier
                .fillMaxHeight(.2f)
                .fillMaxWidth()
                .align(Alignment.TopStart)

                      )
        PokemonDetailStateWrapper(
            pokemonInfo = pokemonInfo,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp,
                    top = topPadding + pokemonImageSize / 2f
                )
                .shadow(10.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color.White)
                .padding(16.dp),
            loadingModifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp,
                    top = topPadding + pokemonImageSize / 2f
                )
         )

        Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()){
            if(pokemonInfo is Resource.Success){
                pokemonInfo.data?.sprites?.let {

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(it.front_default)
                            .crossfade(true)
                            .build(),


                        contentDescription = pokemonInfo.data.name,
                        modifier = Modifier
                            .size(pokemonImageSize)
                            .offset(y = topPadding)
                    )
                }

            }
        }

    }

}

@Composable
fun PokemonDetailTopScreen(
    modifier:Modifier = Modifier,
    navController:NavController
) {

    Box(modifier = modifier.background(
        Brush.verticalGradient(listOf(
            Color.Black,
            Color.Transparent
        )))
         ){
        Icon(imageVector = Icons.Default.ArrowBack,
        contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(36.dp)
                .offset(16.dp, 16.dp)
                .clickable {
                    navController.popBackStack()
                }
        )
    }

}

@Composable
fun PokemonDetailStateWrapper(
    pokemonInfo:Resource<Pokemon>,
    modifier: Modifier = Modifier ,
    loadingModifier:Modifier = Modifier,

) {

    when(pokemonInfo){
        is Resource.Error -> {
            Text(text = pokemonInfo.message!!,
                color = Color.Red,
                modifier = modifier)
        }
        is Resource.Loading -> {
            CircularProgressIndicator( color = MaterialTheme.colorScheme.primary,
                modifier = loadingModifier)

        }
        is Resource.Success -> {
            PokemonDetailInfo(
                pokemonInfo = pokemonInfo.data!!,
                modifier = modifier
                    .offset(y = (-20).dp)
            )

        }
    }

}

@Composable
fun PokemonDetailInfo(
    pokemonInfo: Pokemon,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier
        .fillMaxSize()
        .offset(y = 100.dp)
        .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally) {
       Text(text = "#${pokemonInfo.id} ${pokemonInfo.name}",
            fontWeight = FontWeight.Bold,
           fontSize = 30.sp,
             textAlign = TextAlign.Center,
             color = MaterialTheme.colorScheme.onSurface)
        PokemonDetailTypeSection(types = pokemonInfo.types)
        PokemonDataSection(pokemonWeight = pokemonInfo.weight, pokemonHeight = pokemonInfo.height)

        
    }

}

@Composable
fun PokemonDetailTypeSection(
    types:List<Type>
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
    ){
        for(type in types){
            Box( modifier = Modifier
                .weight(1f)
                .padding(16.dp)
                .clip(CircleShape)
                .background(color = parseTypeToColor(type))
                .height(35.dp), contentAlignment = Alignment.Center){

                Text(text = type.type.name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                },color = Color.White,
                    fontSize = 18.sp
                )

            }

        }
    }

}

@Composable
fun PokemonDataSection(
    pokemonWeight: Int,
    pokemonHeight: Int,
    sectionHeight: Dp = 80.dp
) {
    val pokemonWeightInKg = remember {
        round(pokemonWeight * 100f) / 1000f
    }
    val pokemonHeightInMeters = remember {
        round(pokemonHeight * 100f) / 1000f
    }
    Row(modifier = Modifier
        .fillMaxWidth()) {
        PokemonDataItem(dataUnit ="kg", dataValue =pokemonWeightInKg, dataIcon = painterResource(id = R.drawable.ic_weight),
            modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.size(1.dp,sectionHeight))
        PokemonDataItem(dataUnit ="m", dataValue =pokemonHeightInMeters, dataIcon = painterResource(id = R.drawable.ic_height,
            ),modifier = Modifier.weight(1f))
    }

}

@Composable
fun PokemonDataItem(
    modifier :Modifier = Modifier,
    dataUnit :String,
    dataValue:Float,
    dataIcon:Painter
) {
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
        ) {
        Icon(painter = dataIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$dataValue$dataUnit",
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    
    
}

@Composable
fun PokemonBaseStat(
    statName: String,
    statValue: Int,
    statMaxValue: Int,
    statColor: Color,
    height: Dp = 28.dp,
    animDuration: Int = 1000,
    animDelay: Int = 0
) {
    var isAnimationDisplayed by remember{
        mutableStateOf(false)
    }

    val curPercent = animateFloatAsState(
        targetValue = if(isAnimationDisplayed) statValue/statMaxValue.toFloat() else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = animDelay
        )
    )
    LaunchedEffect(key1 = true) {
        isAnimationDisplayed = true
    }
    
    Box(modifier = Modifier
        .height(height)
        .background(if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray)
        ){
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(height)
                .fillMaxWidth(curPercent.value)
                .clip(CircleShape)
                .background(statColor)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = statName,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = (curPercent.value * statMaxValue).toInt().toString(),
                fontWeight = FontWeight.Bold
            )

        }
    }

}

@Composable
fun PokemonBaseStats(
    pokemonInfo: Pokemon,
    animDelayPerItem: Int = 100
) {
    val maxBaseStat = remember {
        pokemonInfo.stats.maxOf {
            it.base_stat
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Base stats:",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))

        for(i in pokemonInfo.stats.indices) {
            val stat = pokemonInfo.stats[i]
            PokemonBaseStat(
                statName = parseStatToAbbr(stat),
                statValue = stat.base_stat,
                statMaxValue = maxBaseStat,
                statColor = parseStatToColor(stat),
                animDelay = i * animDelayPerItem
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

}