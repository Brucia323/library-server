package book_lending.book_lending.models

import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<Book, Int> {
    fun countByIsbnAndState(isbn: String, state: Int): Int
}
