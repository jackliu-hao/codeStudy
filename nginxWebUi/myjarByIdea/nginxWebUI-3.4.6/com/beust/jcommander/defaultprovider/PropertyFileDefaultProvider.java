package com.beust.jcommander.defaultprovider;

import com.beust.jcommander.IDefaultProvider;
import com.beust.jcommander.ParameterException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class PropertyFileDefaultProvider implements IDefaultProvider {
   public static final String DEFAULT_FILE_NAME = "jcommander.properties";
   private Properties m_properties;

   public PropertyFileDefaultProvider() {
      this.init("jcommander.properties");
   }

   public PropertyFileDefaultProvider(String fileName) {
      this.init(fileName);
   }

   private void init(String fileName) {
      try {
         this.m_properties = new Properties();
         URL url = ClassLoader.getSystemResource(fileName);
         if (url != null) {
            this.m_properties.load(url.openStream());
         } else {
            throw new ParameterException("Could not find property file: " + fileName + " on the class path");
         }
      } catch (IOException var3) {
         throw new ParameterException("Could not open property file: " + fileName);
      }
   }

   public String getDefaultValueFor(String optionName) {
      int index;
      for(index = 0; index < optionName.length() && !Character.isLetterOrDigit(optionName.charAt(index)); ++index) {
      }

      String key = optionName.substring(index);
      return this.m_properties.getProperty(key);
   }
}
