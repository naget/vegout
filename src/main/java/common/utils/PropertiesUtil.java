package common.utils;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 获取配置工具类
 * @author tianfeng5
 */
@Slf4j
public class PropertiesUtil {

    private static Properties props;
    static {
        String fileName = "configs.properties";
        props = new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
        } catch (IOException e) {
            log.error("配置文件读取异常",e);
        }
    }

    public static String getProperty(String key, String defaultValue) {
        String value= props.getProperty(key.trim());
        if (StringUtils.isBlank(value)){
            return defaultValue;
        }
        return value.trim();

    }
    public static String getProperty(String key){
        return getProperty(key, null);
    }
}
