/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.marsrealestate.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class MarsApiStatus {LOADING, ERROR, DONE}

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {

    private val _navigateToSelectedProperty = MutableLiveData<MarsProperty>()

    val navigateToSelectedProperty : LiveData<MarsProperty>
        get() = _navigateToSelectedProperty

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _response = MutableLiveData<String>()

    private val _status = MutableLiveData<MarsApiStatus>()
    val status: LiveData<MarsApiStatus>
        get() = _status

    // The external immutable LiveData for the request status String
    val response: LiveData<String>
        get() = _response

    val _properties = MutableLiveData<List<MarsProperty>>()

    val property : LiveData<List<MarsProperty>>
        get() = _properties
    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getMarsRealEstateProperties()
    }

    /**
     * Sets the value of the status LiveData to the Mars API status.
     */
    private fun getMarsRealEstateProperties() {
       viewModelScope.launch {
           _status.value = MarsApiStatus.LOADING
           try {
               var listResult = MarsApi.retrofitService.getProperties()
               _properties.value = listResult
               _status.value = MarsApiStatus.DONE

           }catch (e:Exception){
               _status.value = MarsApiStatus.ERROR
               _response.value = "Failure: ${e.message}"
           }
        }

    }

    fun displayPropertyDetails(marsProperty: MarsProperty){
        _navigateToSelectedProperty.value = marsProperty
    }

    //This function will avoid unnecessary navigation once the navigation is completed
    fun displayPropertyDetailsComplete(){
        _navigateToSelectedProperty.value = null
    }
}
