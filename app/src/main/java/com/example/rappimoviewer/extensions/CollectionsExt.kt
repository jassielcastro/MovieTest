package com.example.rappimoviewer.extensions

fun <T> List<T>.toArrayList(): ArrayList<T> {
    val newArrayList = arrayListOf<T>()
    newArrayList.addAll(this)
    return newArrayList
}