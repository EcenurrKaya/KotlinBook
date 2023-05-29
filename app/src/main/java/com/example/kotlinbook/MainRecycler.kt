package com.example.kotlinbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinbook.databinding.ActivityMainRecyclerBinding
//Mainim bu
class MainRecycler : AppCompatActivity() {
    private lateinit var binding:ActivityMainRecyclerBinding
    private lateinit var bookList:ArrayList<Book>
    private lateinit var bookAdapter: BookAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainRecyclerBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        bookList=ArrayList<Book>()

        bookAdapter=BookAdapter(bookList)
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        binding.recyclerView.adapter=bookAdapter

        try{
            val database=this.openOrCreateDatabase("Books", MODE_PRIVATE,null)
            val cursor=database.rawQuery("SELECT * FROM books",null)
            val kitapadiIx = cursor.getColumnIndex("kitapadi")
            val idIx=cursor.getColumnIndex("id")

            while(cursor.moveToNext()){
                val name = cursor.getString(kitapadiIx)
                val id = cursor.getInt(idIx)

                val book=Book(name,id)
                bookList.add(book)
            }
            bookAdapter.notifyDataSetChanged()
            cursor.close()
        }
        catch(e:Exception){
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuinflater=menuInflater
        menuinflater.inflate(R.menu.art_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.add_art_item){
            val intent = Intent(this@MainRecycler,KitapFiyat::class.java)
            intent.putExtra("info","new")
            startActivity(intent)
        }
        else if(item.itemId==R.id.bildirim){
            val intent = Intent(this@MainRecycler,Bildirim::class.java)
            intent.putExtra("info", "new")
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

   fun AddBook(view: View){
        val intent = Intent(this@MainRecycler,ArtActivity::class.java)
        intent.putExtra("info","new")
        startActivity(intent)
    }

}