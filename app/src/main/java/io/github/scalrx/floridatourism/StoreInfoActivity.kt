package io.github.scalrx.floridatourism

import android.graphics.BitmapFactory
import android.support.v4.app.FragmentActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException

import java.io.FileReader
import java.io.IOException
import java.text.DecimalFormat

class StoreInfoActivity : FragmentActivity(), OnMapReadyCallback {
    /**
     * Members
     */
    private lateinit var map: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var location: LatLng
    private lateinit var title: String
    private lateinit var snippet: String

    private lateinit var logo: ImageView
    private lateinit var storeName: TextView
    private lateinit var phoneIcon: ImageView
    private lateinit var phoneNumberText: TextView
    private lateinit var addressText: TextView
    private lateinit var locationText: TextView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_info)

        // Initialize what's on the screen
        logo = findViewById(R.id.storeImage)
        storeName = findViewById(R.id.storeName)
        phoneIcon = findViewById(R.id.phoneIcon)
        phoneNumberText = findViewById(R.id.phoneNumberText)
        addressText = findViewById(R.id.addressText)
        locationText = findViewById(R.id.locationText)

        initData()
        val numberFormat = DecimalFormat("#.000000")
        snippet = "(" + numberFormat.format(location.latitude) + ", " + numberFormat.format(location.longitude) + ")"

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    override fun onMapReady(googleMap: GoogleMap)
    {
        // Initialize the map and prepare the information window for the marker
        MapsInitializer.initialize(applicationContext)
        map = googleMap
        val mOpt = MarkerOptions().position(location).title(title).snippet(snippet)

        // Add a marker to the intended location and move the camera
        map.addMarker(mOpt).showInfoWindow()
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f))
        map.setMinZoomPreference(3f)
        map.mapType = GoogleMap.MAP_TYPE_SATELLITE
        mapView.onResume()    // Necessary so that the map completely renders
    }

    // Get data to display on this screen
    private fun initData()
    {
        val parser = JSONParser()
        val internalPath = applicationContext.filesDir.path + "/"
        val jsonFileName = "stores.json"

        try
        {
            val json = parser.parse(FileReader(internalPath + jsonFileName)) as JSONObject
            val stores = json["stores"] as JSONArray?
            val storeIdNumber = intent.getIntExtra("STORE_ID_NUMBER", -1)

            for (store in stores!!)
            {
                val storeObject = store as JSONObject
                val id = Integer.parseInt(storeObject["storeID"].toString())

                if (id == storeIdNumber)
                {
                    // Get the required information
                    val url = storeObject["storeLogoURL"].toString()
                    val imageFileName = url.substringAfterLast('/')
                    val name = storeObject["name"].toString()
                    val address = storeObject["address"].toString()
                    val phone = storeObject["phone"].toString()
                    val state = storeObject["state"].toString()
                    val zipcode = storeObject["zipcode"].toString()
                    val city = storeObject["city"].toString()
                    val latitude = java.lang.Float.parseFloat(storeObject["latitude"].toString())
                    val longitude = java.lang.Float.parseFloat(storeObject["longitude"].toString())

                    // Then set the Views...
                    logo.setImageBitmap(BitmapFactory.decodeFile(internalPath + imageFileName))
                    storeName.text = "$name (#$id)"
                    title = name
                    phoneIcon.setImageResource(android.R.drawable.sym_action_call)
                    phoneNumberText.text = phone
                    addressText.text = address
                    locationText.text = "$city, $state $zipcode"

                    location = LatLng(latitude.toDouble(), longitude.toDouble())

                    break
                }

            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

    }

}