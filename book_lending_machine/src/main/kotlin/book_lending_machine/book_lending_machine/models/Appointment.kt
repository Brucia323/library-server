package book_lending_machine.book_lending_machine.models

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
 * 预约
 * @property id 预约ID
 * @property reader 读者
 * @property isbn ISBN
 * @property state 状态
 * @property time 时间
 */
data class Appointment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null,
    @ManyToOne @JoinColumn(
        name = "reader_id",
        nullable = false
    ) var reader: Reader = Reader(),
    @Column(nullable = false) var isbn: String = "",
    // 预约中=1
    // 已借阅=2
    // 已取消=3
    @Column(nullable = false) var state: Int = 0,
    @Column(nullable = false) var time: LocalDateTime = LocalDateTime.now()
) {
    @Transient
    @Resource
    lateinit var appointmentRepository: AppointmentRepository

    fun isAppointment(isbn: String, reader: Reader): Appointment {
        return appointmentRepository.findByReaderAndStateAndIsbn(
            reader,
            1,
            isbn
        )
    }

    fun countAppointmentByIsbn(isbn: String): Int {
        return appointmentRepository.countByIsbnAndState(isbn, 1)
    }

    fun calculateAppointmentRanking(appointment: Appointment): Int {
        val id: Int = appointment.id!!
        return appointmentRepository.countByIdLessThanEqual(id)
    }

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
