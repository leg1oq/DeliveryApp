# Sistem de Livrare — JavaFX + SQLite


## Configurare baza de date

1. Creați o baza de date PostgreSQL:
```sql
CREATE DATABASE delivery_db;
```

2. Rulați scriptul SQL (din documentul primit) pentru a crea tabelele și insera datele.

3. Dacă username/password diferă de `postgres/postgres`, editați fișierul:
```
src/main/java/com/delivery/util/DatabaseConnection.java
```
Modificați:
```java
private static final String URL = "jdbc:postgresql://localhost:5432/delivery_db";
private static final String USER = "postgres";
private static final String PASSWORD = "postgres";
```

## Rulare

```bash
mvn javafx:run
```

Sau compilare și rulare manuală:
```bash
mvn clean package
java --module-path target/classes -m com.delivery/com.delivery.MainApp
```

## Structura proiectului

```
src/main/java/com/delivery/
├── MainApp.java                    # Punctul de intrare JavaFX
├── model/
│   ├── Entity.java                 # Clasa abstractă de bază (OOP)
│   ├── Persoana.java               # Clasă abstractă (moștenire)
│   ├── Client.java                 # Extinde Persoana
│   ├── Curier.java                 # Extinde Persoana
│   ├── Comanda.java                # Extinde Entity
│   └── StatusComanda.java          # Enum
├── dao/
│   ├── ClientDAO.java              # CRUD clienți
│   ├── CurierDAO.java              # CRUD curieri
│   └── ComandaDAO.java             # CRUD comenzi + rapoarte
├── ui/
│   ├── AlertHelper.java            # Utilitare dialogs
│   ├── ClientiTab.java             # Tab UI clienți
│   ├── CurieriTab.java             # Tab UI curieri
│   ├── ComenziTab.java             # Tab UI comenzi
│   └── RapoarteTab.java            # Tab rapoarte (3 rapoarte)
├── export/
│   └── ExportService.java          # Export CSV + TXT (implementează Exportable)
└── util/
    ├── DatabaseConnection.java     # Singleton conexiune BD
    ├── Validator.java              # Validări
    └── Exportable.java             # Interfață export
```

## Funcționalități

### CRUD
- **Clienți**: adăugare, vizualizare, editare, ștergere + căutare
- **Curieri**: adăugare, vizualizare, editare, ștergere + filtrare disponibilitate
- **Comenzi**: adăugare, vizualizare, editare, ștergere + filtrare status

### Rapoarte
1. Comenzi pe status (număr + valoare totală)
2. Top 10 clienți după valoare comenzi livrate
3. Performanță curieri (livrări + valoare)

### Export
- Clienți → CSV
- Curieri → CSV
- Comenzi → CSV sau TXT

### OOP
- **Clase**: Entity, Persoana, Client, Curier, Comanda
- **Moștenire**: Client → Persoana → Entity; Curier → Persoana → Entity
- **Polimorfism**: metoda `toDisplayString()` + `getRol()` suprascrisă în fiecare clasă
- **Interfață**: `Exportable<T>` implementată de `ExportService`
- **Enum**: `StatusComanda`
- **Colecții**: `List<>`, `ObservableList<>` folosite extensiv
