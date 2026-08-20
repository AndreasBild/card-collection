package de.maulmann.cardcollection

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class CardCollectionApplication

fun main(args: Array<String>) {
	runApplication<CardCollectionApplication>(*args)
}

