package booklending.booklending.utils

import booklending.booklending.models.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface BookRepository : JpaRepository<Book, Long> {
    fun countByIsbnAndState(isbn: String, state: Int): Int

    @Query(nativeQuery = true, value = "select * from book limit ?1,1")
    fun findByLimit(randomNumber: Long): Book
}
