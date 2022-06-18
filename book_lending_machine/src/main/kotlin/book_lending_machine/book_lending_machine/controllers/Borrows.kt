package book_lending_machine.book_lending_machine.controllers

import book_lending_machine.book_lending_machine.models.Borrow
import book_lending_machine.book_lending_machine.models.ReaderRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import javax.annotation.Resource

@RequestMapping("/borrow")
class Borrows : Cors {
    @Resource
    lateinit var readerRepository: ReaderRepository

    @GetMapping("/{readerId}")
    fun getBorrowsByIdWithTimeout(@PathVariable("readerId") readerId: Int): ResponseEntity<Map<String, Int>> {
        val borrow = Borrow()
        val reader = readerRepository.getReferenceById(readerId)
        val count: Int = borrow.countTimeoutByReaderId(reader)
        val responseMap: Map<String, Int> = mapOf("count" to count)
        return ResponseEntity.status(200).body(responseMap)
    }
}
