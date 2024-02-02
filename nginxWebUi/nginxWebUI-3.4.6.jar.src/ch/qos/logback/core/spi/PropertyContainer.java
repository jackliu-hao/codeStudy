package ch.qos.logback.core.spi;

import java.util.Map;

public interface PropertyContainer {
  String getProperty(String paramString);
  
  Map<String, String> getCopyOfPropertyMap();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\spi\PropertyContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */