package com.codepath.nationalparks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Headers
import org.json.JSONArray

// ------------------------------- //
// CHANGE THIS TO BE YOUR API KEY  //
// ------------------------------- //
private const val API_KEY = "jIAylfGw5fuRAbm9xq1o1zlaEnObdjjK0XLKfIJe"

class NationalParksFragment :
    Fragment(),
    OnListFragmentInteractionListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_national_parks_list,
            container,
            false
        )

        val progressBar =
            view.findViewById<ContentLoadingProgressBar>(R.id.progress)

        val recyclerView =
            view.findViewById<RecyclerView>(R.id.list)

        recyclerView.layoutManager =
            LinearLayoutManager(view.context)

        updateAdapter(progressBar, recyclerView)

        return view
    }

    private fun updateAdapter(
        progressBar: ContentLoadingProgressBar,
        recyclerView: RecyclerView
    ) {

        progressBar.show()

        val client = AsyncHttpClient()

        val params = RequestParams()
        params["api_key"] = API_KEY

        client[
            "https://developer.nps.gov/api/v1/parks",
            params,
            object : JsonHttpResponseHandler() {

                override fun onSuccess(
                    statusCode: Int,
                    headers: Headers,
                    json: JSON
                ) {

                    progressBar.hide()

                    // Get the "data" array from the API response
                    val dataJSON =
                        json.jsonObject.get("data") as JSONArray

                    val parksRawJSON =
                        dataJSON.toString()

                    // Convert JSON into NationalPark objects
                    val gson = Gson()

                    val arrayParkType =
                        object :
                            TypeToken<List<NationalPark>>() {}.type

                    val models: List<NationalPark> =
                        gson.fromJson(
                            parksRawJSON,
                            arrayParkType
                        )

                    // Display parks in RecyclerView
                    recyclerView.adapter =
                        NationalParksRecyclerViewAdapter(
                            models,
                            this@NationalParksFragment
                        )

                    Log.d(
                        "NationalParksFragment",
                        "response successful"
                    )
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Headers?,
                    errorResponse: String,
                    t: Throwable?
                ) {

                    progressBar.hide()

                    Log.e(
                        "NationalParksFragment",
                        errorResponse
                    )
                }
            }
        ]
    }

    override fun onItemClick(item: NationalPark) {

        Toast.makeText(
            context,
            "Park Name: " + item.name,
            Toast.LENGTH_LONG
        ).show()
    }
}