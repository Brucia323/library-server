package booklending.booklending.utils

import booklending.booklending.models.Reader
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ReaderRepository : JpaRepository<Reader, Long>{
    @Query(nativeQuery = true, value = "select * from reader r limit ?1,1")
    fun findByLimit(randomNumber: Long): Reader
}
