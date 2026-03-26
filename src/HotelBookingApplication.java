/**
 * Hotel Booking Application
 * Version 3.1 - Centralized Room Inventory
 *
 * Demonstrates HashMap-based inventory management.
 * Each room type is stored in a single source of truth (RoomInventory).
 *
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

// Centralized Inventory Class
class RoomInventory {
    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    // Register room type with initial availability
    public void addRoomType(String roomType, int count) {
        inventory.put(roomType, count);
    }

    // Get current availability
    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    // Update availability (e.g., after booking or cancellation)
    public void updateAvailability(String roomType, int count) {
        if(inventory.containsKey(roomType)) {
            inventory.put(roomType, count);
        }
    }

    // Display current inventory
    public void displayInventory() {
        System.out.println("===== Current Room Inventory =====");
        for(String type : inventory.keySet()) {
            System.out.println(type + " | Available: " + inventory.get(type));
        }
    }
}

// Main Application
public class HotelBookingApplication {

    public static void main(String[] args) {
        System.out.println("===== Book My Stay App v3.1 =====");

        // Room objects
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Display room details
        single.displayRoomDetails();
        doubleRoom.displayRoomDetails();
        suite.displayRoomDetails();

        // Centralized inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType(single.type, 5);
        inventory.addRoomType(doubleRoom.type, 3);
        inventory.addRoomType(suite.type, 2);

        // Display inventory
        inventory.displayInventory();

        // Example: Booking a single room (update inventory)
        System.out.println("\nBooking 1 Single Room...");
        int updatedSingle = inventory.getAvailability(single.type) - 1;
        inventory.updateAvailability(single.type, updatedSingle);

        inventory.displayInventory();
    }
}