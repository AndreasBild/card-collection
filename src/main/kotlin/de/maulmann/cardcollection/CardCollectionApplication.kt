package de.maulmann.cardcollection

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CardCollectionApplication

fun main(args: Array<String>) {
	runApplication<CardCollectionApplication>(*args)
}

