package book_lending.book_lending.models

import org.springframework.data.jpa.repository.JpaRepository

interface WarehouseRepository : JpaRepository<Warehouse, Int>
