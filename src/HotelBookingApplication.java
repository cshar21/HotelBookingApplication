import java.io.*;
import java.util.*;

/**
 * UseCase12DataPersistenceRecovery - Book My Stay App UC12
 * Demonstrates persistence and recovery of hotel booking system state.
 */
public class UseCase12DataPersistenceRecovery {

    // ---------------- UC2: Room Classes ----------------
    static abstract class Room implements Serializable {
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
    static class Inventory implements Serializable {
        private final Map<String, Integer> roomAvailability;

        Inventory() {
            roomAvailability = new HashMap<>();
            roomAvailability.put("Single Room", 5);
            roomAvailability.put("Double Room", 3);
            roomAvailability.put("Suite Room", 2);
        }

        public boolean isAvailable(String roomType) {
            return roomAvailability.getOrDefault(roomType, 0) > 0;
        }

        public boolean allocateRoom(String roomType) {
            if (isAvailable(roomType)) {
                roomAvailability.put(roomType, roomAvailability.get(roomType) - 1);
                return true;
            }
            return false;
        }

        public void releaseRoom(String roomType) {
            roomAvailability.put(roomType, roomAvailability.getOrDefault(roomType, 0) + 1);
        }

        public void displayAvailability() {
            System.out.println("\n===== Current Inventory =====");
            for (String roomType : roomAvailability.keySet()) {
                System.out.println(roomType + " : " + roomAvailability.get(roomType) + " available");
            }
        }

        public Map<String, Integer> getRoomAvailabilityMap() {
            return roomAvailability;
        }
    }

    // ---------------- UC5: Booking Request ----------------
    static class Reservation implements Serializable {
        String guestName;
        String roomType;

        Reservation(String guestName, String roomType) {
            this.guestName = guestName;
            this.roomType = roomType;
        }
    }

    // ---------------- UC8: Booking History ----------------
    static class BookingHistory implements Serializable {
        private final List<Reservation> confirmedReservations = new ArrayList<>();

        public void addReservation(Reservation res) { confirmedReservations.add(res); }
        public void removeReservation(Reservation res) { confirmedReservations.remove(res); }
        public List<Reservation> getAllReservations() { return new ArrayList<>(confirmedReservations); }

        public void displayAll() {
            System.out.println("\n===== Booking History =====");
            for (Reservation res : confirmedReservations) {
                System.out.println("Guest: " + res.guestName + " | Room Type: " + res.roomType);
            }
        }
    }

    // ---------------- UC12: Persistence Service ----------------
    static class PersistenceService {
        private final String filename;

        PersistenceService(String filename) {
            this.filename = filename;
        }

        public void saveState(Inventory inventory, BookingHistory history) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
                out.writeObject(inventory);
                out.writeObject(history);
                System.out.println("System state saved successfully to " + filename);
            } catch (IOException e) {
                System.err.println("Failed to save system state: " + e.getMessage());
            }
        }

        public Object[] loadState() {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
                Inventory inventory = (Inventory) in.readObject();
                BookingHistory history = (BookingHistory) in.readObject();
                System.out.println("System state loaded successfully from " + filename);
                return new Object[]{inventory, history};
            } catch (FileNotFoundException e) {
                System.out.println("Persistence file not found. Starting with fresh state.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Failed to load system state: " + e.getMessage());
            }
            return null;
        }
    }

    // ---------------- Main ----------------
    public static void main(String[] args) {
        System.out.println("=== Book My Stay App UC12 ===");

        PersistenceService persistenceService = new PersistenceService("hotel_state.dat");

        // Attempt to restore previous state
        Object[] restored = persistenceService.loadState();
        Inventory inventory;
        BookingHistory bookingHistory;

        if (restored != null) {
            inventory = (Inventory) restored[0];
            bookingHistory = (BookingHistory) restored[1];
        } else {
            inventory = new Inventory();
            bookingHistory = new BookingHistory();
        }

        // Display current inventory and history
        inventory.displayAvailability();
        bookingHistory.displayAll();

        // Simulate a few bookings
        List<Reservation> requests = Arrays.asList(
                new Reservation("Alice", "Single Room"),
                new Reservation("Bob", "Double Room"),
                new Reservation("Charlie", "Suite Room")
        );

        for (Reservation req : requests) {
            if (inventory.allocateRoom(req.roomType)) {
                bookingHistory.addReservation(req);
                System.out.println("Booking confirmed for " + req.guestName + " | Room: " + req.roomType);
            } else {
                System.out.println("Sorry " + req.guestName + ", " + req.roomType + " not available.");
            }
        }

        // Display updated state
        inventory.displayAvailability();
        bookingHistory.displayAll();

        // Save state before shutdown
        persistenceService.saveState(inventory, bookingHistory);

        System.out.println("System shutdown complete. State persisted.");
    }
}