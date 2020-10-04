import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

public class GoogleProductCategoriesTest {

    private void addLine(List<String> result, String lang, FileWriter writer) throws IOException {
        writer.append("CURRENT_TIMESTAMP");
        writer.append(",");
        writer.append("CURRENT_TIMESTAMP");
        writer.append(",");
        writer.append(lang + StringUtils.leftPad(result.get(0), 8, "0"));
        writer.append(",");
        writer.append(result.get(1).equals("null") ? "null" : (lang + StringUtils.leftPad(result.get(1), 8, "0")));
        writer.append(",");
        writer.append(lang); // Eng
        writer.append(",");
        writer.append(result.get(2));
        writer.append(",");
        writer.append(StringUtils.leftPad(result.get(0), 9, "0"));
        writer.append("\n");
    }

    @Test
    public void parseProductCategoriesToChangeSetData() throws IOException {
        String pathReadEng = getClass().getResource("taxonomy-with-ids.en-US.csv").getFile();
        String pathReadInd = getClass().getResource("taxonomy-with-ids.id-ID.csv").getFile();
        List<List<String>> resultsEng = parseCsv(pathReadEng);
        List<List<String>> resultsInd = parseCsv(pathReadInd);
        assertNotNull(resultsEng);
        assertNotNull(resultsInd);
        FileWriter writer = new FileWriter(new File("t_product_category.csv"));
        writer.append("created_at,updated_at,id,parent_category_id,language_id,name,code\n");
        for (List<String> result : resultsEng) {
            addLine(result, "1", writer);
        }
        for (List<String> result : resultsInd) {
            addLine(result, "2", writer);
        }
        writer.flush();
        writer.close();
    }

    private List<List<String>> parseCsv(String pathRead) throws IOException {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(pathRead))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        }
        List<List<String>> results = new ArrayList<>();
        List<String> lastResult = null;
        List<String> lastChecked = null;
        for (List<String> line : records) {
            List<String> result = new ArrayList<>();
            if (lastChecked != null) {
                for (int col = 1; col < line.size(); col++) {
                    if (col > lastChecked.size()-1) {
                        result.add(line.get(0));
                        result.add(lastChecked.get(0));
                        result.add(line.get(col).replaceAll("\"", "").trim());
                        break;
                    }
                    if (!lastChecked.get(col).toLowerCase().equals(line.get(col).toLowerCase())) {
                        if (col == 1) {
                            result.add(line.get(0));
                            result.add("null");
                            result.add(line.get(col).replaceAll("\"", "").trim());
                        } else if (col < lastChecked.size()-1) {
                            String parent = "null";
                            for (List<String> r : results) {
                                String prev = line.get(col-1).replaceAll("\"", "").trim();
                                if (r.get(2).equals(prev)) {
                                    parent = r.get(0);
                                }
                            }
                            result.add(line.get(0));
                            result.add(parent);
                            result.add(line.get(col).replaceAll("\"", "").trim());
                        } else {
                            result.add(line.get(0));
                            result.add(lastResult.get(1));
                            result.add(line.get(col).replaceAll("\"", "").trim());
                        }
                        break;
                    }
                }
            } else {
                result.add(line.get(0));
                result.add("null");
                result.add(line.get(1).replaceAll("\"", "").trim());
            }
            results.add(result);
            lastResult = result;
            lastChecked = line;
        }
        return results;
    }

}
