package booklending.booklending

import booklending.booklending.models.Reader
import booklending.booklending.models.ReaderRepository
import net.datafaker.Faker
import org.junit.jupiter.api.Test
import org.mindrot.jbcrypt.BCrypt
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import javax.annotation.Resource

@SpringBootTest
class BookLendingMachineApplicationTests {

    @Resource
    lateinit var readerRepository: ReaderRepository

    @Test
    fun contextLoads() {
        val faker = Faker(Locale.CHINA)
        val list = mutableListOf<Reader>()
        for (i in 1..1000) {
            val reader = Reader()
            reader.name = faker.name().fullName()
            reader.mobile = faker.phoneNumber().cellPhone()
            reader.passwordHash = BCrypt.hashpw("123456", BCrypt.gensalt(10))
            reader.deposit = faker.number().numberBetween(100,1000)
            reader.amount = reader.deposit.toDouble()
            reader.state = 1
            list.add(reader)
        }
        val start = LocalDateTime.now()
        readerRepository.saveAll(list)
        println(Duration.between(start, LocalDateTime.now()))
    }

}
