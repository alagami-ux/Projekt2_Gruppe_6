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

SQL-filerne ligger i `src/main/resources/db/`.

1. Start MySQL Server, og åbn forbindelsen i MySQL Workbench som root eller en anden konto med rettighed til at oprette databasen og tabellerne.
2. Vælg **File → Open SQL Script**, og åbn [`schema.sql`](src/main/resources/db/schema.sql).
3. Kør hele scriptet med lyn-knappen. Det opretter `projekt2_gruppe_6_db` samt tabellerne `wishlist` og `wish`. Scriptet opretter ikke brugeren F26A.
4. Ved en frisk installation: Åbn derefter [`data.sql`](src/main/resources/db/data.sql), og kør hele scriptet én gang på samme forbindelse.
5. Kontrollér, at Action Output ikke viser fejl. På en frisk database indsættes to ønskesedler og fem ønsker.

`data.sql` gemmer hver ny ønskeseddels ID med `LAST_INSERT_ID()` og bruger det til de tilhørende ønsker. Kør hele scriptet på samme forbindelse. Det kræver ikke, at listerne får ID 1 og 2, men det er vigtigt, at hele scriptet køres i samme forbindelse.

**Kør startdata-scriptet én gang pr. installation.** Gentagen kørsel opretter yderligere to lister og fem ønsker. `schema.sql` tømmer ikke eksisterende tabeller og ændrer ikke deres struktur.

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

Applikationen bruger MySQL-brugeren `F26A`. Adgangskoden til denne bruger ændres ikke af projektet og må ikke skrives i kildekoden, README eller commits.

Hvis F26A mangler de nødvendige rettigheder, skal en MySQL-administrator logge ind med root eller en anden konto med de nødvendige administratorrettigheder og køre:

GRANT SELECT, INSERT, UPDATE, DELETE
ON projekt2_gruppe_6_db.*
TO 'F26A'@'localhost';

Hvis brugeren `F26A` ikke allerede findes, skal databaseadministratoren oprette den og aftale adgangskoden med underviseren. Del adgangskoden separat fra GitHub.

## Kontrollér databaseindstillingerne

Kontrollér filen `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/projekt2_gruppe_6_db
spring.datasource.username=${DB_USERNAME:F26A}
spring.datasource.password=${DB_PASSWORD}
spring.sql.init.mode=never
```

**Opsætning i IntelliJ IDEA (også på underviserens computer):**

1. Åbn **Run → Edit Configurations…** og vælg Spring Boot-konfigurationen `Projekt2Gruppe6Application`.
2. Klik **Modify options → Environment variables**, hvis feltet ikke vises.
3. Tilføj `DB_USERNAME` med værdien `F26A` og `DB_PASSWORD` med den eksisterende adgangskode til denne MySQL-bruger.
4. Klik **Apply → OK**, og start eller genstart applikationen.
5. Kontrollér `http://localhost:8080/wishlists` for at bekræfte databaseadgang.

Lad **Store as project file** være umarkeret, så den lokale Run-konfiguration ikke deles ved et uheld. Miljøvariabler skal konfigureres på hver computer, der starter applikationen. Undlad at sende screenshots med adgangskoden synlig.

**Bemærk:** Adgangskoden kan stadig findes i tidligere Git-commits, selvom den er fjernet fra den aktuelle version. Den kan ikke betragtes som hemmelig, hvis den deles eller ligger i historikken.

## Start applikationen

Find projektets hovedklasse med @SpringBootApplication i IntelliJ.

Start applikationen med Run.

Kontrollér, at Run-vinduet viser `Started Projekt2Gruppe6Application`, og at
Tomcat er startet på port 8080. Det viser, at Spring Boot er startet.

Forsiden vises på `http://localhost:8080/`. Ønskeseddeloversigten findes på `http://localhost:8080/wishlists`.
En eksisterende ønskeseddel kan vises på
`http://localhost:8080/showWishList?id=DIT_ID`. Erstat `DIT_ID` med et
eksisterende ID fra SQL-opslaget ovenfor. Test også oprettelse, redigering og sletning af ønsker og ønskesedler.
Opstart alene bekræfter ikke, at databaseforespørgsler virker.

## Manuel test

Projektet bruger både manuelle funktionstests og JUnit/MockMvc-controller-tests. Testafhængighederne er deklareret i `pom.xml`.

Brug [den manuelle testtabel](docs/manuel-test.md). Udfyld resultatet, når
hver funktion er afprøvet. En funktion er ikke testet alene ved, at projektet
kan bygges eller Spring Boot starter.

## Automatisk bygning på GitHub

Workflowen [Maven build](.github/workflows/tests.yml) bygger projektet med
Java 21 ved pull requests til `main` og push til `main`.
Den kan også startes fra **Actions → Maven build → Run workflow**,
når ændringen er merged til standardbranchen.

Workflowen kører `bash mvnw --batch-mode --no-transfer-progress package`.
Den kompilerer og pakker applikationen uden at starte MySQL eller applikationen.
Maven `package` kører som udgangspunkt automatiske tests, når tests er til stede og ikke er fravalgt. Workflowen starter ikke selv MySQL eller browseren.

En grøn kørsel betyder, at projektet kunne bygges. Formularer, SQL, navigation
og layout skal stadig afprøves manuelt. Workflowen ændrer ikke merge-reglerne.

## Diagrammer og design

Se [diagrammer og forklaring til holdet](docs/diagrammer.md) for klasse-, sekvens- og deployment-diagrammer samt links til wireframes og ER-diagram med Chen-notation.
