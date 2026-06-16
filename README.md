# blog-service 

Wykorzystane technologie
Warstwa aplikacji i logika biznesowa
Java 17 : Wykorzystanie stabilnej wersji LTS oraz nowoczesnych funkcji języka.

Spring Boot 3.3.5 : Główny framework odpowiedzialny za konfigurację aplikacji oraz wstrzykiwanie zależności.

Spring Boot Starter Web : Odpowiedzialny za budowanie punktów końcowych zgodnie z architekturą REST.

Spring Boot Starter Security : Zaawansowane zabezpieczenie endpointów oraz kontrola dostępu oparta na rolach użytkowników (USER, ADMIN).

Spring Boot Starter Validation : Deklaratywna walidacja danych wejściowych w warstwie obiektów DTO.

Zarządzanie danymi i bazą danych
Spring Data JPA / Hibernate ORM : Warstwa dostępu do danych i mapowanie obiektowo-relacyjne.

Hibernate Envers : Automatyczne wersjonowanie encji oraz pełne audytowanie zmian historycznych w bazie danych.

MariaDB : Relacyjna baza danych wykorzystywana na środowisku lokalnym i produkcyjnym.

Liquibase : Narzędzie do automatycznego zarządzania i wersjonowania schematu bazy danych z konfiguracją wtyczki maven-plugin do generowania plików różnicowych.

Testowanie i zapewnienie jakości
JUnit 5 oraz Mockito : Testowanie jednostkowe logiki biznesowej wraz z automatycznym wstrzykiwaniem atrap obiektów.

Baza danych H2 (In-Memory) : Lekka baza danych działająca w pamięci, dedykowana wyłącznie do izolowanych testów integracyjnych w celu zapewnienia ich powtarzalności.

Spring Security Test : Zaawansowane atrapowanie kontekstu bezpieczeństwa na potrzeby weryfikacji zabezpieczonych punktów końcowych.

AssertJ : Płynne i zaawansowane asercje obiektowe wykorzystywane do walidacji struktur JSON oraz precyzyjnego porównywania znaczników czasu z pomijaniem nanosekund.

Główne funkcje aplikacji
Precyzyjna weryfikacja uprawnień : Dedykowana warstwa walidacyjna w postaci komponentów sprawdzających uprawnienia, która rygorystycznie weryfikuje własność zasobów przed dokonaniem jakichkolwiek modyfikacji danych.

Zaawansowane testy integracyjne : Szerokie pokrycie kodem testowym przy użyciu MockMvc i AssertJ w celu weryfikacji operacji biznesowych, blokowania optymistycznego oraz złożonych struktur dokumentów rozliczeniowych.

Kontrola wersji bazy danych : W pełni zautomatyzowane zarządzanie strukturą tabel za pomocą narzędzia Liquibase, gwarantujące przewidywalność wdrożeń i spójność schematów pomiędzy środowiskami.

Historia zmian danych : Implementacja modułu Hibernate Envers umożliwiająca automatyczne śledzenie i zapisywanie pełnej historii modyfikacji krytycznych tabel systemowych.

Instrukcja uruchomienia lokalnego

Wymagania wstępne:
Zainstalowane środowisko Java 17 JDK

Zainstalowane narzędzie Maven

Uruchomiona lokalna instancja bazy danych MariaDB

Kroki do uruchomienia:
Skopiuj repozytorium kodu:
git clone https://github.com/StrzeleckiRR/blog-core-api.git

Utwórz pustą bazę danych w swojej instancji MariaDB o nazwie: blog

Zbuduj projekt i uruchom migracje bazy danych za pomocą Mavena:
mvn clean install

Uruchomienie aplikacji:
mvn spring-boot:run

Aplikacja zostanie uruchomiona lokalnie pod adresem: http://localhost:8080