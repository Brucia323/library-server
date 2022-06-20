package booklending.booklending.utils

import booklending.booklending.models.Rule
import org.springframework.data.jpa.repository.JpaRepository

interface RuleRepository : JpaRepository<Rule, Long> {
    fun getByName(name: String): Rule
}
