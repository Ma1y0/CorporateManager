package org.ma1y0;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryService {
	static class InventoryItem {

		private int id;
		private String name;
		private int stockLevel;
		private int minStockLevel;

		public InventoryItem(int id, String name, int stockLevel, int minStockLevel) {
			this.id = id;
			this.name = name;
			this.stockLevel = stockLevel;
			this.minStockLevel = minStockLevel;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public int getStockLevel() {
			return stockLevel;
		}

		public int getMinStockLevel() {
			return minStockLevel;
		}

		public void setId(int itemId) {
			this.id = itemId;
		}

		public void setName(String itemName) {
			this.name = itemName;
		}

		public void setStockLevel(int stockLevel) {
			this.stockLevel = stockLevel;
		}

		public void setMinStockLevel(int minStockLevel) {
			this.minStockLevel = minStockLevel;
		}

		@Override
		public String toString() {
			return "InventoryItem{" +
					"id=" + id +
					", name='" + name + '\'' +
					", stockLevel=" + stockLevel +
					", minStockLevel=" + minStockLevel +
					'}';
		}
	}

	public int create(String name, int stockLevel, int minStockLevel) {
		int id = -1;
		try (PreparedStatement pstmt = Database.getInstance().getConnection()
				.prepareStatement("INSERT INTO Inventory (name, stock_level, min_stock_level) VALUES (?, ?, ?)",
						PreparedStatement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, name);
			pstmt.setInt(2, stockLevel);
			pstmt.setInt(3, minStockLevel);
			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {
				try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						id = generatedKeys.getInt(1);
						System.out.println("Employee created successfully with ID: " + id);
					}
				}
			} else {
				System.err.println("Failed to create employee.");
			}

		} catch (SQLException e) {
			System.err.println("Error creating Inventory item " + e.getMessage());
		}
		return id;
	}

	public boolean updateStorck(int id, int newStock) {
		boolean success = false;
		try (PreparedStatement pstmt = Database.getInstance().getConnection()
				.prepareStatement("UPDATE Inventory SET stock_level = ? WHERE id = ?")) {
			pstmt.setInt(1, newStock);
			pstmt.setInt(2, id);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Inventory item with ID " + id + " updated successfully.");
				success = true;
			} else {
				System.err.println("No inventory item found with ID " + id + " for update.");
			}
		} catch (SQLException e) {
			System.err.println("Error updating the stock " + newStock);
		}
		return success;
	}

	public List<InventoryItem> getInvetory() {
		List<InventoryItem> inventory = new ArrayList<>();
		try (PreparedStatement pstmt = Database.getInstance().getConnection()
				.prepareStatement("SELECT * FROM Inventory")) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				InventoryItem item = new InventoryItem(rs.getInt("id"), rs.getString("name"), rs.getInt("stock_level"),
						rs.getInt("min_stock_level"));
				inventory.add(item);
			}
		} catch (SQLException e) {
			System.err.println("Error fetching inventory: " + e.getMessage());
		}

		return inventory;
	}

	public List<InventoryItem> getUnderstocked() {
		List<InventoryItem> understocked = new ArrayList<>();
		try (PreparedStatement pstmt = Database.getInstance().getConnection()
				.prepareStatement("SELECT * FROM Inventory WHERE stock_level < min_stock_level")) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				InventoryItem item = new InventoryItem(rs.getInt("id"), rs.getString("name"), rs.getInt("stock_level"),
						rs.getInt("min_stock_level"));
				understocked.add(item);
			}
		} catch (SQLException e) {
			System.err.println("Error fetching inventory: " + e.getMessage());
		}

		return understocked;
	}
}
