package org.ma1y0;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderService {
	static enum OrderState {
		ACCEPTED,
		WIP,
		COMPLETED
	}

	static class Order {
		private int orderId;
		private String orderName;
		private String description;
		private OrderState status;
		private LocalDate dateReceived;
		private LocalDate dueDate;

		public Order(int orderId, String orderName, String description, OrderState status, LocalDate dateReceived,
				LocalDate dueDate) {
			this.orderId = orderId;
			this.orderName = orderName;
			this.description = description;
			this.status = status;
			this.dateReceived = dateReceived;
			this.dueDate = dueDate;
		}

		public int getOrderId() {
			return orderId;
		}

		public String getOrderName() {
			return orderName;
		}

		public String getDescription() {
			return description;
		}

		public OrderState getStatus() {
			return status;
		}

		public LocalDate getDateReceived() {
			return dateReceived;
		}

		public LocalDate getDueDate() {
			return dueDate;
		}

		public void setOrderId(int orderId) {
			this.orderId = orderId;
		}

		public void setOrderName(String orderName) {
			this.orderName = orderName;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setStatus(OrderState status) {
			this.status = status;
		}

		public void setDateReceived(LocalDate dateReceived) {
			this.dateReceived = dateReceived;
		}

		public void setDueDate(LocalDate dueDate) {
			this.dueDate = dueDate;
		}

		@Override
		public String toString() {
			return "Order{" +
					"orderId=" + orderId +
					", orderName='" + orderName + '\'' +
					", description='" + description + '\'' +
					", status=" + status +
					", dateReceived=" + dateReceived +
					", dueDate=" + dueDate +
					'}';
		}
	}

	public int create(String name, OrderState state, String description, LocalDate received, LocalDate due) {
		int id = -1;

		try (PreparedStatement pstmt = Database.getInstance().getConnection().prepareStatement(
				"INSERT INTO Orders(order_name, description, state, date_received, due_date) VALUES(?,?,?,?,?)",
				PreparedStatement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, name);
			pstmt.setString(2, description);
			pstmt.setString(3, state.name());
			pstmt.setString(4, received.toString());
			pstmt.setString(5, due.toString());

			int affectedRows = pstmt.executeUpdate();

			if (affectedRows > 0) {
				try (ResultSet rs = pstmt.getGeneratedKeys()) {
					if (rs.next()) {
						id = rs.getInt(1);
					}
				}
			}

		} catch (SQLException e) {
			System.err.println("Error creating order: " + e.getMessage());
		}

		return id;
	}

	public boolean addItems(int orderId, Map<Integer, Integer> items) {
		boolean success = false;

		try (PreparedStatement pstmt = Database.getInstance().getConnection()
				.prepareStatement("INSERT INTO OrderItems (order_id, inventory_id, quantity) VALUES (?, ?, ?)")) {

			for (Map.Entry<Integer, Integer> entry : items.entrySet()) {

				pstmt.setInt(1, orderId);
				pstmt.setInt(2, entry.getKey());
				pstmt.setInt(3, entry.getValue());

				pstmt.addBatch();
			}

			int[] rowsAffectedBatch = pstmt.executeBatch();

			boolean batchSuccess = true;
			for (int i : rowsAffectedBatch) {
				if (i <= 0) {
					batchSuccess = false;
					break;
				}
			}
			success = batchSuccess;

		} catch (SQLException e) {
			System.err.println("Error adding items to order: " + e.getMessage());
		}

		return success;
	}

	public List<Order> getAllOrders() {
		List<Order> orders = new ArrayList<>();

		try (PreparedStatement pstmt = Database.getInstance().getConnection()
				.prepareStatement("SELECT id, order_name, description, state, date_received, due_date FROM Orders")) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int orderId = rs.getInt("id");
				String orderName = rs.getString("order_name");
				String description = rs.getString("description");
				OrderState state = OrderState.valueOf(rs.getString("state"));
				LocalDate dateReceived = LocalDate.parse(rs.getString("date_received"));
				String dueDateStr = rs.getString("due_date");
				LocalDate dueDate = LocalDate.parse(dueDateStr);

				Order order = new Order(orderId, orderName, description, state, dateReceived, dueDate);
				orders.add(order);
			}

		} catch (SQLException e) {
			System.err.println("Error retrieving all orders: " + e.getMessage());
		}
		return orders;
	}

	public boolean changeStateById(int id, OrderState newState) {
		boolean success = false;
		try (PreparedStatement pstmt = Database.getInstance().getConnection()
				.prepareStatement("UPDATE Orders SET State = ? WHERE id = ?")) {
			pstmt.setString(1, newState.name());
			pstmt.setInt(2, id);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				success = true;
				System.out.println("Order with id " + id + " successfully updated to state " + newState);
			} else {
				System.out.println("No order found with id " + id + " to update.");
			}

		} catch (SQLException e) {
			System.err.println("Error updating order: " + e.getMessage());
		}
		return success;
	}
}
