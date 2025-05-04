package org.ma1y0;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ma1y0.InventoryService.InventoryItem;
import org.ma1y0.OrderService.OrderState;

public class OrderProcessing {
	private OrderService orderService;
	private InventoryService inventoryService;

	public OrderProcessing() {
		this.orderService = new OrderService();
		this.inventoryService = new InventoryService();
	}

	public static class ShortageInfo {
		private int itemId;
		private String itemName;
		private int requestedQuantity;
		private int availableQuantity;
		private int shortageAmount;

		public ShortageInfo(int itemId, String itemName, int requestedQuantity, int availableQuantity) {
			this.itemId = itemId;
			this.itemName = itemName;
			this.requestedQuantity = requestedQuantity;
			this.availableQuantity = availableQuantity;
			this.shortageAmount = requestedQuantity - availableQuantity;
		}

		public int getItemId() {
			return itemId;
		}

		public String getItemName() {
			return itemName;
		}

		public int getRequestedQuantity() {
			return requestedQuantity;
		}

		public int getAvailableQuantity() {
			return availableQuantity;
		}

		public int getShortageAmount() {
			return shortageAmount;
		}

		@Override
		public String toString() {
			return "Shortage: Item ID=" + itemId +
					", Name='" + itemName + "'" +
					", Requested=" + requestedQuantity +
					", Available=" + availableQuantity +
					", Shortage=" + shortageAmount;
		}
	}

	public int createOrder(String orderName, String description, LocalDate dueDate, Map<Integer, Integer> items) {
		int orderId = orderService.create(orderName, OrderState.ACCEPTED, description, LocalDate.now(), dueDate);

		if (orderId != -1) {
			if (!items.isEmpty()) {
				boolean itemsAdded = orderService.addItems(orderId, items);
				if (!itemsAdded) {
					System.err.println("Failed to add items to order: " + orderId);
				}
			}
		}

		return orderId;
	}

	public List<ShortageInfo> checkAvailability(int orderId) {
		List<ShortageInfo> shortages = new ArrayList<>();
		Map<Integer, Integer> orderItems = getOrderItems(orderId);

		if (orderItems.isEmpty()) {
			System.out.println("No items found in order: " + orderId);
			return shortages;
		}

		List<InventoryItem> inventory = inventoryService.getInvetory();
		Map<Integer, InventoryItem> inventoryMap = new HashMap<>();

		for (InventoryItem item : inventory) {
			inventoryMap.put(item.getId(), item);
		}

		for (Map.Entry<Integer, Integer> entry : orderItems.entrySet()) {
			int itemId = entry.getKey();
			int requestedQuantity = entry.getValue();

			InventoryItem item = inventoryMap.get(itemId);
			if (item == null) {
				System.err.println("Item with ID " + itemId + " not found in inventory.");
				continue;
			}

			if (item.getStockLevel() < requestedQuantity) {
				shortages.add(new ShortageInfo(
						itemId,
						item.getName(),
						requestedQuantity,
						item.getStockLevel()));
			}
		}

		return shortages;
	}

	public boolean processOrder(int orderId) {
		List<ShortageInfo> shortages = checkAvailability(orderId);

		if (!shortages.isEmpty()) {
			System.out.println("Cannot process order due to stock shortages:");
			for (ShortageInfo shortage : shortages) {
				System.out.println(shortage);
			}
			return false;
		}

		Map<Integer, Integer> orderItems = getOrderItems(orderId);

		boolean inventoryUpdated = updateInventory(orderItems);
		if (!inventoryUpdated) {
			System.err.println("Failed to update inventory for order: " + orderId);
			return false;
		}

		boolean stateChanged = orderService.changeStateById(orderId, OrderState.WIP);
		if (!stateChanged) {
			System.err.println("Failed to change order state for order: " + orderId);
			return false;
		}

		System.out.println("Order " + orderId + " processed successfully.");
		return true;
	}

	public boolean completeOrder(int orderId) {
		boolean stateChanged = orderService.changeStateById(orderId, OrderState.COMPLETED);
		if (stateChanged) {
			System.out.println("Order " + orderId + " marked as completed.");
			return true;
		} else {
			System.err.println("Failed to complete order: " + orderId);
			return false;
		}
	}

	private Map<Integer, Integer> getOrderItems(int orderId) {
		Map<Integer, Integer> items = new HashMap<>();

		try (PreparedStatement pstmt = Database.getInstance().getConnection()
				.prepareStatement("SELECT inventory_id, quantity FROM OrderItems WHERE order_id = ?")) {
			pstmt.setInt(1, orderId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int itemId = rs.getInt("inventory_id");
				int quantity = rs.getInt("quantity");
				items.put(itemId, quantity);
			}

		} catch (SQLException e) {
			System.err.println("Error retrieving order items: " + e.getMessage());
		}

		return items;
	}

	private boolean updateInventory(Map<Integer, Integer> items) {
		if (items.isEmpty()) {
			return true;
		}

		boolean allSuccessful = true;

		List<InventoryItem> inventory = inventoryService.getInvetory();
		Map<Integer, Integer> currentStock = new HashMap<>();

		for (InventoryItem item : inventory) {
			currentStock.put(item.getId(), item.getStockLevel());
		}

		for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
			int itemId = entry.getKey();
			int quantity = entry.getValue();

			if (!currentStock.containsKey(itemId)) {
				System.err.println("Item with ID " + itemId + " not found in inventory.");
				allSuccessful = false;
				continue;
			}

			int newStock = currentStock.get(itemId) - quantity;
			if (newStock < 0) {
				System.err.println("Not enough stock for item ID " + itemId);
				allSuccessful = false;
				continue;
			}

			boolean updated = inventoryService.updateStorck(itemId, newStock);
			if (!updated) {
				System.err.println("Failed to update stock for item ID " + itemId);
				allSuccessful = false;
			}
		}

		return allSuccessful;
	}

}
