package com.example.service.hotelBooking;

import com.example.db.DatabaseManager;
import com.example.models.Room;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class HotelBookingServiceImpl extends UnicastRemoteObject implements HotelBookingService {
    public HotelBookingServiceImpl() throws RemoteException {
        super();
        DatabaseManager.initializeDatabase();
    }

    @Override
    public boolean bookRoom(long roomId, String guestName, String reservationDate) throws RemoteException {
        return DatabaseManager.bookRoom(roomId, guestName, reservationDate);
    }

    @Override
    public List<Room> getAvailableRooms() throws RemoteException {
        return DatabaseManager.getAvailableRooms();
    }

    @Override
    public boolean addRoom(Room room) throws RemoteException {
        return DatabaseManager.addRoom(room);
    }

    @Override
    public boolean deleteRoom(long roomId) throws RemoteException {
        return DatabaseManager.deleteRoom(roomId);
    }

    @Override
    public boolean updateRoom(Room room) throws RemoteException {
        return DatabaseManager.updateRoom(room);
    }

    @Override
    public boolean addReservation(long userId, long roomId, String reservationDate) throws RemoteException {
        return DatabaseManager.addReservation(userId, roomId, reservationDate);
    }

    @Override
    public boolean deleteReservation(long userId, long roomId) throws RemoteException {
        return DatabaseManager.deleteReservation(userId, roomId);
    }

    @Override
    public List<Room> getUserReservations(long userId) throws RemoteException {
        return DatabaseManager.getUserReservations(userId);
    }
}
