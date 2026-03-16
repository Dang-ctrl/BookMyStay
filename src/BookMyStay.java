import java.util.HashMap;
import java.util.Map;

public class BookMyStay {
    public static void main(String[] args) {
        // UC1
        System.out.println("Welcome to the Hotel Booking Management System");
        System.out.println("System initialized successfully.");

        // UC2
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        int singleAvailable = 5;
        int doubleAvailable = 3;
        int suiteAvailable = 2;

        System.out.println("\nHotel Room Initialization");

        System.out.println("\nSingle Room:");
        singleRoom.displayRoomDetails();
        System.out.println("Available: " + singleAvailable);

        System.out.println("\nDouble Room:");
        doubleRoom.displayRoomDetails();
        System.out.println("Available: " + doubleAvailable);

        System.out.println("\nSuite Room:");
        suiteRoom.displayRoomDetails();
        System.out.println("Available: " + suiteAvailable);

        // UC3
        RoomInventory inventory = new RoomInventory();

        System.out.println("\nHotel Room Inventory Status");

        System.out.println("\nSingle Room:");
        singleRoom.displayRoomDetails();
        System.out.println("Available Rooms: " + inventory.getRoomAvailability().get("Single"));

        System.out.println("\nDouble Room:");
        doubleRoom.displayRoomDetails();
        System.out.println("Available Rooms: " + inventory.getRoomAvailability().get("Double"));

        System.out.println("\nSuite Room:");
        suiteRoom.displayRoomDetails();
        System.out.println("Available Rooms: " + inventory.getRoomAvailability().get("Suite"));

        // UC4
        RoomSearchService searchService = new RoomSearchService();

        System.out.println("\nRoom Search");
        searchService.searchAvailableRooms(inventory, singleRoom, doubleRoom, suiteRoom);
    }
}

abstract class Room {
    protected int numberOfBeds;
    protected int squareFeet;
    protected double pricePerNight;

    public Room(int numberOfBeds, int squareFeet, double pricePerNight) {
        this.numberOfBeds = numberOfBeds;
        this.squareFeet = squareFeet;
        this.pricePerNight = pricePerNight;
    }

    public void displayRoomDetails() {
        System.out.println("Beds: " + numberOfBeds);
        System.out.println("Size: " + squareFeet + " sqft");
        System.out.println("Price per night: " + pricePerNight);
    }
}

class SingleRoom extends Room {
    public SingleRoom() { super(1, 250, 1500.0); }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super(2, 400, 2500.0); }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super(3, 750, 5000.0); }
}

class RoomInventory {
    private Map<String, Integer> roomAvailability;

    public RoomInventory() {
        roomAvailability = new HashMap<>();
        initializeInventory();
    }

    private void initializeInventory() {
        roomAvailability.put("Single", 5);
        roomAvailability.put("Double", 3);
        roomAvailability.put("Suite", 2);
    }

    public Map<String, Integer> getRoomAvailability() { return roomAvailability; }

    public void updateAvailability(String roomType, int count) { roomAvailability.put(roomType, count); }
}

class RoomSearchService {
    public void searchAvailableRooms(
            RoomInventory inventory,
            Room singleRoom,
            Room doubleRoom,
            Room suiteRoom) {

        Map<String, Integer> availability = inventory.getRoomAvailability();

        if (availability.get("Single") > 0) {
            System.out.println("\nSingle Room:");
            singleRoom.displayRoomDetails();
            System.out.println("Available: " + availability.get("Single"));
        }

        if (availability.get("Double") > 0) {
            System.out.println("\nDouble Room:");
            doubleRoom.displayRoomDetails();
            System.out.println("Available: " + availability.get("Double"));
        }

        if (availability.get("Suite") > 0) {
            System.out.println("\nSuite Room:");
            suiteRoom.displayRoomDetails();
            System.out.println("Available: " + availability.get("Suite"));
        }
    }
}