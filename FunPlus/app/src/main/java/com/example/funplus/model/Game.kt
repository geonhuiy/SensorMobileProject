package com.example.funplus.model

data class Game(val num1: Int, val num2: Int, val sign: String){
    override fun toString(): String {
        return "$num1 $sign $num2"
    }
}