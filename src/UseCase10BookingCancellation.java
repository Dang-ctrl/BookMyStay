// UseCase10BookingCancellation.java

import java.util.*;

// =========================
// MAIN CLASS
// =========================
public class UseCase10BookingCancellation {

    public static void main(String[] args) {

        System.out.println("Booking Cancellation System\n");

        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();

        RoomAllocationService allocationService =
                new RoomAllocationService(history);

        CancellationService cancellationService =
                new CancellationService(history, inventory);

        // Bookings
        Reservation r1 = new Reservation("Abhi", "Single");
        Reservation r2 = new Reservation("Rahul", "Double");

        allocationService.allocateRoom(r1, inventory);
        allocationService.allocateRoom(r2, inventory);

        System.out.println("\n--- Before Cancellation ---");
        history.displayHistory();

        // Cancel one booking
        cancellationService.cancelBooking(r1);

        System.out.println("\n--- After Cancellation ---");
        history.displayHistory();
    }
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
    private boolean isCancelled = false;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    public boolean isCancelled() { return isCancelled; }
    public void cancel() { isCancelled = true; }
}

// =========================
// BOOKING HISTORY
// =========================
class BookingHistory {
    private List<Reservation> history = new ArrayList<>();

    public void add(Reservation r) {
        history.add(r);
    }

    public List<Reservation> getAll() {
        return history;
    }

    public void remove(Reservation r) {
        history.remove(r);
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
// ALLOCATION SERVICE
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

            history.add(r);

        } else {
            System.out.println("Booking Failed: " +
                    r.getGuestName() + " (" + type + ")");
        }
    }
}

// =========================
// CANCELLATION SERVICE (UC10 CORE)
// =========================
class CancellationService {

    private BookingHistory history;
    private RoomInventory inventory;

    // Stack for rollback tracking
    private Stack<String> rollbackStack = new Stack<>();

    public CancellationService(BookingHistory history,
                               RoomInventory inventory) {
        this.history = history;
        this.inventory = inventory;
    }

    public void cancelBooking(Reservation r) {

        // Validation
        if (!history.getAll().contains(r)) {
            System.out.println("Cancellation Failed: Booking not found");
            return;
        }

        if (r.isCancelled()) {
            System.out.println("Cancellation Failed: Already cancelled");
            return;
        }

        String type = r.getRoomType();

        // Step 1: Push to rollback stack
        rollbackStack.push(type);

        // Step 2: Restore inventory
        Map<String, Integer> avail = inventory.getAvailability();
        inventory.update(type, avail.get(type) + 1);

        // Step 3: Mark as cancelled
        r.cancel();

        // Step 4: Remove from history
        history.remove(r);

        System.out.println("Booking Cancelled: " +
                r.getGuestName() + " (" + type + ")");
    }
}