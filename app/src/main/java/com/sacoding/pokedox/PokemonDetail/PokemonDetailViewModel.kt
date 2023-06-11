package com.sacoding.pokedox.PokemonDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sacoding.pokedox.data.remote.responses.Pokemon
import com.sacoding.pokedox.repository.MainRepository
import com.sacoding.pokedox.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: MainRepository
) :ViewModel(){

   suspend fun getPokemonDetail(name:String) :Resource<Pokemon> {
           return repository.getPokemonDetail(name)
   }
}