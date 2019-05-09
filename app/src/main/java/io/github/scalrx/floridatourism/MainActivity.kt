package io.github.scalrx.floridatourism

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

import org.apache.commons.io.FileUtils
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException

import java.io.File
import java.io.FileReader
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var requestText: TextView
    private lateinit var fetchResourcesButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var internalPath: String
    private lateinit var jsonFilename: String

    private val JSON_LINK= "http://sandbox.bottlerocketapps.com/BR_Android_CodingExam_2015_Server/stores.json"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize some parts of the stage
        requestText = findViewById(R.id.requestText)
        fetchResourcesButton = findViewById(R.id.fetchResourcesButton)
        progressBar = findViewById(R.id.progressBar)

        // Determine whether we need to download the JSON file, or if it already exists
        internalPath = applicationContext.filesDir.path + "/"
        jsonFilename = JSON_LINK.substringAfterLast('/')
        val jsonFile = File(internalPath + jsonFilename)

        // If the file doesn't exist or data verification failed...
        if (!jsonFile.exists() || !verifyData()) {

            fetchResourcesButton.setOnClickListener {
                val jsonDownload = DownloadAsync()
                jsonDownload.execute(JSON_LINK)
            }


        } else {
            // Otherwise... Hooray! We don't have to do anything. Move on to the next screen!
            if (verifyData()) {
                val goToNextActivity = Intent(applicationContext, RecyclerViewActivity::class.java)
                finish()
                startActivity(goToNextActivity)
            }
        }
    }

    // Called to see if we're missing some resources that are supposed to be downloaded from the internet
    private fun verifyData(): Boolean
    {
        val parser = JSONParser()
        try
        {
            val fr = FileReader(internalPath + jsonFilename)
            val jsonObject = parser.parse(fr) as JSONObject
            val stores = jsonObject["stores"] as JSONArray?

            for (store in stores!!)
            {
                val storeObject = store as JSONObject
                val url = storeObject["storeLogoURL"].toString()
                val output = url.substringAfterLast('/')

                val file = File(internalPath + output)

                // If the file doesn't exist, return false immediately
                if (!file.exists())
                    return false
            }
            // Otherwise, we found all the files
            fr.close()
            return true

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        // Exception happened, we shouldn't progress
        return false
    }

    // Code to asynchronously download all of the required assets for the app.
    private inner class DownloadAsync : AsyncTask<String, Int, IOException>()
    {
        override fun onPreExecute() {
            super.onPreExecute()
            requestText.setText(R.string.accept_download)
            progressBar.visibility = View.VISIBLE
        }

        override fun onPostExecute(exception: IOException?) {
            super.onPostExecute(exception)

            // Unable to download for whatever reason
            if (exception != null) {
                // Alter objects on screen accordingly to display error message.
                requestText.setText(R.string.download_failed)
                progressBar.visibility = View.INVISIBLE
            } else {
                // Verify that the data we downloaded is all there
                if (verifyData()) {
                    // Take the user forward to the next activity
                    val goToNextActivity = Intent(applicationContext, RecyclerViewActivity::class.java)
                    finish()
                    startActivity(goToNextActivity)
                }
            }
        }

        override fun doInBackground(vararg strings: String): IOException? {
            val link = strings[0]
            val outputFileName = link.substringAfterLast('/')
            try {
                downloadFile(link, outputFileName)

                // Now download the images from the JSON file
                val parser = JSONParser()
                try {
                    val fr = FileReader(internalPath + jsonFilename)
                    val jsonObject = parser.parse(fr) as JSONObject
                    val stores = jsonObject["stores"] as JSONArray?

                    for (store in stores!!) {
                        val storeObject = store as JSONObject
                        val url = storeObject["storeLogoURL"].toString()
                        val output = url.substringAfterLast('/')

                        downloadFile(url, output)
                    }
                    fr.close()

                } catch (e: IOException) {
                    e.printStackTrace()
                    return e
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                parser.reset()
            } catch (e: IOException) {
                e.printStackTrace()
                return e
            }

            return null
        }

        @Throws(IOException::class)
        private fun downloadFile(link: String, outputFileName: String) {
            try {
                val url = URL(link)
                val file = File(internalPath + outputFileName)
                FileUtils.copyURLToFile(url, file)

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }

        }
    }
}