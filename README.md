# assembly-planner-backend

## Beschreibung

Dies ist das Backend für unser Montageplanungs-Tool. Mit diesem können Kunden, Aufträge, Termine, Mitarbeiter und weitere Ressourcen verwaltet werden.
Als Programmiersprache wurde Java gewählt. Konkret wurde sich für das gängige Framework "Spring Boot" entschieden. Dies begründet sich darin,
dass diese Programmiersprache oftmals in Firmen am gängigsten ist und somit voraussichtlich am besten unterstützt wird.
Weiterhin ist die Fachkenntnis der Projektteilnehmer im Bereich Java am höchsten. Dadurch kann die Entwicklungszeit verringert werden.
Als Alternativen kamen die Nutzung von Spring Boot unter Kotlin, als auch die Entwicklung eines Microservices mittels der Sprache GoLang in Frage.

Die Verwendung von Kotlin würde einige wesentliche Vorteile mit sich bringen. Grundsätzlich ist nicht ausgeschlossen, dass in der Zukunft einige Teile
der Applikation mittels Kotlin geschrieben werden. Dies würde langfristig in höherer Entwicklungsgeschwindigkeit und besserer Wartbarkeit resultieren,
da Kotlin z.B. die Behandlung von null-Werten erheblich vereinfacht. Da Kotlin zu JVM-Bytecode kompiliert wird, ist eine Mischung der Technologien problemlos
möglich. Allerdings wurde sich für die jetztige Entwicklung gegen die Nutzung von Kotlin entschieden, da sich der Syntax beider Sprachen teilweise erheblich
unterscheidet und somit ein initial höherer Entwicklungsaufwand zu erwarten war.

Gegen die Nutzung von GoLang wurde sich ebenfalls aufgrund der höheren Entwicklungszeit entschieden. Im Bereich Golang gibt es bisher nur Microservice-Frameworks,
jedoch keine so allumfassenden Umgebungen wie Spring-Boot. Dies würde ebenfalls in höherem Entwicklungsaufwand und potenziell schlecherer Wartbarkeit münden.
Da GoLang eine relativ neue Sprache ist kann weiterhin davon ausgegangen werden, dass das Know-How zu dieser Sprache in vielen Firmen noch nicht ausreichend
ausgeprägt ist, was zu einer langsameren Verbreitung führen könnte.

## Running

Die Software wird zum gegebenen Zeitpunkt nicht als vollständig kompiliertes Projekt ausgeliefert.
Es gibt mehrere Varianten, wie das System derzeit gestartet werden kann:
1. Nutzung von IDEs: Die Applikation kann in allen gängigen Java-IDEs mit Maven-Unterstützung gestartet werden. Wir empfehlen die Verwendung von IntelliJ. Hierbei ist es ausreichend, die Main-Methode der Klasse `AssemblyplannerApplication` auszuführen. Im Normalfall sollten IDEs diese automatisch mit den korrekten Parametern starten.
2. Starten der Software mittels Maven: Das Backend kann mittels Command-Line gestartet werden. Hierzu wird der Befehl `./mvnw spring-boot:run` im Root-Directory des Repositories ausgeführt. Unter Umständen ist es erforderlich, zuvor die Dependencies der Applikation mittels `mvn clean install` zu laden.

## Tests

Für verschiedene Szenarien werden Unit-Tests bereitgestellt. Diese dienen dazu, kritische Funktionalitäten des Backends zu validieren und Fehler frühzeitig zu erkennen.
Wir nutzen Maven Surefire um sicherzustellen, dass der Maven-Kompilierungsvorgang abgebrochen wird, sobald Test-Failures festgestellt werden.
Unit-Tests können mittels `mvn test` ausgeführt werden.

## Konfiguration

Die Konfiguration wird mittels des Spring-Boot Konfigurationsfile `application.properties` im Ordner `resources` durchgeführt.
Diese Konfigurationsdatei wurde in der [Spring-Boot Dokumentation](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html) bereits ausführlich dokumentiert.
Weitere spezifische Konfigurationen können unter den jeweiligen Modul-Konfigurationen eingesehen werden.

## Datenbank

Die Applikation wird aktuell mit einer In-Memory-Datenbank (H2) ausgeliefert. Diese wird zu jedem Start der Applikation neu erstellt. 
Mittels der im Abschnitt "Konfiguration" beschriebenen konfigurationsoptionen kann jede beliebige Datenbank angebunden werden.
Hierzu muss allerdings die entsprechende Klasse bereitgestellt werden. Eine beispielhafte Konfiguration wird in der [Spring Boot Dokumentation](https://spring.io/guides/gs/accessing-data-mysql) beschrieben.
Zu Testzwecken empfehlen wir die Nutzung der vorkonfigurierten In-Memory database.

## Dokumentation

Die Applikation kommuniziert ausschließlich via REST. Alle REST-Endpunkte können in der von der Applikation selbst bereitgestellten Swagger-Dokumentation eingesehen werden.
Sobald die Applikation gestartet ist, kann die REST-Dokumentation unter `http://localhost:8080/swagger-ui/index.html` abgerufen werden.

## Login

Die Applikation erstellt beim Start automatisch einen Administrations-Nutzer. Die Login-Daten für diesen Nutzer sind:
- Passwort: admin
- Username: admin

Der Login mittels REST erfolgt durch Nutzung des REST-Endpunkts `/auth/login`.
Der folgende Payload kann zum Einloggen als Administrator genutzt werden:
```json
{
  "username": "admin",
  "password": "admin"
}
```
Die Response enthält sowohl einen Access-Token, als auch einen Refresh-Token. Der Access-Token wird als Authentifizierung genutzt. 
Er kann kopiert und anschließend als Bearer-Token genutzt werden. Hierzu ist ein Klick auf "Authorize" erforderlich. 
Nach eintragen des durch `/auth/login` erhaltenen JWT-Tokens kann die API in vollem Umfang genutzt werden.

Der Refresh-Token kann genutzt werden, um in einem bestimmten Zeitraum nach dem erstellen des Tokens Logins ohne die Eingabe von Passwörtern durchzuführen.
Hierzu kann der Endpunkt `/auth/refresh` genutzt werden. Der Refresh-Token wird übermittelt. 
Als Response erhält der Nutzer die Nutzer-ID, für den der Refresh-Token erstellt wurde, als auch einen neuen Access-Token.

## Voraussetzungen

Die Voraussetzungen für die Verwendung des Backends sind:
- Java 17+ (Tests durchgeführt unter Java 21)
- Maven (Tests durchgeführt unter Maven 3.9.5)
