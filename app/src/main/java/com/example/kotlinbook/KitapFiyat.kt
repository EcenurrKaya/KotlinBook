package com.example.kotlinbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.kotlinbook.databinding.ActivityKitapFiyatBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class KitapFiyat : AppCompatActivity() {
    private lateinit var binding: ActivityKitapFiyatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityKitapFiyatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressbar.visibility= View.INVISIBLE

        binding.btnSearch.setOnClickListener{
            searchBook()
        }
    }
    private fun searchBook(){
        binding.progressbar.visibility= View.VISIBLE
        val query = binding.editInputBook.text.toString()
        val client = AsyncHttpClient()
        val url="https://www.googleapis.com/books/v1/volumes?q=${query}"
        client.get(url, object: AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>,
                responseBody: ByteArray
            ) {
                val result = String(responseBody)
                Log.d(TAG,result)
                binding.progressbar.visibility= View.INVISIBLE

                try{
                    val jsonObject  = JSONObject(result)
                    val itemsArray = jsonObject.getJSONArray("items")

                    var i=0
                    var bookTitle=""
                    var bookAuthor=""

                    while(i<1){
                        val book = itemsArray.getJSONObject(i)
                        val volumeInfo = book.getJSONObject("volumeInfo")
                        try{
                            bookTitle=volumeInfo.getString("title")
                            bookAuthor=volumeInfo.getString("authors")
                        }catch(e:Exception){
                            e.printStackTrace()
                        }
                        i++
                        // itemsArray.length()
                    }

                    binding.apply {
                        tvTitleReult.text=bookTitle
                        tvAuthorReult.text=bookAuthor
                    }
                }catch(e:Exception){
                    Toast.makeText(this@KitapFiyat,e.message, Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                val errorMessage = when(statusCode){
                    401->"Status Code: Bad Request"
                    403->"Status Code: Forbidden"
                    404->"Status Code: Not found"
                    else->"Status Code: ${error?.message}"
                }
                Toast.makeText(this@KitapFiyat,errorMessage, Toast.LENGTH_SHORT).show()

            }

        })
    }
    companion object{
        private val TAG= MainActivity::class.java.simpleName
    }
}