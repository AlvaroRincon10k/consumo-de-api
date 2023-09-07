package com.example.retrofitkotlinejemplo

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofitkotlinejemplo.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), androidx.appcompat.widget.SearchView.OnQueryTextListener {

    lateinit var imagesPuppies : List<String>
    lateinit var dogsAdapter: DogsAdapter

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.searchBreed.setOnQueryTextListener(this)
    }

    private fun initCharacter(puppies: DogsResponse){
        if(puppies.status == "success"){
            imagesPuppies = puppies.images
        }
        dogsAdapter = DogsAdapter(imagesPuppies)
        rvDogs.setHasFixedSize(true)
        rvDogs.layoutManager = LinearLayoutManager(this)
        rvDogs.adapter = dogsAdapter
    }

    private fun getRetofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/breed/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun searchByName(query: String){
        CoroutineScope(Dispatchers.IO).launch{
            val call = getRetofit().create(APIService::class.java).getCharacterByName("$query/images").execute()
            val puppies = call.body() as DogsResponse
            runOnUiThread{
                if(puppies.status == "success"){
                    initCharacter(puppies)
                }else{
                    showErrorDialog()
                }
                hideKeyboard()
            }
        }
    }

    private fun hideKeyboard() {
        //val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //imm.hideSoftInputFromWindow(viewRoot.windowToken, 0)

        val view: View? = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }
    }

    private fun showErrorDialog() {
        Toast.makeText(this, "Ha ocurrido un error, intentelo de nuevo.", Toast.LENGTH_SHORT).show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchByName(query.lowercase())
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        return true
    }
}