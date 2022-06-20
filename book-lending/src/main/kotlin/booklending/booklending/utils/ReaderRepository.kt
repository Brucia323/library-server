package booklending.booklending.utils

import booklending.booklending.models.Reader
import org.springframework.data.jpa.repository.JpaRepository

interface ReaderRepository : JpaRepository<Reader, Long>
