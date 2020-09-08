package com.jakode.contacts.utils

import android.widget.ImageView
import com.jakode.contacts.R
import com.squareup.picasso.Picasso

object ImageSetter {
    fun set(url: String, target: ImageView) {
        Picasso.get().load(url).placeholder(R.drawable.cover_1).into(target)
    }
}