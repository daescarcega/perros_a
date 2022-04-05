package edu.itesm.prros

import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Adapter
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import edu.itesm.prros.adapter.PerrosAdapter
import edu.itesm.prros.databinding.ActivityMainBinding
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
    private val perrosPics = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAdapter()
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
    private fun buscarPerrosPorRaza(raza : String) {
        CoroutineScope(Dispatchers.IO).launch {
            val llamado = RetrofitSingleton.getRetroFit()
                .create(PerrosAPIService::class.java)
                .getPerrosPorRaza("$raza/images")
            val perrosResponse : PerroResponse? = llamado.body()
           runOnUiThread {
               if (llamado.isSuccessful) {
                   val imagenes: List<String> = perrosResponse?.images ?: emptyList()
                   perrosPics.clear()
                   perrosPics.addAll(imagenes)
                   adapter.notifyDataSetChanged()
               } else {
                   Toast.makeText(
                       this@MainActivity, "Error",
                       Toast.LENGTH_LONG
                   ).show()
               }
               hideKeyboard()
           }

        }
    }

    override fun onQueryTextSubmit(searchString: String?): Boolean {
        if(!searchString.isNullOrEmpty()){
            buscarPerrosPorRaza(searchString.lowercase())
        }
        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        return true
    }


}