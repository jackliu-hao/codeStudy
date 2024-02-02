package org.codehaus.plexus.util.xml;

public interface XMLWriter {
   void startElement(String var1);

   void addAttribute(String var1, String var2);

   void writeText(String var1);

   void writeMarkup(String var1);

   void endElement();
}
