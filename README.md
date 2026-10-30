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

SQL-filerne ligger i `src/main/resources/db/`.

1. Åbn MySQL Workbench med en bruger, der må oprette databaser og tabeller.
2. Kør [schema.sql](src/main/resources/db/schema.sql). Det opretter databasen `projekt2_gruppe_6_db` og tabellerne `wishlist` og `wish`.
3. Hvis databasen er ny, kan du køre [data.sql](src/main/resources/db/data.sql) **én gang** for at indsætte eksempeldata.

Hvis du kører `data.sql` igen, får du flere kopier af eksempeldata. `schema.sql` sletter ikke eksisterende data.

MySQL-brugeren skal have rettigheder til at læse, oprette, ændre og slette data i databasen. Kontakt en databaseadministrator, hvis brugeren mangler rettigheder.

## Database i IntelliJ

I `src/main/resources/application.properties` bruger vi miljøvariabler:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/projekt2_gruppe_6_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.sql.init.mode=never
```

Sådan sætter du dem op:

1. Gå til **Run → Edit Configurations**.
2. Vælg `Projekt2Gruppe6Application`.
3. Find **Environment variables**. Klik på **Modify options**, hvis feltet mangler.
4. Tilføj `DB_USERNAME` med din MySQL-bruger og `DB_PASSWORD` med brugerens adgangskode.
5. Klik **Apply** og **OK**.

Brug de MySQL-oplysninger, du har fået til projektet. Du skal **ikke ændre adgangskoden i MySQL**. Skriv heller ikke adgangskoden i README eller i et Git-commit. Lad **Store as project file** være umarkeret, så din lokale opsætning ikke deles.

## Start appen

1. Vælg `Projekt2Gruppe6Application` i IntelliJ.
2. Klik på den grønne **Run**-knap.
3. Åbn [forsiden](http://localhost:8080/).
4. Åbn [Mine ønskesedler](http://localhost:8080/wishlists).

Hvis ønskesedlerne vises, kan appen hente data fra MySQL. Hvis du får en databasefejl, så kontrollér, at MySQL kører, og at miljøvariablerne er korrekte.

## Test

Kør de automatiske tests fra terminalen i IntelliJ:

```powershell
.\mvnw.cmd clean verify
```

Kontrollér, at der står `BUILD SUCCESS`. Vi har også JUnit/MockMvc-tests i projektet.

Test derefter funktionerne i browseren: opret, vis, rediger og slet både ønskesedler og ønsker. Brug [vores manuelle testskema](docs/manuel-test.md) til at skrive resultaterne ned.

## GitHub

Når vi laver en ændring, arbejder vi på en branch, committer, pusher og opretter en Pull Request til `main`.

GitHub Actions bygger projektet med Java 21 og Maven, når vi opretter en Pull Request eller pusher til `main`. Se [workflowet](.github/workflows/tests.yml).

En bestået build erstatter ikke manuel test i browseren.

## Diagrammer

Vi har samlet vores diagrammer og forklaringer i [docs/diagrammer.md](docs/diagrammer.md).
