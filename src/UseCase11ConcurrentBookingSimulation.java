import java.util.*;

// =========================
// MAIN CLASS
// =========================
public class UseCase11ConcurrentBookingSimulation {

    public static void main(String[] args) {

        System.out.println("Concurrent Booking Simulation\n");

        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue queue = new BookingRequestQueue();

        // Add requests (simulate multiple users)
        queue.addRequest(new Reservation("Abhi", "Single"));
        queue.addRequest(new Reservation("Rahul", "Single"));
        queue.addRequest(new Reservation("Sneha", "Single"));

        // Create multiple threads
        Thread t1 = new Thread(new BookingProcessor(queue, inventory), "Thread-1");
        Thread t2 = new Thread(new BookingProcessor(queue, inventory), "Thread-2");

        t1.start();
        t2.start();
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
// INVENTORY (THREAD SAFE)
// =========================
class RoomInventory {

    private Map<String, Integer> availability = new HashMap<>();

    public RoomInventory() {
        availability.put("Single", 2);
        availability.put("Double", 2);
        availability.put("Suite", 1);
    }

    public synchronized boolean allocate(String type) {

        int count = availability.getOrDefault(type, 0);

        if (count > 0) {
            availability.put(type, count - 1);
            return true;
        }

        return false;
    }
}

// =========================
// QUEUE (THREAD SAFE)
// =========================
class BookingRequestQueue {

    private Queue<Reservation> queue = new LinkedList<>();

    public synchronized void addRequest(Reservation r) {
        queue.offer(r);
    }

    public synchronized Reservation getNextRequest() {
        return queue.poll();
    }
}

// =========================
// BOOKING PROCESSOR (THREAD)
// =========================
class BookingProcessor implements Runnable {

    private BookingRequestQueue queue;
    private RoomInventory inventory;

    public BookingProcessor(BookingRequestQueue queue, RoomInventory inventory) {
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {

        while (true) {

            Reservation r;

            // Critical section: get request
            synchronized (queue) {
                r = queue.getNextRequest();
            }

            if (r == null) break;

            // Critical section: allocate room
            boolean success;
            synchronized (inventory) {
                success = inventory.allocate(r.getRoomType());
            }

            if (success) {
                System.out.println(Thread.currentThread().getName() +
                        " → Booking Confirmed: " +
                        r.getGuestName() + " (" + r.getRoomType() + ")");
            } else {
                System.out.println(Thread.currentThread().getName() +
                        " → Booking Failed: " +
                        r.getGuestName() + " (" + r.getRoomType() + ")");
            }

            try {
                Thread.sleep(100); // simulate delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}