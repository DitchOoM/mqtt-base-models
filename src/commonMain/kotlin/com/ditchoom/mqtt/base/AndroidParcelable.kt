@file:OptIn(ExperimentalMultiplatform::class)

package com.ditchoom.mqtt.base

@OptionalExpectation
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
expect annotation class Parcelize()

expect interface Parcelable