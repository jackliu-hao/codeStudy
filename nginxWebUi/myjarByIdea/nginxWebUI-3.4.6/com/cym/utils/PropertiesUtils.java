package com.cym.utils;

import cn.hutool.core.io.resource.ClassPathResource;
import com.cym.NginxWebUI;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.noear.solon.annotation.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PropertiesUtils {
   static Logger logger = LoggerFactory.getLogger(NginxWebUI.class);

   public Properties getPropertis(String name) {
      Properties properties = new Properties();

      try {
         ClassPathResource resource = new ClassPathResource(name);
         InputStream in = resource.getStream();
         properties.load(in);
      } catch (IOException var5) {
         logger.error((String)var5.getMessage(), (Throwable)var5);
      }

      return properties;
   }
}
