package ch.qos.logback.core.joran.event.stax;

import ch.qos.logback.core.joran.spi.ElementPath;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.stream.Location;
import javax.xml.stream.events.Attribute;

public class StartEvent extends StaxEvent {
   List<Attribute> attributes;
   public ElementPath elementPath;

   StartEvent(ElementPath elementPath, String name, Iterator<Attribute> attributeIterator, Location location) {
      super(name, location);
      this.populateAttributes(attributeIterator);
      this.elementPath = elementPath;
   }

   private void populateAttributes(Iterator<Attribute> attributeIterator) {
      for(; attributeIterator.hasNext(); this.attributes.add(attributeIterator.next())) {
         if (this.attributes == null) {
            this.attributes = new ArrayList(2);
         }
      }

   }

   public ElementPath getElementPath() {
      return this.elementPath;
   }

   public List<Attribute> getAttributeList() {
      return this.attributes;
   }

   Attribute getAttributeByName(String name) {
      if (this.attributes == null) {
         return null;
      } else {
         Iterator var2 = this.attributes.iterator();

         Attribute attr;
         do {
            if (!var2.hasNext()) {
               return null;
            }

            attr = (Attribute)var2.next();
         } while(!name.equals(attr.getName().getLocalPart()));

         return attr;
      }
   }

   public String toString() {
      return "StartEvent(" + this.getName() + ")  [" + this.location.getLineNumber() + "," + this.location.getColumnNumber() + "]";
   }
}
