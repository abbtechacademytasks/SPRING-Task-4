# Task21 bonus: NESTED və retry

## NESTED (+5)

```http
POST /test-demo/api/students/{id}/courses/bulk
Content-Type: application/json

{"courseIds":[8,9,10]}
```

BulkEnrollmentService bir JDBC REQUIRED tranzaksiyası açır. Hər kurs üçün ayrı
NestedEnrollmentService bean-inin proxy-si NESTED savepoint yaradır. Kurslar ID üzrə
sıralanır ki, bulk çağırışlar eyni sıra ilə row lock alsın.

Bu demo əvvəl student_course-a müvəqqəti INSERT edir, sonra tutumu yoxlayır.
Kurs doludursa CourseFullException atılır: nested proxy həmin INSERT-i savepoint-ə
qədər geri qaytarır. Outer service yalnız bu exception-ı tutur və növbəti kursa keçir.
Uğurlu kursların hamısı outer commit-də saxlanır; nested metodun qayıtması ayrıca commit deyil.

Olmayan kurs və ya başqa gözlənilməyən xəta ötürülür və bütün bulk tranzaksiyası geri alınır.
Təkrar, boş və müsbət olmayan courseIds 400 verir. Artıq yazılmış kurs 409 verir;
yalnız dolu kurslar skip edilir. Cavabdakı ID-lər artan sıradadır.

TransactionConfig-də iki manager var: default @Primary JpaTransactionManager əvvəlki
JPA metodları üçündür; nestedTransactionManager isə JdbcTransactionManager-dir və yalnız
bulk/nested metodlarında açıq seçilir. JdbcTemplate həmin JDBC connection-dan istifadə edir.
Bu qol managed JPA entity dəyişdirmir və başqa JPA tranzaksiyasının içindən çağırılmır.
JDBC savepoint JPA persistence context-dəki Java-obyektləri əvvəlki vəziyyətə qaytarmır.
[Spring JpaTransactionManager](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/orm/jpa/JpaTransactionManager.html),
[JdbcTransactionManager](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/support/JdbcTransactionManager.html).

Course sətri FOR UPDATE ilə lock olunur. Bu, həmin lock-a əməl edən bulk əməliyyatlarını
koordinasiya edir; köhnə isolation-demo endpoint-lərinin qəsdən göstərdiyi yarışları dəyişmir.
Student/Course membership collection-ları bu layihədə L2/query cache-də saxlanmır;
bulk-dan sonra yeni JPA GET real əlaqələri oxuyur. Bu üsulu cached collection olan başqa
layihəyə köçürəndə cache invalidation ayrıca həll edilməlidir.

## Retry (+5)

```http
POST /test-demo/api/isolation/employees/{id}/raise?amount=100&isolation=REPEATABLE_READ&delayMs=3000&retry=true
POST /test-demo/api/isolation/courses/{courseId}/enroll/{studentId}?isolation=SERIALIZABLE&delayMs=3000&retry=true
```

retry default false-dur: əvvəlki anomaliya demoları dəyişməyib. true variantında controller
IsolationRetryService-i çağırır. Onun NOT_SUPPORTED sərhədi JPA caller tranzaksiyasını
suspend edir; ayrı IsolationDemoService proxy-si hər cəhd üçün yeni REQUIRED tranzaksiya açır.

ConcurrencyFailureException həm metod zamanı, həm commit zamanı proxy-dən çıxa bilər.
Retry yalnız əvvəlki rollback bitəndən sonra başlayır. İlk cəhd daxil olmaqla maksimum
3 cəhddir, əlavə3retry deyil. Yenidən oxunur və biznes şərti təzədən yoxlanır.
3-cü konflikt də ötürülür və mövcud handler HTTP409 verir.

Yalnız müvəqqəti concurrency konfliktləri təkrarlanır. Course is full kimi biznes409,
validation400, notFound404 və constraint xətası kor-koranə təkrarlanmır. Salary retry üçün
RR, enrollment üçün SERIALIZABLE tələb olunur; digər isolation + retry=true 400 verir.
Bu, demo mexanizmidir: HTTP request-in şəbəkə xətasına görə yenidən göndərilməsi üçün
idempotency həlli deyil. D3 retry yeni yer yaratmır, dolu kursu görüb imtina edə bilər.

## Real nəticələr — 2 noyabr 2026

Yoxlamalar ayrıca school_task21_bonus_checks bazasında aparılıb. Mövcud migration faylları dəyişməyib.

| Yoxlama | Nəticə |
|---|---|
| Bulk: boş, dolu, boş kurs | 2 uğurlu əlaqə commit; dolu kursun INSERT-i savepoint ilə rollback |
| Bulk: uğurlu kursdan sonra olmayan kurs | HTTP404; bütün batch rollback, yeni əlaqə0 |
| İki paralel salary +100, retry=true | İki ilkin oxu təsdiqləndi; HTTP200/200; salary1200 |
| İki tələbə, capacity1, retry=true | İki ilkin count təsdiqləndi; HTTP200/409; yalnız1əlaqə |
| Gradle test / bootJar | 13 test, 0 failure, BUILD SUCCESSFUL |
| Əvvəlki Postman regression | 183 request / 244 assertion / 0 failure |
| Yeni bonus Postman | 20 request / 25 assertion / 0 failure |
| Əvvəlki isolation-demo.sh | 6/6 müqayisə keçdi; öz müvəqqəti məlumatları silindi |

[JUnit xülasəsi](junit-summary.json), [integration test XML/log](integration-results.xml),
[real paralel HTTP nəticələri və cleanup](bonus-http-results.json),
[transaction log çıxarışları](transaction-excerpts.log),
[bonus Postman log-u](postman-run.log), [regression](regression-run.log),
[isolation script](isolation-demo/summary.txt).

[Bonus Postman kolleksiyası](../../../postman/Task21-Bonus.postman_collection.json)
dev seed course1-in capacity1 və boş olmasını tələb edir. Yalnız yaratdığı tələbələri,
kursları və işçini silir; seed kursu silmir. Bu kolleksiya ardıcıldır; paralellik ayrıca
integration test və HTTP nəticələrində sübut olunub. Kolleksiyada fullCourseId dəyişdirilə bilər.

## Kodu birlikdə oxumaq üçün sıra

1. StudentController.enrollBulk: JSON-dan gələn courseIds və service çağırışı.
2. TransactionConfig: default JPA manager və ayrıca JDBC manager niyə var?
3. BulkEnrollmentService.enroll: validation, outer REQUIRED, sıralama və yalnız CourseFullException catch.
4. NestedEnrollmentService.enroll: proxy sərhədi, NESTED, müvəqqəti INSERT və exception.
5. JdbcEnrollmentRepository: SQL və eyni connection; burada save/dirty checking yoxdur.
6. IsolationDemoController: retry=false ilə əvvəlki yol, retry=true ilə yeni facade.
7. IsolationRetryService: NOT_SUPPORTED, Supplier, 3 cəhd, exception-un commit-dən sonra tutulması.
8. BonusIntegrationTests: savepoint, outer rollback və iki real paralel salary oxusu.
9. IsolationRetryServiceTests: 3 cəhddə dayanmaq, biznes konfliktini təkrarlamamaq, uğurdan sonra dayandırmaq.

Bu sıra istifadəçi ilə sonrakı sətir-sətir izah üçündür; hazır kod mövzunun artıq öyrənildiyini göstərmir.
