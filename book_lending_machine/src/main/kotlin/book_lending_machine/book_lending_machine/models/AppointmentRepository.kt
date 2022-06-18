package book_lending_machine.book_lending_machine.models

import org.springframework.data.jpa.repository.JpaRepository

interface AppointmentRepository : JpaRepository<Appointment, Int> {
    fun findByReaderAndStateAndIsbn(
        reader: Reader,
        state: Int,
        isbn: String
    ): Appointment

    fun countByIsbnAndState(isbn: String, state: Int): Int

    fun countByIdLessThanEqual(id: Int): Int
}
