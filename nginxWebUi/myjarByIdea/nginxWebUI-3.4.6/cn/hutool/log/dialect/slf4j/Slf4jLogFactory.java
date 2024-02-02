package cn.hutool.log.dialect.slf4j;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLoggerFactory;

public class Slf4jLogFactory extends LogFactory {
   public Slf4jLogFactory() {
      this(true);
   }

   public Slf4jLogFactory(boolean failIfNOP) {
      super("Slf4j");
      this.checkLogExist(LoggerFactory.class);
      if (failIfNOP) {
         final StringBuilder buf = new StringBuilder();
         PrintStream err = System.err;

         try {
            System.setErr(new PrintStream(new OutputStream() {
               public void write(int b) {
                  buf.append((char)b);
               }
            }, true, "US-ASCII"));
         } catch (UnsupportedEncodingException var8) {
            throw new Error(var8);
         }

         try {
            if (LoggerFactory.getILoggerFactory() instanceof NOPLoggerFactory) {
               throw new NoClassDefFoundError(buf.toString());
            }

            err.print(buf);
            err.flush();
         } finally {
            System.setErr(err);
         }

      }
   }

   public Log createLog(String name) {
      return new Slf4jLog(name);
   }

   public Log createLog(Class<?> clazz) {
      return new Slf4jLog(clazz);
   }
}
