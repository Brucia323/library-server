package booklending.booklending.models

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface BorrowRepository : JpaRepository<Borrow, Int> {
    fun countByReaderAndReturnTimeIsNullAndBorrowTimeBefore(
        reader: Reader,
        borrowTime: LocalDateTime
    ): Int
}
