package org.codehaus.plexus.util.cli;

public class DefaultConsumer implements StreamConsumer {
   public void consumeLine(String line) {
      System.out.println(line);
   }
}
