package com.github.odiszapc.nginxparser;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

public class NgxDumper {
   private NgxConfig config;
   private static final int PAD_SIZE = 2;
   private static final String PAD_SYMBOL = "  ";
   private static final String LBRACE = "{";
   private static final String RBRACE = "}";
   private static final String LF = "\n";
   private static final String CRLF = "\r\n";

   public NgxDumper(NgxConfig config) {
      this.config = config;
   }

   public String dump() {
      StringWriter writer = new StringWriter();
      this.writeToStream(this.config, new PrintWriter(writer), 0);
      return writer.toString();
   }

   public void dump(OutputStream out) {
      this.writeToStream(this.config, new PrintWriter(out), 0);
   }

   private void writeToStream(NgxBlock config, PrintWriter writer, int level) {
      Iterator i$ = config.iterator();

      while(i$.hasNext()) {
         NgxEntry entry = (NgxEntry)i$.next();
         NgxEntryType type = NgxEntryType.fromClass(entry.getClass());
         switch (type) {
            case BLOCK:
               NgxBlock block = (NgxBlock)entry;
               writer.append(this.getOffset(level)).append(block.toString()).append(this.getLineEnding());
               this.writeToStream(block, writer, level + 1);
               writer.append(this.getOffset(level)).append("}").append(this.getLineEnding());
               break;
            case IF:
               NgxIfBlock ifBlock = (NgxIfBlock)entry;
               writer.append(this.getOffset(level)).append(ifBlock.toString()).append(this.getLineEnding());
               this.writeToStream(ifBlock, writer, level + 1);
               writer.append(this.getOffset(level)).append("{").append(this.getLineEnding());
            case COMMENT:
            case PARAM:
               writer.append(this.getOffset(level)).append(entry.toString()).append(this.getLineEnding());
         }
      }

      writer.flush();
   }

   public String getOffset(int level) {
      String offset = "";

      for(int i = 0; i < level; ++i) {
         offset = offset + "  ";
      }

      return offset;
   }

   public String getLineEnding() {
      return "\n";
   }
}
