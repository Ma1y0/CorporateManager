package org.ma1y0;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.ma1y0.EmployeeService.Employee;
import org.ma1y0.InventoryService.InventoryItem;
import org.ma1y0.OrderService.Order;
import org.ma1y0.OrderService.OrderState;
import org.ma1y0.OrderProcessing.ShortageInfo;

/**
 * CompanySystem provides a command-line interface for the corporate management
 * application.
 */
public class CompanySystem {
	private Scanner scanner;
	private EmployeeService employeeService;
	private OrderService orderService;
	private InventoryService inventoryService;
	private OrderProcessing orderProcessing;
	private boolean running;
	private DateTimeFormatter dateFormatter;

	/**
	 * Constructor initializes all necessary services
	 */
	public CompanySystem() {
		scanner = new Scanner(System.in);
		employeeService = new EmployeeService();
		orderService = new OrderService();
		inventoryService = new InventoryService();
		orderProcessing = new OrderProcessing();
		dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	}

	/**
	 * Start the CLI system
	 */
	public void start() {
		running = true;
		System.out.println("========================================");
		System.out.println("Welcome to the Corporate Management System");
		System.out.println("========================================");

		while (running) {
			displayMainMenu();
			int choice = getIntInput("Enter your choice: ");
			handleMainMenuChoice(choice);
		}

		scanner.close();
		System.out.println("System shut down. Goodbye!");
	}

	/**
	 * Display the main menu options
	 */
	private void displayMainMenu() {
		System.out.println("\n--- Main Menu ---");
		System.out.println("1. Employee Management");
		System.out.println("2. Inventory Management");
		System.out.println("3. Order Management");
		System.out.println("4. Reports");
		System.out.println("0. Exit");
	}

	/**
	 * Handle the user's choice from main menu
	 */
	private void handleMainMenuChoice(int choice) {
		switch (choice) {
			case 1:
				employeeMenu();
				break;
			case 2:
				inventoryMenu();
				break;
			case 3:
				orderMenu();
				break;
			case 4:
				reportsMenu();
				break;
			case 0:
				running = false;
				break;
			default:
				System.out.println("Invalid choice. Please try again.");
		}
	}

	// =========== EMPLOYEE MANAGEMENT ===========

	/**
	 * Display and handle employee management menu
	 */
	private void employeeMenu() {
		boolean returnToMain = false;

		while (!returnToMain) {
			System.out.println("\n--- Employee Management ---");
			System.out.println("1. List All Employees");
			System.out.println("2. Add New Employee");
			System.out.println("3. View Employee Details");
			System.out.println("4. Update Employee");
			System.out.println("5. Remove Employee");
			System.out.println("0. Return to Main Menu");

			int choice = getIntInput("Enter your choice: ");

			switch (choice) {
				case 1:
					listAllEmployees();
					break;
				case 2:
					addNewEmployee();
					break;
				case 3:
					viewEmployeeDetails();
					break;
				case 4:
					updateEmployee();
					break;
				case 5:
					removeEmployee();
					break;
				case 0:
					returnToMain = true;
					break;
				default:
					System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	private void listAllEmployees() {
		System.out.println("\n--- All Employees ---");
		List<Employee> employees = employeeService.getAllEmployees();

		if (employees.isEmpty()) {
			System.out.println("No employees found in the system.");
			return;
		}

		for (Employee employee : employees) {
			System.out.println(employee);
		}

		int totalWages = employeeService.sumWages();
		System.out.println("\nTotal wages: $" + totalWages);
	}

	private void addNewEmployee() {
		System.out.println("\n--- Add New Employee ---");

		String name = getStringInput("Enter employee first name: ");
		String surname = getStringInput("Enter employee last name: ");
		int wage = getIntInput("Enter employee wage: ");
		String position = getStringInput("Enter employee position: ");

		int employeeId = employeeService.create(wage, name, surname, position);

		if (employeeId != -1) {
			System.out.println("Employee added successfully with ID: " + employeeId);
		} else {
			System.out.println("Failed to add employee.");
		}
	}

	private void viewEmployeeDetails() {
		System.out.println("\n--- View Employee Details ---");
		int id = getIntInput("Enter employee ID: ");

		Employee employee = employeeService.getEmployeeById(id);

		if (employee != null) {
			System.out.println(employee);
		} else {
			System.out.println("No employee found with ID: " + id);
		}
	}

	private void updateEmployee() {
		System.out.println("\n--- Update Employee ---");
		int id = getIntInput("Enter employee ID to update: ");

		Employee employee = employeeService.getEmployeeById(id);
		if (employee == null) {
			System.out.println("No employee found with ID: " + id);
			return;
		}

		System.out.println("Current employee details: " + employee);

		String name = getStringInput("Enter new first name (or press Enter to keep current): ");
		if (name.isEmpty()) {
			name = employee.getName();
		}

		String surname = getStringInput("Enter new last name (or press Enter to keep current): ");
		if (surname.isEmpty()) {
			surname = employee.getSurname();
		}

		String wageStr = getStringInput("Enter new wage (or press Enter to keep current): ");
		int wage = wageStr.isEmpty() ? employee.getWage() : Integer.parseInt(wageStr);

		String position = getStringInput("Enter new position (or press Enter to keep current): ");
		if (position.isEmpty()) {
			position = employee.getPosition();
		}

		boolean success = employeeService.updateEmployee(id, name, surname, wage, position);

		if (success) {
			System.out.println("Employee updated successfully.");
		} else {
			System.out.println("Failed to update employee.");
		}
	}

	private void removeEmployee() {
		System.out.println("\n--- Remove Employee ---");
		int id = getIntInput("Enter employee ID to remove: ");

		boolean confirmed = getBooleanInput("Are you sure you want to remove employee #" + id + "? (y/n): ");
		if (!confirmed) {
			System.out.println("Operation cancelled.");
			return;
		}

		boolean success = employeeService.removeEmployeeById(id);

		if (success) {
			System.out.println("Employee removed successfully.");
		} else {
			System.out.println("Failed to remove employee. Employee ID may not exist.");
		}
	}

	// =========== INVENTORY MANAGEMENT ===========

	/**
	 * Display and handle inventory management menu
	 */
	private void inventoryMenu() {
		boolean returnToMain = false;

		while (!returnToMain) {
			System.out.println("\n--- Inventory Management ---");
			System.out.println("1. List All Inventory");
			System.out.println("2. Add New Item");
			System.out.println("3. Update Stock Level");
			System.out.println("4. View Understocked Items");
			System.out.println("0. Return to Main Menu");

			int choice = getIntInput("Enter your choice: ");

			switch (choice) {
				case 1:
					listAllInventory();
					break;
				case 2:
					addNewInventoryItem();
					break;
				case 3:
					updateStockLevel();
					break;
				case 4:
					viewUnderstockedItems();
					break;
				case 0:
					returnToMain = true;
					break;
				default:
					System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	private void listAllInventory() {
		System.out.println("\n--- All Inventory Items ---");
		List<InventoryItem> inventory = inventoryService.getInvetory();

		if (inventory.isEmpty()) {
			System.out.println("No items found in inventory.");
			return;
		}

		System.out.println("ID | Name | Stock Level | Min Stock Level");
		System.out.println("----------------------------------------");
		for (InventoryItem item : inventory) {
			System.out.printf("%d | %s | %d | %d%n",
					item.getId(), item.getName(), item.getStockLevel(), item.getMinStockLevel());
		}
	}

	private void addNewInventoryItem() {
		System.out.println("\n--- Add New Inventory Item ---");

		String name = getStringInput("Enter item name: ");
		int stockLevel = getIntInput("Enter initial stock level: ");
		int minStockLevel = getIntInput("Enter minimum stock level: ");

		int itemId = inventoryService.create(name, stockLevel, minStockLevel);

		if (itemId != -1) {
			System.out.println("Inventory item added successfully with ID: " + itemId);
		} else {
			System.out.println("Failed to add inventory item.");
		}
	}

	private void updateStockLevel() {
		System.out.println("\n--- Update Stock Level ---");

		listAllInventory();
		int itemId = getIntInput("Enter item ID to update: ");
		int newStockLevel = getIntInput("Enter new stock level: ");

		boolean success = inventoryService.updateStorck(itemId, newStockLevel);

		if (success) {
			System.out.println("Stock level updated successfully.");
		} else {
			System.out.println("Failed to update stock level. Item ID may not exist.");
		}
	}

	private void viewUnderstockedItems() {
		System.out.println("\n--- Understocked Items ---");
		List<InventoryItem> understocked = inventoryService.getUnderstocked();

		if (understocked.isEmpty()) {
			System.out.println("No understocked items found.");
			return;
		}

		System.out.println("ID | Name | Current Stock | Min Stock | Shortage");
		System.out.println("--------------------------------------------------");
		for (InventoryItem item : understocked) {
			int shortage = item.getMinStockLevel() - item.getStockLevel();
			System.out.printf("%d | %s | %d | %d | %d%n",
					item.getId(), item.getName(), item.getStockLevel(), item.getMinStockLevel(), shortage);
		}
	}

	// =========== ORDER MANAGEMENT ===========

	/**
	 * Display and handle order management menu
	 */
	private void orderMenu() {
		boolean returnToMain = false;

		while (!returnToMain) {
			System.out.println("\n--- Order Management ---");
			System.out.println("1. List All Orders");
			System.out.println("2. Create New Order");
			System.out.println("3. Add Items to Order");
			System.out.println("4. Check Order Availability");
			System.out.println("5. Process Order");
			System.out.println("6. Complete Order");
			System.out.println("0. Return to Main Menu");

			int choice = getIntInput("Enter your choice: ");

			switch (choice) {
				case 1:
					listAllOrders();
					break;
				case 2:
					createNewOrder();
					break;
				case 3:
					addItemsToOrder();
					break;
				case 4:
					checkOrderAvailability();
					break;
				case 5:
					processOrder();
					break;
				case 6:
					completeOrder();
					break;
				case 0:
					returnToMain = true;
					break;
				default:
					System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	private void listAllOrders() {
		System.out.println("\n--- All Orders ---");
		List<Order> orders = orderService.getAllOrders();

		if (orders.isEmpty()) {
			System.out.println("No orders found in the system.");
			return;
		}

		for (Order order : orders) {
			System.out.println(order);
		}
	}

	private void createNewOrder() {
		System.out.println("\n--- Create New Order ---");

		String orderName = getStringInput("Enter order name: ");
		String description = getStringInput("Enter order description: ");

		LocalDate dueDate = null;
		while (dueDate == null) {
			String dueDateStr = getStringInput("Enter due date (YYYY-MM-DD): ");
			try {
				dueDate = LocalDate.parse(dueDateStr, dateFormatter);
			} catch (DateTimeParseException e) {
				System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
			}
		}

		boolean addItems = getBooleanInput("Do you want to add items to this order now? (y/n): ");
		Map<Integer, Integer> items = new HashMap<>();

		if (addItems) {
			listAllInventory();
			boolean addingItems = true;

			while (addingItems) {
				int itemId = getIntInput("Enter item ID to add (0 to finish): ");
				if (itemId == 0) {
					addingItems = false;
					continue;
				}

				int quantity = getIntInput("Enter quantity: ");
				items.put(itemId, quantity);

				addingItems = getBooleanInput("Add another item? (y/n): ");
			}
		}

		int orderId = orderProcessing.createOrder(orderName, description, dueDate, items);

		if (orderId != -1) {
			System.out.println("Order created successfully with ID: " + orderId);
		} else {
			System.out.println("Failed to create order.");
		}
	}

	private void addItemsToOrder() {
		System.out.println("\n--- Add Items to Order ---");

		listAllOrders();
		int orderId = getIntInput("Enter order ID to add items to: ");

		listAllInventory();
		Map<Integer, Integer> items = new HashMap<>();
		boolean addingItems = true;

		while (addingItems) {
			int itemId = getIntInput("Enter item ID to add (0 to finish): ");
			if (itemId == 0) {
				addingItems = false;
				continue;
			}

			int quantity = getIntInput("Enter quantity: ");
			items.put(itemId, quantity);

			addingItems = getBooleanInput("Add another item? (y/n): ");
		}

		if (!items.isEmpty()) {
			boolean success = orderService.addItems(orderId, items);

			if (success) {
				System.out.println("Items added to order successfully.");
			} else {
				System.out.println("Failed to add items to order.");
			}
		} else {
			System.out.println("No items were added to the order.");
		}
	}

	private void checkOrderAvailability() {
		System.out.println("\n--- Check Order Availability ---");

		listAllOrders();
		int orderId = getIntInput("Enter order ID to check: ");

		List<ShortageInfo> shortages = orderProcessing.checkAvailability(orderId);

		if (shortages.isEmpty()) {
			System.out.println("All items are available for order #" + orderId);
		} else {
			System.out.println("Order #" + orderId + " has the following shortages:");
			for (ShortageInfo shortage : shortages) {
				System.out.println(shortage);
			}
		}
	}

	private void processOrder() {
		System.out.println("\n--- Process Order ---");

		listAllOrders();
		int orderId = getIntInput("Enter order ID to process: ");

		boolean confirmed = getBooleanInput(
				"Are you sure you want to process order #" + orderId + "? This will update inventory. (y/n): ");
		if (!confirmed) {
			System.out.println("Operation cancelled.");
			return;
		}

		boolean success = orderProcessing.processOrder(orderId);

		if (success) {
			System.out.println("Order #" + orderId + " processed successfully.");
		} else {
			// The orderProcessing.processOrder already outputs error messages
		}
	}

	private void completeOrder() {
		System.out.println("\n--- Complete Order ---");

		listAllOrders();
		int orderId = getIntInput("Enter order ID to mark as completed: ");

		boolean confirmed = getBooleanInput("Are you sure you want to mark order #" + orderId + " as completed? (y/n): ");
		if (!confirmed) {
			System.out.println("Operation cancelled.");
			return;
		}

		boolean success = orderProcessing.completeOrder(orderId);

		if (success) {
			System.out.println("Order #" + orderId + " marked as completed.");
		} else {
			System.out.println("Failed to complete order #" + orderId);
		}
	}

	// =========== REPORTS ===========

	/**
	 * Display and handle reports menu
	 */
	private void reportsMenu() {
		boolean returnToMain = false;

		while (!returnToMain) {
			System.out.println("\n--- Reports ---");
			System.out.println("1. Employee Wage Summary");
			System.out.println("2. Inventory Status Report");
			System.out.println("3. Order Status Summary");
			System.out.println("0. Return to Main Menu");

			int choice = getIntInput("Enter your choice: ");

			switch (choice) {
				case 1:
					employeeWageSummary();
					break;
				case 2:
					inventoryStatusReport();
					break;
				case 3:
					orderStatusSummary();
					break;
				case 0:
					returnToMain = true;
					break;
				default:
					System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	private void employeeWageSummary() {
		System.out.println("\n--- Employee Wage Summary ---");

		List<Employee> employees = employeeService.getAllEmployees();

		if (employees.isEmpty()) {
			System.out.println("No employees found in the system.");
			return;
		}

		int totalWages = employeeService.sumWages();
		int employeeCount = employees.size();
		double averageWage = employeeCount > 0 ? (double) totalWages / employeeCount : 0;

		System.out.println("Total employees: " + employeeCount);
		System.out.println("Total wages: $" + totalWages);
		System.out.printf("Average wage: $%.2f%n", averageWage);

		// Find highest and lowest paid employees
		Employee highestPaid = null;
		Employee lowestPaid = null;

		for (Employee employee : employees) {
			if (highestPaid == null || employee.getWage() > highestPaid.getWage()) {
				highestPaid = employee;
			}

			if (lowestPaid == null || employee.getWage() < lowestPaid.getWage()) {
				lowestPaid = employee;
			}
		}

		if (highestPaid != null) {
			System.out.println("\nHighest paid employee: " + highestPaid.getName() + " " +
					highestPaid.getSurname() + " ($" + highestPaid.getWage() + ")");
		}

		if (lowestPaid != null) {
			System.out.println("Lowest paid employee: " + lowestPaid.getName() + " " +
					lowestPaid.getSurname() + " ($" + lowestPaid.getWage() + ")");
		}
	}

	private void inventoryStatusReport() {
		System.out.println("\n--- Inventory Status Report ---");

		List<InventoryItem> inventory = inventoryService.getInvetory();

		if (inventory.isEmpty()) {
			System.out.println("No items found in inventory.");
			return;
		}

		int totalItems = inventory.size();
		int totalStock = 0;

		for (InventoryItem item : inventory) {
			totalStock += item.getStockLevel();
		}

		List<InventoryItem> understocked = inventoryService.getUnderstocked();
		int understockedCount = understocked.size();

		System.out.println("Total inventory items: " + totalItems);
		System.out.println("Total items in stock: " + totalStock);
		System.out.println("Understocked items: " + understockedCount);

		if (understockedCount > 0) {
			System.out.println("\nUnderstocked Item Details:");
			System.out.println("ID | Name | Current Stock | Min Stock | Shortage");
			System.out.println("--------------------------------------------------");

			for (InventoryItem item : understocked) {
				int shortage = item.getMinStockLevel() - item.getStockLevel();
				System.out.printf("%d | %s | %d | %d | %d%n",
						item.getId(), item.getName(), item.getStockLevel(), item.getMinStockLevel(), shortage);
			}
		}
	}

	private void orderStatusSummary() {
		System.out.println("\n--- Order Status Summary ---");

		List<Order> orders = orderService.getAllOrders();

		if (orders.isEmpty()) {
			System.out.println("No orders found in the system.");
			return;
		}

		int totalOrders = orders.size();
		int acceptedCount = 0;
		int wipCount = 0;
		int completedCount = 0;

		for (Order order : orders) {
			switch (order.getStatus()) {
				case ACCEPTED:
					acceptedCount++;
					break;
				case WIP:
					wipCount++;
					break;
				case COMPLETED:
					completedCount++;
					break;
			}
		}

		System.out.println("Total orders: " + totalOrders);
		System.out.println("Orders by status:");
		System.out.println("  ACCEPTED: " + acceptedCount);
		System.out.println("  WIP: " + wipCount);
		System.out.println("  COMPLETED: " + completedCount);

		// Show orders due soon (within 7 days)
		LocalDate today = LocalDate.now();
		LocalDate sevenDaysLater = today.plusDays(7);

		System.out.println("\nOrders due within the next 7 days:");
		boolean foundDueSoon = false;

		for (Order order : orders) {
			if (order.getStatus() != OrderState.COMPLETED &&
					order.getDueDate() != null &&
					!order.getDueDate().isBefore(today) &&
					!order.getDueDate().isAfter(sevenDaysLater)) {

				System.out.println("Order #" + order.getOrderId() + ": " + order.getOrderName() +
						" (Due: " + order.getDueDate() + ", Status: " + order.getStatus() + ")");
				foundDueSoon = true;
			}
		}

		if (!foundDueSoon) {
			System.out.println("No orders due within the next 7 days.");
		}
	}

	// =========== HELPER METHODS ===========

	/**
	 * Get integer input from the user
	 */
	private int getIntInput(String prompt) {
		int value = 0;
		boolean validInput = false;

		while (!validInput) {
			System.out.print(prompt);
			try {
				String input = scanner.nextLine().trim();
				value = Integer.parseInt(input);
				validInput = true;
			} catch (NumberFormatException e) {
				System.out.println("Please enter a valid number.");
			}
		}

		return value;
	}

	/**
	 * Get string input from the user
	 */
	private String getStringInput(String prompt) {
		System.out.print(prompt);
		return scanner.nextLine().trim();
	}

	/**
	 * Get yes/no input from the user
	 */
	private boolean getBooleanInput(String prompt) {
		while (true) {
			System.out.print(prompt);
			String input = scanner.nextLine().trim().toLowerCase();

			if (input.equals("y") || input.equals("yes")) {
				return true;
			} else if (input.equals("n") || input.equals("no")) {
				return false;
			} else {
				System.out.println("Please enter 'y' or 'n'.");
			}
		}
	}

	/**
	 * Main method to start the system
	 */
	public static void main(String[] args) {
		CompanySystem system = new CompanySystem();
		system.start();
	}
}
