package com.example.goodgun.openAPI

import com.google.gson.annotations.SerializedName

data class BarcodeList(
    @SerializedName("C005") val barcodeResult: BarcodeDto,
)

data class BarcodeDto(
    @SerializedName("total_count") val barcodeCount: String,
    @SerializedName("row") val barcodeDto: List<BarcodeItem>,
)
data class BarcodeItem(
    @SerializedName("PRDLST_NM") val productName: String,
)
