package com.sacoding.pokedox.repository

import com.sacoding.pokedox.util.Resource
import com.sacoding.pokedox.data.remote.api.PokemonApi
import com.sacoding.pokedox.data.remote.responses.Pokemon
import com.sacoding.pokedox.data.remote.responses.PokemonList
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject


@ActivityScoped
class MainRepository   @Inject constructor(
    private val api:PokemonApi
){

    suspend fun getPokemonList(limit:Int,offset:Int): Resource<PokemonList> {

        val response =  try {
            api.getPokemonList(limit, offset)
        }catch (e:Exception){
            return Resource.Error("An unknown error occurred")
        }
        return Resource.Success(response)
    }
    suspend fun getPokemonDetail(name:String): Resource<Pokemon> {

        val response =  try {
            api.getPokemon(name)
        }catch (e:Exception){
            return Resource.Error("An unknown error occurred")
        }
        return Resource.Success(response)
    }

}