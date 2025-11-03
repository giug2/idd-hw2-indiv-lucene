# Sistema di Ricerca Testuale con Apache Lucene e Spring Boot
Progetto realizzato per il corso di **Ingegneria dei Dati** (Anno Accademico 2025/2026) dell'Università degli Studi Roma Tre.  

## Obiettivo del Progetto
L'obiettivo di questo progetto è sviluppare un sistema software in grado di creare, indicizzare e ricercare documenti testuali utilizzando **Apache Lucene**.  
Il sistema implementa una pipeline completa che include:
1.  **Generazione automatica di un dataset** di file di testo da Wikipedia.
2.  **Indicizzazione** dei documenti tramite Lucene.
3.  **Un'interfaccia web** (basata su Spring Boot e Thymeleaf) per la ricerca testuale.
4.  **Valutazione** delle prestazioni del motore di ricerca in termini di Precision e Recall.

## Tecnologie Utilizzate
* **Java 17**: Linguaggio principale per lo sviluppo del backend.
* **Apache Lucene (v10.3.1)**: Libreria core per l'indicizzazione e la ricerca full-text.
* **Spring Boot**: Framework per la creazione dell'applicazione web e la gestione dei controller.
* **Thymeleaf**: Motore di template per la generazione dinamica delle pagine HTML.
* **Python 3**: Utilizzato per gli script di generazione del dataset (con la libreria `wikipedia`) e per l'automazione dei test (con `requests` e `BeautifulSoup`).

## Prerequisiti
* Java 17 (o superiore)
* Python 3 
* Maven (per la gestione delle dipendenze Java)


  
``mvn spring-boot:run``

## Autori
[Gaglione Giulia](https://github.com/giug2) 
