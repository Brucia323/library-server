package booklending.booklending.utils

import booklending.booklending.models.Appointment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.lang.Nullable

interface AppointmentRepository : JpaRepository<Appointment, Long> {
    @Nullable
    @Query(
        value = "select * from Appointment where reader_id = ?1 and isbn = ?2 and state = 2 order by time limit 1",
        nativeQuery = true
    )
    fun findByReaderAndIsbn(
        readerId: Long,
        isbn: String
    ): Appointment

    @Nullable
    @Query(
        value = "select * from Appointment where isbn = ?1 and state = 3 order by time limit 1",
        nativeQuery = true
    )
    fun findByIsbn(
        isbn: String
    ): Appointment

    fun countByIsbnAndState(isbn: String, state: Int): Int

    fun countByIdLessThanEqual(id: Long): Int
}
