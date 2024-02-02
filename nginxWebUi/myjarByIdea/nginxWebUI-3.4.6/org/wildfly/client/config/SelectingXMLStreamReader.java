package org.wildfly.client.config;

import java.util.NoSuchElementException;
import java.util.Set;

class SelectingXMLStreamReader extends AbstractDelegatingXMLStreamReader {
   private final Set<String> namespaces;
   private int state;
   private int level;
   private static final int ST_SEEKING = 0;
   private static final int ST_FOUND_PRE = 1;
   private static final int ST_FOUND = 2;
   private static final int ST_DONE = 3;

   SelectingXMLStreamReader(boolean closeDelegate, ConfigurationXMLStreamReader delegate, Set<String> namespaces) {
      super(closeDelegate, delegate);
      this.namespaces = namespaces;
   }

   public boolean hasNext() throws ConfigXMLParseException {
      switch (this.state) {
         case 0:
            while(super.hasNext()) {
               int next = super.next();
               switch (next) {
                  case 1:
                     if (this.namespaces.contains(super.getNamespaceURI())) {
                        this.state = 1;
                        return true;
                     }

                     this.getDelegate().skipContent();
                     break;
                  case 2:
                     this.state = 3;
                     return false;
               }
            }

            return false;
         case 1:
            return true;
         case 2:
            return super.hasNext();
         case 3:
            return false;
         default:
            throw new IllegalStateException();
      }
   }

   public int next() throws ConfigXMLParseException {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         switch (this.state) {
            case 1:
               this.state = 2;
               return super.getEventType();
            case 2:
               int next = super.next();
               switch (next) {
                  case 1:
                     ++this.level;
                     break;
                  case 2:
                     if (this.level-- == 0) {
                        this.state = 3;
                     }
               }

               return next;
            default:
               throw new IllegalStateException();
         }
      }
   }
}
