package book_lending.book_lending.models

import org.springframework.data.jpa.repository.JpaRepository

interface RuleRepository : JpaRepository<Rule, Int> {
    fun getByName(name: String): Rule
}
