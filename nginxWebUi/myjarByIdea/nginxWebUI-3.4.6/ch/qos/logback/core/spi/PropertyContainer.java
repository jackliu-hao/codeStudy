package ch.qos.logback.core.spi;

import java.util.Map;

public interface PropertyContainer {
   String getProperty(String var1);

   Map<String, String> getCopyOfPropertyMap();
}
