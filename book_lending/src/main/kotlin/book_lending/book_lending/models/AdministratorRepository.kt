package book_lending.book_lending.models

import org.springframework.data.jpa.repository.JpaRepository

interface AdministratorRepository : JpaRepository<Administrator, Int>