package edu.itesm.prros

import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Adapter
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import edu.itesm.prros.adapter.PerrosAdapter
import edu.itesm.prros.databinding.ActivityMainBinding
import edu.itesm.prros.mvvm.ListaPerrosViewModel
import edu.itesm.prros.patterns.RetrofitSingleton
import edu.itesm.prros.patterns.RetrofitSingleton.getRetroFit
import edu.itesm.prros.response.PerroResponse
import edu.itesm.prros.service.PerrosAPIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(),
    android.widget.SearchView.OnQueryTextListener {
    private lateinit var adapter: PerrosAdapter
    private lateinit var binding: ActivityMainBinding
    private  lateinit var viewModel: ListaPerrosViewModel
    private val perrosPics = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAdapter()
        initViewModel()
    }
    private fun initViewModel(){
        viewModel = ViewModelProvider(this).get(ListaPerrosViewModel::class.java)
        viewModel.getLiveDataObserver().observe(this, Observer {
            if (!it.isEmpty()){
                adapter.setImagenes(it)
                adapter.notifyDataSetChanged()
                print("This is done")
            }
        })
    }
    private fun initAdapter(){
        adapter = PerrosAdapter(perrosPics)
        binding.perros.layoutManager = LinearLayoutManager(this)
        binding.perros.adapter = adapter
        //buscarPerrosPorRaza("labrador")
        binding.busqueda.setOnQueryTextListener(this)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }


    override fun onQueryTextSubmit(searchString: String?): Boolean {
        if(!searchString.isNullOrEmpty()){
            //buscarPerrosPorRaza(searchString.lowercase())
            consultaPerros(searchString)
        }
        return true
    }

    fun  consultaPerros(searchString: String?){
        if(   !searchString.isNullOrEmpty()) {
            viewModel.perroAPICall(searchString)
            hideKeyboard()
        }
    }
    override fun onQueryTextChange(p0: String?): Boolean {
        return true
    }


}