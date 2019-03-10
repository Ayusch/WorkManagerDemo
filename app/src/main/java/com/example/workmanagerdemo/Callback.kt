package com.example.workmanagerdemo

interface Callback<T> {
    fun onSuccess(t: T)
    fun onError(e: Throwable)
}