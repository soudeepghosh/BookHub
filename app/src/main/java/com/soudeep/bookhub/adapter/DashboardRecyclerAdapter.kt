package com.soudeep.bookhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.soudeep.bookhub.R
import com.soudeep.bookhub.activity.DescriptionActivity
import com.soudeep.bookhub.model.Book
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter(val context:Context,val itemList:ArrayList<Book>): RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_dasboard_single_row,parent,false)
        return DashboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val book = itemList[position]
        holder.textBookName.text = book.bookName
        holder.txtBookAuthor.text = book.bookAuthor
        holder.txtBookPrice.text = book.bookPrice
        holder.txtBookRating.text = book.bookRating
        //holder.imgBookImage.setImageResource(book.bookImage)
        Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover).into(holder.imgBookImage)

        holder.llContent.setOnClickListener{
//            Toast.makeText(context,"Clicked on ${holder.textBookName.text}",Toast.LENGTH_SHORT).show()
            val intent = Intent(context,DescriptionActivity::class.java)
            intent.putExtra("book_id",book.bookId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    class DashboardViewHolder(view:View):RecyclerView.ViewHolder(view){
        val textBookName:TextView = view.findViewById(R.id.txtBookName)
        val imgBookImage:ImageView = view.findViewById(R.id.imgBookImage)
        val txtBookAuthor:TextView = view.findViewById(R.id.txtBookAuthor)
        val txtBookPrice:TextView = view.findViewById(R.id.txtBookPrice)
        val txtBookRating:TextView = view.findViewById(R.id.txtBookRating)
        val llContent:LinearLayout = view.findViewById(R.id.llContent)
    }
}
