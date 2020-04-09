package com.apptest.nensalparc

import java.io.Serializable


data class Reservation(
    var name: String? = null,
    var address: String? = null,
    var imageUrl: String? = null,
    var placeID: String? = null,
    var reservationDate: String?=null,
    var reservationHour: String?=null,
    var reservedDate: String?=null,
    var reservedHour: String?=null,
    var duration: String?=null

): Serializable