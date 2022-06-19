package booklending.booklending.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import javax.persistence.*

@Entity
@JsonIgnoreProperties(value = ["hibernateLazyInitializer"])
@DynamicInsert
@DynamicUpdate
/**
 * 仓库
 * @property id 仓库ID
 * @property name 仓库名
 * @property books 图书
 */
data class Warehouse(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null,
    @Column(nullable = false) var name: String = "",
    @OneToMany(mappedBy = "warehouse") var books: Set<Book> = setOf()
) {

}
