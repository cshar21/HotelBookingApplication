/**
 * Hotel Booking Application
 * Version 4.1 - Room Search & Availability Check
 *
 * Demonstrates read-only access to centralized inventory.
 * Guests can view available rooms without modifying state.
 *
 * @author YourName
 * @version 4.1
 */

import java.util.HashMap;

// Abstract class
abstract class Room {
    String type;
    int beds;
    double price;

    Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    abstract void displayRoomDetails();
}

// Concrete Room classes
class SingleRoom extends Room {
    SingleRoom() { super("Single Room", 1, 1000); }
    void displayRoomDetails() { System.out.println(type + " | Beds: " + beds + " | Price: " + price); }
}

class DoubleRoom extends Room {
    DoubleRoom() { super("Double Room", 2, 2000); }
    void displayRoomDetails() { System.out.println(type + " | Beds: " + beds + " | Price: " + price); }
}

class SuiteRoom extends Room {
    SuiteRoom() { super("Suite Room", 3, 5000); }
    void displayRoomDetails() { System.out.println(type + " | Beds: " + beds + " | Price: " + price); }
}

// Centralized Inventory
class RoomInventory {
    private HashMap<String, Integer> inventory;

    public RoomInventory() { inventory = new HashMap<>(); }

    public void addRoomType(String roomType, int count) { inventory.put(roomType, count); }
    public int getAvailability(String roomType) { return inventory.getOrDefault(roomType, 0); }
    public void updateAvailability(String roomType, int count) { if(inventory.containsKey(roomType)) inventory.put(roomType, count); }

    public void displayInventory() {
        System.out.println("===== Current Room Inventory =====");
        for(String type : inventory.keySet())
            System.out.println(type + " | Available: " + inventory.get(type));
    }

    // UC4: Read-only access for search
    public HashMap<String, Integer> getInventorySnapshot() {
        return new HashMap<>(inventory); // return a copy to prevent modifications
    }
}

// Main Application
public class HotelBookingApplication {

    public static void main(String[] args) {
        System.out.println("===== Book My Stay App v4.1 =====");

        // Room objects
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Centralized inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType(single.type, 5);
        inventory.addRoomType(doubleRoom.type, 0); // Simulate fully booked
        inventory.addRoomType(suite.type, 2);

        // Display inventory
        inventory.displayInventory();

        // UC4: Search service - read-only room availability
        System.out.println("\n===== Available Rooms for Guests =====");
        searchAvailableRooms(inventory, new Room[]{single, doubleRoom, suite});
    }

    // UC4: Search method
    public static void searchAvailableRooms(RoomInventory inventory, Room[] rooms) {
        HashMap<String, Integer> snapshot = inventory.getInventorySnapshot();

        for(Room room : rooms) {
            int available = snapshot.getOrDefault(room.type, 0);
            if(available > 0) {
                room.displayRoomDetails();
                System.out.println("Available: " + available + "\n");
            }
        }
    }
}