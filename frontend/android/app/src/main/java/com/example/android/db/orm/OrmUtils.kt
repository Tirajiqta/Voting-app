package com.example.android.db.orm

import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

class OrmUtils {
    companion object {
        @JvmStatic
        fun <T : Any> getTableName(clazz: KClass<T>): String {
            return clazz.findAnnotation<Table>()?.name ?: clazz.simpleName
            ?: throw IllegalArgumentException("No class name")
        }
    }
}