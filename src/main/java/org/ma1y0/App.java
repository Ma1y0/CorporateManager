package org.ma1y0;

import java.util.List;

import org.ma1y0.EmployeeService.Employee;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		EmployeeService employeeService = new EmployeeService();

		int id = employeeService.create(500, "Mark", "Richman", "CEO");
		System.out.println("Created new employee: " + id);
		List<Employee> employees = employeeService.getAllEmployees();
		for (Employee employee : employees) {
			System.out.println(employee);
		}
	}
}
