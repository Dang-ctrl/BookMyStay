import java.io.*;
import java.util.*;

// =========================
// MAIN CLASS
// =========================
public class UseCase12DataPersistenceRecovery {

    public static void main(String[] args) {

        System.out.println("Data Persistence & Recovery System\n");

        PersistenceService persistenceService = new PersistenceService();

        // Try to load previous state
        SystemState state = persistenceService.loadState();

        RoomInventory inventory = state.inventory;
        BookingHistory history = state.history;

        RoomAllocationService allocationService =
                new RoomAllocationService(history);

        // New bookings
        allocationService.allocateRoom(
                new Reservation("Abhi", "Single"), inventory);

        allocationService.allocateRoom(
                new Reservation("Rahul", "Double"), inventory);

        System.out.println("\n--- Current Bookings ---");
        history.displayHistory();

        // Save state before exit
        persistenceService.saveState(new SystemState(inventory, history));

        System.out.println("\nState saved successfully!");
    }
}

// =========================
// SYSTEM STATE (SERIALIZABLE)
// =========================
class SystemState implements Serializable {
    public RoomInventory inventory;
    public BookingHistory history;

    public SystemState(RoomInventory inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }
}

// =========================
// PERSISTENCE SERVICE (UC12 CORE)
// =========================
class PersistenceService {

    private static final String FILE_NAME = "system_state.dat";

    public void saveState(SystemState state) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            oos.writeObject(state);

        } catch (IOException e) {
            System.out.println("Error saving state: " + e.getMessage());
        }
    }

    public SystemState loadState() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            System.out.println("Previous state loaded successfully!\n");
            return (SystemState) ois.readObject();

        } catch (Exception e) {
            System.out.println("No previous state found. Starting fresh.\n");
            return new SystemState(new RoomInventory(), new BookingHistory());
        }
    }
}

// =========================
// INVENTORY (SERIALIZABLE)
// =========================
class RoomInventory implements Serializable {

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
// RESERVATION (SERIALIZABLE)
// =========================
class Reservation implements Serializable {
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
// BOOKING HISTORY (SERIALIZABLE)
// =========================
class BookingHistory implements Serializable {

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