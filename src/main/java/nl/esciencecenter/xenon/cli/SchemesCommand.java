package nl.esciencecenter.xenon.cli;

import nl.esciencecenter.xenon.AdaptorStatus;
import nl.esciencecenter.xenon.Xenon;

import com.beust.jcommander.Parameters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Parameters(commandDescription="List allowed Xenon schemes")
public class SchemesCommand {
    public void run(Xenon xenon) {
        AdaptorStatus[] adaptors = xenon.getAdaptorStatuses();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        // TODO make prettier list with scheme, description and location
        System.out.print(gson.toJson(adaptors));
    }
}
