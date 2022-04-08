package edu.itesm.prros.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.itesm.prros.patterns.RetrofitSingleton
import edu.itesm.prros.response.PerroResponse
import edu.itesm.prros.service.PerrosAPIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListaPerrosViewModel: ViewModel() {
    var liveData : MutableLiveData<List<String>>
    init {
        liveData = MutableLiveData()
    }
    fun getLiveDataObserver(): MutableLiveData<List<String>>{
        return liveData
    }
    fun perroAPICall(raza:String){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitSingleton.getRetroFit()
                .create(PerrosAPIService::class.java)
                .getPerrosPorRaza("$raza/images")
            call.enqueue(object : Callback<PerroResponse> {

                override fun onResponse(call: Call<PerroResponse>,
                                        response: Response<PerroResponse>) {
                    liveData.postValue(response.body()?.images?: emptyList())
                }

                override fun onFailure(call: Call<PerroResponse>, t: Throwable) {
                    liveData.postValue(emptyList())
                }

            })
        }
    }
}