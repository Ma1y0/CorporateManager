package org.ma1y0;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ma1y0.EmployeeService.Employee;
import org.ma1y0.InventoryService.InventoryItem;
import org.ma1y0.OrderService.OrderState;
import org.ma1y0.OrderProcessing.ShortageInfo;

/**
 * Main application class
 */
public class App {
	public static void main(String[] args) {
		// Initialize services
		EmployeeService employeeService = new EmployeeService();
		OrderService orderService = new OrderService();
		InventoryService inventoryService = new InventoryService();
		OrderProcessing orderProcessing = new OrderProcessing();

		// Ensure we have some inventory items
		setupInventory(inventoryService);

		// Display current inventory status
		System.out.println("\n----- Current Inventory -----");
		displayInventory(inventoryService);

		// Create a new order with multiple items
		System.out.println("\n----- Creating a new order -----");
		Map<Integer, Integer> orderItems = new HashMap<>();
		orderItems.put(1, 10); // 10 of item #1
		orderItems.put(2, 5); // 5 of item #2
		orderItems.put(3, 15); // 15 of item #3

		int orderId = orderProcessing.createOrder(
				"Office Equipment Order",
				"Order for new office equipment",
				LocalDate.now().plusDays(7),
				orderItems);

		System.out.println("Created order with ID: " + orderId);

		// Check availability of items
		System.out.println("\n----- Checking availability -----");
		List<ShortageInfo> shortages = orderProcessing.checkAvailability(orderId);

		if (shortages.isEmpty()) {
			System.out.println("All items are available for order #" + orderId);

			// Process the order (update inventory and change state to WIP)
			System.out.println("\n----- Processing order -----");
			boolean processed = orderProcessing.processOrder(orderId);

			if (processed) {
				System.out.println("Order #" + orderId + " has been processed successfully");

				// Display updated inventory
				System.out.println("\n----- Updated Inventory -----");
				displayInventory(inventoryService);

				// Complete the order
				System.out.println("\n----- Completing order -----");
				boolean completed = orderProcessing.completeOrder(orderId);

				if (completed) {
					System.out.println("Order #" + orderId + " has been completed");
				}
			}
		} else {
			System.out.println("Cannot process order due to inventory shortages:");
			for (ShortageInfo shortage : shortages) {
				System.out.println(shortage);
			}
		}

		// Example with stock shortage
		System.out.println("\n----- Creating an order with insufficient stock -----");
		Map<Integer, Integer> largeOrder = new HashMap<>();
		largeOrder.put(1, 100); // 100 of item #1 - more than we have in stock
		largeOrder.put(2, 20); // 20 of item #2

		int largeOrderId = orderProcessing.createOrder(
				"Large Equipment Order",
				"Large order that exceeds our inventory",
				LocalDate.now().plusDays(14),
				largeOrder);

		System.out.println("Created large order with ID: " + largeOrderId);

		// Check availability for the large order
		System.out.println("\n----- Checking availability for large order -----");
		List<ShortageInfo> largeOrderShortages = orderProcessing.checkAvailability(largeOrderId);

		if (largeOrderShortages.isEmpty()) {
			System.out.println("All items are available for large order");
			orderProcessing.processOrder(largeOrderId);
		} else {
			System.out.println("Cannot process large order due to inventory shortages:");
			for (ShortageInfo shortage : largeOrderShortages) {
				System.out.println(shortage);
			}
		}
	}

	/**
	 * Helper method to ensure we have some inventory items
	 */
	private static void setupInventory(InventoryService inventoryService) {
		// Check if we have items, if not create them
		List<InventoryItem> existingInventory = inventoryService.getInvetory();

		if (existingInventory.isEmpty()) {
			// Create some inventory items
			inventoryService.create("Laptop", 50, 10);
			inventoryService.create("Keyboard", 100, 20);
			inventoryService.create("Mouse", 75, 15);
			inventoryService.create("Monitor", 30, 5);
			inventoryService.create("Webcam", 60, 12);
		}
	}

	/**
	 * Helper method to display current inventory
	 */
	private static void displayInventory(InventoryService inventoryService) {
		List<InventoryItem> inventory = inventoryService.getInvetory();
		for (InventoryItem item : inventory) {
			System.out.println(item);
		}
	}
}
