package ch.qos.logback.core.net;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

public abstract class SyslogAppenderBase<E> extends AppenderBase<E> {
   static final String SYSLOG_LAYOUT_URL = "http://logback.qos.ch/codes.html#syslog_layout";
   static final int MAX_MESSAGE_SIZE_LIMIT = 65000;
   Layout<E> layout;
   String facilityStr;
   String syslogHost;
   protected String suffixPattern;
   SyslogOutputStream sos;
   int port = 514;
   int maxMessageSize;
   Charset charset;

   public void start() {
      int errorCount = 0;
      if (this.facilityStr == null) {
         this.addError("The Facility option is mandatory");
         ++errorCount;
      }

      if (this.charset == null) {
         this.charset = Charset.defaultCharset();
      }

      try {
         this.sos = this.createOutputStream();
         int systemDatagramSize = this.sos.getSendBufferSize();
         if (this.maxMessageSize == 0) {
            this.maxMessageSize = Math.min(systemDatagramSize, 65000);
            this.addInfo("Defaulting maxMessageSize to [" + this.maxMessageSize + "]");
         } else if (this.maxMessageSize > systemDatagramSize) {
            this.addWarn("maxMessageSize of [" + this.maxMessageSize + "] is larger than the system defined datagram size of [" + systemDatagramSize + "].");
            this.addWarn("This may result in dropped logs.");
         }
      } catch (UnknownHostException var3) {
         this.addError("Could not create SyslogWriter", var3);
         ++errorCount;
      } catch (SocketException var4) {
         this.addWarn("Failed to bind to a random datagram socket. Will try to reconnect later.", var4);
      }

      if (this.layout == null) {
         this.layout = this.buildLayout();
      }

      if (errorCount == 0) {
         super.start();
      }

   }

   public abstract SyslogOutputStream createOutputStream() throws UnknownHostException, SocketException;

   public abstract Layout<E> buildLayout();

   public abstract int getSeverityForEvent(Object var1);

   protected void append(E eventObject) {
      if (this.isStarted()) {
         try {
            String msg = this.layout.doLayout(eventObject);
            if (msg == null) {
               return;
            }

            if (msg.length() > this.maxMessageSize) {
               msg = msg.substring(0, this.maxMessageSize);
            }

            this.sos.write(msg.getBytes(this.charset));
            this.sos.flush();
            this.postProcess(eventObject, this.sos);
         } catch (IOException var3) {
            this.addError("Failed to send diagram to " + this.syslogHost, var3);
         }

      }
   }

   protected void postProcess(Object event, OutputStream sw) {
   }

   public static int facilityStringToint(String facilityStr) {
      if ("KERN".equalsIgnoreCase(facilityStr)) {
         return 0;
      } else if ("USER".equalsIgnoreCase(facilityStr)) {
         return 8;
      } else if ("MAIL".equalsIgnoreCase(facilityStr)) {
         return 16;
      } else if ("DAEMON".equalsIgnoreCase(facilityStr)) {
         return 24;
      } else if ("AUTH".equalsIgnoreCase(facilityStr)) {
         return 32;
      } else if ("SYSLOG".equalsIgnoreCase(facilityStr)) {
         return 40;
      } else if ("LPR".equalsIgnoreCase(facilityStr)) {
         return 48;
      } else if ("NEWS".equalsIgnoreCase(facilityStr)) {
         return 56;
      } else if ("UUCP".equalsIgnoreCase(facilityStr)) {
         return 64;
      } else if ("CRON".equalsIgnoreCase(facilityStr)) {
         return 72;
      } else if ("AUTHPRIV".equalsIgnoreCase(facilityStr)) {
         return 80;
      } else if ("FTP".equalsIgnoreCase(facilityStr)) {
         return 88;
      } else if ("NTP".equalsIgnoreCase(facilityStr)) {
         return 96;
      } else if ("AUDIT".equalsIgnoreCase(facilityStr)) {
         return 104;
      } else if ("ALERT".equalsIgnoreCase(facilityStr)) {
         return 112;
      } else if ("CLOCK".equalsIgnoreCase(facilityStr)) {
         return 120;
      } else if ("LOCAL0".equalsIgnoreCase(facilityStr)) {
         return 128;
      } else if ("LOCAL1".equalsIgnoreCase(facilityStr)) {
         return 136;
      } else if ("LOCAL2".equalsIgnoreCase(facilityStr)) {
         return 144;
      } else if ("LOCAL3".equalsIgnoreCase(facilityStr)) {
         return 152;
      } else if ("LOCAL4".equalsIgnoreCase(facilityStr)) {
         return 160;
      } else if ("LOCAL5".equalsIgnoreCase(facilityStr)) {
         return 168;
      } else if ("LOCAL6".equalsIgnoreCase(facilityStr)) {
         return 176;
      } else if ("LOCAL7".equalsIgnoreCase(facilityStr)) {
         return 184;
      } else {
         throw new IllegalArgumentException(facilityStr + " is not a valid syslog facility string");
      }
   }

   public String getSyslogHost() {
      return this.syslogHost;
   }

   public void setSyslogHost(String syslogHost) {
      this.syslogHost = syslogHost;
   }

   public String getFacility() {
      return this.facilityStr;
   }

   public void setFacility(String facilityStr) {
      if (facilityStr != null) {
         facilityStr = facilityStr.trim();
      }

      this.facilityStr = facilityStr;
   }

   public int getPort() {
      return this.port;
   }

   public void setPort(int port) {
      this.port = port;
   }

   public int getMaxMessageSize() {
      return this.maxMessageSize;
   }

   public void setMaxMessageSize(int maxMessageSize) {
      this.maxMessageSize = maxMessageSize;
   }

   public Layout<E> getLayout() {
      return this.layout;
   }

   public void setLayout(Layout<E> layout) {
      this.addWarn("The layout of a SyslogAppender cannot be set directly. See also http://logback.qos.ch/codes.html#syslog_layout");
   }

   public void stop() {
      if (this.sos != null) {
         this.sos.close();
      }

      super.stop();
   }

   public String getSuffixPattern() {
      return this.suffixPattern;
   }

   public void setSuffixPattern(String suffixPattern) {
      this.suffixPattern = suffixPattern;
   }

   public Charset getCharset() {
      return this.charset;
   }

   public void setCharset(Charset charset) {
      this.charset = charset;
   }
}
