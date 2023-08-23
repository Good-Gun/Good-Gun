package com.example.goodgun.openAPI

import com.google.gson.annotations.SerializedName

data class BarcodeList(
    @SerializedName("I2570") val barcodeResult: BarcodeDto,
)

data class BarcodeDto(
    @SerializedName("total_count") val barcodeCount: String,
    @SerializedName("row") val barcodeDto: List<BarcodeItem>,
)
data class BarcodeItem(
    @SerializedName("PRDT_NM") val productName: String,
)
