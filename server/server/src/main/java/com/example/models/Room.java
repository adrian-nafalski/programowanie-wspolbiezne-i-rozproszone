package com.example.models;

import com.example.enums.EQUIPMENT;

import java.io.Serializable;

public class Room implements Serializable {
    private long id;
    private String name;
    private double price;
    private String imagePath;
    private long maxPeopleInTheRoom;
    private String reservationDate;
    private EQUIPMENT equipment;
    private boolean active;
    private String description;

    public Room(long id, String name, double price, String imagePath, long maxPeopleInTheRoom, String reservationDate, EQUIPMENT equipment, boolean active, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
        this.maxPeopleInTheRoom = maxPeopleInTheRoom;
        this.reservationDate = reservationDate;
        this.equipment = equipment;
        this.active = active;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public long getMaxPeopleInTheRoom() {
        return maxPeopleInTheRoom;
    }

    public void setMaxPeopleInTheRoom(long maxPeopleInTheRoom) {
        this.maxPeopleInTheRoom = maxPeopleInTheRoom;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    public EQUIPMENT getEquipment() {
        return equipment;
    }

    public void setEquipment(EQUIPMENT equipment) {
        this.equipment = equipment;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
