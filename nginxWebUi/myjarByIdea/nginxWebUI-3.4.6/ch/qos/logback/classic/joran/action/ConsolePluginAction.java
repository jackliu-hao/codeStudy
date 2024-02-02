package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.net.SocketAppender;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import org.xml.sax.Attributes;

public class ConsolePluginAction extends Action {
   private static final String PORT_ATTR = "port";
   private static final Integer DEFAULT_PORT = 4321;

   public void begin(InterpretationContext ec, String name, Attributes attributes) throws ActionException {
      String portStr = attributes.getValue("port");
      Integer port = null;
      if (portStr == null) {
         port = DEFAULT_PORT;
      } else {
         try {
            port = Integer.valueOf(portStr);
         } catch (NumberFormatException var9) {
            this.addError("Port " + portStr + " in ConsolePlugin config is not a correct number");
         }
      }

      LoggerContext lc = (LoggerContext)ec.getContext();
      SocketAppender appender = new SocketAppender();
      appender.setContext(lc);
      appender.setIncludeCallerData(true);
      appender.setRemoteHost("localhost");
      appender.setPort(port);
      appender.start();
      Logger root = lc.getLogger("ROOT");
      root.addAppender(appender);
      this.addInfo("Sending LoggingEvents to the plugin using port " + port);
   }

   public void end(InterpretationContext ec, String name) throws ActionException {
   }
}
