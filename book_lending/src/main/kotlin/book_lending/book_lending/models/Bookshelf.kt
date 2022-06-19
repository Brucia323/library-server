package book_lending.book_lending.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.Hibernate
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import javax.persistence.*

@Entity
@JsonIgnoreProperties(value = ["hibernateLazyInitializer"])
@DynamicInsert
@DynamicUpdate
/**
 * 书架
 * @property id 书架ID
 * @property name 名称
 * @property category 分类
 * @property area 位置
 * @property books 书
 */
data class Bookshelf(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null,
    @Column(nullable = false) var name: String = "",
    @ManyToOne @JoinColumn(
        name = "category_id",
        nullable = false
    ) var category: Category = Category(),
    @Column(nullable = false) var area: String = "",
    @OneToMany(mappedBy = "bookshelf") var books: Set<Book> = setOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(
                other
            )
        ) return false
        other as Bookshelf

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }

}
