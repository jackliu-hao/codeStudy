package javax.mail;

import java.net.InetAddress;

public abstract class Authenticator {
   private InetAddress requestingSite;
   private int requestingPort;
   private String requestingProtocol;
   private String requestingPrompt;
   private String requestingUserName;

   private void reset() {
      this.requestingSite = null;
      this.requestingPort = -1;
      this.requestingProtocol = null;
      this.requestingPrompt = null;
      this.requestingUserName = null;
   }

   final PasswordAuthentication requestPasswordAuthentication(InetAddress addr, int port, String protocol, String prompt, String defaultUserName) {
      this.reset();
      this.requestingSite = addr;
      this.requestingPort = port;
      this.requestingProtocol = protocol;
      this.requestingPrompt = prompt;
      this.requestingUserName = defaultUserName;
      return this.getPasswordAuthentication();
   }

   protected final InetAddress getRequestingSite() {
      return this.requestingSite;
   }

   protected final int getRequestingPort() {
      return this.requestingPort;
   }

   protected final String getRequestingProtocol() {
      return this.requestingProtocol;
   }

   protected final String getRequestingPrompt() {
      return this.requestingPrompt;
   }

   protected final String getDefaultUserName() {
      return this.requestingUserName;
   }

   protected PasswordAuthentication getPasswordAuthentication() {
      return null;
   }
}
