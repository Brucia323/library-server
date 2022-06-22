package booklending.booklending.utils

import booklending.booklending.models.Book
import booklending.booklending.models.Borrow
import booklending.booklending.models.Reader
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.lang.Nullable
import java.time.LocalDate

interface BorrowRepository : JpaRepository<Borrow, Long> {
    @Nullable
    fun countByReaderAndReturnTimeIsNullAndBorrowTimeBefore(
        reader: Reader, borrowTime: LocalDate
    ): Int

    fun findByBook(book: Book): Borrow

    @Query(nativeQuery = true, value = "select * from borrow b limit ?1,1")
    fun findByLimit(randomNumber: Long): Borrow
}
