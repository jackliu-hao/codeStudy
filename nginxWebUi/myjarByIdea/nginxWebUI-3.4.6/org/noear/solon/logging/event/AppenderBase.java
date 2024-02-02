package org.noear.solon.logging.event;

public abstract class AppenderBase implements Appender {
   private String name;

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void start() {
   }

   public void stop() {
      Appender.super.stop();
   }
}
