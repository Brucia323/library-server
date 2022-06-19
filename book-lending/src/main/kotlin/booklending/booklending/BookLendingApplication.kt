package booklending.booklending

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class BookLendingApplication

fun main(args: Array<String>) {
    runApplication<BookLendingApplication>(*args)
}
