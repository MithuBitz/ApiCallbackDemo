package com.mibtech.myapplication

data class ResponseData(
    val message: String,
    val userId: Int,
    val name: String,
    val email: String,
    val mobile: Int,
    val profile_details: ProfileDetail,
    val data_list: List<DataListDetail>
)

data class ProfileDetail(
    val is_profile_completed: Boolean,
    val rating: Double
)

data class DataListDetail(
    val id: Int,
    val value: String
)