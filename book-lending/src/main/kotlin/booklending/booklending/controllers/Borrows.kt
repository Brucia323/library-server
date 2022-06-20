package booklending.booklending.controllers

import booklending.booklending.models.Appointment
import booklending.booklending.models.Borrow
import booklending.booklending.models.Reader
import booklending.booklending.models.Rule
import booklending.booklending.utils.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import javax.annotation.Resource

@CrossOrigin
@RestController
@RequestMapping("/borrow")
class Borrows {

    @Resource
    lateinit var readerRepository: ReaderRepository

    @Resource
    lateinit var bookRepository: BookRepository

    @Resource
    lateinit var borrowRepository: BorrowRepository

    @GetMapping("/timeout/{readerId}")
    fun getBorrowsByReaderIdWithTimeout(@PathVariable("readerId") readerId: Long): ResponseEntity<Map<String, Int>> {
        val reader: Reader = readerRepository.getReferenceById(readerId)
        val count: Int = countTimeoutByReaderId(reader)
        return ResponseEntity.status(200).body(mapOf("count" to count))
    }

    @GetMapping("/eligibility")
    fun getBorrowingEligibility(@RequestBody body: Map<String, Long>): ResponseEntity<Map<String, Boolean>> {
        val bookId: Long = body["bookId"]!!
        val book = bookRepository.getReferenceById(bookId)
        val readerId: Long = body["readerId"]!!
        val reader = readerRepository.getReferenceById(readerId)
        val appointment = isAppointment(book.isbn, reader)
        val bookCount = countBookByIsbnWithOnShelf(book.isbn)
        if (appointment == null) {
            val appointmentCount = countAppointmentByIsbn(book.isbn)
            if (bookCount < appointmentCount) {
                return ResponseEntity
                    .status(200)
                    .body(mapOf("Eligibility" to false))
            }
            return ResponseEntity.status(200).body(mapOf("Eligibility" to true))
        }
        val appointmentRanking = calculateAppointmentRanking(appointment)
        if (bookCount < appointmentRanking) {
            return ResponseEntity.status(200)
                .body(mapOf("Eligibility" to false))
        }
        return ResponseEntity.status(200).body(mapOf("Eligibility" to true))
    }

    @GetMapping("/check-loan-amount/{readerId}")
    fun checkLoanAmount(
        @RequestBody body: Map<String, List<Long>>,
        @PathVariable("readerId") readerId: Long
    ): ResponseEntity<Map<String, Boolean>> {
        val reader = readerRepository.getReferenceById(readerId)
        var count = 0.0
        val bookList = body["bookList"] ?: return ResponseEntity.status(400).build()
        bookList.forEach {
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
        @RequestBody body: Map<String, List<Long>>
    ): ResponseEntity<Any> {
        val reader = readerRepository.getReferenceById(readerId)
        var count = 0.0
        val bookList = body["bookList"] ?: return ResponseEntity.status(400).build()
        bookList.forEach {
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
    fun returnBook(@RequestBody body: Map<String, List<Long>>): ResponseEntity<Any> {
        val bookList = body["bookList"] ?: return ResponseEntity.status(400).build()
        bookList.forEach {
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

    @Resource
    lateinit var appointmentRepository: AppointmentRepository

    fun isAppointment(isbn: String, reader: Reader): Appointment? {
        return appointmentRepository.findByReaderAndStateAndIsbn(
            reader,
            1,
            isbn
        )
    }

    fun countAppointmentByIsbn(isbn: String): Int {
        return appointmentRepository.countByIsbnAndState(isbn, 1)
    }

    fun calculateAppointmentRanking(appointment: Appointment): Int {
        val id: Long = appointment.id!!
        return appointmentRepository.countByIdLessThanEqual(id)
    }

    fun countBookByIsbnWithOnShelf(isbn: String): Int {
        return bookRepository.countByIsbnAndState(isbn, 1)
    }

    fun countTimeoutByReaderId(reader: Reader): Int {
        val rule = getRuleByName("借阅时长") // TODO
        val duration = rule.value
        val now = LocalDate.now()
        val latestBorrowingTime = now.minusDays(duration.toLong())
        return borrowRepository.countByReaderAndReturnTimeIsNullAndBorrowTimeBefore(
            reader,
            latestBorrowingTime
        )
    }

    @Resource
    lateinit var ruleRepository: RuleRepository

    fun getRuleByName(name: String): Rule {
        return ruleRepository.getByName(name)
    }
}
