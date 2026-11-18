# Inventory Management System (Java JDBC + MySQL)

## 1. Database setup (Mac terminal)
    mysql -u root -p < sql/schema.sql

## 2. Set your MySQL password
Edit `src/main/resources/db.properties` (db.user and db.password).

## 3. Run in IntelliJ
File > Open > select the InventoryManagement folder (pom.xml) > "Open as Project".
Wait for Maven to download the MySQL driver, then right-click `Main.java` > Run.

## 4. Run from terminal (needs Maven: brew install maven)
    cd InventoryManagement
    mvn compile exec:java

Requires JDK 17+ (brew install openjdk@17, or download in IntelliJ: File > Project Structure > SDKs).
