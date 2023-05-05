package com.example.kotlinbook

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinbook.databinding.RecyclerRowBinding

class BookAdapter(val bookList:ArrayList<Book>):RecyclerView.Adapter<BookAdapter.BookHolder>() {
    class BookHolder(val binding:RecyclerRowBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BookHolder(binding)
    }

    override fun getItemCount(): Int {
      return bookList.size
    }

    override fun onBindViewHolder(holder: BookHolder, position: Int) {
        holder.binding.textView3.text=bookList.get(position).name
        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context,ArtActivity::class.java)
            intent.putExtra("info","old")
            intent.putExtra("id", bookList.get(position).id)
            holder.itemView.context.startActivity(intent)
        }

    }
}