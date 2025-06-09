# Card Collection

## Database Setup

This section describes how to set up the database for this project on a new device.

### Schema Migration (Flyway)

This project uses [Flyway](https://flywaydb.org/) to manage database schema migrations.

1.  **Install MySQL:** Ensure you have a MySQL server installed and running.
2.  **Create a Database:** You'll need to create an empty database in MySQL for the application. For example, `CREATE DATABASE card_collection;` (you can choose a different name). You will also need a MySQL user that has permissions to connect to this database, create tables, and modify data.
3.  **Configure Database Connection:** The application expects database connection details to be configured. This is typically done in `src/main/resources/application.properties` or `src/main/resources/application.yml`. (You may need to create this file if it doesn't already exist in `src/main/resources/`). You'll need to set properties like:
    *   `spring.datasource.url` (e.g., `jdbc:mysql://localhost:3306/card_collection?useSSL=false&serverTimezone=UTC`)
    *   `spring.datasource.username` (your MySQL username)
    *   `spring.datasource.password` (your MySQL password)
    *   `spring.flyway.locations` (usually defaults to `classpath:db/migration` which is correct for this project; specify if your migrations are elsewhere or if Flyway isn't finding them)
4.  **Run the Application:** When the Spring Boot application starts, Flyway will automatically check the database. If the schema is new or outdated, Flyway will apply the necessary migration scripts located in `src/main/resources/db/migration` to create or update the schema.

### Populating with Initial Data (SQL Dump)

After the schema has been created by Flyway (or if you want to start with a pre-populated database), you can import data from an SQL dump.

The main SQL dump file is located at `src/main/resources/sql/dump/Dump20250305.sql`.

**Steps to Import (using MySQL command line):**

1.  **Ensure Schema Exists and User Has Permissions:** Make sure the database and schema are already created (as described in the Flyway section). The MySQL user you use for the import (`YOUR_USERNAME` in the command below) must have sufficient permissions (e.g., `SELECT`, `INSERT`, `DELETE`, `UPDATE`, `CREATE`, `DROP` on the target database).
2.  **Navigate to the SQL dump directory (optional but convenient):**
    Open your terminal or command prompt and navigate to the directory containing the dump file.
    ```bash
    cd path/to/your/project/src/main/resources/sql/dump/
    ```
3.  **Import the Dump:**
    Use the `mysql` command-line tool to import the data. You will be prompted for your MySQL user's password.
    ```bash
    mysql -u YOUR_USERNAME -p --show-warnings YOUR_DATABASE_NAME < Dump20250305.sql
    ```
    Replace `YOUR_USERNAME` with your MySQL username and `YOUR_DATABASE_NAME` with the name of the database you created (e.g., `card_collection`).

    For example:
    ```bash
    mysql -u root -p --show-warnings card_collection < Dump20250305.sql
    ```

**Note:**
*   Importing a dump file will overwrite any existing data in the tables defined within the dump.
*   The other `.sql` files in the `src/main/resources/sql/dump/` directory appear to be individual table dumps. `Dump20250305.sql` is likely a full dump of all relevant tables.
*   If you encounter issues, ensure the character set and collation of your database match those used in the dump file (typically UTF-8).
