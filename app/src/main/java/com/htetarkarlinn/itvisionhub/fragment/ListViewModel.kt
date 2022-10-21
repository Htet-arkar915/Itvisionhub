package com.htetarkarlinn.itvisionhub.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListViewModel : ViewModel(){
    private val _text = MutableLiveData<String>().apply {
        value = "This is list Fragment Section"
    }
    val text: LiveData<String> = _text

}
