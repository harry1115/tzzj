package cn.comgroup.tzmedia.server.util.property;

import java.io.IOException;
import java.util.Properties;
import javax.servlet.ServletContext;

/**
 * PropertiesUtils
 *
 * @author pcnsh197
 */
public class PropertiesUtils {

    private static Properties tzmediaProperties = null;

    /**
     * Method getProperties
     *
     * @param context
     * @return Properties
     */
    public static Properties getProperties(ServletContext context) {
        if (tzmediaProperties == null) {
            tzmediaProperties = new Properties();
            String configProperties = "/WEB-INF/tzmedia.properties";
            try {
                tzmediaProperties.load(context.getResourceAsStream(configProperties));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return tzmediaProperties;
    }
}
