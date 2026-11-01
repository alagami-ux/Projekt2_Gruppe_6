# Manuel test

Brug fiktive testdata og en lokal database. Notér dato, tester og resultat.
"Ikke udført" betyder, at scenariet endnu ikke er dokumenteret i denne tabel.
Funktioner under udvikling kan først testes, når de er implementeret.

| Nr. | Handling                                                                                        | Forventet resultat                                                                                                | Faktisk resultat                                                                                                                       |
|-----|-------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| 1   | Opret en ønskeseddel med en titel.                                                              | Listen gemmes i MySQL og vises med titlen.                                                                        | Ikke udført                                                                                                                            |
| 2   | Forsøg oprettelse med tom titel og kun mellemrum.                                               | Forståelig fejlbesked; ingen liste gemmes.                                                                        | Ikke udført                                                                                                                            |
| 3   | Åbn en eksisterende ønskeseddel og derefter en tom liste.                                       | Korrekt titel og ønsker; tom liste viser tom-beskeden.                                                            | Bestået 23-10-2026, aL. ID 1 viser ønsker; ID 13 viser tom-beskeden.                                                                   |
| 4   | Åbn et ID, som SQL-opslaget bekræfter ikke findes. Prøv også manglende og ikke-numerisk ID.     | Ukendt ID giver 404; manglende/ikke-numerisk ID afvises.                                                          | Bestået 23-10-2026, aL. ID 9 giver 404; abc og manglende ID giver 400.                                                                 |
| 5   | Tilføj et ønske med navn, antal og pris; prøv også valgfrie tomme felter.                       | Ønsket gemmes på den valgte liste med korrekte oplysninger. Tom pris er ikke nulpris.                             | Ikke udført                                                                                                                            |
| 6   | Prøv tomt navn, navn med mellemrum, tomt antal, 0, negativt antal og negativ pris.              | Ugyldigt input afvises med besked, og øvrige indtastninger bevares.                                               | Ikke udført                                                                                                                            |
| 7   | Redigér et ønske og en ønskeseddels titel; prøv også annullering.                               | Gemte ændringer vises. Annullering bevarer tidligere data. Andre lister påvirkes ikke.                            | Ikke udført                                                                                                                            |
| 8   | Slet et ønske og en ønskeseddel; prøv bekræftelse og annullering.                               | Kun valgte data slettes. Sletning af liste sletter dens ønsker. Annullering bevarer data.                         | Ikke udført                                                                                                                            |
| 9   | Genstart applikationen, og åbn de gemte lister igen.                                            | Data og ændringer er bevaret.                                                                                     | Bestået for eksisterende data 23-10-2026, aL. ID 1 og 13 viser samme indhold efter genstart. Redigering er ikke testet.                |
| 10  | Prøv gyldigt, tomt og defekt billedlink. Åbn siden på mobil og desktop.                         | Billede eller gaveikon vises. Tekst og kort kan læses uden vandret rulning.                                       | Delvist udført 23-10-2026, aL. Gaveikon vises, og mobilvisning er bestået. Gyldigt, tomt og defekt billedlink er ikke særskilt testet. |
| 11  | Brug hjemmesidens links og formular-knapper gennem et helt forløb.                              | Navigation fører til eksisterende sider; handlinger virker som beskrevet.                                         | Ikke udført                                                                                                                            |
| 12  | På en lokal testdatabase: stop MySQL midlertidigt, og prøv at gemme. Start MySQL igen bagefter. | Ingen falsk bekræftelse. Forståelig fejlbesked og bevarede indtastninger. Kontrollér data før et nyt gemmeforsøg. | Ikke udført                                                                                                                            |
| 13  | Hent projektet på en anden computer og følg README.                                             | Database og startdata kan gendannes, og applikationen kan startes og bruges.                                      | Ikke udført                                                                                                                            |

Ved fejl: Notér handling, forventet og faktisk resultat i den relevante GitHub-opgave.
Kontrollér med SELECT i Workbench, at de rigtige data er gemt.

## Udførte visningstests

Dato: 23-10-2026  
Tester: aL

| Test | Handling | Faktisk resultat | Status |
| --- | --- | --- | --- |
| Vis ønskeseddel | Åbn `/showWishList?id=1` | Titel, ønsker, beskrivelser, antal og priser vises | Bestået |
| Ukendt ID | Åbn `/showWishList?id=9` | HTTP 404 – Not Found | Bestået |
| Ugyldigt ID | Åbn `/showWishList?id=abc` | HTTP 400 – Bad Request | Bestået |
| Manglende ID | Åbn `/showWishList` | HTTP 400 – Bad Request | Bestået |
| Tom ønskeseddel | Åbn `/showWishList?id=13` | Titlen “Tom testliste” og beskeden “Du har endnu ikke tilføjet nogen ønsker.” vises | Bestået |
| Data efter genstart | Genstart appen, og åbn ID 1 og 13 igen | Begge viser samme indhold som før genstart | Bestået |
| Mobilvisning | Åbn ønskesedlen i smal visning | Billeder placeres over teksten. Ingen vandret rulning | Bestået |

Resultaterne er baseret på aLs manuelle kontroller og skærmbilleder.
Oprettelse gennem formularer, redigering, sletning og øvrige funktioner er ikke testet her.

### #30 – Databasetest af sletning af ønskeseddel

Dato: 26-10-2026
Tester: aL
Resultat: Bestået

- Sletning af testlisten ramte 1 række.
- Testlistens tilknyttede ønske blev slettet automatisk.
- Den anden liste og dens ønske blev bevaret.
- Gentagen sletning ramte 0 rækker.
- Testændringer blev rullet tilbage med ROLLBACK.

Testen blev udført direkte i MySQL Workbench.
Java-metoden og controllerforløbet er endnu ikke funktionstestet.

## #55 – Manuel test af titelredigering

Dato: 26. oktober 2026
Miljø: Lokal applikation startet fra IntelliJ.
Resultater rapporteret af testeren.

| Test | Resultat |
|---|---|
| Formularen viser den nuværende titel | Bestået |
| Tom titel afvises med dansk fejlbesked | Bestået |
| Titel med kun mellemrum afvises | Bestået |
| Annuller bevarer den oprindelige titel | Bestået |
| Gyldig ny titel vises på ønskesedlen og i oversigten | Bestået |
| Gem uden ændringer giver ingen fejl | Bestået |
| Titel på præcis 150 tegn accepteres | Bestået |
| Listens ID, ønsker, antal og priser bevares | Bestået |
| En anden ønskeseddel påvirkes ikke | Bestået |
| Den nye titel bevares efter genstart | Bestået |
| Ukendt liste giver dansk fejlbesked og tilbage-link | Bestået |

### Mangler at blive verificeret

- Serveren afviser 151 tegn uden databaseændring.
- Databasefejl giver en dansk besked og bevarer indtastningen.
- Gennemgående integrationstest mod rigtig MySQL af Java-repository-kald (ikke dækket af de eksisterende mock-tests).

#55: Manuelle titeltests og tidligere registrerede automatiske controller-tests er bestået. Integrationstest ved faktisk databasefejl og direkte Java-repository-kald mod MySQL er ikke dokumenteret.

### Eksisterende automatiske controllertests – #55

26. oktober 2026: `.\mvnw.cmd -U test`
    Resultat: 9 tests bestået, 0 failures, 0 errors, 0 skipped.

Disse tests findes i kodebasens `WishListControllerTest.java`. De er **ikke gruppens primære manuelle testmetode**. Den registrerede kørsel 26. oktober kontrollerede titelvalidering, grænsen på 150/151 tegn,
uændret titel, ukendt liste og simulerede databasefejl.
Repositoryet er mocket; rigtig MySQL testes ikke her.

### SQL-test – #55

26. oktober 2026, MySQL Workbench.

- Titelændring på testliste A: Bestået.
- Testliste B forbliver uændret: Bestået.
- Ønskets ID, liste-ID, navn, antal og pris bevares: Bestået.

Alle tre SQL-kontroller returnerede true.
Testen kører SQL direkte; Java-repository-metoden testes ikke her.


## Opfølgning 31-10-2026 – teststatus efter kodeændringer

Denne opfølgning er adskilt fra de historiske testresultater i tabellen ovenfor. Tidligere
"Ikke udført"-markeringer er bevaret, så de oprindelige registreringer ikke overskrives.

### Testpunkt 1 – Brugerflow og validering

Tester har 31-10-2026 oplyst, at den samlede gennemgang af **test nr. 1, 2, 5, 6, 7 og 8**
er udført: oprettelse, ugyldigt input, redigering, annullering og sletning af ønskesedler
og ønsker. **Resultat, konkrete input, testdata og skærmbilleder er ikke indført for hvert
enkelt scenarie.** Status: *Udført ifølge tester – detaljeret resultat afventer registrering*.
Dette afsnit dokumenterer ikke i sig selv, at samtlige delscenarier bestod.

### Testpunkt 2 – Ugyldigt antal

Tester har 31-10-2026 oplyst, at test af ugyldigt antal, herunder meget stort heltal,
er udført i både **Opret ønske** og **Rediger ønske**.
Koden begrænser antal til 1–999 og har danske `typeMismatch`-meddelelser.
**Faktisk viste fejltekst og eventuelle afvigelser mangler at blive noteret**.
Status: *Udført ifølge tester – detaljeret resultat afventer registrering*.

### Testpunkt 3 – Forsidens billedkort

Tester har 31-10-2026 oplyst, at forsidens to billedkort er testet og bestået:
**Saml dine ønsker** (venstre) og **Hold overblik** (højre). De to billeder
vises på desktop i samme størrelse med bevaret tekst. Tester har desuden
oplyst, at navigation og mobilvisning er testet.
Status: *Bestået ifølge tester*. Browser- og mobiltesten er ikke gentaget her.

### Testpunkt 4 – Fejl ved sletning og databaseadgang

**Ikke manuelt verificeret.** Kodegennemgang 31-10-2026 peger på, at
`WishController.confirmDeleteWish` og `WishController.deleteWish` i nogle
fejlgrene returnerer `showWishList` uden konsekvent at tilføje både
`wishlist` og `wishes` til modellen. Det bør testes, fordi Thymeleaf
ellers kan mangle data ved rendering.

Reproducerbare testtrin på lokal testdatabase:

1. Opret en testønskeseddel med ét ønske. Notér id for begge.
2. Slet ønsket, og prøv derefter bekræftelses-URL'en med det slettede id og korrekt wishlistId.
   Forventet: forståelig dansk besked, ingen sletning af andre data og ingen serverfejl.
3. Prøv `/confirmDeleteWish?id=<ukendt>&wishlistId=<eksisterende>`.
   Forventet: fejlbesked og mulighed for at komme tilbage.
4. På en isoleret testdatabase: stop MySQL og prøv både slettebekræftelse og
   en gemmehandling; start derefter MySQL igen.
   Forventet: ingen falsk succes og en forståelig fejlvisning.
5. Kontrollér rækkerne i MySQL før/efter. Notér HTTP-status, faktisk skærmtekst,
   dato og tester.

Først når dette er udført, må punktet markeres Bestået.
