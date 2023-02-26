package com.soudeep.bookhub.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.soudeep.bookhub.R
import com.soudeep.bookhub.adapter.DashboardRecyclerAdapter
import com.soudeep.bookhub.model.Book
import com.soudeep.bookhub.util.ConnectionManager
import org.json.JSONException
import java.util.Collections

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerDashboard:RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    //lateinit var btnCheckInternet:Button
    lateinit var progressBar:ProgressBar
    lateinit var progressLayout:RelativeLayout

    lateinit var recyclerAdapter: DashboardRecyclerAdapter

    val bookInfoList = arrayListOf<Book>()

/*    val ratingComparator = Comparator<Book> {book1, book2 ->
        if((book1.bookRating.compareTo(book2.bookRating,true) == 0){
            //sort according to name if rating is same...
            return book1.bookName.compareTo(book2.bookName,true)
            }else{
                return book1.bookRating.compareTo(book2.bookRating,true)
        }
    }*/
val ratingComparator = Comparator<Book> {book1, book2 ->
    if(book1.bookRating.compareTo(book2.bookRating,true) == 0){
        //sort according to name if rating is same...
        book1.bookName.compareTo(book2.bookName,true)
    }else{
        book1.bookRating.compareTo(book2.bookRating,true)
    }
}



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        setHasOptionsMenu(true)

        recyclerDashboard = view.findViewById(R.id.recyclerdashboard)
        layoutManager = LinearLayoutManager(activity)
//        btnCheckInternet = view.findViewById(R.id.btnCheckInternet)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE

        /*btnCheckInternet.setOnClickListener{
            if (ConnectionManager().checkConnectivity(activity as Context)){
//                Internet is available
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Success")
                dialog.setMessage("Internet connection is found")
                dialog.setPositiveButton("Ok"){text,listener ->
//                    Do Nothing
                }
                dialog.setNegativeButton("Cancel"){text,listener ->
//                    Do Nothing
                }
                dialog.create()
                dialog.show()
            }else{
//                Internet is not available
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Error")
                dialog.setMessage("Internet connection is not found")
                dialog.setPositiveButton("Ok"){text,listener ->
//                    Do Nothing
                }
                dialog.setNegativeButton("Cancel"){text,listener ->
//                    Do Nothing
                }
                dialog.create()
                dialog.show()
            }
        }*/



        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v1/book/fetch_books/"
        if(ConnectionManager().checkConnectivity(activity as Context)){


            val jsonObjectRequest = object: JsonObjectRequest(Request.Method.GET,url,null,Response.Listener {
                // Here we will handle the response

                try{
                    progressLayout.visibility = View.GONE
                    val success = it.getBoolean("success")
                    if(success){

                        val data = it.getJSONArray("data")
                        for(i in 0 until data.length()){
                            val bookJsonObject = data.getJSONObject(i)
                            val bookObject = Book(
                                bookJsonObject.getString("book_id"),
                                bookJsonObject.getString("name"),
                                bookJsonObject.getString("author"),
                                bookJsonObject.getString("rating"),
                                bookJsonObject.getString("price"),
                                bookJsonObject.getString("image")
                            )
                            bookInfoList.add(bookObject)
                            recyclerAdapter = DashboardRecyclerAdapter(activity as Context,bookInfoList)

                            recyclerDashboard.adapter = recyclerAdapter
                            recyclerDashboard.layoutManager = layoutManager

                            /*recyclerDashboard.addItemDecoration(
                                DividerItemDecoration(
                                    recyclerDashboard.context,
                                    (layoutManager as LinearLayoutManager).orientation
                                )
                            )*/
                        }
                    }else{
                        Toast.makeText(activity as Context,"Error Occurred !!", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: JSONException){
                    Toast.makeText(activity as Context,"Unexpected error occurred !!", Toast.LENGTH_SHORT).show()
                }


            },Response.ErrorListener {
                // Here we will handle the errors
               Toast.makeText(activity as Context,"Volley Error Occurred !!", Toast.LENGTH_SHORT).show()

            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String,String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "aedc698c2d1636"
                    return headers
                }
            }


            queue.add(jsonObjectRequest)
        }else{
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet connection is not found")
            dialog.setPositiveButton("Open Settings"){text,listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit"){text,listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()

        }


        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_dasboard,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item?.itemId
        if (id==R.id.action_sort){
            Collections.sort(bookInfoList,ratingComparator)
            bookInfoList.reverse()
        }
        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }
}