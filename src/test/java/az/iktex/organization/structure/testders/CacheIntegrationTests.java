package az.iktex.organization.structure.testders;

import az.iktex.organization.structure.testders.entity.Author;
import az.iktex.organization.structure.testders.entity.Book;
import az.iktex.organization.structure.testders.entity.Course;
import az.iktex.organization.structure.testders.repository.CourseRepository;
import az.iktex.organization.structure.testders.service.BookService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5432/school_task21_cache_checks",
        "spring.jpa.properties.hibernate.session.events.log=false"
})
class CacheIntegrationTests {

    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private BookService bookService;
    @PersistenceContext
    private EntityManager entityManager;

    private SessionFactory sessionFactory;
    private Statistics statistics;

    @BeforeEach
    void resetCache() {
        sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        statistics = sessionFactory.getStatistics();
        sessionFactory.getCache().evictAllRegions();
        statistics.clear();
    }

    @Test
    void findByIdUsesL2ButJoinFetchStillExecutesSql() {
        inTransaction(() -> entityManager.find(Course.class, 1L).getTitle());
        long statementsAfterFirst = statistics.getPrepareStatementCount();
        inTransaction(() -> entityManager.find(Course.class, 1L).getTitle());
        assertEquals(1, statementsAfterFirst);
        assertEquals(statementsAfterFirst, statistics.getPrepareStatementCount());
        assertEquals(1, statistics.getDomainDataRegionStatistics("courses").getHitCount());

        inTransaction(() -> courseRepository.findByIdWithStudents(1L).orElseThrow().getStudents().size());
        inTransaction(() -> courseRepository.findByIdWithStudents(1L).orElseThrow().getStudents().size());
        assertEquals(statementsAfterFirst + 2, statistics.getPrepareStatementCount());
        System.out.println("CACHE CHECK: findById SELECTs=1+0; JOIN FETCH SELECTs=1+1");
    }

    @Test
    void bothAuthorBookCollectionsAreCachedAcrossTransactions() {
        bookService.addAuthor(1L, 1L);
        try {
            sessionFactory.getCache().evictAllRegions();
            statistics.clear();
            inTransaction(this::readAuthorAndBookCollections);
            assertTrue(statistics.getDomainDataRegionStatistics("author-authorBooks").getPutCount() > 0);
            assertTrue(statistics.getDomainDataRegionStatistics("book-authorBooks").getPutCount() > 0);
            statistics.clear();
            inTransaction(this::readAuthorAndBookCollections);
            assertEquals(0, statistics.getPrepareStatementCount());
            assertTrue(statistics.getDomainDataRegionStatistics("author-authorBooks").getHitCount() > 0);
            assertTrue(statistics.getDomainDataRegionStatistics("book-authorBooks").getHitCount() > 0);
            System.out.println("CACHE CHECK: both AuthorBook collections hit; second transaction SELECTs=0");
        } finally {
            bookService.removeAuthor(1L, 1L);
        }
        inTransaction(() -> {
            assertTrue(entityManager.find(Author.class, 1L).getAuthorBooks().isEmpty());
            assertTrue(entityManager.find(Book.class, 1L).getAuthorBooks().isEmpty());
            return null;
        });
    }

    @Test
    void modifyingBulkUpdateEvictsCourseRegion() {
        int originalCredit = inTransaction(() -> entityManager.find(Course.class, 1L).getCredit());
        inTransaction(() -> entityManager.find(Course.class, 2L).getCredit());
        assertTrue(sessionFactory.getCache().containsEntity(Course.class, 1L));
        assertTrue(sessionFactory.getCache().containsEntity(Course.class, 2L));
        try {
            assertEquals(1, inTransaction(() -> courseRepository.updateCredit(1L, originalCredit + 1)));
            assertFalse(sessionFactory.getCache().containsEntity(Course.class, 1L));
            assertFalse(sessionFactory.getCache().containsEntity(Course.class, 2L));
            statistics.clear();
            assertEquals(originalCredit + 1,
                    inTransaction(() -> entityManager.find(Course.class, 1L).getCredit()));
            assertEquals(1, statistics.getDomainDataRegionStatistics("courses").getMissCount());
            System.out.println("CACHE CHECK: @Modifying evicted entire courses region, including untouched Course 2");
        } finally {
            inTransaction(() -> courseRepository.updateCredit(1L, originalCredit));
        }
    }

    @Test
    void nonstrictBookUpdateInvalidatesActualCachedEntity() {
        BigDecimal oldPrice = inTransaction(() -> entityManager.find(Book.class, 1L).getPrice());
        inTransaction(() -> entityManager.find(Book.class, 1L).getPrice());
        assertTrue(statistics.getDomainDataRegionStatistics("books").getHitCount() > 0);
        BigDecimal newPrice = oldPrice.add(new BigDecimal("1.00"));
        try {
            inTransaction(() -> {
                entityManager.find(Book.class, 1L).setPrice(newPrice);
                return null;
            });
            assertFalse(sessionFactory.getCache().containsEntity(Book.class, 1L));
            BigDecimal actual = inTransaction(() -> entityManager.find(Book.class, 1L).getPrice());
            assertEquals(0, newPrice.compareTo(actual));
            System.out.println("CACHE CHECK: NONSTRICT_READ_WRITE invalidated cached Book; new price returned");
        } finally {
            inTransaction(() -> {
                entityManager.find(Book.class, 1L).setPrice(oldPrice);
                return null;
            });
        }
    }

    private Integer readAuthorAndBookCollections() {
        Author author = entityManager.find(Author.class, 1L);
        Book book = entityManager.find(Book.class, 1L);
        assertEquals(1, author.getAuthorBooks().size());
        assertEquals(1, book.getAuthorBooks().size());
        author.getAuthorBooks().forEach(link -> assertNotNull(link.getBook().getTitle()));
        book.getAuthorBooks().forEach(link -> assertNotNull(link.getAuthor().getName()));
        return author.getAuthorBooks().size();
    }

    private <T> T inTransaction(Supplier<T> action) {
        return new TransactionTemplate(transactionManager).execute(status -> action.get());
    }
}
