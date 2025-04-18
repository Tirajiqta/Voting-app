package com.example.android.db.orm

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Column(
    val name: String,
    val primaryKey: Boolean = false,
    val autoIncrement: Boolean = false
)