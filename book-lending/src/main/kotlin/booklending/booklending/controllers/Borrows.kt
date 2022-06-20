package booklending.booklending.controllers

import booklending.booklending.models.Appointment
import booklending.booklending.models.Borrow
import booklending.booklending.utils.BookRepository
import booklending.booklending.utils.BorrowRepository
import booklending.booklending.utils.ReaderRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
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
    fun getBorrowsByReaderIdWithTimeout(@PathVariable("readerId") readerId: Long): ResponseEntity<Map<String, Int>> {
        val borrow = Borrow()
        val reader = readerRepository.getReferenceById(readerId)
        val count: Int = borrow.countTimeoutByReaderId(reader)
        return ResponseEntity.status(200).body(mapOf("count" to count))
    }

    @GetMapping("/eligibility")
    fun getBorrowingEligibility(@RequestBody body: Map<String, Any>): ResponseEntity<Map<String, Boolean>> {
        val bookId: Long = body["bookId"] as Long
        val book = bookRepository.getReferenceById(bookId)
        val readerId = body["readerId"] as Long
        val reader = readerRepository.getReferenceById(readerId)
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

    @GetMapping("/check-loan-amount/{readerId}")
    fun checkLoanAmount(
        @RequestBody body: List<Long>,
        @PathVariable("readerId") readerId: Long
    ): ResponseEntity<Map<String, Boolean>> {
        val reader = readerRepository.getReferenceById(readerId)
        var count = 0.0
        body.forEach {
            val bookId: Long = it
            val book = bookRepository.getReferenceById(bookId)
            count += book.price
        }
        if (count < reader.amount) {
            return ResponseEntity.status(200).body(mapOf("result" to true))
        }
        return ResponseEntity.status(200).body(mapOf("result" to false))
    }

    @PostMapping("/{readerId}")
    fun createBorrow(
        @PathVariable("readerId") readerId: Long,
        @RequestBody body: List<Long>
    ): ResponseEntity<Any> {
        val reader = readerRepository.getReferenceById(readerId)
        var count = 0.0
        body.forEach {
            val bookId = it
            val book = bookRepository.getReferenceById(bookId)
            count += book.price
            val borrow = Borrow(null, reader, book, LocalDate.now(), null)
            borrowRepository.save(borrow)
            book.state = 2
            bookRepository.save(book)
        }
        reader.amount = reader.amount - count
        readerRepository.save(reader)
        return ResponseEntity.status(201).build()
    }

    @PutMapping("/return-book")
    fun returnBook(@RequestBody body: List<Long>): ResponseEntity<Any> {
        body.forEach {
            val borrowId = it
            val borrow = borrowRepository.getReferenceById(borrowId)
            borrow.reader.amount += borrow.book.price
            readerRepository.save(borrow.reader)
            borrow.book.state = 3
            bookRepository.save(borrow.book)
            borrow.returnTime = LocalDate.now()
            borrowRepository.save(borrow)
        }
        return ResponseEntity.status(200).build()
    }
}
