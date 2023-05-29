package com.example.kotlinbook

import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kotlinbook.databinding.ActivityArtBinding
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream

class ArtActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArtBinding
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher:ActivityResultLauncher<String>
    var selectedBitmap : Bitmap? = null
    private lateinit var database:SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArtBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        database=this.openOrCreateDatabase("Books", MODE_PRIVATE,null)

        registerLauncher()

        val intent = intent
        val info = intent.getStringExtra("info")
        if(info.equals("new")){
            binding.editTextKitapAdi.setText("")
            binding.editTextZet.setText("")
            binding.imageView.setImageResource(R.drawable.boook)
            //binding.floatingActionButton.visibility=View.VISIBLE

        }
        else{
            //binding.floatingActionButton.visibility=View.INVISIBLE
            val selectedId=intent.getIntExtra("id",1)
            val cursor =database.rawQuery("SELECT * FROM books WHERE id=?", arrayOf(selectedId.toString()))

            val kitapIx=cursor.getColumnIndex("kitapadi")
            val ozetIx=cursor.getColumnIndex("ozet")
            val imageIx=cursor.getColumnIndex("image")

            while(cursor.moveToNext()){
                binding.editTextKitapAdi.setText(cursor.getString(kitapIx))
                binding.editTextZet.setText(cursor.getString(ozetIx))

                val byteArray=cursor.getBlob(imageIx)
                val bitmap=BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                binding.imageView.setImageBitmap(bitmap)

            }

        }
    }

    fun selectimage(view: View){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_MEDIA_IMAGES)== PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Galeri için izin gerekli",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver",View.OnClickListener {
                        permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                    }).show()
                }
                else{
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
            else{
                val intenttogallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intenttogallery)
            }

        }
        else{
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Galeri için izin gerekli",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver",View.OnClickListener {
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
                }
                else{
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
            else{
                val intenttogallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intenttogallery)
            }

        }


    }

    fun savebutonclicked(view:View){
        Toast.makeText(applicationContext,"Kaydedildi",Toast.LENGTH_LONG).show()
        val kitapadi = binding.editTextKitapAdi.text.toString()
        val ozet=binding.editTextZet.text.toString()

        if(selectedBitmap!=null){
            val smallBitmap=makesmallBitmap(selectedBitmap!!,  300)

            val outputstream =ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputstream)
            val byteArray=outputstream.toByteArray()

            try{

                database.execSQL("CREATE TABLE IF NOT EXISTS books (id INTEGER PRIMARY KEY, kitapadi VARCHAR, ozet VARCHAR,image BLOB)")

                val sqlString = "INSERT INTO books (kitapadi,ozet,image) VALUES(?,?,?)"
                val statement = database.compileStatement(sqlString)

                statement.bindString(1,kitapadi)
                statement.bindString(2,ozet)
                statement.bindBlob(3,byteArray)
                statement.execute()

            }
            catch(e:Exception){
                e.printStackTrace()
            }
            val intent = Intent(this@ArtActivity,MainRecycler::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

    }


    private fun makesmallBitmap(image:Bitmap, maximumSize:Int):Bitmap{
        var width = image.width
        var height = image.height

        val bitmapratio : Double=width.toDouble()/height.toDouble()
        if(bitmapratio>1){
            width=maximumSize
            val scaledHeight = width/bitmapratio
            height=scaledHeight.toInt()

        }
        else{
            height=maximumSize
            val scaledWidth = height*bitmapratio
            width=scaledWidth.toInt()

        }
        return Bitmap.createScaledBitmap(image, width,height,true)
    }

    private fun registerLauncher(){
        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            if(result.resultCode== RESULT_OK){
                val intentfromresult = result.data
                if(intentfromresult!=null){
                    val imagedata = intentfromresult.data
                    if(imagedata!=null){
                        try{
                            if(Build.VERSION.SDK_INT>=28){
                                val source= ImageDecoder.createSource(this@ArtActivity.contentResolver,imagedata)
                                selectedBitmap=ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }
                            else{
                                selectedBitmap=MediaStore.Images.Media.getBitmap(contentResolver,imagedata)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }
                        }
                        catch(e:Exception){
                            e.printStackTrace()

                        }
                    }
                }
            }
        }
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
            if(result){
                val intenttogallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intenttogallery)

            }
            else{
                Toast.makeText(this@ArtActivity,"İzin alınmalı",Toast.LENGTH_LONG).show()

            }
        }
    }
}