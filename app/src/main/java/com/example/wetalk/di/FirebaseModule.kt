package com.example.wetalk.di

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {
    @Provides
    fun provideContext(@ApplicationContext context: Context) : Context = context.applicationContext

    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat("HH:mm:ss")
            .setLenient()
            .create()
    }

    @Provides
    fun provideDataBaseInstance(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    fun provideDatabaseReference(db: FirebaseDatabase): DatabaseReference = db.reference
}