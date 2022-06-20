package booklending.booklending.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.Hibernate
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDate
import javax.persistence.*

@Entity
@JsonIgnoreProperties(value = ["hibernateLazyInitializer"])
@DynamicInsert
@DynamicUpdate
/**
 * 图书
 * @property id 图书ID
 * @property name 书名
 * @property writer 作者
 * @property isbn ISBN
 * @property price 价格
 * @property publicTime 出版时间
 * @property publisher 出版社
 * @property state 状态
 * @property bookshelf 书架
 * @property warehouse 仓库
 * @property category 分类
 * @property coverUrl 封面url
 * @property info 更多信息
 * @property borrows 借阅记录
 */
data class Book(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @Column(nullable = false) var name: String = "",
    @Column(nullable = false) var writer: String = "",
    @Column(nullable = false) var isbn: String = "",
    @Column(nullable = false) var price: Double = 0.0,
    @Column(nullable = false) var publicTime: LocalDate = LocalDate.now(),
    @Column(nullable = false) var publisher: String = "",
    // 在架=1
    // 借出=2
    // 下架=3
    @Column(nullable = false) var state: Int = 0,
    @ManyToOne @JoinColumn(
        name = "bookshelf_id",
        nullable = true
    ) var bookshelf: Bookshelf = Bookshelf(),
    @ManyToOne @JoinColumn(
        name = "warehouse_id",
        nullable = true
    ) var warehouse: Warehouse = Warehouse(),
    @ManyToOne @JoinColumn(
        name = "category_id",
        nullable = false
    ) var category: Category = Category(),
    @Column(nullable = false) var coverUrl: String = "",
    @Column(nullable = true) var info: String = "",
    @OneToMany(mappedBy = "book") var borrows: Set<Borrow> = setOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(
                other
            )
        ) return false
        other as Book

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
