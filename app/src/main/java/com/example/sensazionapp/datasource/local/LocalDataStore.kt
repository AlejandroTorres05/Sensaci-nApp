package com.example.sensazionapp.datasource.local

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.prefs.Preferences

object LocalDataSourceProvider{

    private var instance: LocalDataStore?=null

    fun init(dataStore: DataStore<Preferences>){
        if(instance==null){
            instance=LocalDataStore(dataStore)
        }
    }

    fun get():LocalDataStore{
        return instance?: throw IllegalStateException("LocalDataStore NO esta inicializado")
    }
}

class LocalDataStore(val dataStore: DataStore<Preferences>) {
    suspend fun save(key:String, value:String){
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey(key)] = value
        }
    }

    fun load(key:String): Flow<String> = dataStore.data.map { prefs->
        prefs[stringPreferencesKey(key)] ?:""
    }

}