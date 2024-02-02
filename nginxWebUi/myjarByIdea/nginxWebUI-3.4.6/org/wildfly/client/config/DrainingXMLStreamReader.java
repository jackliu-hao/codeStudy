package org.wildfly.client.config;

final class DrainingXMLStreamReader extends AbstractDelegatingXMLStreamReader {
   DrainingXMLStreamReader(boolean closeDelegate, ConfigurationXMLStreamReader delegate) {
      super(closeDelegate, delegate);
   }

   public void close() throws ConfigXMLParseException {
      try {
         while(this.hasNext()) {
            this.next();
         }

         super.close();
      } catch (Throwable var4) {
         try {
            super.close();
         } catch (Throwable var3) {
            var4.addSuppressed(var3);
         }

         throw var4;
      }
   }
}
