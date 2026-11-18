package com.retail;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection();
             Scanner sc = new Scanner(System.in)) {

            System.out.println("Connected to retail_db successfully.");
            ProductDAO dao = new ProductDAO(con);

            while (true) {
                System.out.println("\n===== Inventory Management System =====");
                System.out.println("1. Insert Product Records");
                System.out.println("2. Show Low-Stock Products by Category (qty < 20, price DESC)");
                System.out.println("3. Update Product Stock");
                System.out.println("4. Search and Modify Product Price");
                System.out.println("5. Total Inventory Value and Delete (if qty = 0)");
                System.out.println("6. View All Products");
                System.out.println("0. Exit");
                System.out.print("Enter choice: ");

                int choice = readInt(sc);

                switch (choice) {
                    case 1:
                        insertMenu(sc, dao);
                        break;
                    case 2:
                        System.out.print("Enter category: ");
                        dao.showLowStockByCategory(sc.nextLine().trim());
                        break;
                    case 3:
                        System.out.print("Enter product ID: ");
                        int id3 = readInt(sc);
                        System.out.print("Enter quantity to add: ");
                        int add = readInt(sc);
                        dao.addStock(id3, add);
                        break;
                    case 4:
                        System.out.print("Enter product ID: ");
                        int id4 = readInt(sc);
                        System.out.print("Enter new price: ");
                        double price = readDouble(sc);
                        dao.updatePrice(id4, price);
                        break;
                    case 5:
                        dao.showTotalInventoryValue();
                        System.out.print("Enter product ID to delete: ");
                        int id5 = readInt(sc);
                        dao.deleteIfZeroStock(id5);
                        break;
                    case 6:
                        dao.showAll();
                        break;
                    case 0:
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            System.out.println("Check db.properties (user/password) and that MySQL is running.");
        }
    }

    private static void insertMenu(Scanner sc, ProductDAO dao) {
        System.out.println("1. Insert 3 sample products");
        System.out.println("2. Enter my own product");
        System.out.print("Choice: ");
        int c = readInt(sc);
        if (c == 1) {
            dao.insertProduct(101, "Laptop", "Electronics", 10, 55000.0);
            dao.insertProduct(102, "Mouse", "Electronics", 15, 499.0);
            dao.insertProduct(103, "Notebook", "Stationery", 50, 60.0);
        } else if (c == 2) {
            System.out.print("Product ID: ");
            int id = readInt(sc);
            System.out.print("Name: ");
            String name = sc.nextLine().trim();
            System.out.print("Category: ");
            String cat = sc.nextLine().trim();
            System.out.print("Quantity: ");
            int qty = readInt(sc);
            System.out.print("Price: ");
            double price = readDouble(sc);
            dao.insertProduct(id, name, cat, qty, price);
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private static int readInt(Scanner sc) {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid whole number: ");
            }
        }
    }

    private static double readDouble(Scanner sc) {
        while (true) {
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }
}
