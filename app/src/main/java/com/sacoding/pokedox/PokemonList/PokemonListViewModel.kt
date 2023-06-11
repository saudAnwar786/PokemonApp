package com.sacoding.pokedox.PokemonList

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.sacoding.pokedox.util.Resource
import com.sacoding.pokedox.data.models.PokemonListEntry
import com.sacoding.pokedox.repository.MainRepository
import com.sacoding.pokedox.util.Constants
import com.sacoding.pokedox.util.Constants.PAGE_SIZE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel() {
    private var curPage = 0

    var pokemonList = mutableStateOf<List<PokemonListEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    var isSearchingStarting = true
    var isSearching = mutableStateOf(false)
    var cachedPokemonList = listOf<PokemonListEntry>()
    init {
        loadPokemonpaginatedList()
    }
    fun searchPokemon(query:String){
        var listToSearch = if(isSearchingStarting){
            pokemonList.value
        }else{
            cachedPokemonList
        }

        viewModelScope.launch(Dispatchers.Default)
        {
            if(query.isEmpty()){
                pokemonList.value = cachedPokemonList
                isSearching.value = false
                isSearchingStarting = true
                return@launch
            }
               val result =  listToSearch.filter {
                    it.pokemonName.contains(query.trim(),ignoreCase = true) ||
                            it.number.toString() == query.trim()
                }

            if(isSearchingStarting){
                cachedPokemonList = pokemonList.value
                isSearchingStarting = false
            }

            pokemonList.value = result
            isSearching.value = true

        }
    }

    fun loadPokemonpaginatedList() {
        viewModelScope.launch {
        isLoading.value = true
        val response = repository.getPokemonList(Constants.PAGE_SIZE,curPage*PAGE_SIZE)
        when(response){
            is Resource.Error -> {
                 loadError.value = response.message!!
                 isLoading.value = false
            }
            is Resource.Success -> {

                endReached.value = curPage * PAGE_SIZE >= response.data!!.count
                val pokemonEntries = response.data.results.mapIndexed { index, entry ->
                    val number = if(entry.url.endsWith("/")) {
                        entry.url.dropLast(1).takeLastWhile {
                            it.isDigit()
                        }.toInt()
                    }else{
                        entry.url.dropLastWhile { it.isDigit() }.toInt()
                    }
                    val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                    PokemonListEntry(entry.name.capitalize(Locale.ROOT), number, url)
                }
                curPage++
                loadError.value = ""
                isLoading.value = false
                pokemonList.value += pokemonEntries

            }
            is Resource.Loading ->{

            }
        }
    }
    }
    fun getDominantColor( drawable:Drawable,onFinish:(Color)->Unit){
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888,true)
         Palette.Builder(bmp).generate(){pallete->
            pallete?.dominantSwatch?.rgb?.let {colorValue->
                onFinish(Color(colorValue))
            }
        }

    }
}