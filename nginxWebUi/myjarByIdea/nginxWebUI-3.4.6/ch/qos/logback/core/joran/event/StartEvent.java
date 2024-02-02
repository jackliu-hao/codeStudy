package ch.qos.logback.core.joran.event;

import ch.qos.logback.core.joran.spi.ElementPath;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.helpers.AttributesImpl;

public class StartEvent extends SaxEvent {
   public final Attributes attributes;
   public final ElementPath elementPath;

   StartEvent(ElementPath elementPath, String namespaceURI, String localName, String qName, Attributes attributes, Locator locator) {
      super(namespaceURI, localName, qName, locator);
      this.attributes = new AttributesImpl(attributes);
      this.elementPath = elementPath;
   }

   public Attributes getAttributes() {
      return this.attributes;
   }

   public String toString() {
      StringBuilder b = new StringBuilder("StartEvent(");
      b.append(this.getQName());
      if (this.attributes != null) {
         for(int i = 0; i < this.attributes.getLength(); ++i) {
            if (i > 0) {
               b.append(' ');
            }

            b.append(this.attributes.getLocalName(i)).append("=\"").append(this.attributes.getValue(i)).append("\"");
         }
      }

      b.append(")  [");
      b.append(this.locator.getLineNumber());
      b.append(",");
      b.append(this.locator.getColumnNumber());
      b.append("]");
      return b.toString();
   }
}
