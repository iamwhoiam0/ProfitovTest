package com.example.profitovtest.data.entities

data class Entry(
    val payload: Payload,
    val type: String
)

data class Payload(
    val text: String?,
    val url: String?
)