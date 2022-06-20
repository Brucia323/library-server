package booklending.booklending.utils

import booklending.booklending.models.Book
import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<Book, Long> {
    fun countByIsbnAndState(isbn: String, state: Int): Int
}
