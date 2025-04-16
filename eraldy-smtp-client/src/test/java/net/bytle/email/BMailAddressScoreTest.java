package net.bytle.email;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.mail.internet.AddressException;
import net.bytle.type.Maps;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class BMailAddressScoreTest {

    @Disabled
    @Test()
    public void basicTest() throws AddressException, IOException {


        CsvMapper csvMapper = new CsvMapper(); // the mapper is the entry point
        CsvSchema schema = CsvSchema.emptySchema(); // the jackson schema (we detect the columns)
        final String hash = "80f1bfd55c";
        Path path = Paths.get("C:\\Users\\gerard\\Desktop\\members_export_" + hash + "\\subscribed_members_export_" + hash + ".csv");
        //Map<BMailAddress, EmailScore> scores = new HashMap<>();
        Map<String, Integer> rootDomainCounts = new HashMap<>();
        try (MappingIterator<String[]> csvSelectStream = csvMapper
                .readerFor(String[].class)
                // This setting will transform the json as array to get a String[]
                .with(CsvParser.Feature.WRAP_AS_ARRAY)
                .with(schema)
                .readValues(path.toFile())) {

            Integer count = 0;
            while (csvSelectStream.hasNextValue()) {
                String[] row = csvSelectStream.nextValue();
                String emailAddress = row[0];
                BMailInternetAddress email = BMailInternetAddress.of(emailAddress);
                String rootDomain = email.getDomainName().getApexName().toStringWithoutRoot();
                Integer rootDomainCount = rootDomainCounts.get(rootDomain);
                if (rootDomainCount == null) {
                    rootDomainCount = 0;
                }
                rootDomainCount++;
                rootDomainCounts.put(rootDomain, rootDomainCount);
                count++;
            }
            Assertions.assertEquals((Integer) 924, count, "count");
            Assertions.assertEquals(141, rootDomainCounts.keySet().size(), "rootDomainCount");
            for (Map.Entry<String, Integer> entry : Maps.getMapAsListEntrySortedByValue(rootDomainCounts)) {
                System.out.println(entry.getKey() + " " + entry.getValue());
            }
        }

    }

}

