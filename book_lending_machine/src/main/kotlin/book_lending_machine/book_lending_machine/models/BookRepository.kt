package book_lending_machine.book_lending_machine.models

import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<Book, Int> {
    fun countByIsbnAndState(isbn: String, state: Int): Int
}
