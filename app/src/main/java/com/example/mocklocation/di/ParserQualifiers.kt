package com.example.mocklocation.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CsvParser

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TvasParser