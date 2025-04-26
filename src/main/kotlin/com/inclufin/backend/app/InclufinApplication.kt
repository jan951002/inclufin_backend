package com.inclufin.backend.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class InclufinApplication

fun main(args: Array<String>) {
    runApplication<InclufinApplication>(*args)
}
