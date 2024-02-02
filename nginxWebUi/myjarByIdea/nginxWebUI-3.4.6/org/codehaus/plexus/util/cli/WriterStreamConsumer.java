package org.codehaus.plexus.util.cli;

import java.io.PrintWriter;
import java.io.Writer;

public class WriterStreamConsumer implements StreamConsumer {
   private PrintWriter writer;

   public WriterStreamConsumer(Writer writer) {
      this.writer = new PrintWriter(writer);
   }

   public void consumeLine(String line) {
      this.writer.println(line);
      this.writer.flush();
   }
}
