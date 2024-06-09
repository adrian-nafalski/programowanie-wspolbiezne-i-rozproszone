package com.example.db;

import com.example.enums.EQUIPMENT;
import com.example.enums.GRANTS;
import com.example.models.Room;
import com.example.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:hotel.db";

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS rooms (" +
                    "id INTEGER PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "price REAL NOT NULL," +
                    "imagePath TEXT," +
                    "maxPeopleInTheRoom INTEGER NOT NULL," +
                    "reservationDate TEXT," +
                    "equipment TEXT NOT NULL," +
                    "active INTEGER NOT NULL DEFAULT 1," +
                    "description TEXT)";
            stmt.execute(sql);

            String userSql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "login TEXT NOT NULL UNIQUE," +
                    "password TEXT NOT NULL," +
                    "grants TEXT NOT NULL)";
            stmt.execute(userSql);

            // Dodanie domyślnego administratora, jeśli nie istnieje
            String adminCheckSql = "SELECT COUNT(*) FROM users WHERE login = 'admin'";
            ResultSet rs = stmt.executeQuery(adminCheckSql);
            if (rs.next() && rs.getInt(1) == 0) {
                String adminInsertSql = "INSERT INTO users (login, password, grants) VALUES ('admin', 'admin', 'ADMIN')";
                stmt.execute(adminInsertSql);
            }

            String reservationSql = "CREATE TABLE IF NOT EXISTS reservations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "userId INTEGER NOT NULL," +
                    "roomId INTEGER NOT NULL," +
                    "reservationDate TEXT NOT NULL," +
                    "FOREIGN KEY (userId) REFERENCES users(id)," +
                    "FOREIGN KEY (roomId) REFERENCES rooms(id))";
            stmt.execute(reservationSql);

            System.out.println("Baza danych zainicjalizowana");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM rooms WHERE active = 1")) {

            while (rs.next()) {
                Room room = new Room(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("imagePath"),
                        rs.getLong("maxPeopleInTheRoom"),
                        rs.getString("reservationDate"),
                        EQUIPMENT.valueOf(rs.getString("equipment")),
                        rs.getInt("active") == 1,
                        rs.getString("description")
                );
                rooms.add(room);
            }
            System.out.println("Dostępne pokoje: " + rooms);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public static boolean bookRoom(long roomId, String guestName, String reservationDate) {
        Connection conn = null;
        PreparedStatement updateRoomStmt = null;
        PreparedStatement addReservationStmt = null;

        String updateRoomSql = "UPDATE rooms SET reservationDate = ?, active = 0 WHERE id = ?";
        String addReservationSql = "INSERT INTO reservations (userId, roomId, reservationDate) VALUES (?, ?, ?)";

        try {
            conn = DriverManager.getConnection(URL);
            conn.setAutoCommit(false);

            updateRoomStmt = conn.prepareStatement(updateRoomSql);
            updateRoomStmt.setString(1, reservationDate);
            updateRoomStmt.setLong(2, roomId);
            updateRoomStmt.executeUpdate();

            addReservationStmt = conn.prepareStatement(addReservationSql);
            addReservationStmt.setLong(1, getUserId(guestName));
            addReservationStmt.setLong(2, roomId);
            addReservationStmt.setString(3, reservationDate);
            addReservationStmt.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (updateRoomStmt != null) updateRoomStmt.close();
                if (addReservationStmt != null) addReservationStmt.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean addRoom(Room room) {
        String sql = "INSERT INTO rooms (name, price, imagePath, maxPeopleInTheRoom, reservationDate, equipment, active, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, room.getName());
            pstmt.setDouble(2, room.getPrice());
            pstmt.setString(3, room.getImagePath());
            pstmt.setLong(4, room.getMaxPeopleInTheRoom());
            pstmt.setString(5, room.getReservationDate());
            pstmt.setString(6, room.getEquipment().name());
            pstmt.setInt(7, room.isActive() ? 1 : 0);
            pstmt.setString(8, room.getDescription());
            pstmt.executeUpdate();
            System.out.println("Pokój dodany: " + room);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteRoom(long roomId) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, roomId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateRoom(Room room) {
        String sql = "UPDATE rooms SET name = ?, price = ?, imagePath = ?, maxPeopleInTheRoom = ?, reservationDate = ?, equipment = ?, active = ?, description = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, room.getName());
            pstmt.setDouble(2, room.getPrice());
            pstmt.setString(3, room.getImagePath());
            pstmt.setLong(4, room.getMaxPeopleInTheRoom());
            pstmt.setString(5, room.getReservationDate());
            pstmt.setString(6, room.getEquipment().name());
            pstmt.setInt(7, room.isActive() ? 1 : 0);
            pstmt.setString(8, room.getDescription());
            pstmt.setLong(9, room.getId());
            pstmt.executeUpdate();
            System.out.println("Pokój zaktualizowany: " + room);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean registerUser(User user) {
        String sql = "INSERT INTO users (login, password, grants) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getLogin());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getGrants().name());
            pstmt.executeUpdate();
            System.out.println("Użytkownik zarejestrowany: " + user);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static User loginUser(String login, String password) {
        String sql = "SELECT * FROM users WHERE login = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getLong("id"),
                        rs.getString("login"),
                        rs.getString("password"),
                        GRANTS.valueOf(rs.getString("grants"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean addReservation(long userId, long roomId, String reservationDate) {
        String sql = "INSERT INTO reservations (userId, roomId, reservationDate) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setLong(2, roomId);
            pstmt.setString(3, reservationDate);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteReservation(long userId, long roomId) {
        Connection conn = null;
        PreparedStatement deleteReservationStmt = null;
        PreparedStatement updateRoomStmt = null;

        String deleteReservationSql = "DELETE FROM reservations WHERE userId = ? AND roomId = ?";
        String updateRoomSql = "UPDATE rooms SET active = 1, reservationDate = '' WHERE id = ?";

        try {
            conn = DriverManager.getConnection(URL);
            conn.setAutoCommit(false);

            deleteReservationStmt = conn.prepareStatement(deleteReservationSql);
            deleteReservationStmt.setLong(1, userId);
            deleteReservationStmt.setLong(2, roomId);
            deleteReservationStmt.executeUpdate();

            updateRoomStmt = conn.prepareStatement(updateRoomSql);
            updateRoomStmt.setLong(1, roomId);
            updateRoomStmt.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (deleteReservationStmt != null) deleteReservationStmt.close();
                if (updateRoomStmt != null) updateRoomStmt.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Room> getUserReservations(long userId) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.* FROM rooms r " +
                "JOIN reservations res ON r.id = res.roomId " +
                "WHERE res.userId = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Room room = new Room(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("imagePath"),
                        rs.getLong("maxPeopleInTheRoom"),
                        rs.getString("reservationDate"),
                        EQUIPMENT.valueOf(rs.getString("equipment")),
                        rs.getBoolean("active"),
                        rs.getString("description")
                );
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    private static long getUserId(String guestName) throws SQLException {
        String sql = "SELECT id FROM users WHERE login = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, guestName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("id");
            } else {
                throw new SQLException("Użytkownik nie znaleziony!");
            }
        }
    }
}