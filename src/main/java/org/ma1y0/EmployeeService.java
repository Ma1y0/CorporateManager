package org.ma1y0;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeService {
	public static class Employee {
		private int id;
		private String name;
		private String surname;
		private int wage;
		private String position;

		public Employee(int id, String name, String surname, int wage, String position) {
			this.id = id;
			this.name = name;
			this.surname = surname;
			this.wage = wage;
			this.position = position;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getSurname() {
			return surname;
		}

		public int getWage() {
			return wage;
		}

		public String getPosition() {
			return position;
		}

		@Override
		public String toString() {
			return "Employee [ID=" + id + ", Name=" + name + " " + surname + ", Wage=" + wage + ", Position=" + position
					+ "]";
		}
	}

	public int create(int wage, String name, String surname, String position) {
		int userID = -1;
		try (PreparedStatement pstmt = Database.getInstance().getConnection()
				.prepareStatement("INSERT INTO Employees(wage, name, surname, position) VALUES(?, ?, ?, ?)",
						PreparedStatement.RETURN_GENERATED_KEYS)) {

			pstmt.setInt(1, wage);
			pstmt.setString(2, name);
			pstmt.setString(3, surname);
			pstmt.setString(4, position);
			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {
				try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						userID = generatedKeys.getInt(1);
						System.out.println("Employee created successfully with ID: " + userID);
					}
				}
			} else {
				System.err.println("Failed to create employee.");
			}
		} catch (SQLException e) {
			System.err.println("Error creating employee: " + e.getMessage());
		}

		return userID;
	}

	public List<Employee> getAllEmployees() {
		List<Employee> employees = new ArrayList<>();
		try (PreparedStatement pstmt = Database.getInstance().getConnection()
				.prepareStatement("SELECT * FROM Employees")) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Employee employee = new Employee(rs.getInt("id"), rs.getString("name"), rs.getString("surname"),
						rs.getInt("wage"), rs.getString("position"));
				employees.add(employee);
			}

		} catch (SQLException e) {
			System.err.println("Error fetching employees: " + e.getMessage());
		}

		return employees;
	}
}
