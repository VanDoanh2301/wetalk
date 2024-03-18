package com.example.wetalk.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiOne    //port: 8080

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiTwo    //port: 8090

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiThree   //port: 8060

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiFour    //port: 8050

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiFive     //port: soketIO

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiSix    //port: 8040
