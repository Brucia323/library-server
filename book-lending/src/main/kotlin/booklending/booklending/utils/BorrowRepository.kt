package booklending.booklending.utils

import booklending.booklending.models.Borrow
import booklending.booklending.models.Reader
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface BorrowRepository : JpaRepository<Borrow, Long> {
    fun countByReaderAndReturnTimeIsNullAndBorrowTimeBefore(
        reader: Reader, borrowTime: LocalDate
    ): Int
}
