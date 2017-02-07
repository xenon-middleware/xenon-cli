package nl.esciencecenter.xenon.cli;

public class QueuesOutput {
    private final String defaultQueue;
    private final String[] queues;

    public QueuesOutput(String[] queues, String defaultQueue) {
        this.queues = queues;
        this.defaultQueue = defaultQueue;
    }

    @Override
    public String toString() {
        String sep = System.getProperty("line.separator");
        return "Available queues: '" + String.join(",", queues) + "'" + sep + "Default queue: '" + defaultQueue + "'";
    }
}
