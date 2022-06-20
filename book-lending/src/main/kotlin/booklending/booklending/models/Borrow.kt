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
 * 借阅记录
 * @property id 借阅ID
 * @property reader 读者
 * @property book 图书
 * @property borrowTime 借阅时间
 * @property returnTime 归还时间
 */
data class Borrow(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @ManyToOne @JoinColumn(
        name = "reader_id", nullable = false
    ) var reader: Reader = Reader(),
    @ManyToOne @JoinColumn(
        name = "book_id",
        nullable = false
    ) var book: Book = Book(),
    @Column(nullable = false) var borrowTime: LocalDate = LocalDate.now(),
    @Column(nullable = true) var returnTime: LocalDate? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(
                other
            )
        ) return false
        other as Borrow

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
