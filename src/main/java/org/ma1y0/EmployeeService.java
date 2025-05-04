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

	public Employee getEmployeeById(int id) {
		try (PreparedStatement pstmt = Database.getInstance().getConnection()
				.prepareStatement("SELECT * FROM Employees WHERE id = ? LIMIT 1")) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (!rs.next()) {
				System.out.println("No employee found: " + id);
				return null;
			}

			return new Employee(rs.getInt("id"), rs.getString("name"), rs.getString("surname"),
					rs.getInt("wage"), rs.getString("position"));
		} catch (SQLException e) {
			System.err.println("Error fetching employee: " + e.getMessage());
		}

		return null;
	}

	public boolean removeEmployeeById(int id) {
		boolean success = false;
		try (PreparedStatement pstmt = Database.getInstance().getConnection()
				.prepareStatement("DELETE FROM Employees WHERE id = ?")) {
			pstmt.setInt(1, id);
			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println("Employee with ID " + id + " deleted successfully.");
				success = true;
			} else {
				System.err.println("No employee found with ID " + id + " for deletion.");
			}
		} catch (SQLException e) {
			System.err.println("Error deleting employee: " + e.getMessage());
		}

		return success;
	}

	public boolean updateEmployee(int id, String name, String surname, int wage, String position) {
		boolean success = false;
		try (PreparedStatement pstmt = Database.getInstance().getConnection()
				.prepareStatement("UPDATE Employees SET name = ?, surname = ?, wage = ?, position = ? WHERE id = ?")) {

			pstmt.setString(1, name);
			pstmt.setString(2, surname);
			pstmt.setInt(3, wage);
			pstmt.setString(4, position);
			pstmt.setInt(5, id);

			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Employee with ID " + id + " updated successfully.");
				success = true;
			} else {
				System.err.println("No employee found with ID " + id + " for update.");
			}

		} catch (SQLException e) {
			System.err.println("Error updating user: " + e.getMessage());
		}

		return success;
	}

	public int sumWages() {
		int sum = -1;
		try (PreparedStatement pstm = Database.getInstance().getConnection()
				.prepareStatement("SELECT SUM(wage) FROM Employees")) {
			ResultSet rs = pstm.executeQuery();

			if (rs.next()) {
				sum = rs.getInt(1);
			} else {
				System.err.println("No wages to sum");
			}
		} catch (SQLException e) {
			System.err.println("Error suming employee wages: " + e.getMessage());
		}
		return sum;
	}
}
