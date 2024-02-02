package ch.qos.logback.classic.jmx;

import ch.qos.logback.core.joran.spi.JoranException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;

public interface JMXConfiguratorMBean {
   void reloadDefaultConfiguration() throws JoranException;

   void reloadByFileName(String var1) throws JoranException, FileNotFoundException;

   void reloadByURL(URL var1) throws JoranException;

   void setLoggerLevel(String var1, String var2);

   String getLoggerLevel(String var1);

   String getLoggerEffectiveLevel(String var1);

   List<String> getLoggerList();

   List<String> getStatuses();
}
