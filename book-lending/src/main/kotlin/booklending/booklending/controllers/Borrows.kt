package booklending.booklending.controllers

import booklending.booklending.models.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.annotation.Resource

@RequestMapping("/borrow")
class Borrows : Cors {

    @Resource
    lateinit var readerRepository: ReaderRepository

    @Resource
    lateinit var bookRepository: BookRepository

    @Resource
    lateinit var borrowRepository: BorrowRepository

    @GetMapping("/{readerId}")
    fun getBorrowsByReaderIdWithTimeout(@PathVariable("readerId") readerId: Int): ResponseEntity<Map<String, Int>> {
        val borrow = Borrow()
        val reader = readerRepository.getReferenceById(readerId)
        val count: Int = borrow.countTimeoutByReaderId(reader)
        return ResponseEntity.status(200).body(mapOf("count" to count))
    }

    @GetMapping("/eligibility")
    fun getBorrowingEligibility(@RequestBody body: Map<String, Any>): ResponseEntity<Map<String, Boolean>> {
        val bookId: Int = body["bookId"] as Int
        val book = bookRepository.getReferenceById(bookId)
        val reader = readerRepository.getReferenceById(body["readerId"] as Int)
        var appointment = Appointment()
        appointment = appointment.isAppointment(book.isbn, reader)
        val bookCount = book.countBookByIsbnWithOnShelf(book.isbn)
        if (appointment == null) {
            val appointmentCount = appointment.countAppointmentByIsbn(book.isbn)
            if (bookCount < appointmentCount) {
                return ResponseEntity
                    .status(200)
                    .body(mapOf("Eligibility" to false))
            }
            return ResponseEntity.status(200).body(mapOf("Eligibility" to true))
        }
        val appointmentRanking =
            appointment.calculateAppointmentRanking(appointment)
        if (bookCount < appointmentRanking) {
            return ResponseEntity.status(200)
                .body(mapOf("Eligibility" to false))
        }
        return ResponseEntity.status(200).body(mapOf("Eligibility" to true))
    }

    @PostMapping("/{readerId}")
    fun createBorrow(
        @PathVariable("readerId") readerId: Int,
        @RequestBody body: List<Int>
    ): ResponseEntity<Any> {
        val reader = readerRepository.getReferenceById(readerId)
        body.forEach {
            val bookId = it
            val book = bookRepository.getReferenceById(bookId)
            val borrow = Borrow(null, reader, book, LocalDateTime.now(), null)
            borrowRepository.save(borrow)
        }
        return ResponseEntity.status(201).build()
    }
}
