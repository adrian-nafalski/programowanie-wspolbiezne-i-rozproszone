package com.example;


import com.example.db.DatabaseManager;
import com.example.service.hotelBooking.HotelBookingService;
import com.example.service.hotelBooking.HotelBookingServiceImpl;
import com.example.service.userService.UserService;
import com.example.service.userService.UserServiceImpl;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class HotelBookingServer {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            HotelBookingService hotelService = new HotelBookingServiceImpl();
            UserService userService = new UserServiceImpl();
            DatabaseManager.initializeDatabase();
            Naming.rebind("HotelBookingService", hotelService);
            Naming.rebind("UserService", userService);
            System.out.println("Serwer działa ...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
