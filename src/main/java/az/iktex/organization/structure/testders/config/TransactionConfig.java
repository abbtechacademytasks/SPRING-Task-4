package az.iktex.organization.structure.testders.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;

import javax.sql.DataSource;

@Configuration
public class TransactionConfig {
    @Bean
    @Primary
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public JdbcTransactionManager nestedTransactionManager(DataSource dataSource) {
        // Savepoint-lər yalnız bulk endpoint-in JDBC əməliyyatlarını idarə edir.
        return new JdbcTransactionManager(dataSource);
    }
}
