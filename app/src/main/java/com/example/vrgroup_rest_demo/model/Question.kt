package com.example.vrgroup_rest_demo.model

data class Question(
    var choices: List<Choice>,
    var id: Int,
    var text: String,
    var type: Type
)