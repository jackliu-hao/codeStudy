package org.codehaus.plexus.util.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;

public class SerializerXMLWriter implements XMLWriter {
   private final XmlSerializer serializer;
   private final String namespace;
   private final Stack elements = new Stack();
   private List exceptions;

   public SerializerXMLWriter(String namespace, XmlSerializer serializer) {
      this.serializer = serializer;
      this.namespace = namespace;
   }

   public void startElement(String name) {
      try {
         this.serializer.startTag(this.namespace, name);
         this.elements.push(name);
      } catch (IOException var3) {
         this.storeException(var3);
      }

   }

   public void addAttribute(String key, String value) {
      try {
         this.serializer.attribute(this.namespace, key, value);
      } catch (IOException var4) {
         this.storeException(var4);
      }

   }

   public void writeText(String text) {
      try {
         this.serializer.text(text);
      } catch (IOException var3) {
         this.storeException(var3);
      }

   }

   public void writeMarkup(String text) {
      try {
         this.serializer.cdsect(text);
      } catch (IOException var3) {
         this.storeException(var3);
      }

   }

   public void endElement() {
      try {
         this.serializer.endTag(this.namespace, (String)this.elements.pop());
      } catch (IOException var2) {
         this.storeException(var2);
      }

   }

   private void storeException(IOException e) {
      if (this.exceptions == null) {
         this.exceptions = new ArrayList();
      }

      this.exceptions.add(e);
   }

   public List getExceptions() {
      return this.exceptions == null ? Collections.EMPTY_LIST : this.exceptions;
   }
}
