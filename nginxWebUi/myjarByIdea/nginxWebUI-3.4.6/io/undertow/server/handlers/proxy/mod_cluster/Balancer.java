package io.undertow.server.handlers.proxy.mod_cluster;

import io.undertow.UndertowLogger;
import java.util.concurrent.atomic.AtomicInteger;

public class Balancer {
   private final String name;
   private final boolean stickySession;
   private final String stickySessionCookie;
   private final String stickySessionPath;
   private final boolean stickySessionRemove;
   private final boolean stickySessionForce;
   private final int waitWorker;
   private final int maxRetries;
   private final int id;
   private static final AtomicInteger idGen = new AtomicInteger();

   Balancer(BalancerBuilder b) {
      this.id = idGen.incrementAndGet();
      this.name = b.getName();
      this.stickySession = b.isStickySession();
      this.stickySessionCookie = b.getStickySessionCookie();
      this.stickySessionPath = b.getStickySessionPath();
      this.stickySessionRemove = b.isStickySessionRemove();
      this.stickySessionForce = b.isStickySessionForce();
      this.waitWorker = b.getWaitWorker();
      this.maxRetries = b.getMaxRetries();
      UndertowLogger.ROOT_LOGGER.balancerCreated(this.id, this.name, this.stickySession, this.stickySessionCookie, this.stickySessionPath, this.stickySessionRemove, this.stickySessionForce, this.waitWorker, this.maxRetries);
   }

   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public boolean isStickySession() {
      return this.stickySession;
   }

   public String getStickySessionCookie() {
      return this.stickySessionCookie;
   }

   public String getStickySessionPath() {
      return this.stickySessionPath;
   }

   public boolean isStickySessionRemove() {
      return this.stickySessionRemove;
   }

   public boolean isStickySessionForce() {
      return this.stickySessionForce;
   }

   public int getWaitWorker() {
      return this.waitWorker;
   }

   public int getMaxRetries() {
      return this.maxRetries;
   }

   /** @deprecated */
   @Deprecated
   public int getMaxattempts() {
      return this.maxRetries;
   }

   public String toString() {
      return "balancer: Name: " + this.name + ", Sticky: " + (this.stickySession ? 1 : 0) + " [" + this.stickySessionCookie + "]/[" + this.stickySessionPath + "], remove: " + (this.stickySessionRemove ? 1 : 0) + ", force: " + (this.stickySessionForce ? 1 : 0) + ", Timeout: " + this.waitWorker + ", Maxtry: " + this.maxRetries;
   }

   static BalancerBuilder builder() {
      return new BalancerBuilder();
   }

   public static final class BalancerBuilder {
      private String name = "mycluster";
      private boolean stickySession = true;
      private String stickySessionCookie = "JSESSIONID";
      private String stickySessionPath = "jsessionid";
      private boolean stickySessionRemove = false;
      private boolean stickySessionForce = true;
      private int waitWorker = 0;
      private int maxRetries = 1;

      public String getName() {
         return this.name;
      }

      public BalancerBuilder setName(String name) {
         this.name = name;
         return this;
      }

      public boolean isStickySession() {
         return this.stickySession;
      }

      public BalancerBuilder setStickySession(boolean stickySession) {
         this.stickySession = stickySession;
         return this;
      }

      public String getStickySessionCookie() {
         return this.stickySessionCookie;
      }

      public BalancerBuilder setStickySessionCookie(String stickySessionCookie) {
         if (stickySessionCookie != null && stickySessionCookie.length() > 30) {
            this.stickySessionCookie = stickySessionCookie.substring(0, 30);
            UndertowLogger.ROOT_LOGGER.stickySessionCookieLengthTruncated(stickySessionCookie, this.stickySessionCookie);
         } else {
            this.stickySessionCookie = stickySessionCookie;
         }

         return this;
      }

      public String getStickySessionPath() {
         return this.stickySessionPath;
      }

      public BalancerBuilder setStickySessionPath(String stickySessionPath) {
         this.stickySessionPath = stickySessionPath;
         return this;
      }

      public boolean isStickySessionRemove() {
         return this.stickySessionRemove;
      }

      public BalancerBuilder setStickySessionRemove(boolean stickySessionRemove) {
         this.stickySessionRemove = stickySessionRemove;
         return this;
      }

      public boolean isStickySessionForce() {
         return this.stickySessionForce;
      }

      public BalancerBuilder setStickySessionForce(boolean stickySessionForce) {
         this.stickySessionForce = stickySessionForce;
         return this;
      }

      public int getWaitWorker() {
         return this.waitWorker;
      }

      public BalancerBuilder setWaitWorker(int waitWorker) {
         this.waitWorker = waitWorker;
         return this;
      }

      public int getMaxRetries() {
         return this.maxRetries;
      }

      public BalancerBuilder setMaxRetries(int maxRetries) {
         this.maxRetries = maxRetries;
         return this;
      }

      /** @deprecated */
      @Deprecated
      public int getMaxattempts() {
         return this.maxRetries;
      }

      /** @deprecated */
      @Deprecated
      public BalancerBuilder setMaxattempts(int maxattempts) {
         this.maxRetries = maxattempts;
         return this;
      }

      public Balancer build() {
         return new Balancer(this);
      }
   }
}
