package org.ma1y0;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	private static Database instance = null;
	private Connection connection = null;
	private final String dbUrl = "jdbc:sqlite:db.sqlite3";

	public static Database getInstance() {
		if (instance == null) {
			instance = new Database();
		}
		return instance;
	}

	public Connection getConnection() {
		return connection;
	}

	private Database() {
		try {
			// Load the SQLite JDBC driver
			Class.forName("org.sqlite.JDBC");
			// Establish the connection
			connection = DriverManager.getConnection(dbUrl);
			System.out.println("Database connection established.");

			createEmployeeTable();
			createOrdersTable();

		} catch (ClassNotFoundException e) {
			System.err.println("SQLite JDBC driver not found: " + e.getMessage());
		} catch (SQLException e) {
			System.err.println("Database connection error: " + e.getMessage());
		}
	}

	private void createEmployeeTable() {
		String sql = "CREATE TABLE IF NOT EXISTS Employees ("
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "name TEXT NOT NULL,"
				+ "surname TEXT NOT NULL,"
				+ "wage INT NOT NULL,"
				+ "position TEXT NOT NULL"
				+ ");";

		try (Statement stmt = connection.createStatement()) {
			stmt.execute(sql);
			System.out.println("Employees table created or already exists.");
		} catch (SQLException e) {
			System.err.println("Error creating Employees table: " + e.getMessage());
		}
	}

	private void createOrdersTable() {
		String sql = "CREATE TABLE IF NOT EXISTS Orders ("
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "order_name TEXT NOT NULL,"
				+ "description TEXT,"
				+ "state TEXT CHECK(state IN ('ACCEPTED', 'WIP', 'COMPLETED')) NOT NULL,"
				+ "date_received TEXT NOT NULL,"
				+ "due_date TEXT"
				+ ");";
		try (Statement stmt = connection.createStatement()) {
			stmt.execute(sql);
			System.out.println("Orders table created or already exists.");
		} catch (SQLException e) {
			System.err.println("Error creating Orders table: " + e.getMessage());
		}

	}
}
