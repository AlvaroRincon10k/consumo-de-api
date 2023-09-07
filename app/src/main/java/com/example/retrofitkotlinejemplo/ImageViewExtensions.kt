package com.example.retrofitkotlinejemplo

import android.widget.ImageView
import com.squareup.picasso.Picasso

    fun ImageView.fromUrl(url: String) {
        Picasso.get().load(url).into(this)

}