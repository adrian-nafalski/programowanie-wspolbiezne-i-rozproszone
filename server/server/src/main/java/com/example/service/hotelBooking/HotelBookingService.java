package com.example.service.hotelBooking;

import com.example.models.Room;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface HotelBookingService extends Remote {
    boolean bookRoom(long roomId, String guestName, String reservationDate) throws RemoteException;
    List<Room> getAvailableRooms() throws RemoteException;
    boolean addRoom(Room room) throws RemoteException;
    boolean deleteRoom(long roomId) throws RemoteException;
    boolean updateRoom(Room room) throws RemoteException;
    boolean addReservation(long userId, long roomId, String reservationDate) throws RemoteException;
    boolean deleteReservation(long userId, long roomId) throws RemoteException;
    List<Room> getUserReservations(long userId) throws RemoteException;
}
