package org.ma1y0;

import java.time.LocalDate;
import java.util.List;

import org.ma1y0.EmployeeService.Employee;
import org.ma1y0.InventoryService.InventoryItem;
import org.ma1y0.OrderService.OrderState;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		EmployeeService employeeService = new EmployeeService();
		OrderService orderService = new OrderService();
		InventoryService inventoryService = new InventoryService();

		// int id = employeeService.create(20, "Janis", "Ian", "Space dyke");
		// System.out.println("Created new employee: " + id);
		// List<Employee> employees = employeeService.getAllEmployees();
		// for (Employee employee : employees) {
		// System.out.println(employee);
		// }
		//
		// Employee employee = employeeService.getEmployeeById(5);
		// System.out.println("Employee id 5: " + employee);
		//
		// // boolean b = employeeService.removeEmployeeById(2);
		// // System.out.println("removed?: " + b);
		// int sum = employeeService.sumWages();
		// System.out.println("The sum of all employee wages is " + sum);

		// int order1Id = orderService.create(
		// "Website Redesign",
		// OrderService.OrderState.ACCEPTED,
		// "Complete overhaul of the company website design.",
		// LocalDate.now(),
		// LocalDate.now().plusMonths(1));
		// System.out.println("Created order 1 with ID: " + order1Id);
		//
		// int order2Id = orderService.create(
		// "Mobile App Development",
		// OrderService.OrderState.WIP,
		// "Development of a new mobile application for iOS and Android.",
		// LocalDate.now().minusDays(5),
		// LocalDate.now().plusMonths(3));
		// System.out.println("Created order 2 with ID: " + order2Id);
		//
		// int order3Id = orderService.create(
		// "Marketing Campaign Setup",
		// OrderService.OrderState.COMPLETED,
		// "Setup and launch of a new online marketing campaign.",
		// LocalDate.now().minusWeeks(2),
		// LocalDate.now().minusDays(3));
		// System.out.println("Created order 3 with ID: " + order3Id);
		//
		// int order4Id = orderService.create(
		// "Database Migration",
		// OrderService.OrderState.ACCEPTED,
		// "Migrating data from an old database system to a new one.",
		// LocalDate.now(),
		// LocalDate.now().plusWeeks(4));
		// System.out.println("Created order 4 with ID: " + order4Id);
		// boolean success = orderService.changeStateById(4, OrderState.WIP);
		// System.out.println("Success?: " + success);
		//
		// List<OrderService.Order> allOrders = orderService.getAllOrders();
		// System.out.println("Found " + allOrders.size() + " orders:");
		// for (OrderService.Order order : allOrders) {
		// System.out.println(order);
		// }

		// int itemId1 = inventoryService.create("Laptop", 50, 10);
		// int itemId2 = inventoryService.create("Keyboard", 100, 20);
		// int itemId3 = inventoryService.create("Mouse", 75, 15);
		// int itemId4 = inventoryService.create("Monitor", 30, 5);
		// int itemId5 = inventoryService.create("Webcam", 60, 12);
		// boolean success = inventoryService.updateStorck(4, 56866856);
		// System.out.println("Updated: " + success);
		// List<InventoryItem> inventory = inventoryService.getInvetory();
		// for (InventoryItem item : inventory) {
		// System.out.println(item);
		// }
		List<InventoryItem> understocked = inventoryService.getUnderstocked();
		for (InventoryItem item : understocked) {
			System.out.println(item);
		}

	}
}
