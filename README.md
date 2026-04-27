# Java-based POS System

Production-ready starter POS built with JavaFX + H2 and structured for multi-client rebranding.

## Tech Stack
- Java 17
- JavaFX (FXML + CSS)
- Maven
- H2 embedded database
- OpenPDF for receipt generation

## Project Structure
- `src/main/java/com/pos` application source
- `src/main/resources/fxml` JavaFX layouts
- `src/main/resources/css` styling
- `src/main/resources/config.properties` default branding config
- `database` H2 data files (auto-created)
- `receipts` generated PDF receipts
- `DEPARTMENTAL-STORE-DATASET.md` product code and price catalog

## Run in development
```bash
mvn clean javafx:run
```

## Build JAR
```bash
mvn clean package
```

## Default Users
- Admin: `admin` / `admin123`
- Cashier: `cashier` / `cashier123`
- Detailed login guide: see `LOGIN-CREDENTIALS.md`

## Demo Dataset
- Departmental-store demo products are auto-seeded by `DatabaseInitializer`.
- Expanded large-store dataset includes beverages, snacks, dairy, bakery, household, personal care, staples, frozen, canned, baby care, and stationery.
- Product codes like `DS1001`, `DS2001`, `DS7003`, `DS9002`, `DSA001`, `DSB006` can be searched directly.
- All dataset prices are stored in **PKR**.
- Full list: `DEPARTMENTAL-STORE-DATASET.md`

## Rebranding and Reselling
Branding keys are loaded from `company_settings` table and `config.properties`.

Update:
- `company.name`
- `company.address`
- `company.phone`
- `company.logo`
- `tax.rate`
- `receipt.footer`

Use hidden Branding Wizard in Dashboard with `Ctrl + Shift + B`.

## Backup and Restore (simple)
- Backup: copy the `database/posdb.mv.db` file
- Restore: replace that file while the app is not running
