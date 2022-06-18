package book_lending_machine.book_lending_machine.models

import org.springframework.data.jpa.repository.JpaRepository

interface WarehouseRepository : JpaRepository<Warehouse, Int>
