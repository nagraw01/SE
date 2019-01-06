package com.example.mg156.smarttraveler

import android.widget.TextView
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.LruCache
import android.view.LayoutInflater
import android.widget.ImageView
import java.lang.ref.WeakReference

class CustomInfoWindowAdapter(ctx: Context) : GoogleMap.InfoWindowAdapter {

    lateinit var context: Context
    lateinit var mWindow: View

    init {
        context = ctx
        mWindow = LayoutInflater.from(ctx).inflate(R.layout.custom_info_window_item, null)
    }

    fun renderWindow(view: View,marker: Marker){
        val info_title = view.findViewById(R.id.title) as TextView
        val info_snippet = view.findViewById(R.id.snippet) as TextView
        //val info_image = view.findViewById(R.id.image) as ImageView

        info_title.setText(marker.title)
        info_snippet.setText(marker.snippet)

        /*val venueId = marker.tag as String

        val task = DownloadMovieImage(info_image)
        task.execute(venueId)*/
    }

    override fun getInfoWindow(marker: Marker): View? {
        renderWindow(mWindow,marker)
        return mWindow
    }

    override fun getInfoContents(marker: Marker): View {
        renderWindow(mWindow,marker)
        return mWindow
        //val img = view.findViewById(R.id.pic)



        /*val infoWindowData = marker.tag as InfoWindowData?

        val imageId = context.getResources().getIdentifier(infoWindowData!!.getImage().toLowerCase(),
                "drawable", context.getPackageName())
        img.setImageResource(imageId)*/

    }

    /*inner class DownloadMovieImage(img: ImageView) : AsyncTask<String, Void, Bitmap>() {
        val weakImg = WeakReference<ImageView>(img)
        override fun doInBackground(vararg params: String?): Bitmap {
            val result = MyUtility.downloadImageusingHTTPGetRequest(params[0]!!)
            return result!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            val img = weakImg.get()
            if (img != null) {
                img.setImageBitmap(result)
            }
        }
    }*/
}