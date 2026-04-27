## Point of Sale (POS) System - Technical and Reseller Documentation

### Version
2.0.0

### Date
21 April 2026

### Author
POS Development Team

### Project Type
Java Desktop Application (Reseller Edition)

## Abstract
This report presents a complete Java-based Point of Sale (POS) system developed for retail and departmental-store operations. The platform supports secure role-based login, inventory management, customer records, checkout workflow, receipt generation, and date-range sales reporting. The system is intentionally structured for resale, allowing branding and business identity changes without source code modification. The deployed implementation uses JavaFX for UI, H2 for embedded persistence, Maven for build lifecycle, and OpenPDF for printable documents.

## Table of Contents
1. Introduction  
2. Objectives and Scope  
3. System Overview  
4. Dataset and Product Catalog (PKR)  
5. Methodology and Design  
6. Database Schema  
7. Implementation Details  
8. User Interfaces and Workflow  
9. Reporting, Slips, and PDF Generation  
10. Reseller Customization Model  
11. Installation and Deployment  
12. Evaluation and Operational Notes  
13. Troubleshooting and Recovery  
14. Security, Roles, and Access Control  
15. Limitations  
16. Future Enhancements  
17. Conclusion  
Appendix A: Default Credentials  
Appendix B: Important File References  
Appendix C: Command Reference

## 1. Introduction
The POS system is designed for real-world retail operations where sales speed, reporting clarity, and easy business onboarding are essential. It can run in local shop environments without dedicated server infrastructure and can be rebranded for multiple clients from one codebase.

### 1.1 Problem Statement
Traditional small-store software often hardcodes company identity and is costly to customize. This project resolves that by separating technical core from branding settings and keeping deployment simple.

### 1.2 Key Features
- Login with Admin and Cashier roles
- Product, category, and customer management
- Sales cart with discount and tax calculation
- Receipt PDF generation
- Dashboard sales filters (custom date range, today, monthly, yearly)
- Print/save sales report PDF to user-selected location
- Duplicate-checkout prevention and sale correction workflow
- Embedded demo dataset in PKR for departmental stores

## 2. Objectives and Scope
### 2.1 Primary Objectives
- Build a stable POS foundation for direct usage and resale
- Provide print-ready receipts and summary slips
- Support non-technical rebranding
- Include realistic seed data for demonstrations

### 2.2 Scope
- Desktop-first workflow (JavaFX)
- Local embedded database
- Departmental-store product catalog with searchable product codes

### 2.3 Out of Scope
- Cloud synchronization
- Multi-branch centralized inventory
- Online payments gateway integration

## 3. System Overview
The system uses layered architecture:
- Presentation layer: FXML views + controllers
- Domain layer: models for users, products, sales, sale items
- Data access layer: DAO classes
- Utility layer: database bootstrap, config manager, PDF generators

Core process flow:
`Login -> Dashboard -> Product/Sale operations -> Database updates -> PDF outputs`

## 4. Dataset and Product Catalog (PKR)
The application includes a seeded open-source departmental-store catalog with 80+ SKUs. Data is inserted by `DatabaseInitializer` using MERGE strategy to avoid duplicate barcodes.

### 4.1 Categories
- Beverages
- Snacks
- Dairy
- Bakery
- Household
- Personal Care
- Staples
- Frozen
- Canned
- Baby Care
- Stationery

### 4.2 Pricing Standard
All seeded prices are in PKR (Pakistani Rupees).

### 4.3 Product Code Search
Products can be located by code in POS search fields:
- `DS1001`, `DS7003`, `DS9002`, `DSA001`, `DSB006`

Reference catalog:
- `DEPARTMENTAL-STORE-DATASET.md`

## 5. Methodology and Design
### 5.1 Patterns
- MVC for UI separation
- DAO for persistence isolation
- Utility classes for cross-cutting concerns

### 5.2 Data Strategy
- H2 embedded storage for simple deployment
- Auto schema initialization
- Seed injection for demo readiness

### 5.3 Transaction Integrity
Sales save operation is transactional:
- sale header insert
- line-item insert
- stock decrement
- rollback on failure

## 6. Database Schema
- `users(id, username, password, role)`
- `categories(id, name)`
- `products(id, name, barcode, category_id, price, stock_qty)`
- `customers(id, full_name, phone, email)`
- `sales(id, customer_id, cashier_user_id, created_at, subtotal, discount, tax, total)`
- `sale_items(id, sale_id, product_id, quantity, unit_price, line_total)`
- `company_settings(setting_key, setting_value)`

## 7. Implementation Details
### 7.1 Technology
- Java 17
- JavaFX (FXML + CSS)
- Maven
- H2 database
- OpenPDF

### 7.2 Recent Functional Additions
- Save dialog for PDF output to user-selected PC location
- Auto-open saved PDF where desktop integration is supported
- Checkout locking to prevent accidental duplicate submissions
- Admin right-click delete sale (with stock restoration)

## 8. User Interfaces and Workflow
### 8.1 Login
Users authenticate as Admin or Cashier.

### 8.2 Dashboard
Shows company identity, low stock alerts, total sales range, and sales list with date/time and totals.

### 8.3 Product Management
CRUD operations, barcode search, stock updates.

### 8.4 New Sale
Search product -> add quantity -> apply discount -> checkout -> save receipt PDF.

## 9. Reporting, Slips, and PDF Generation
### 9.1 Slip Types
- Today Slip
- Monthly Slip
- Yearly Slip
- Custom Range

### 9.2 Report Print Workflow
When Print PDF is clicked:
1. Date range and report label resolved
2. Save File dialog opens (default Downloads)
3. Report PDF generated to chosen path
4. Confirmation shown and file can open automatically

### 9.3 Receipt Workflow
After checkout:
1. Sale is committed to DB
2. Save dialog for receipt appears
3. Receipt PDF saved locally

## 10. Reseller Customization Model
Branding values are configurable from:
- `config.properties`
- `company_settings` table
- Branding Wizard (`Ctrl + Shift + B`)

Editable settings:
- `company.name`
- `company.address`
- `company.phone`
- `company.logo`
- `tax.rate`
- `receipt.footer`

## 11. Installation and Deployment
### 11.1 Requirements
- Java 17
- Maven
- OS with write permissions

### 11.2 Commands
- `mvn clean compile`
- `mvn clean javafx:run`
- `mvn clean package`

### 11.3 Runtime Data Folders
- `database/`
- `receipts/`

## 12. Evaluation and Operational Notes
- Data persistence stable in local usage
- PDF generation reliable with Save dialog workflow
- Duplicate checkout prevention working at UI level
- Sales correction available for Admin role

## 13. Troubleshooting and Recovery
| Issue | Cause | Action |
|---|---|---|
| PDF not found after print | Save canceled or unknown path | Reprint and choose explicit destination (Downloads/Desktop) |
| Duplicate sale entry | Multiple submissions in older flow | Use admin right-click delete; stock auto-restored |
| `mvn` not recognized | Maven PATH missing | Install Maven and add `maven/bin` to PATH |
| Seed data not visible | Existing DB already populated | Delete `database/posdb.mv.db` and restart |

## 14. Security, Roles, and Access Control
- Admin and Cashier role model
- Admin-only privileged actions (sale deletion)
- User-friendly alert handling for invalid operations

## 15. Limitations
- No centralized multi-branch sync
- No cloud backup pipeline by default
- Passwords are plain-text in demo mode (production should hash)

## 16. Future Enhancements
- Role permissions matrix and audit trail
- Password hashing + account lock policies
- Supplier and purchase-order modules
- Advanced accounting exports
- Barcode scanner auto-focus mode

## 17. Conclusion
The POS system is now production-oriented for local deployments and strongly prepared for reseller workflows. It includes realistic departmental-store data in PKR, robust PDF outputs, duplicate-checkout protection, and correction controls for accidental entries. The architecture remains extensible while preserving deployment simplicity.

## Appendix A: Default Credentials
- Admin: `admin / admin123`
- Cashier: `cashier / cashier123`

## Appendix B: Important File References
- `README.md`
- `LOGIN-CREDENTIALS.md`
- `DEPARTMENTAL-STORE-DATASET.md`
- `POS-System-Report.doc`
- `src/main/resources/config.properties`

## Appendix C: Command Reference
- `mvn clean compile`
- `mvn clean javafx:run`
- `mvn clean package`
