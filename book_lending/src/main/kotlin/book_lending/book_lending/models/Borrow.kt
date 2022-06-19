package book_lending.book_lending.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.Hibernate
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime
import javax.annotation.Resource
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
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null,
    @ManyToOne @JoinColumn(
        name = "reader_id", nullable = false
    ) var reader: Reader = Reader(),
    @ManyToOne @JoinColumn(
        name = "book_id",
        nullable = false
    ) var book: Book = Book(),
    @Column(nullable = false) var borrowTime: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = true) var returnTime: LocalDateTime? = null
) {
    @Transient
    @Resource
    lateinit var borrowRepository: BorrowRepository

    fun countTimeoutByReaderId(reader: Reader): Int {
        var rule = Rule()
        rule = rule.getRuleByName("借阅时长") // TODO
        val duration = rule.value
        val now = LocalDateTime.now()
        val latestBorrowingTime = now.minusDays(duration.toLong())
        return borrowRepository.countByReaderAndReturnTimeIsNullAndBorrowTimeBefore(
            reader,
            latestBorrowingTime
        )
    }

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
