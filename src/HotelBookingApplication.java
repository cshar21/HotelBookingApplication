/**
 * Hotel Booking Application
 * Version 6.1 - Reservation Confirmation & Room Allocation
 *
 * Processes queued booking requests and allocates rooms safely.
 * Ensures unique room IDs, prevents double-booking, and updates inventory.
 *
 * Author: YourName
 * Version: 6.1
 */

import java.util.*;

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

    // Decrement inventory after allocation
    public boolean allocateRoom(String roomType) {
        int available = inventory.getOrDefault(roomType, 0);
        if(available > 0) {
            inventory.put(roomType, available - 1);
            return true;
        }
        return false;
    }

    public void displayInventory() {
        System.out.println("===== Current Room Inventory =====");
        for(String type : inventory.keySet())
            System.out.println(type + " | Available: " + inventory.get(type));
    }
}

// UC5 Reservation Request
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

// UC5 Booking Request Queue
class BookingRequestQueue {
    private Queue<Reservation> queue;

    public BookingRequestQueue() { queue = new LinkedList<>(); }

    public void addRequest(Reservation r) { queue.offer(r); }

    public Reservation peekNext() { return queue.peek(); }
    public Reservation processNext() { return queue.poll(); }

    public boolean isEmpty() { return queue.isEmpty(); }
}

// UC6 Room Allocation Service
class RoomAllocationService {
    private RoomInventory inventory;
    private Map<String, Set<String>> allocatedRooms; // roomType -> Set of unique room IDs
    private int nextRoomId;

    public RoomAllocationService(RoomInventory inventory) {
        this.inventory = inventory;
        allocatedRooms = new HashMap<>();
        nextRoomId = 1;
    }

    // Allocate room for a reservation
    public boolean allocateRoom(Reservation res) {
        String type = res.roomType;
        if(inventory.allocateRoom(type)) { // only allocate if available
            allocatedRooms.putIfAbsent(type, new HashSet<>());
            String roomId = generateRoomId(type);
            allocatedRooms.get(type).add(roomId);
            System.out.println("Reservation Confirmed! Guest: " + res.guestName + " | Room Type: " + type + " | Room ID: " + roomId);
            return true;
        } else {
            System.out.println("Sorry, no available rooms for Guest: " + res.guestName + " | Room Type: " + type);
            return false;
        }
    }

    // Generate unique room ID
    private String generateRoomId(String roomType) {
        return roomType.substring(0,2).toUpperCase() + String.format("%03d", nextRoomId++);
    }

    public void displayAllocatedRooms() {
        System.out.println("\n===== Allocated Rooms =====");
        for(String type : allocatedRooms.keySet()) {
            System.out.println(type + ": " + allocatedRooms.get(type));
        }
    }
}

// Main Application
public class HotelBookingApplication {

    public static void main(String[] args) {
        System.out.println("===== Book My Stay App v6.1 =====");

        // Room objects
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Centralized inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType(single.type, 2);
        inventory.addRoomType(doubleRoom.type, 2);
        inventory.addRoomType(suite.type, 1);

        inventory.displayInventory();

        // Booking Request Queue (UC5)
        BookingRequestQueue requestQueue = new BookingRequestQueue();
        requestQueue.addRequest(new Reservation("Alice", "Single Room"));
        requestQueue.addRequest(new Reservation("Bob", "Double Room"));
        requestQueue.addRequest(new Reservation("Charlie", "Suite Room"));
        requestQueue.addRequest(new Reservation("David", "Single Room"));
        requestQueue.addRequest(new Reservation("Eve", "Suite Room")); // simulate fully booked

        // UC6: Room Allocation Service
        RoomAllocationService allocationService = new RoomAllocationService(inventory);

        System.out.println("\n===== Processing Booking Requests =====");
        while(!requestQueue.isEmpty()) {
            Reservation res = requestQueue.processNext();
            allocationService.allocateRoom(res);
        }

        // Display final allocated rooms and inventory
        allocationService.displayAllocatedRooms();
        inventory.displayInventory();
    }
}