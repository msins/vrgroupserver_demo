package com.example.vrgroup_rest_demo.model

import com.google.gson.annotations.SerializedName

data class GameDto(@SerializedName("scenarios") var scenarioDtos: List<ScenarioDto>)