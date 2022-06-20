package booklending.booklending.utils

import booklending.booklending.models.Appointment
import booklending.booklending.models.Reader
import org.springframework.data.jpa.repository.JpaRepository

interface AppointmentRepository : JpaRepository<Appointment, Long> {
    fun findByReaderAndStateAndIsbn(
        reader: Reader,
        state: Int,
        isbn: String
    ): Appointment

    fun countByIsbnAndState(isbn: String, state: Int): Int

    fun countByIdLessThanEqual(id: Long): Int
}
