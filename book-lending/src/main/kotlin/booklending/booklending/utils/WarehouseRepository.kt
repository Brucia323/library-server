package booklending.booklending.utils

import booklending.booklending.models.Warehouse
import org.springframework.data.jpa.repository.JpaRepository

interface WarehouseRepository : JpaRepository<Warehouse, Long>
