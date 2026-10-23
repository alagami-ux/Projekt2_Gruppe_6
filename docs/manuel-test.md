# Manuel test

Brug fiktive testdata og en lokal database. Notér dato, tester og resultat.
"Ikke udført" betyder, at scenariet endnu ikke er dokumenteret i denne tabel.
Funktioner under udvikling kan først testes, når de er implementeret.

| Nr. | Handling | Forventet resultat | Faktisk resultat |
| --- | --- | --- | --- |
| 1 | Opret en ønskeseddel med en titel. | Listen gemmes i MySQL og vises med titlen. | Ikke udført |
| 2 | Forsøg oprettelse med tom titel og kun mellemrum. | Forståelig fejlbesked; ingen liste gemmes. | Ikke udført |
| 3 | Åbn en eksisterende ønskeseddel og derefter en tom liste. | Korrekt titel og ønsker; tom liste viser tom-beskeden. | Bestået 23-10-2026, aL. ID 1 viser ønsker; ID 13 viser tom-beskeden. |
| 4 | Åbn et ID, som SQL-opslaget bekræfter ikke findes. Prøv også manglende og ikke-numerisk ID. | Ukendt ID giver 404; manglende/ikke-numerisk ID afvises. | Bestået 23-10-2026, aL. ID 9 giver 404; abc og manglende ID giver 400. |
| 5 | Tilføj et ønske med navn, antal og pris; prøv også valgfrie tomme felter. | Ønsket gemmes på den valgte liste med korrekte oplysninger. Tom pris er ikke nulpris. | Ikke udført |
| 6 | Prøv tomt navn, navn med mellemrum, tomt antal, 0, negativt antal og negativ pris. | Ugyldigt input afvises med besked, og øvrige indtastninger bevares. | Ikke udført |
| 7 | Redigér et ønske og en ønskeseddels titel; prøv også annullering. | Gemte ændringer vises. Annullering bevarer tidligere data. Andre lister påvirkes ikke. | Ikke udført |
| 8 | Slet et ønske og en ønskeseddel; prøv bekræftelse og annullering. | Kun valgte data slettes. Sletning af liste sletter dens ønsker. Annullering bevarer data. | Ikke udført |
| 9 | Genstart applikationen, og åbn de gemte lister igen. | Data og ændringer er bevaret. | Bestået for eksisterende data 23-10-2026, aL. ID 1 og 13 viser samme indhold efter genstart. Redigering er ikke testet. |
| 10 | Prøv gyldigt, tomt og defekt billedlink. Åbn siden på mobil og desktop. | Billede eller gaveikon vises. Tekst og kort kan læses uden vandret rulning. | Delvist udført 23-10-2026, aL. Gaveikon vises, og mobilvisning er bestået. Gyldigt, tomt og defekt billedlink er ikke særskilt testet. |
| 11 | Brug hjemmesidens links og formular-knapper gennem et helt forløb. | Navigation fører til eksisterende sider; handlinger virker som beskrevet. | Ikke udført |
| 12 | På en lokal testdatabase: stop MySQL midlertidigt, og prøv at gemme. Start MySQL igen bagefter. | Ingen falsk bekræftelse. Forståelig fejlbesked og bevarede indtastninger. Kontrollér data før et nyt gemmeforsøg. | Ikke udført |
| 13 | Hent projektet på en anden computer og følg README. | Database og startdata kan gendannes, og applikationen kan startes og bruges. | Ikke udført |

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
