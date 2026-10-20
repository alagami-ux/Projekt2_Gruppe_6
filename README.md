# Projekt2_Gruppe_6

Digital ønskeseddel – miniprojekt på 2. semester, gruppe 6.

## Status

Grundopsætning med Spring Boot, en dansk startside, JDBC/MySQL-konfiguration og SQL-scripts. US1–US8 er endnu ikke implementeret. Der er ikke login i grundopsætningen.

## Teknologi

- Java 17 eller nyere (projektet kompileres til Java 17).
- Spring Boot 4.1.1 og Maven Wrapper.
- Spring Web MVC, Thymeleaf, JDBC, MySQL Driver og Validation.
- MySQL 8.0.16 eller nyere, så CHECK-regler håndhæves.
- H2 bruges kun af den automatiske opstartstest; den erstatter ikke test mod MySQL.

## Åbn i IntelliJ

1. Klon repositoryet, eller hent branchen `setup/spring-boot` i en eksisterende klon.
2. Åbn `pom.xml` som projekt, eller højreklik på filen og vælg **Add as Maven Project**, hvis IntelliJ ikke registrerer Maven automatisk.
3. Vælg en installeret JDK 17 eller nyere, og lad Maven hente dependencies.

## Gendan databasen

Targetmaskinen forventes ifølge opgaven at have MySQL, MySQL Workbench og brugeren `F26A` med password `1234` installeret/oprettet.

1. Åbn MySQL Workbench med en bruger, der må oprette databasen og tabellerne (typisk root).
2. Åbn og kør hele `src/main/resources/schema.sql`.
3. Åbn og kør hele `src/main/resources/data.sql` **én gang** på den nye database. Gentagen kørsel tilføjer flere testlister; scriptet sletter ikke eksisterende data.
4. Kontrollér, at databasen `projekt2_gruppe_6_db` indeholder `wishlist` og `wish`.
5. Kontrollér, at `F26A` har læse- og skriverettigheder til databasen. En lokal administrator kan om nødvendigt køre følgende efter oprettelsen:

```sql
GRANT SELECT, INSERT, UPDATE, DELETE
ON projekt2_gruppe_6_db.* TO 'F26A'@'localhost';
```

Brugeren skal allerede findes. Hvis den mangler på udviklingsmaskinen, kan root oprette den med:

```sql
CREATE USER IF NOT EXISTS 'F26A'@'localhost' IDENTIFIED BY '1234';
```

Kør derefter GRANT-sætningen. CREATE USER ændrer ikke password på en bruger, der allerede findes.

`application.properties` indeholder databasenavn, URL og de obligatoriske demooplysninger som krævet i programmeringstillægget. Der bruges ikke miljøvariabler til databaseforbindelsen. SQL-scripts køres manuelt og ikke automatisk ved hver opstart.

## Start applikationen

Kør `Projekt2Gruppe6Application` i IntelliJ, eller brug IntelliJ-terminalen:

Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

macOS/Linux:

```sh
./mvnw spring-boot:run
```

Åbn <http://localhost:8080>. Port 8080 skal være ledig. Maven Wrapper henter Maven og dependencies ved første kørsel og kræver internetadgang.

## Test

Windows PowerShell:

```powershell
.\mvnw.cmd test
```

macOS/Linux:

```sh
./mvnw test
```

Testen starter webserveren på en ledig port og kontrollerer, at startsiden og CSS-filen kan hentes. Den bruger en isoleret H2-database og kræver ikke en lokal MySQL-server. SQL-scripts og brugerrettigheder skal desuden afprøves manuelt mod MySQL og på en anden maskine før aflevering.

## Videre arbejde

- Aftal datamodellen med gruppen. SQL-udkastet har `wishlist` og `wish`, inklusive `image_url` til billedlink.
- Implementér modeller, repositories og controllers for US1–US7.
- Implementér formularer, servervalidering og tests af acceptkriterierne.
- Implementér kun US8, når MVP'en er færdig og testet.
