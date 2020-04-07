package com.apptest.nensalparc

import java.io.Serializable


data class AreaInfoModel(
    val name: String? = null,
    val address: String? = null,
    val imageUrl: String? = null
): Serializable