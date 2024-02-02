package ch.qos.logback.core.joran.event;

import org.xml.sax.Locator;

public class BodyEvent extends SaxEvent {
   private String text;

   BodyEvent(String text, Locator locator) {
      super((String)null, (String)null, (String)null, locator);
      this.text = text;
   }

   public String getText() {
      return this.text != null ? this.text.trim() : this.text;
   }

   public String toString() {
      return "BodyEvent(" + this.getText() + ")" + this.locator.getLineNumber() + "," + this.locator.getColumnNumber();
   }

   public void append(String str) {
      this.text = this.text + str;
   }
}
