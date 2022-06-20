package booklending.booklending.utils

import booklending.booklending.models.Administrator
import org.springframework.data.jpa.repository.JpaRepository

interface AdministratorRepository : JpaRepository<Administrator, Long>
