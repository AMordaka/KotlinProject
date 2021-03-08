package com.mordaka.arkadiusz

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
internal class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}
