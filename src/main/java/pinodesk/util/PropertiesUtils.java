package pinodesk.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertiesUtils {

    public static Properties getApplicationProperties() {
        Properties prop = new Properties();
        try (InputStream is = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) {
                prop.load(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

}
