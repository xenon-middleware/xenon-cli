package nl.esciencecenter.xenon.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
        List<String> lines = new ArrayList<>();
        lines.add("Available queues: " + String.join(", ", queues));
        if (defaultQueue != null) {
            lines.add("Default queue: " + defaultQueue + "");
        }
        return String.join(sep, lines);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueuesOutput that = (QueuesOutput) o;
        return Objects.equals(defaultQueue, that.defaultQueue) &&
                Arrays.equals(queues, that.queues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultQueue, queues);
    }
}
