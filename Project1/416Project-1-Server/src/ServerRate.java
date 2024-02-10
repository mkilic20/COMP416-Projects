import java.util.LinkedList;
import java.util.Queue;

public class ServerRate {
    private final Queue<Long> timestampsOfRequests;
    private final long maximumRequests;
    private final long durationInSeconds;

    public ServerRate(long maximumRequests, long durationInSeconds) {
        this.maximumRequests = maximumRequests;
        this.durationInSeconds = durationInSeconds;
        this.timestampsOfRequests = new LinkedList<>();
    }

    public synchronized boolean attemptLogin() {
        long currentTime = System.currentTimeMillis();

        while (!timestampsOfRequests.isEmpty() && currentTime - timestampsOfRequests.peek() > durationInSeconds * 1000) {
            timestampsOfRequests.remove();
        }

        if (timestampsOfRequests.size() < maximumRequests) {
            timestampsOfRequests.offer(currentTime);
            return true;
        }
        return false;
    }
}
