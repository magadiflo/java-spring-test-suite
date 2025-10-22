package dev.magadiflo.app.integration.controller;

import dev.magadiflo.app.constants.TestScripts;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Tag("integration3")
@ActiveProfiles("test")
@Sql(scripts = {TestScripts.CLEANUP_MYSQL, TestScripts.DATA_TEST}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerTestRestTemplateTest {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private DataSource dataSource;

    @Test
    void shouldUseMySQLDatabase() throws SQLException {
        String url = this.dataSource.getConnection().getMetaData().getURL();
        log.info("Usando base de datos: {}", url);
        assertThat(url).contains("mysql", "db_spring_rest_api_test");
    }
}
