import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class ProductBarcodeTest {

    private void addLine(List<String> result, FileWriter writer) throws IOException {
        writer.append("CURRENT_TIMESTAMP");
        writer.append(",");
        writer.append("CURRENT_TIMESTAMP");
        writer.append(",");
        writer.append(result.get(0));
        writer.append(",");
        writer.append(result.get(1));
        writer.append(",");
        writer.append("1");
        writer.append(",");
        writer.append("PCS");
        writer.append("\n");
    }

    @Test
    public void parseProductCategoriesToChangeSetData() throws IOException {
        String path = getClass().getResource("product-barcode.txt").getFile();
        List<List<String>> parsed = parseTxt(path);
        assertNotNull(parsed);
        FileWriter writer = new FileWriter(new File("t_product.csv"));
        writer.append("created_at,updated_at,code,name,unit_id,unit_label\n");
        for (List<String> result : parsed) {
            addLine(result, writer);
        }
        writer.flush();
        writer.close();
    }

    private List<List<String>> parseTxt(String path) throws IOException {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                int whitespaceIdx = line.indexOf(" ");
                String barcode = line.substring(0, whitespaceIdx);
                String name = line.substring(whitespaceIdx + 1, line.length());
                if (barcode.length() >= 11) {
                    records.add(Arrays.asList(barcode, name.toUpperCase()));
                }
            }
        }
        return records;
    }

}
