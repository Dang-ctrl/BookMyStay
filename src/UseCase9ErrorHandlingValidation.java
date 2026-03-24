import java.util.*;

// =========================
// MAIN CLASS
// =========================
public class UseCase9ErrorHandlingValidation {

    public static void main(String[] args) {

        System.out.println("Error Handling & Validation System\n");

        RoomInventory inventory = new RoomInventory();

        BookingRequestQueue queue = new BookingRequestQueue();

        // VALID + INVALID INPUTS
        queue.addRequest(new Reservation("Abhi", "Single"));
        queue.addRequest(new Reservation("Subha", "InvalidType")); // ❌ invalid
        queue.addRequest(new Reservation("Rahul", "Suite"));
        queue.addRequest(new Reservation("Kiran", "Suite")); // ❌ may exceed availability

        RoomAllocationService allocationService = new RoomAllocationService();

        while (queue.hasPendingRequests()) {
            Reservation r = queue.getNextRequest();

            try {
                allocationService.allocateRoom(r, inventory);
            } catch (InvalidBookingException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }

        System.out.println("\nSystem continues running safely.");
    }
}

// =========================
// CUSTOM EXCEPTION (UC9 CORE)
// =========================
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// =========================
// VALIDATOR (UC9 CORE)
// =========================
class BookingValidator {

    public static void validate(Reservation r, RoomInventory inventory)
            throws InvalidBookingException {

        String type = r.getRoomType();

        // Validate room type
        if (!inventory.getAvailability().containsKey(type)) {
            throw new InvalidBookingException(
                    "Invalid room type: " + type
            );
        }

        // Validate availability
        int available = inventory.getAvailability().get(type);

        if (available <= 0) {
            throw new InvalidBookingException(
                    "No rooms available for type: " + type
            );
        }
    }
}

// =========================
// ROOM INVENTORY
// =========================
class RoomInventory {
    private Map<String, Integer> availability = new HashMap<>();

    public RoomInventory() {
        availability.put("Single", 1);
        availability.put("Double", 1);
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
// ROOM ALLOCATION (UPDATED FOR UC9)
// =========================
class RoomAllocationService {

    public void allocateRoom(Reservation r, RoomInventory inventory)
            throws InvalidBookingException {

        // ✅ VALIDATION FIRST (FAIL-FAST)
        BookingValidator.validate(r, inventory);

        String type = r.getRoomType();
        int available = inventory.getAvailability().get(type);

        // Safe update
        inventory.update(type, available - 1);

        System.out.println("Booking Confirmed: " +
                r.getGuestName() + " (" + type + ")");
    }
}