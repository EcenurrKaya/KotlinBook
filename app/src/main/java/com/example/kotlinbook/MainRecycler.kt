package com.example.kotlinbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.kotlinbook.databinding.ActivityArtBinding
import com.example.kotlinbook.databinding.ActivityMainRecyclerBinding
//Mainim bu
class MainRecycler : AppCompatActivity() {
    private lateinit var binding:ActivityMainRecyclerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainRecyclerBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuinflater=menuInflater
        menuinflater.inflate(R.menu.art_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.add_art_item){
            val intent = Intent(this@MainRecycler,KitapFiyat::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    fun AddBook(view: View){
        val intent = Intent(this@MainRecycler,ArtActivity::class.java)
        startActivity(intent)
    }
}