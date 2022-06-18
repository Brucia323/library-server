package book_lending_machine.book_lending_machine

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class BookLendingMachineApplication

fun main(args: Array<String>) {
    runApplication<BookLendingMachineApplication>(*args)
}
