package org.codehaus.plexus.util.xml;

import java.io.PrintWriter;
import java.io.Writer;

public class CompactXMLWriter extends PrettyPrintXMLWriter {
   public CompactXMLWriter(PrintWriter writer) {
      super(writer);
   }

   public CompactXMLWriter(Writer writer) {
      super(writer);
   }

   protected void endOfLine() {
   }
}
