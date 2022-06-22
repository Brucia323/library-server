package booklending.booklending.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.Hibernate
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@JsonIgnoreProperties(value = ["hibernateLazyInitializer"])
@DynamicInsert
@DynamicUpdate
/**
 * 预约
 * @property id 预约ID
 * @property reader 读者
 * @property isbn ISBN
 * @property state 状态
 * @property time 时间
 */
data class Appointment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @ManyToOne @JoinColumn(
        name = "reader_id",
        nullable = false
    ) var reader: Reader = Reader(),
    @Column(nullable = false) var isbn: String = "",
    // 预约成功=1
    // 待取书=2
    // 排队中=3
    // 预约取消=4
    @Column(nullable = false) var state: Int = 0,
    @Column(nullable = false) var time: LocalDateTime = LocalDateTime.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(
                other
            )
        ) return false
        other as Appointment

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
