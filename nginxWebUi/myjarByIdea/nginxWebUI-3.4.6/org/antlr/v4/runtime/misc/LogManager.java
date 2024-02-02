package org.antlr.v4.runtime.misc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class LogManager {
   protected List<Record> records;

   public void log(String component, String msg) {
      Record r = new Record();
      r.component = component;
      r.msg = msg;
      if (this.records == null) {
         this.records = new ArrayList();
      }

      this.records.add(r);
   }

   public void log(String msg) {
      this.log((String)null, msg);
   }

   public void save(String filename) throws IOException {
      FileWriter fw = new FileWriter(filename);
      BufferedWriter bw = new BufferedWriter(fw);

      try {
         bw.write(this.toString());
      } finally {
         bw.close();
      }

   }

   public String save() throws IOException {
      String dir = ".";
      String defaultFilename = dir + "/antlr-" + (new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss")).format(new Date()) + ".log";
      this.save(defaultFilename);
      return defaultFilename;
   }

   public String toString() {
      if (this.records == null) {
         return "";
      } else {
         String nl = System.getProperty("line.separator");
         StringBuilder buf = new StringBuilder();
         Iterator i$ = this.records.iterator();

         while(i$.hasNext()) {
            Record r = (Record)i$.next();
            buf.append(r);
            buf.append(nl);
         }

         return buf.toString();
      }
   }

   public static void main(String[] args) throws IOException {
      LogManager mgr = new LogManager();
      mgr.log("atn", "test msg");
      mgr.log("dfa", "test msg 2");
      System.out.println(mgr);
      mgr.save();
   }

   protected static class Record {
      long timestamp = System.currentTimeMillis();
      StackTraceElement location = (new Throwable()).getStackTrace()[0];
      String component;
      String msg;

      public Record() {
      }

      public String toString() {
         StringBuilder buf = new StringBuilder();
         buf.append((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")).format(new Date(this.timestamp)));
         buf.append(" ");
         buf.append(this.component);
         buf.append(" ");
         buf.append(this.location.getFileName());
         buf.append(":");
         buf.append(this.location.getLineNumber());
         buf.append(" ");
         buf.append(this.msg);
         return buf.toString();
      }
   }
}
