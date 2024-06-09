package com.example.client;

import com.example.models.Room;
import com.example.service.hotelBooking.HotelBookingService;

import java.rmi.Naming;
import java.util.List;

public class HotelBookingServiceClient {
    private HotelBookingService service;

    public HotelBookingServiceClient() {
        try {
            service = (HotelBookingService) Naming.lookup("//localhost/HotelBookingService");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean bookRoom(long id, String guestName, String reservationDate) {
        try {
            return service.bookRoom(id, guestName, reservationDate);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addRoom(Room room) {
        try {
            return service.addRoom(room);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRoom(Room room) {
        try {
            return service.updateRoom(room);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRoom(long roomId) {
        try {
            return service.deleteRoom(roomId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Room> getAvailableRooms() {
        try {
            return service.getAvailableRooms();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Room> getUserReservations(long userId) {
        try {
            return service.getUserReservations(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteReservation(long userId, long roomId) {
        try {
            return service.deleteReservation(userId, roomId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
