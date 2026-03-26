import java.util.*;
import java.util.concurrent.*;

/**
 * HotelBookingApplication - Book My Stay App
 * Version 7.0 (UC1–UC11 Fully Integrated)
 *
 * Demonstrates core Java, OOP, data structures, and concurrency.
 *
 * Author: Debop
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

    static class SingleRoom extends Room { SingleRoom() { super("Single Room", 1, 1000); } }
    static class DoubleRoom extends Room { DoubleRoom() { super("Double Room", 2, 1800); } }
    static class SuiteRoom extends Room { SuiteRoom() { super("Suite Room", 3, 3000); } }

    // ---------------- UC3: Inventory Management ----------------
    static class Inventory {
        private final Map<String, Integer> roomAvailability;

        Inventory() {
            roomAvailability = new HashMap<>();
            roomAvailability.put("Single Room", 5);
            roomAvailability.put("Double Room", 3);
            roomAvailability.put("Suite Room", 2);
        }

        public synchronized boolean isAvailable(String roomType) {
            return roomAvailability.getOrDefault(roomType, 0) > 0;
        }

        public synchronized boolean allocateRoom(String roomType) {
            if (isAvailable(roomType)) {
                roomAvailability.put(roomType, roomAvailability.get(roomType) - 1);
                return true;
            }
            return false;
        }

        public synchronized void releaseRoom(String roomType) {
            roomAvailability.put(roomType, roomAvailability.getOrDefault(roomType, 0) + 1);
        }

        public synchronized void displayAvailability() {
            System.out.println("\n===== Current Inventory =====");
            for (String roomType : roomAvailability.keySet()) {
                System.out.println(roomType + " : " + roomAvailability.get(roomType) + " available");
            }
        }

        public synchronized Map<String, Integer> getRoomAvailabilityMap() {
            return new HashMap<>(roomAvailability);
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

    // ---------------- UC6: Room Allocation ----------------
    static class RoomAllocationService {
        private final Inventory inventory;
        private final Map<String, Set<String>> allocatedRooms;
        private int roomCounter = 1;

        RoomAllocationService(Inventory inventory) {
            this.inventory = inventory;
            this.allocatedRooms = new HashMap<>();
        }

        public synchronized String allocateRoom(String roomType) {
            if (!inventory.allocateRoom(roomType)) return null;
            String roomId = roomType.substring(0, 2).toUpperCase() + String.format("%03d", roomCounter++);
            allocatedRooms.putIfAbsent(roomType, new HashSet<>());
            allocatedRooms.get(roomType).add(roomId);
            return roomId;
        }

        public synchronized void releaseRoom(String roomType, String roomId) {
            if (allocatedRooms.containsKey(roomType) && allocatedRooms.get(roomType).remove(roomId)) {
                inventory.releaseRoom(roomType);
            }
        }

        public synchronized void displayAllocatedRooms() {
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
        private final Map<String, List<Service>> reservationServices = new HashMap<>();

        public synchronized void addService(String roomId, Service service) {
            reservationServices.putIfAbsent(roomId, new ArrayList<>());
            reservationServices.get(roomId).add(service);
        }

        public synchronized double getTotalServiceCost(String roomId) {
            List<Service> services = reservationServices.getOrDefault(roomId, new ArrayList<>());
            double total = 0;
            for (Service s : services) total += s.cost;
            return total;
        }

        public synchronized void displayAllServices() {
            System.out.println("\n===== Add-On Services Per Reservation =====");
            for (String roomId : reservationServices.keySet()) {
                System.out.println("Room ID: " + roomId);
                for (Service s : reservationServices.get(roomId)) {
                    System.out.println("  - " + s.name + " | Cost: " + s.cost);
                }
                System.out.println("  Total Add-On Cost: " + getTotalServiceCost(roomId));
            }
        }

        public synchronized void removeServices(String roomId) {
            reservationServices.remove(roomId);
        }
    }

    // ---------------- UC8: Booking History & Reporting ----------------
    static class BookingHistory {
        private final List<Reservation> confirmedReservations = new ArrayList<>();

        public synchronized void addReservation(Reservation res) { confirmedReservations.add(res); }
        public synchronized void removeReservation(Reservation res) { confirmedReservations.remove(res); }
        public synchronized List<Reservation> getAllReservations() { return new ArrayList<>(confirmedReservations); }
    }

    static class BookingReportService {
        public void generateReport(BookingHistory history) {
            System.out.println("\n===== Booking History Report =====");
            for (Reservation res : history.getAllReservations()) {
                System.out.println("Guest: " + res.guestName + " | Room Type: " + res.roomType);
            }
            System.out.println("Total Bookings: " + history.getAllReservations().size());
        }
    }

    // ---------------- UC9: Validation ----------------
    static class InvalidBookingException extends Exception {
        public InvalidBookingException(String message) { super(message); }
    }

    static class BookingValidator {
        public static void validateRoomType(String roomType, Inventory inventory) throws InvalidBookingException {
            if (!inventory.getRoomAvailabilityMap().containsKey(roomType)) {
                throw new InvalidBookingException("Invalid room type: " + roomType);
            }
        }

        public static void validateAvailability(String roomType, Inventory inventory) throws InvalidBookingException {
            if (!inventory.isAvailable(roomType)) {
                throw new InvalidBookingException("No rooms available for type: " + roomType);
            }
        }

        public static void validateService(Service service) throws InvalidBookingException {
            if (service == null || service.name == null || service.cost < 0) {
                throw new InvalidBookingException("Invalid add-on service: " + (service != null ? service.name : "null"));
            }
        }
    }

    // ---------------- UC10: Booking Cancellation ----------------
    static class CancellationService {
        RoomAllocationService allocationService;
        BookingHistory history;
        AddOnServiceManager serviceManager;
        Map<String, String> confirmedReservations; // guestName -> roomId

        CancellationService(RoomAllocationService allocationService, BookingHistory history,
                            AddOnServiceManager serviceManager, Map<String, String> confirmedReservations) {
            this.allocationService = allocationService;
            this.history = history;
            this.serviceManager = serviceManager;
            this.confirmedReservations = confirmedReservations;
        }

        public synchronized void cancelBooking(String guestName) {
            if (!confirmedReservations.containsKey(guestName)) {
                System.out.println("No booking found for " + guestName);
                return;
            }

            String roomId = confirmedReservations.get(guestName);
            Reservation target = null;
            for (Reservation res : history.getAllReservations()) {
                if (res.guestName.equals(guestName)) {
                    target = res;
                    break;
                }
            }

            if (target != null) {
                allocationService.releaseRoom(target.roomType, roomId);
                history.removeReservation(target);
                serviceManager.removeServices(roomId);
                confirmedReservations.remove(guestName);
                System.out.println("Cancelled booking for " + guestName + " | Room ID: " + roomId);
            }
        }
    }

    // ---------------- UC11: Concurrent Booking ----------------
    static class BookingTask implements Runnable {
        Reservation request;
        RoomAllocationService allocationService;
        BookingHistory history;
        Map<String, String> confirmedReservations;

        BookingTask(Reservation request, RoomAllocationService allocationService,
                    BookingHistory history, Map<String, String> confirmedReservations) {
            this.request = request;
            this.allocationService = allocationService;
            this.history = history;
            this.confirmedReservations = confirmedReservations;
        }

        @Override
        public void run() {
            String roomId = allocationService.allocateRoom(request.roomType);
            if (roomId != null) {
                System.out.println("Reservation confirmed for " + request.guestName + " | RoomID: " + roomId);
                history.addReservation(request);
                synchronized (confirmedReservations) {
                    confirmedReservations.put(request.guestName, roomId);
                }
            } else {
                System.out.println("Sorry " + request.guestName + ", " + request.roomType + " not available.");
            }
        }
    }

    // ---------------- Main ----------------
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Book My Stay App v7.0 ===");

        // Rooms
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();
        singleRoom.displayRoom();
        doubleRoom.displayRoom();
        suiteRoom.displayRoom();

        // Inventory & Allocation
        Inventory inventory = new Inventory();
        inventory.displayAvailability();
        RoomAllocationService allocationService = new RoomAllocationService(inventory);
        BookingHistory bookingHistory = new BookingHistory();
        AddOnServiceManager serviceManager = new AddOnServiceManager();
        Map<String, String> confirmedReservations = new HashMap<>();

        // Sample concurrent booking requests
        List<Reservation> requests = Arrays.asList(
                new Reservation("Alice", "Single Room"),
                new Reservation("Bob", "Double Room"),
                new Reservation("Charlie", "Suite Room"),
                new Reservation("David", "Single Room"),
                new Reservation("Eve", "Double Room"),
                new Reservation("Frank", "Suite Room"),
                new Reservation("Grace", "Single Room")
        );

        ExecutorService executor = Executors.newFixedThreadPool(3);
        for (Reservation req : requests) {
            executor.submit(new BookingTask(req, allocationService, bookingHistory, confirmedReservations));
        }
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // Add-on services (using room ID)
        for (String guest : confirmedReservations.keySet()) {
            String roomId = confirmedReservations.get(guest);
            if (guest.equals("Alice")) serviceManager.addService(roomId, new Service("Breakfast", 200));
            if (guest.equals("Bob")) serviceManager.addService(roomId, new Service("Spa", 800));
        }

        serviceManager.displayAllServices();
        allocationService.displayAllocatedRooms();
        inventory.displayAvailability();

        // Booking history report
        BookingReportService reportService = new BookingReportService();
        reportService.generateReport(bookingHistory);

        // UC10: Cancellation demo
        System.out.println("\n--- Processing Cancellations ---");
        CancellationService cancellationService = new CancellationService(allocationService, bookingHistory, serviceManager, confirmedReservations);
        cancellationService.cancelBooking("Alice");
        cancellationService.cancelBooking("Zara"); // non-existent
        allocationService.displayAllocatedRooms();
        inventory.displayAvailability();
        reportService.generateReport(bookingHistory);
        serviceManager.displayAllServices();
    }
}