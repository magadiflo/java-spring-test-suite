package dev.magadiflo.app.integration.repository;

import dev.magadiflo.app.constants.TestScripts;
import dev.magadiflo.app.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles("test-h2")
@Sql(scripts = TestScripts.DATA_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DataJpaTest
class AccountRepositoryH2Test {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DataSource dataSource;

    @Test
    void shouldUseH2Database() throws SQLException {
        String url = this.dataSource.getConnection().getMetaData().getURL();
        log.info("Usando base de datos: {}", url);
        assertThat(url).contains("h2");
    }
}
