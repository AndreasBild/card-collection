# Card Collection

## Database Setup

This section describes how to set up the database for this project on a new device.

### Automatic Database Initialization & Migration (Flyway)

This project uses [Flyway](https://flywaydb.org/) to manage database schema migrations and initial seed data automatically.

1. **Install MySQL:** Ensure you have a MySQL server installed and running.
2. **Create a Database:** Create an empty database in MySQL for the application:
   ```sql
   CREATE DATABASE card_collection;
   ```
3. **Configure Database Connection:** Set database connection details in `src/main/resources/application.properties` (or environment variables):
   * `spring.datasource.url` (e.g., `jdbc:mysql://localhost:3306/card_collection?useSSL=false&serverTimezone=UTC`)
   * `spring.datasource.username` (your MySQL username)
   * `spring.datasource.password` (your MySQL password)
4. **Run the Application:** Start the Spring Boot application (e.g., `./mvnw spring-boot:run`). Flyway will automatically execute the baseline migration scripts in `src/main/resources/db/migration` to create all tables and populate the seed data in a single step.

### Manual SQL Dump Backup / Restore (Optional)

A complete SQL dump of the database is maintained at `src/main/resources/sql/dump/Dump.sql` for reference or manual backup/restore needs.

If you ever need to manually restore the database from this dump using the MySQL CLI:
```bash
mysql -u YOUR_USERNAME -p card_collection < src/main/resources/sql/dump/Dump.sql
```

