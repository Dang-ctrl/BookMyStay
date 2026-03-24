import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

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

        // UC5
        System.out.println("\nBooking Request Queue");

        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        Reservation r1 = new Reservation("Abhi", "Single");
        Reservation r2 = new Reservation("Subha", "Single");
        Reservation r3 = new Reservation("Vanmathi", "Suite");

        bookingQueue.addRequest(r1);
        bookingQueue.addRequest(r2);
        bookingQueue.addRequest(r3);

        while (bookingQueue.hasPendingRequests()) {
            Reservation r = bookingQueue.getNextRequest();
            System.out.println("Processing booking for Guest: " + r.getGuestName() + ", Room Type: " + r.getRoomType());
        }

        // UC6
        BookingRequestQueue bookingQueue2 = new BookingRequestQueue();
        bookingQueue2.addRequest(new Reservation("Abhi", "Single"));
        bookingQueue2.addRequest(new Reservation("Subha", "Single"));
        bookingQueue2.addRequest(new Reservation("Vanmathi", "Suite"));

        RoomAllocationService allocationService = new RoomAllocationService();

        System.out.println("\nRoom Allocation Processing");

        while (bookingQueue2.hasPendingRequests()) {
            Reservation r = bookingQueue2.getNextRequest();
            allocationService.allocateRoom(r, inventory);
        }

        // =========================
        // UC7: Add-On Service Selection
        // =========================
        System.out.println("\nAdd-On Service Selection");

        AddOnServiceManager serviceManager = new AddOnServiceManager();

        AddOnService breakfast = new AddOnService("Breakfast", 500);
        AddOnService spa = new AddOnService("Spa", 1500);
        AddOnService pickup = new AddOnService("Airport Pickup", 800);

        String res1 = "RES-1";
        String res2 = "RES-2";

        serviceManager.addService(res1, breakfast);
        serviceManager.addService(res1, spa);
        serviceManager.addService(res2, pickup);

        serviceManager.displayServices(res1);
        System.out.println("Total Add-On Cost: " + serviceManager.calculateTotalCost(res1));

        serviceManager.displayServices(res2);
        System.out.println("Total Add-On Cost: " + serviceManager.calculateTotalCost(res2));
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

class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

class BookingRequestQueue {
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() { requestQueue = new LinkedList<>(); }

    public void addRequest(Reservation reservation) { requestQueue.offer(reservation); }

    public Reservation getNextRequest() { return requestQueue.poll(); }

    public boolean hasPendingRequests() { return !requestQueue.isEmpty(); }
}

class RoomAllocationService {
    private Set<String> allocatedRoomIds;
    private Map<String, Set<String>> assignedRoomsByType;

    public RoomAllocationService() {
        allocatedRoomIds = new HashSet<>();
        assignedRoomsByType = new HashMap<>();
    }

    public void allocateRoom(Reservation reservation, RoomInventory inventory) {
        String roomType = reservation.getRoomType();
        Map<String, Integer> availability = inventory.getRoomAvailability();

        if (availability.getOrDefault(roomType, 0) > 0) {
            String roomId = generateRoomId(roomType);
            allocatedRoomIds.add(roomId);
            assignedRoomsByType.computeIfAbsent(roomType, k -> new HashSet<>()).add(roomId);
            inventory.updateAvailability(roomType, availability.get(roomType) - 1);
            System.out.println("Booking confirmed for Guest: " + reservation.getGuestName() + ", Room ID: " + roomId);
        } else {
            System.out.println("Booking failed for Guest: " + reservation.getGuestName() + ", Room Type: " + roomType + " not available.");
        }
    }

    private String generateRoomId(String roomType) {
        int count = assignedRoomsByType.getOrDefault(roomType, new HashSet<>()).size() + 1;
        return roomType + "-" + count;
    }
}

// =========================
// UC7 Classes
// =========================
class AddOnService {
    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() { return serviceName; }
    public double getCost() { return cost; }
}

class AddOnServiceManager {
    private Map<String, java.util.List<AddOnService>> reservationServices;

    public AddOnServiceManager() {
        reservationServices = new HashMap<>();
    }

    public void addService(String reservationId, AddOnService service) {
        reservationServices
                .computeIfAbsent(reservationId, k -> new java.util.ArrayList<>())
                .add(service);
    }

    public void displayServices(String reservationId) {
        System.out.println("\nServices for Reservation ID: " + reservationId);

        java.util.List<AddOnService> services = reservationServices.get(reservationId);

        if (services == null || services.isEmpty()) {
            System.out.println("No add-on services selected.");
            return;
        }

        for (AddOnService service : services) {
            System.out.println("- " + service.getServiceName() + " (Cost: " + service.getCost() + ")");
        }
    }

    public double calculateTotalCost(String reservationId) {
        double total = 0;
        java.util.List<AddOnService> services = reservationServices.get(reservationId);

        if (services != null) {
            for (AddOnService service : services) {
                total += service.getCost();
            }
        }
        return total;
    }
}