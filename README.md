# Projekt2_Gruppe_6
Digital ønskeseddel – miniprojekt på 2. semester, gruppe 6.

## Projektbeskrivelse

Ønskeskyen er en Spring Boot-applikation, hvor brugeren kan oprette og administrere ønskelister og ønsker.

Denne README beskriver, hvordan databasen gendannes, og hvordan applikationen startes på en ny computer (target-maskinen).

## Krav

- Java 21
- IntelliJ IDEA
- MySQL Server og MySQL Workbench.
- Projektets kildekode fra GitHub.

Projektet bruger Spring Boot og Maven. Maven Wrapper følger med.

## Hent projektet

Hent projektet fra GitHub.

Åbn projektmappen i IntelliJ IDEA.

Vent på, at Maven indlæser projektets afhængigheder.

## Gendan databasen og indlæs startdata

SQL-filerne ligger i `src/main/resources/templates/sql/`.

1. Start MySQL Server, og åbn forbindelsen i MySQL Workbench som root eller en anden konto med rettighed til at oprette databasen og tabellerne.
2. Vælg **File → Open SQL Script**, og åbn [`schema.sql`](src/main/resources/templates/sql/schema.sql).
3. Kør hele scriptet med lyn-knappen. Det opretter `projekt2_gruppe_6_db` samt tabellerne `wishlist` og `wish`. Scriptet opretter ikke brugeren F26A.
4. Ved en frisk installation: Åbn derefter [`data.sql`](src/main/resources/templates/sql/data.sql), og kør hele scriptet én gang på samme forbindelse.
5. Kontrollér, at Action Output ikke viser fejl. På en frisk database indsættes to ønskesedler og fem ønsker.

`data.sql` gemmer hver ny ønskeseddels ID med `LAST_INSERT_ID()` og bruger det til de tilhørende ønsker. Kør hele scriptet på samme forbindelse. Det kræver ikke, at listerne får ID 1 og 2, og eksisterende rækker ændres ikke.

**Kør startdata-scriptet én gang pr. installation.** Gentagen kørsel opretter yderligere to lister og fem ønsker. `schema.sql` tømmer ikke eksisterende tabeller og ændrer ikke deres struktur, hvis de allerede findes. Hvis `data.sql` stopper med en fejl, kør `ROLLBACK;` på samme forbindelse, ret fejlen, og prøv igen. En almindelig statement-fejl ruller ikke nødvendigvis hele transaktionen tilbage automatisk.

Kontrollér data i en ny query-fane:

```sql
SELECT id, title
FROM projekt2_gruppe_6_db.wishlist
ORDER BY id;

SELECT id, wishlist_id, name
FROM projekt2_gruppe_6_db.wish
ORDER BY id;
```

I `src/main/resources/application.properties` skal `spring.sql.init.mode=never` være sat. SQL-filerne køres manuelt i Workbench, ikke automatisk ved opstart.

## Rettigheder til brugeren F26A

Applikationen bruger MySQL-brugeren F26A med adgangskoden 1234.

Hvis F26A mangler de nødvendige rettigheder, skal en MySQL-administrator logge ind med root eller en anden konto med de nødvendige administratorrettigheder og køre:

GRANT SELECT, INSERT, UPDATE, DELETE
ON projekt2_gruppe_6_db.*
TO 'F26A'@'localhost';

Hvis brugeren F26A ikke allerede findes, skal den først oprettes med den aftalte adgangskode.

## Kontrollér databaseindstillingerne

Kontrollér filen src/main/resources/application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/projekt2_gruppe_6_db
spring.datasource.username=F26A
spring.datasource.password=1234

## Start applikationen

Find projektets hovedklasse med @SpringBootApplication i IntelliJ.

Start applikationen med Run.

Kontrollér, at Run-vinduet viser `Started Projekt2Gruppe6Application`, og at
Tomcat er startet på port 8080. Det viser, at Spring Boot er startet.

Forsiden på `http://localhost:8080/` er endnu ikke implementeret. Adressen
returnerer derfor HTTP 404. Det betyder ikke i sig selv, at opstarten fejlede.

Brugerfladen er under udvikling og er endnu ikke klar til brug. Visning af en
ønskeseddel kræver controlleren fra #27 og den tilhørende Thymeleaf-side.
Opstart alene bekræfter ikke, at databaseforespørgsler virker; se testene nedenfor.

## Test

Højreklik på `src/test/java` i IntelliJ, og vælg **Run 'All Tests'**.
Testene med `@SpringBootTest` kontrollerer også, at Spring-konteksten kan indlæses.

`WishListRepositoryTests` kontrollerer hentning af ønskesedler. Testene kræver,
at MySQL og projektdatabasen er klar, og at F26A må læse, indsætte og slette.
Testen henter det nye ID med `getGeneratedKeys()` og sletter kun sin egen
testrække i en finally-blok, også hvis en assertion fejler. Der bruges ikke
`@Transactional` til oprydningen.

`WishListRepositoryFailureTests` tester databasefejl og lukning af ressourcer
uden MySQL.

## Automatiske tests på GitHub

Workflowen [Maven tests](.github/workflows/tests.yml) bygger projektet og kører
alle Maven-tests ved pull requests til `main` og ved push til `main`.
Den kan også startes manuelt fra fanen **Actions → Maven tests → Run workflow**,
når workflowen er merged til standardbranchen.

GitHub bruger Java 21 og en separat MySQL 8.4-testdatabase. Workflowen indlæser
`src/main/resources/templates/sql/schema.sql`. Testene opretter selv deres data,
så `data.sql` indlæses ikke. Testdatabasen fjernes efter kørslen og har ingen
forbindelse til databasen på gruppens computere. Loginoplysningerne i workflowen
gælder kun den midlertidige testdatabase.

Resultatet vises på pull requesten og under **Actions**. Testrapporterne kan
hentes som `test-reports` på den enkelte kørsel og gemmes i syv dage.
Layout og brugeroplevelse kontrolleres stadig manuelt i browseren.

Workflowen ændrer ikke reglerne for merge. Når første kørsel består, kan holdet
vælge **Java 21 and MySQL tests** som krævet status check i reglerne for `main`.

## Diagrammer og design

Se [diagrammer og forklaring til holdet](docs/diagrammer.md) for klasse-, sekvens- og deployment-diagrammer samt links til wireframes og ER-diagram med Chen-notation.

