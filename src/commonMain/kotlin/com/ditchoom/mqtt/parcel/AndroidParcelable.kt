@file:OptIn(ExperimentalMultiplatform::class)

package com.ditchoom.mqtt.parcel

@OptionalExpectation
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
expect annotation class Parcelize()