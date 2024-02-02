package com.cym.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.noear.solon.core.message.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TailLogThread extends Thread {
   static Logger logger = LoggerFactory.getLogger(TailLogThread.class);
   private BufferedReader reader;
   private Session session;

   public TailLogThread(InputStream in, Session session) {
      this.reader = new BufferedReader(new InputStreamReader(in));
      this.session = session;
   }

   public void run() {
      while(true) {
         try {
            String line;
            if ((line = this.reader.readLine()) != null) {
               this.session.send(line + "<br>");
               continue;
            }
         } catch (IOException var3) {
            logger.error((String)var3.getMessage(), (Throwable)var3);
         }

         return;
      }
   }
}
