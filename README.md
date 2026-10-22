# Projekt2_Gruppe_6
Digital ønskeseddel – miniprojekt på 2. semester, gruppe 6.

#" Projektbeskrivelse

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

## Gendan databasen

Åbn MySQL Workbench, og forbind til den lokale MySQL Server.

Åbn SQL-scriptet til databaseoprettelse i projektets sql-mappe.

Kør scriptet for at oprette databasen projekt2_gruppe_6_db og dens tabeller.

Kør eventuelt scriptet med testdata, hvis det ønskes.

Brug en MySQL-konto med de nødvendige rettigheder til at oprette databasen og tabellerne.

Rettigheder til brugeren F26A

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

Åbn browseren på:

http://localhost:8080

Applikationen er nu klar til brug.

## Test

Højreklik på Projekt2Gruppe6ApplicationTests under src/test/java,
og vælg Run.

Testen contextLoads kontrollerer, at Spring-konteksten kan indlæses.
Den bekræfter ikke i sig selv, at SQL-forespørgsler virker.

Kør også `WishListRepositoryTests` under `src/test/java`. Testene kræver,
at MySQL og projektdatabasen er klar, og at F26A må læse, indsætte og slette.
Testen henter det nye ID med `getGeneratedKeys()` og sletter kun sin egen
testrække i en finally-blok, også hvis en assertion fejler. Der bruges ikke
`@Transactional` til oprydningen.

`WishListRepositoryFailureTests` tester databasefejl og lukning af ressourcer
uden MySQL.

## Diagrammer og design

Se [diagrammer og forklaring til holdet](docs/diagrammer.md) for klasse-, sekvens- og deployment-diagrammer samt links til wireframes og ER-diagram med Chen-notation.

