package book_lending.book_lending.models

import org.springframework.data.jpa.repository.JpaRepository

interface ReaderRepository : JpaRepository<Reader, Int>
