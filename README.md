# Ønskeskyen

Miniprojekt på 2. semester – datamatiker, gruppe 6.

Vi har lavet en webapp, hvor man kan oprette ønskesedler og tilføje, redigere og slette ønsker.

Projektet bruger **Java 21, Spring Boot, Thymeleaf og MySQL**. Maven bruges til at bygge og teste projektet.

## Kom i gang

Projektet kræver JDK 21, IntelliJ IDEA og MySQL. Trinene og terminalkommandoerne nedenfor er til Windows.

1. Download og udpak projektet, eller klon det fra GitHub.
2. I IntelliJ: Vælg **File → Open**, og vælg projektets **pom.xml**.
3. Vælg **Open as Project**, hvis IntelliJ spørger.
4. Vælg **Trust Project**, hvis du bliver spurgt.
5. Vent, til Maven har hentet dependencies og indlæst projektet.

### Vælg JDK 21

1. Åbn **File → Project Structure → Project**.
2. Vælg **JDK 21** under **SDK**.
3. Vælg **SDK default** under **Language level**.
4. Klik **Apply → OK**.

Hvis JDK 21 ikke findes, vælg **Download JDK**, version **21** og fx **Eclipse Temurin**.

### Kontrollér buildet

Åbn terminalen i projektets rodmappe, hvor `pom.xml` ligger. Kontrollér først Java-versionen, som Maven bruger:

```powershell
.\mvnw.cmd -version
```

Outputtet skal vise Java 21. Terminalens Java-version kan være forskellig fra den SDK, der er valgt i IntelliJ.
Hvis den er forkert, skal `JAVA_HOME` pege på installationsmappen for JDK 21 (ikke dens `bin`-mappe).
Genstart derefter IntelliJ og kontrollér versionen igen.

Kør herefter:

```powershell
.\mvnw.cmd clean verify
```

Forventet resultat: **BUILD SUCCESS**. Følg derefter afsnittet **Opret databasen**, og start appen som beskrevet under **Start appen**. Sørg for, at MySQL kører.

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

## Fejlsøgning

### error: release version 5 not supported

Buildet forsøger at bruge Java 5, selv om projektet kræver Java 21.

Kontrollér, at `pom.xml` er åbnet som Maven-projekt, og at JDK 21 er valgt.
Åbn Maven-panelet, klik **Reload All Maven Projects**, og vent på indlæsningen.
Kontrollér eventuelle fejl fra Maven, hvis Spring-imports fortsat er røde.
Hvis Maven ikke kan hente en parent POM eller dependencies, skal den konkrete Maven-fejl løses først.

Hvis Maven-buildet lykkes, men IntelliJ-buildet fejler, kontrollér
**Settings → Build, Execution, Deployment → Compiler → Java Compiler**.
Projektets **Target bytecode version** skal være **21**.

### Failed to delete ... target\\classes\\application.properties

Stop alle kørende instanser af appen, og prøv `clean verify` igen.

Hvis fejlen fortsætter:

1. Luk IntelliJ helt.
2. Åbn projektets rodmappe i Stifinder.
3. Slet kun mappen **target**, som indeholder genererede buildfiler.
4. Åbn projektet igen, og kør `.\mvnw.cmd clean verify`.

Maven gendanner `target`. Slet ikke `src/main/resources/application.properties`.
