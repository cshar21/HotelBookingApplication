/**
 * Hotel Booking Application
 * Version 5.1 - Booking Request Queue (First-Come-First-Served)
 *
 * Demonstrates fair handling of booking requests using a Queue.
 * Requests are collected in arrival order without modifying inventory.
 *
 * @author YourName
 * @version 5.1
 */

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

// Abstract Room
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

    public HashMap<String, Integer> getInventorySnapshot() { return new HashMap<>(inventory); }

    public void displayInventory() {
        System.out.println("===== Current Room Inventory =====");
        for(String type : inventory.keySet())
            System.out.println(type + " | Available: " + inventory.get(type));
    }
}

// UC5: Reservation Request
class Reservation {
    String guestName;
    String roomType;

    Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    void displayRequest() {
        System.out.println("Guest: " + guestName + " | Requested Room: " + roomType);
    }
}

// UC5: Booking Request Queue
class BookingRequestQueue {
    private Queue<Reservation> queue;

    public BookingRequestQueue() { queue = new LinkedList<>(); }

    // Add request to queue
    public void addRequest(Reservation r) {
        queue.offer(r); // FIFO
        System.out.println("Request added for guest: " + r.guestName);
    }

    // Peek at next request (without removing)
    public Reservation peekNext() { return queue.peek(); }

    // Process request (removes from queue)
    public Reservation processNext() { return queue.poll(); }

    // Display all queued requests
    public void displayQueue() {
        System.out.println("\n===== Booking Requests Queue =====");
        if(queue.isEmpty()) System.out.println("No pending requests.");
        else for(Reservation r : queue) r.displayRequest();
    }
}

// Main Application
public class HotelBookingApplication {

    public static void main(String[] args) {
        System.out.println("===== Book My Stay App v5.1 =====");

        // Room objects
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Centralized inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType(single.type, 5);
        inventory.addRoomType(doubleRoom.type, 3);
        inventory.addRoomType(suite.type, 2);

        inventory.displayInventory();

        // UC5: Booking Request Queue
        BookingRequestQueue requestQueue = new BookingRequestQueue();

        // Add multiple booking requests (FIFO)
        requestQueue.addRequest(new Reservation("Alice", "Single Room"));
        requestQueue.addRequest(new Reservation("Bob", "Double Room"));
        requestQueue.addRequest(new Reservation("Charlie", "Suite Room"));
        requestQueue.addRequest(new Reservation("David", "Single Room"));

        // Display queue
        requestQueue.displayQueue();

        // Peek at next request
        System.out.println("\nNext request to process:");
        Reservation next = requestQueue.peekNext();
        if(next != null) next.displayRequest();

        // Process first request
        System.out.println("\nProcessing first request...");
        Reservation processed = requestQueue.processNext();
        if(processed != null) processed.displayRequest();

        // Display updated queue
        requestQueue.displayQueue();
    }
}