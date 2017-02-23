package nl.esciencecenter.xenon.cli;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class ListJobsOutputTest {

    @Test
    public void test_equal() {
        List<String> jobs1 = Arrays.stream(new String[]{"job1", "job2"}).collect(Collectors.toList());
        ListJobsOutput list1 = new ListJobsOutput("somewhere", "default", jobs1);
        List<String> jobs2 = Arrays.stream(new String[]{"job1", "job2"}).collect(Collectors.toList());
        ListJobsOutput list2 = new ListJobsOutput("somewhere", "default", jobs2);
        assertEquals(list1, list2);
    }

    @Test
    public void test_toString() {
        List<String> jobs1 = Arrays.stream(new String[]{"job1", "job2"}).collect(Collectors.toList());
        ListJobsOutput list1 = new ListJobsOutput("somewhere", "default", jobs1);

        String sep = System.getProperty("line.separator");
        String expected = "job1" + sep + "job2" + sep;
        assertEquals(expected, list1.toString());
    }
}
