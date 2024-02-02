package ch.qos.logback.core.joran.event;

import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

public class SaxEvent {
   public final String namespaceURI;
   public final String localName;
   public final String qName;
   public final Locator locator;

   SaxEvent(String namespaceURI, String localName, String qName, Locator locator) {
      this.namespaceURI = namespaceURI;
      this.localName = localName;
      this.qName = qName;
      this.locator = new LocatorImpl(locator);
   }

   public String getLocalName() {
      return this.localName;
   }

   public Locator getLocator() {
      return this.locator;
   }

   public String getNamespaceURI() {
      return this.namespaceURI;
   }

   public String getQName() {
      return this.qName;
   }
}
