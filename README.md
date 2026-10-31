# Ønskeskyen

Miniprojekt på 2. semester – datamatiker, gruppe 6.

Vi har lavet en webapp, hvor man kan oprette ønskesedler og tilføje, redigere og slette ønsker.

Projektet bruger **Java 21, Spring Boot, Thymeleaf og MySQL**. Maven bruges til at bygge og teste projektet.

## Kom i gang

Du skal have Java 21, IntelliJ IDEA og MySQL installeret.

1. Hent projektet fra GitHub, og åbn det i IntelliJ.
2. Vent på, at Maven henter projektets dependencies.
3. Start MySQL.

## Opret databasen

SQL-filen ligger i `src/main/resources/db/setup.sql`.

1. Åbn MySQL Workbench med en bruger, der må oprette databaser og tabeller.
2. Åbn [setup.sql](src/main/resources/db/setup.sql), og kør hele filen med lyn-knappen.
3. Filen opretter databasen, tabellerne og to eksempelønskesedler med fem ønsker.

Hvis databasen allerede indeholder ønskesedler, bevares de, og der indsættes ikke nye eksempeldata. Filen ændrer ikke på eksisterende tabellers struktur.

MySQL-brugeren, som appen bruger, skal have adgang til databasen. Kontakt underviseren, hvis brugeren mangler rettigheder.

## Database i IntelliJ

`src/main/resources/application.properties` angiver forbindelsen til den lokale MySQL-database **direkte**. Den nuværende version bruger ikke `DB_USERNAME` eller `DB_PASSWORD` som miljøvariabler. Filen indeholder:

- URL: `jdbc:mysql://localhost:3306/projekt2_gruppe_6_db`
- Et lokalt MySQL-brugernavn og en adgangskode
- `spring.sql.init.mode=never` (databasen oprettes manuelt med `setup.sql`)

Brug den lokale MySQL-bruger, som projektet er sat op med, og sørg for, at den har de nødvendige rettigheder til `projekt2_gruppe_6_db`. Hvis du kører projektet på en anden computer, skal oplysningerne passe til MySQL-installationen på den computer.

**Bemærk ved offentlig GitHub-aflevering:** Fordi loginoplysninger står direkte i en versioneret konfigurationsfil, skal kontoen være en isoleret lokal projekt-/testbruger uden adgang til andre systemer. Genbrug ikke adgangskoden andre steder, og medtag ikke loginoplysninger i screenshots eller rapporter.

## Start appen

1. Vælg `Projekt2Gruppe6Application` i IntelliJ.
2. Klik på den grønne **Run**-knap.
3. Åbn [forsiden](http://localhost:8080/).
4. Åbn [Mine ønskesedler](http://localhost:8080/wishlists).

Hvis ønskesedlerne vises, kan appen hente data fra MySQL. Hvis du får en databasefejl, så kontrollér, at MySQL kører, og at forbindelsesoplysningerne i `application.properties` stemmer med din lokale MySQL-installation.

## Test

Projektets primære dokumenterede funktionstest er **manuel test i browseren og kontrol i MySQL Workbench**. Repositoryet indeholder desuden en eksisterende `WishListControllerTest.java` med JUnit/MockMvc; den er ikke grundlaget for gruppens manuelle testresultater.

Du kan kontrollere Maven-build og de eksisterende automatiske tests fra terminalen i IntelliJ:

```powershell
.\mvnw.cmd clean verify
```

Kontrollér, om der står `BUILD SUCCESS`. En bestået Maven-build dokumenterer ikke i sig selv, at alle brugerflows virker i browseren.

Test derefter funktionerne i browseren: opret, vis, rediger og slet både ønskesedler og ønsker. Brug [vores manuelle testskema](docs/manuel-test.md) til at skrive resultaterne ned.

## GitHub

Når vi laver en ændring, arbejder vi på en branch, committer, pusher og opretter en Pull Request til `main`.

GitHub Actions bygger projektet med Java 21 og Maven, når vi opretter en Pull Request eller pusher til `main`. Se [workflowet](.github/workflows/tests.yml).

En bestået build erstatter ikke manuel test i browseren.

## Diagrammer

Vi har samlet vores diagrammer og forklaringer i [docs/diagrammer.md](docs/diagrammer.md).
