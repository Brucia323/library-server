package booklending.booklending.controllers

import booklending.booklending.models.*
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

    @PostMapping("/eligibility")
    fun getBorrowingEligibility(@RequestBody body: Map<String, Long>): ResponseEntity<Map<String, Boolean>> {
        val bookId: Long = body["bookId"]!!
        val book = bookRepository.getReferenceById(bookId)
        if (book.state != 1) {
            return ResponseEntity.status(200)
                .body(mapOf("Eligibility" to false))
        }
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

    @PostMapping("/check-loan-amount/{readerId}")
    fun checkLoanAmount(
        @RequestBody bookList: List<Long>,
        @PathVariable("readerId") readerId: Long
    ): ResponseEntity<Map<String, Boolean>> {
        val reader = readerRepository.getReferenceById(readerId)
        var count = 0.0
        bookList.forEach {
            val bookId: Long = it
            val book = bookRepository.getReferenceById(bookId)
            count += book.price
        }
        if (count <= reader.amount) {
            return ResponseEntity.status(200).body(mapOf("result" to true))
        }
        return ResponseEntity.status(200).body(mapOf("result" to false))
    }

    @PostMapping("/{readerId}")
    fun createBorrow(
        @PathVariable("readerId") readerId: Long,
        @RequestBody bookList: List<Long>
    ): ResponseEntity<Any> {
        val reader = readerRepository.getReferenceById(readerId)
        var count = 0.0
        bookList.forEach {
            val bookId = it
            val book = bookRepository.getReferenceById(bookId)
            val appointment = appointmentRepository.findByReaderAndIsbn(
                reader.id!!,
                book.isbn
            )
            if (appointment != null) {
                appointment.state = 1
                appointmentRepository.save(appointment)
            }
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
    fun returnBook(@RequestBody borrowList: List<Long>): ResponseEntity<Any> {
        borrowList.forEach {
            val borrowId = it
            val borrow = borrowRepository.getReferenceById(borrowId)
            val book = bookRepository.getReferenceById(borrow.book.id!!)
            val appointment = appointmentRepository.findByIsbn(
                book.isbn
            )
            if (appointment != null) {
                appointment.state = 2
                appointmentRepository.save(appointment)
            }
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
        return appointmentRepository.findByReaderAndIsbn(
            reader.id!!,
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
        val rule = getRuleByName("borrow_max")
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

    @PostMapping("/get-book")
    fun getBook(@RequestBody bookIds: List<Long>): ResponseEntity<Map<String, List<Book>>> {
        val book = bookRepository.findAllById(bookIds)
        return ResponseEntity.status(200).body(mapOf("book" to book))
    }

    @PostMapping("/get-borrow")
    fun getBorrow(@RequestBody borrows: List<Long>): ResponseEntity<Map<String, MutableList<Borrow>>> {
        val borrowList = borrowRepository.findAllById((borrows))
        return ResponseEntity.status(200).body(mapOf("borrow" to borrowList))
    }

    //    以下代码为演示需要，上线前请删除

    @GetMapping("/random-reader")
    fun getRandomReader(): ResponseEntity<Map<String, Long?>> {
        val count = readerRepository.count()
        var randomNumber = (0..count).random()
        var reader = readerRepository.getReferenceById(randomNumber)
        while (reader.state != 1) {
            randomNumber = (0..count).random()
            reader = readerRepository.getReferenceById(randomNumber)
        }
        return ResponseEntity.status(200)
            .body(mapOf("readerId" to randomNumber))
    }

    @GetMapping("/random-book")
    fun getRandomBook(): ResponseEntity<Map<String, MutableList<Long>>> {
        val count = bookRepository.count()
        var randomNumber = (0..count).random()
        var book = bookRepository.findByLimit(randomNumber)
        while (book.state != 1) {
            randomNumber = (0..count).random()
            book = bookRepository.findByLimit(randomNumber)
        }
        val books = mutableListOf(book.id!!)
        val rand = (1..10).random()
        for (i in 1..rand) {
            book = bookRepository.findByLimit(randomNumber + i)
            if (book.state == 1) {
                books.add(book.id!!)
            }
        }
        return ResponseEntity.status(200).body(mapOf("book" to books))
    }

    @GetMapping("/random-borrow")
    fun getRandomBorrow(): ResponseEntity<Map<String, ArrayList<Long>>> {
        val count = borrowRepository.count()
        var randomNumber = (0..count).random()
        var borrow = borrowRepository.findByLimit(randomNumber)
        while (borrow.returnTime != null) {
            randomNumber = (0..count).random()
            borrow = borrowRepository.findByLimit(randomNumber)
        }
        val borrows = arrayListOf(randomNumber)
        val rand = (1..5).random()
        for (i in 1..rand) {
            borrow = borrowRepository.findByLimit(randomNumber + i)
            if (borrow.returnTime == null) {
                borrows.add(randomNumber + i)
            }
        }
        return ResponseEntity.status(200).body(mapOf("borrow" to borrows))
    }
}
