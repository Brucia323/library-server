package book_lending_machine.book_lending_machine.models

import com.fasterxml.jackson.annotation.JsonIgnore
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
 * 读者
 * @property id 读者ID
 * @property name 姓名
 * @property mobile 手机号
 * @property passwordHash 哈希密码
 * @property deposit 押金
 * @property amount 额度
 * @property state 状态
 * @property appointments 预约记录
 * @property borrows 借阅记录
 */
data class Reader(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null,
    @Column(nullable = false) var name: String = "",
    @Column(nullable = false) var mobile: String = "",
    @Column(nullable = false) @JsonIgnore var passwordHash: String = "",
    @Column(nullable = false) var deposit: Double = 0.0,
    @Column(nullable = false) var amount: Double = 0.0,
    @Column(nullable = false) var state: Int = 0,
    @OneToMany(mappedBy = "reader") var appointments: Set<Appointment> = setOf(),
    @OneToMany(mappedBy = "reader") var borrows: Set<Borrow> = setOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(
                other
            )
        ) return false
        other as Reader

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }

}
