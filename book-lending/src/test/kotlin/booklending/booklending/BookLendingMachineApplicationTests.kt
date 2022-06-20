package booklending.booklending

import booklending.booklending.models.Administrator
import booklending.booklending.models.Reader
import booklending.booklending.utils.AdministratorRepository
import booklending.booklending.utils.ReaderRepository
import net.datafaker.Faker
import org.apache.logging.log4j.kotlin.Logging
import org.junit.jupiter.api.Test
import org.mindrot.jbcrypt.BCrypt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import javax.annotation.Resource
import javax.sql.DataSource

@SpringBootTest
class BookLendingMachineApplicationTests : Logging {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Resource
    lateinit var administratorRepository: AdministratorRepository

    @Resource
    lateinit var readerRepository: ReaderRepository

    @Autowired
    lateinit var dataSource: DataSource

    @Test
    fun contextLoads() {
        createAdministrator()
        createReader()
    }

    fun createAdministrator() {
        val faker = Faker(Locale.CHINA)
        val list = mutableListOf<Administrator>()
        for (i in 1..10) {
            val administrator = Administrator()
            administrator.name = faker.name().fullName()
            administrator.mobile = faker.phoneNumber().cellPhone()
            administrator.passwordHash =
                BCrypt.hashpw("123456", BCrypt.gensalt(10))
            administrator.role = "admin"
            list.add(administrator)
        }
        val start = LocalDateTime.now()
        administratorRepository.saveAll(list)
        logger.info(Duration.between(start, LocalDateTime.now()))
    }

    fun createReader() {
        val faker = Faker(Locale.CHINA)
        val list = mutableListOf<Reader>()
        for (i in 1..1000) {
            val reader = Reader()
            reader.name = faker.name().fullName()
            reader.mobile = faker.phoneNumber().cellPhone()
            reader.passwordHash = BCrypt.hashpw("123456", BCrypt.gensalt(10))
            reader.deposit = faker.number().numberBetween(100, 1000)
            reader.amount = reader.deposit.toDouble()
            reader.state = 1
            list.add(reader)
        }
        val start = LocalDateTime.now()
        readerRepository.saveAll(list)
        logger.info(Duration.between(start, LocalDateTime.now()))
    }

}
