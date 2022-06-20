package booklending.booklending.utils

import booklending.booklending.models.Bookshelf
import org.springframework.data.jpa.repository.JpaRepository

interface BookshelfRepository : JpaRepository<Bookshelf, Long>
