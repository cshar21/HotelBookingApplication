import java.util.*;

/**
 * HotelBookingApplication - Book My Stay App
 * Version 4.0 (Refactored to include Booking History & Reporting)
 *
 * Demonstrates core Java, OOP, and data structures through a Hotel Booking Management System.
 * Includes Use Cases 1–8.
 *
 * Author: YourName
 * Version: 4.0
 */
public class HotelBookingApplication {

    // ---------------- UC2: Room Classes ----------------
    abstract static class Room {
        String type;
        int beds;
        double price;

        Room(String type, int beds, double price) {
            this.type = type;
            this.beds = beds;
            this.price = price;
        }

        void displayRoom() {
            System.out.println(type + " | Beds: " + beds + " | Price: " + price);
        }
    }

    static class SingleRoom extends Room {
        SingleRoom() { super("Single Room", 1, 1000); }
    }

    static class DoubleRoom extends Room {
        DoubleRoom() { super("Double Room", 2, 1800); }
    }

    static class SuiteRoom extends Room {
        SuiteRoom() { super("Suite Room", 3, 3000); }
    }

    // ---------------- UC3: Inventory Management ----------------
    static class Inventory {
        private Map<String, Integer> roomAvailability;

        Inventory() {
            roomAvailability = new HashMap<>();
            roomAvailability.put("Single Room", 5);
            roomAvailability.put("Double Room", 3);
            roomAvailability.put("Suite Room", 2);
        }

        boolean isAvailable(String roomType) {
            return roomAvailability.getOrDefault(roomType, 0) > 0;
        }

        void allocateRoom(String roomType) {
            if (isAvailable(roomType)) {
                roomAvailability.put(roomType, roomAvailability.get(roomType) - 1);
            }
        }

        void displayAvailability() {
            System.out.println("\n===== Current Inventory =====");
            for (String roomType : roomAvailability.keySet()) {
                System.out.println(roomType + " : " + roomAvailability.get(roomType) + " available");
            }
        }
    }

    // ---------------- UC5: Booking Request ----------------
    static class Reservation {
        String guestName;
        String roomType;

        Reservation(String guestName, String roomType) {
            this.guestName = guestName;
            this.roomType = roomType;
        }
    }

    static class BookingQueue {
        Queue<Reservation> reservationQueue;

        BookingQueue() {
            reservationQueue = new LinkedList<>();
        }

        void addRequest(Reservation r) {
            reservationQueue.add(r);
        }

        Reservation getNextRequest() {
            return reservationQueue.poll();
        }

        boolean hasPendingRequests() {
            return !reservationQueue.isEmpty();
        }
    }

    // ---------------- UC6: Room Allocation Service ----------------
    static class RoomAllocationService {
        Inventory inventory;
        Map<String, Set<String>> allocatedRooms; // roomType -> roomIDs
        int roomCounter;

        RoomAllocationService(Inventory inventory) {
            this.inventory = inventory;
            allocatedRooms = new HashMap<>();
            roomCounter = 1;
        }

        String allocateRoom(String roomType) {
            if (!inventory.isAvailable(roomType)) return null;

            String roomId = roomType.substring(0, 2).toUpperCase() + String.format("%03d", roomCounter++);
            allocatedRooms.putIfAbsent(roomType, new HashSet<>());
            allocatedRooms.get(roomType).add(roomId);
            inventory.allocateRoom(roomType);
            return roomId;
        }

        void displayAllocatedRooms() {
            System.out.println("\n===== Allocated Rooms =====");
            for (String roomType : allocatedRooms.keySet()) {
                System.out.println(roomType + " : " + allocatedRooms.get(roomType));
            }
        }
    }

    // ---------------- UC7: Add-On Services ----------------
    static class Service {
        String name;
        double cost;

        Service(String name, double cost) {
            this.name = name;
            this.cost = cost;
        }
    }

    static class AddOnServiceManager {
        Map<String, List<Service>> reservationServices;

        AddOnServiceManager() {
            reservationServices = new HashMap<>();
        }

        void addService(String reservationId, Service service) {
            reservationServices.putIfAbsent(reservationId, new ArrayList<>());
            reservationServices.get(reservationId).add(service);
        }

        double getTotalServiceCost(String reservationId) {
            List<Service> services = reservationServices.getOrDefault(reservationId, new ArrayList<>());
            double total = 0;
            for (Service s : services) total += s.cost;
            return total;
        }

        void displayAllServices() {
            System.out.println("\n===== Add-On Services Per Reservation =====");
            for (String resId : reservationServices.keySet()) {
                System.out.println("Reservation: " + resId);
                for (Service s : reservationServices.get(resId)) {
                    System.out.println("  - " + s.name + " | Cost: " + s.cost);
                }
                System.out.println("  Total Add-On Cost: " + getTotalServiceCost(resId));
            }
        }
    }

    // ---------------- UC8: Booking History & Reporting ----------------
    static class BookingHistory {
        List<Reservation> confirmedReservations;

        BookingHistory() {
            confirmedReservations = new ArrayList<>();
        }

        void addReservation(Reservation res) {
            confirmedReservations.add(res);
        }

        List<Reservation> getAllReservations() {
            return confirmedReservations;
        }
    }

    static class BookingReportService {
        void generateReport(BookingHistory history) {
            System.out.println("\n===== Booking History Report =====");
            for (Reservation res : history.getAllReservations()) {
                System.out.println("Guest: " + res.guestName + " | Room Type: " + res.roomType);
            }
            System.out.println("Total Bookings: " + history.getAllReservations().size());
        }
    }

    // ---------------- Main Method ----------------
    public static void main(String[] args) {
        // UC1: Welcome Message
        System.out.println("Welcome to Book My Stay App v4.0!");

        // UC2: Initialize rooms
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        singleRoom.displayRoom();
        doubleRoom.displayRoom();
        suiteRoom.displayRoom();

        // UC3: Inventory
        Inventory inventory = new Inventory();
        inventory.displayAvailability();

        // UC5: Booking Queue
        BookingQueue queue = new BookingQueue();
        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Double Room"));
        queue.addRequest(new Reservation("Charlie", "Suite Room"));

        // UC6: Room Allocation
        RoomAllocationService allocationService = new RoomAllocationService(inventory);
        Map<String, String> confirmedReservations = new HashMap<>(); // guest -> roomId
        BookingHistory bookingHistory = new BookingHistory();

        while (queue.hasPendingRequests()) {
            Reservation res = queue.getNextRequest();
            String roomId = allocationService.allocateRoom(res.roomType);
            if (roomId != null) {
                System.out.println("Reservation Confirmed for " + res.guestName + " | RoomID: " + roomId);
                confirmedReservations.put(res.guestName, roomId);
                bookingHistory.addReservation(res); // UC8: Add to history
            } else {
                System.out.println("Sorry " + res.guestName + ", " + res.roomType + " not available.");
            }
        }

        allocationService.displayAllocatedRooms();
        inventory.displayAvailability();

        // UC7: Add-On Services
        AddOnServiceManager serviceManager = new AddOnServiceManager();
        for (String guest : confirmedReservations.keySet()) {
            String roomId = confirmedReservations.get(guest);
            if (guest.equals("Alice")) {
                serviceManager.addService(roomId, new Service("Breakfast", 200));
                serviceManager.addService(roomId, new Service("Airport Pickup", 500));
            }
            if (guest.equals("Bob")) {
                serviceManager.addService(roomId, new Service("Spa", 800));
            }
            if (guest.equals("Charlie")) {
                serviceManager.addService(roomId, new Service("Welcome Drinks", 300));
            }
        }
        serviceManager.displayAllServices();

        // UC8: Booking History Report
        BookingReportService reportService = new BookingReportService();
        reportService.generateReport(bookingHistory);
    }
}