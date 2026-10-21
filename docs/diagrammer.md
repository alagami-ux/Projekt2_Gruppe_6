# Diagrammer – Ønskeskyen

Diagrammerne beskriver den planlagte implementering. Klasser, controller-endpoints og fejlhåndtering skal implementeres og testes, før de kan betragtes som dokumentation af fungerende kode.

## Klassediagram

```mermaid
classDiagram
    class WishList {
        -int id
        -String title
        +getId() int
        +getTitle() String
        +setTitle(String title) void
    }
    class Wish {
        -int id
        -int wishlistId
        -String name
        -String description
        -int quantity
        -BigDecimal price
        -String productUrl
        -String imageUrl
        +getId() int
        +getWishlistId() int
        +getName() String
        +setName(String name) void
    }
    WishList "1" -- "0..*" Wish : indeholder
```

Getters og setters er forkortet. Hvert ønske tilhører præcis én ønskeseddel via `wishlistId`. En ønskeseddel kan indeholde nul eller flere ønsker. Repositoryet henter ønskerne, så `WishList` behøver ikke en `List<Wish>` i første version.

Pris bruger `BigDecimal`, som passer til MySQL-feltet `DECIMAL(10,2)`. Titel og navn skal være udfyldt, antal skal være positivt, og en angivet pris skal være mindst nul.

## Sekvensdiagram – Gem et ønske

```mermaid
sequenceDiagram
    actor U as Bruger
    participant B as Browser
    participant WC as WishController
    participant WR as WishRepository
    participant LC as WishListController
    participant LR as WishListRepository
    participant DB as MySQL

    U->>B: Trykker Gem
    B->>WC: POST /saveCreateWish
    WC->>WC: Valider med @Valid og BindingResult
    alt Ugyldigt input
        WC-->>B: Formular med fejl og indtastede værdier
        B-->>U: Vis fejlbeskeder
    else Gyldigt input
        WC->>WR: createWish(wish)
        WR->>DB: Parameteriseret INSERT via JdbcTemplate
        DB->>DB: Kontrollér constraints og generér ID
        DB-->>WR: Én række indsat
        WR-->>WC: Gemt
        WC-->>B: HTTP 302 til /showWishList?id=...
        B->>LC: GET /showWishList?id=...
        LC->>LR: getWishlistById(id)
        LR->>DB: SELECT ønskeseddel via JdbcTemplate
        DB-->>LR: Ønskesedlens id og title
        LR-->>LC: WishList
        LC->>WR: getWishesByWishlistId(id)
        WR->>DB: SELECT ønsker via JdbcTemplate
        DB-->>WR: Ønskernes oplysninger
        WR-->>LC: Liste af Wish
        LC->>LC: Tilføj ønskeseddel og ønsker til Model
        LC-->>B: Thymeleaf-side med titel og ønsker
        B-->>U: Vis opdateret ønskeseddel
    end
```

`JdbcTemplate` er samlet i repositoryernes databasekald for at holde hoveddiagrammet læsbart. Databasefejl vises separat nedenfor.

## Sekvensdiagram – Databasefejl under Gem

Foreslået håndtering: Controlleren håndterer `DataAccessException`, bevarer formularens værdier og viser en generel fejlbesked.

```mermaid
sequenceDiagram
    actor U as Bruger
    participant B as Browser
    participant C as WishController
    participant R as WishRepository
    participant J as JdbcTemplate
    participant DB as MySQL

    U->>B: Trykker Gem
    B->>C: POST /saveCreateWish
    C->>C: Input er gyldigt
    C->>R: createWish(wish)
    R->>J: update(SQL, parametre)
    J->>DB: Forsøg INSERT
    DB-->>J: Databasefejl
    J-->>R: DataAccessException
    R-->>C: DataAccessException
    C->>C: Log teknisk fejl og tilføj formularfejl
    C-->>B: Formular med bevarede værdier og fejlbesked
    B-->>U: Gemning kunne ikke bekræftes
```

Brugerbesked:

> Vi kunne ikke bekræfte, at ønsket blev gemt. Kontrollér ønskesedlen, før du prøver igen.

En mistet forbindelse kan i enkelte tilfælde betyde, at databasen har gemt ønsket, men at svaret er gået tabt. Derfor vises ingen falsk bekræftelse, og der foretages ikke automatisk et nyt forsøg. Fejl ved genindlæsning efter redirect og manglende ønskeseddel er ikke vist i disse diagrammer.

## Deployment – én target-maskine

```mermaid
flowchart TB
    subgraph Target["Target-maskine"]
        Browser["Browser"]
        subgraph Java["Java 21"]
            App["Spring Boot-applikation<br/>Indbygget Tomcat"]
        end
        DB[("MySQL Server<br/>projekt2_gruppe_6_db")]
        Workbench["MySQL Workbench"]
        Browser -->|"HTTP · port 8080"| App
        App -->|"JDBC · port 3306<br/>Bruger: F26A"| DB
        Workbench -.->|"Installation som root<br/>schema.sql og data.sql"| DB
    end
```

Dette er en deployment-visning med Mermaids flowchart-symboler. Alle dele kører på samme computer. Workbench bruges til opsætning; Spring Boot kommunikerer direkte med MySQL under drift. Installationen er beskrevet i [README](../README.md).

## Enkel forklaring til holdet

Når brugeren trykker **Gem**:

1. Formularen sender oplysningerne til controlleren.
2. Controlleren kontrollerer input. Ved fejl vises formularen igen med fejlbeskeder.
3. Repositoryet gemmer gyldige oplysninger i MySQL gennem `JdbcTemplate`.
4. Browseren sendes videre til ønskesedlen.
5. Applikationen henter titel og ønsker fra databasen og viser den opdaterede side.

Hvis databasen fejler, viser vi en forståelig fejlbesked og bevarer det indtastede. Vi fortæller ikke brugeren, at ønsket er gemt.

Vi har delt flowet i to sekvensdiagrammer, så det normale flow og databasefejl er lette at følge. Koden skal implementeres og testes, så den følger diagrammerne.

## Wireframes og ER-diagram

Begge findes på det samme tldraw-board:

- [Wireframes – desktop og mobil](https://www.tldraw.com/f/SC8uJKYfJs_BzxX0tho5A): Find skærmene til højre for de oprindelige diagrammer.
- [ER-diagram med Chen-notation](https://www.tldraw.com/f/SC8uJKYfJs_BzxX0tho5A): Find diagrammet med entiteter som rektangler, attributter som ovaler og relationen som en rombe.

Mermaid-filerne i GitHub er fremover reference for klasse-, sekvens- og deployment-diagrammer. Ældre versioner af klasse- og sekvensdiagrammer på tldraw-boardet kan være forældede.
