package org.codehaus.plexus.util.xml;

import java.io.PrintWriter;
import java.io.Writer;

public class Xpp3DomWriter {
   public static void write(Writer writer, Xpp3Dom dom) {
      write((XMLWriter)(new PrettyPrintXMLWriter(writer)), dom);
   }

   public static void write(PrintWriter writer, Xpp3Dom dom) {
      write((XMLWriter)(new PrettyPrintXMLWriter(writer)), dom);
   }

   public static void write(XMLWriter xmlWriter, Xpp3Dom dom) {
      write(xmlWriter, dom, true);
   }

   public static void write(XMLWriter xmlWriter, Xpp3Dom dom, boolean escape) {
      xmlWriter.startElement(dom.getName());
      String[] attributeNames = dom.getAttributeNames();

      String value;
      for(int i = 0; i < attributeNames.length; ++i) {
         value = attributeNames[i];
         xmlWriter.addAttribute(value, dom.getAttribute(value));
      }

      Xpp3Dom[] children = dom.getChildren();

      for(int i = 0; i < children.length; ++i) {
         write(xmlWriter, children[i], escape);
      }

      value = dom.getValue();
      if (value != null) {
         if (escape) {
            xmlWriter.writeText(value);
         } else {
            xmlWriter.writeMarkup(value);
         }
      }

      xmlWriter.endElement();
   }
}
