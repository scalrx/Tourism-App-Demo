package io.github.scalrx.floridatourism

import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException

import java.io.FileReader
import java.io.IOException
import java.util.ArrayList

class RecyclerViewActivity : AppCompatActivity(), StoreAdapter.OnStoreListener {
    private var recyclerView: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private lateinit var storeEntries: ArrayList<StoreEntry>

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)

        // Create the list of items we'll be using
        storeEntries = ArrayList()
        fillList(storeEntries)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.setItemViewCacheSize(storeEntries.size)
        layoutManager = LinearLayoutManager(this)
        adapter = StoreAdapter(storeEntries, this)

        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = adapter
    }

    // Fills out the list using the appropriate items
    private fun fillList(LIST: ArrayList<StoreEntry>)
    {
        // Parse the JSON file
        val internalPath = applicationContext.filesDir.path + "/"
        val jsonFilename = "stores.json"
        val parser = JSONParser()
        try {
            val fr = FileReader(internalPath + jsonFilename)
            val jsonObject = parser.parse(fr) as JSONObject
            val stores = jsonObject["stores"] as JSONArray?

            for (store in stores!!) {
                val storeObject = store as JSONObject

                val address = storeObject["address"].toString()
                val phone = storeObject["phone"].toString()
                val url = storeObject["storeLogoURL"].toString()
                val id = Integer.parseInt(storeObject["storeID"].toString())
                val output = url.substringAfterLast('/')
                val logo = BitmapFactory.decodeFile(internalPath + output)

                val se = StoreEntry(logo, address, phone, id)
                LIST.add(se)

            }
            // Otherwise, we found all the files
            fr.close()

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

    }

    override fun onStoreClick(i: Int) {
        val intent = Intent(this, StoreInfoActivity::class.java)
        intent.putExtra("STORE_ID_NUMBER", storeEntries[i].id)
        startActivity(intent)

    }


}