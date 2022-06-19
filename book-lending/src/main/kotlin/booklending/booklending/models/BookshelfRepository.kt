package booklending.booklending.models

import org.springframework.data.jpa.repository.JpaRepository

interface BookshelfRepository : JpaRepository<Bookshelf, Int>
