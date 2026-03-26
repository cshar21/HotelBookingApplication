/**
 * Hotel Booking Application
 * Version 2.1 - Room Initialization
 *
 * Demonstrates abstraction, inheritance, and basic availability.

 */

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

    // Abstract method
    abstract void displayRoomDetails();
}

// Single Room
class SingleRoom extends Room {

    SingleRoom() {
        super("Single Room", 1, 1000);
    }

    void displayRoomDetails() {
        System.out.println(type + " | Beds: " + beds + " | Price: " + price);
    }
}

// Double Room
class DoubleRoom extends Room {

    DoubleRoom() {
        super("Double Room", 2, 2000);
    }

    void displayRoomDetails() {
        System.out.println(type + " | Beds: " + beds + " | Price: " + price);
    }
}

// Suite Room
class SuiteRoom extends Room {

    SuiteRoom() {
        super("Suite Room", 3, 5000);
    }

    void displayRoomDetails() {
        System.out.println(type + " | Beds: " + beds + " | Price: " + price);
    }
}

// Main Class
public class HotelBookingApplication {

    public static void main(String[] args) {

        System.out.println("===== Book My Stay App v2.1 =====");

        // Create room objects (Polymorphism)
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Static availability
        int singleAvailable = 5;
        int doubleAvailable = 3;
        int suiteAvailable = 2;

        // Display details
        single.displayRoomDetails();
        System.out.println("Available: " + singleAvailable);

        doubleRoom.displayRoomDetails();
        System.out.println("Available: " + doubleAvailable);

        suite.displayRoomDetails();
        System.out.println("Available: " + suiteAvailable);
    }
}