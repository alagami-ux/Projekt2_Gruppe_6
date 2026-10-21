# Projekt2_Gruppe_6
Digital ønskeseddel – miniprojekt på 2. semester, gruppe 6.

## Krav

- Java 21
- IntelliJ IDEA
- MySQL Server og MySQL Workbench

Projektet bruger Spring Boot og Maven. Maven Wrapper følger med.

## Hent projektet

Klon repositoryet:
https://github.com/alagami-ux/Projekt2_Gruppe_6

Åbn projektets pom.xml i IntelliJ som et Maven-projekt.
Vælg Java 21 som Project SDK, og lad Maven indlæse dependencies.

## Opret lokal database

1. Start MySQL Server.
2. Åbn MySQL Workbench, og forbind som root.
3. Åbn og kør hele src/main/resources/schema.sql.
4. Åbn og kør hele src/main/resources/data.sql én gang.

schema.sql opretter databasen projekt2_gruppe_6_db,
tabellerne wishlist og wish samt databasebrugeren F26A.

data.sql indsætter en ønskeseddel med tre ønsker.
Gentagen kørsel tilføjer flere testdata.

Root-adgangskoden er den lokale adgangskode fra installationen
af MySQL. Den skal ikke indsættes i projektets filer.

## Databaseforbindelse

src/main/resources/application.properties skal indeholde:

```properties
spring.application.name=Projekt2_Gruppe_6

spring.datasource.url=jdbc:mysql://localhost:3306/projekt2_gruppe_6_db
spring.datasource.username=F26A
spring.datasource.password=1234

spring.sql.init.mode=never
```

SQL-scripts køres manuelt i Workbench.
Spring Boot kører dem ikke automatisk ved opstart.

## Start applikationen

1. Sørg for, at MySQL Server kører.
2. Åbn Projekt2Gruppe6Application under src/main/java.
3. Klik på den grønne trekant ved main-metoden, og vælg Run.
4. Åbn http://localhost:8080 i browseren.

Opstarten er gennemført, når Run-vinduet viser:
Started Projekt2Gruppe6Application

Forsiden er endnu ikke implementeret. Derfor returnerer
adressen / indtil videre HTTP 404.

## Test

Højreklik på Projekt2Gruppe6ApplicationTests under src/test/java,
og vælg Run.

Testen contextLoads kontrollerer, at Spring-konteksten kan indlæses.
Den bekræfter ikke i sig selv, at SQL-forespørgsler virker.

## Diagrammer og design

Se [diagrammer og forklaring til holdet](docs/diagrammer.md) for klasse-, sekvens- og deployment-diagrammer samt links til wireframes og ER-diagram med Chen-notation.
