package org.wildfly.client.config;

import java.util.NoSuchElementException;
import javax.xml.stream.XMLStreamException;

final class ScopedXMLStreamReader extends AbstractDelegatingXMLStreamReader {
   private int level;

   ScopedXMLStreamReader(boolean closeDelegate, ConfigurationXMLStreamReader delegate) {
      super(closeDelegate, delegate);
   }

   public boolean hasNext() throws ConfigXMLParseException {
      try {
         return this.level >= 0 && this.getDelegate().hasNext();
      } catch (XMLStreamException var2) {
         throw ConfigXMLParseException.from(var2);
      }
   }

   public int next() throws ConfigXMLParseException {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         int next;
         try {
            next = this.getDelegate().next();
         } catch (XMLStreamException var3) {
            throw ConfigXMLParseException.from(var3);
         }

         switch (next) {
            case 1:
               ++this.level;
               break;
            case 2:
               --this.level;
         }

         return next;
      }
   }
}
