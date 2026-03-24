import java.util.*;

// =========================
// MAIN CLASS
// =========================
public class UseCase8BookingHistoryReport {

    public static void main(String[] args) {

        System.out.println("Booking History & Reporting System\n");

        RoomInventory inventory = new RoomInventory();
        BookingHistory bookingHistory = new BookingHistory();

        RoomAllocationService allocationService = new RoomAllocationService(bookingHistory);

        BookingRequestQueue queue = new BookingRequestQueue();

        queue.addRequest(new Reservation("Abhi", "Single"));
        queue.addRequest(new Reservation("Subha", "Double"));
        queue.addRequest(new Reservation("Vanmathi", "Suite"));
        queue.addRequest(new Reservation("Rahul", "Suite"));

        while (queue.hasPendingRequests()) {
            Reservation r = queue.getNextRequest();
            allocationService.allocateRoom(r, inventory);
        }

        // =========================
        // ADMIN VIEW
        // =========================
        System.out.println("\n--- Booking History ---");
        bookingHistory.displayHistory();

        System.out.println("\n--- Booking Report ---");
        BookingReportService reportService = new BookingReportService();
        reportService.generateReport(bookingHistory);
    }
}

// =========================
// ROOM CLASSES
// =========================
abstract class Room {
    protected int beds;
    protected int size;
    protected double price;

    public Room(int beds, int size, double price) {
        this.beds = beds;
        this.size = size;
        this.price = price;
    }
}

class SingleRoom extends Room {
    public SingleRoom() { super(1, 250, 1500); }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super(2, 400, 2500); }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super(3, 750, 5000); }
}

// =========================
// INVENTORY
// =========================
class RoomInventory {
    private Map<String, Integer> availability = new HashMap<>();

    public RoomInventory() {
        availability.put("Single", 2);
        availability.put("Double", 2);
        availability.put("Suite", 1);
    }

    public Map<String, Integer> getAvailability() {
        return availability;
    }

    public void update(String type, int count) {
        availability.put(type, count);
    }
}

// =========================
// RESERVATION
// =========================
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

// =========================
// QUEUE
// =========================
class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) { queue.offer(r); }
    public Reservation getNextRequest() { return queue.poll(); }
    public boolean hasPendingRequests() { return !queue.isEmpty(); }
}

// =========================
// BOOKING HISTORY (UC8 CORE)
// =========================
class BookingHistory {
    private List<Reservation> history = new ArrayList<>();

    public void add(Reservation r) {
        history.add(r);
    }

    public List<Reservation> getAll() {
        return history;
    }

    public void displayHistory() {
        if (history.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Reservation r : history) {
            System.out.println("Guest: " + r.getGuestName() +
                    " | Room: " + r.getRoomType());
        }
    }
}

// =========================
// REPORT SERVICE (UC8 CORE)
// =========================
class BookingReportService {

    public void generateReport(BookingHistory history) {

        List<Reservation> bookings = history.getAll();

        System.out.println("Total Bookings: " + bookings.size());

        Map<String, Integer> roomCount = new HashMap<>();

        for (Reservation r : bookings) {
            roomCount.put(
                    r.getRoomType(),
                    roomCount.getOrDefault(r.getRoomType(), 0) + 1
            );
        }

        System.out.println("\nBookings by Room Type:");
        for (String type : roomCount.keySet()) {
            System.out.println(type + ": " + roomCount.get(type));
        }
    }
}

// =========================
// ALLOCATION SERVICE (MODIFIED FOR UC8)
// =========================
class RoomAllocationService {

    private BookingHistory history;

    public RoomAllocationService(BookingHistory history) {
        this.history = history;
    }

    public void allocateRoom(Reservation r, RoomInventory inventory) {

        Map<String, Integer> avail = inventory.getAvailability();
        String type = r.getRoomType();

        if (avail.getOrDefault(type, 0) > 0) {

            inventory.update(type, avail.get(type) - 1);

            System.out.println("Booking Confirmed: " +
                    r.getGuestName() + " (" + type + ")");

            // ✅ UC8: STORE HISTORY
            history.add(r);

        } else {
            System.out.println("Booking Failed: " +
                    r.getGuestName() + " (" + type + ")");
        }
    }
}