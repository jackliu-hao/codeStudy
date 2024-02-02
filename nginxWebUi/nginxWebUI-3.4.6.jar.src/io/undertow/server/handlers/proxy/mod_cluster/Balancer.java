/*     */ package io.undertow.server.handlers.proxy.mod_cluster;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Balancer
/*     */ {
/*     */   private final String name;
/*     */   private final boolean stickySession;
/*     */   private final String stickySessionCookie;
/*     */   private final String stickySessionPath;
/*     */   private final boolean stickySessionRemove;
/*     */   private final boolean stickySessionForce;
/*     */   private final int waitWorker;
/*     */   private final int maxRetries;
/*     */   private final int id;
/*  76 */   private static final AtomicInteger idGen = new AtomicInteger();
/*     */   
/*     */   Balancer(BalancerBuilder b) {
/*  79 */     this.id = idGen.incrementAndGet();
/*  80 */     this.name = b.getName();
/*  81 */     this.stickySession = b.isStickySession();
/*  82 */     this.stickySessionCookie = b.getStickySessionCookie();
/*  83 */     this.stickySessionPath = b.getStickySessionPath();
/*  84 */     this.stickySessionRemove = b.isStickySessionRemove();
/*  85 */     this.stickySessionForce = b.isStickySessionForce();
/*  86 */     this.waitWorker = b.getWaitWorker();
/*  87 */     this.maxRetries = b.getMaxRetries();
/*  88 */     UndertowLogger.ROOT_LOGGER.balancerCreated(this.id, this.name, this.stickySession, this.stickySessionCookie, this.stickySessionPath, this.stickySessionRemove, this.stickySessionForce, this.waitWorker, this.maxRetries);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getId() {
/*  93 */     return this.id;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  97 */     return this.name;
/*     */   }
/*     */   
/*     */   public boolean isStickySession() {
/* 101 */     return this.stickySession;
/*     */   }
/*     */   
/*     */   public String getStickySessionCookie() {
/* 105 */     return this.stickySessionCookie;
/*     */   }
/*     */   
/*     */   public String getStickySessionPath() {
/* 109 */     return this.stickySessionPath;
/*     */   }
/*     */   
/*     */   public boolean isStickySessionRemove() {
/* 113 */     return this.stickySessionRemove;
/*     */   }
/*     */   
/*     */   public boolean isStickySessionForce() {
/* 117 */     return this.stickySessionForce;
/*     */   }
/*     */   
/*     */   public int getWaitWorker() {
/* 121 */     return this.waitWorker;
/*     */   }
/*     */   
/*     */   public int getMaxRetries() {
/* 125 */     return this.maxRetries;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public int getMaxattempts() {
/* 130 */     return this.maxRetries;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 135 */     return "balancer: Name: " + this.name + 
/* 136 */       ", Sticky: " + (this.stickySession ? 1 : 0) + " [" + 
/* 137 */       this.stickySessionCookie + "]/[" + this.stickySessionPath + 
/* 138 */       "], remove: " + (this.stickySessionRemove ? 1 : 0) + 
/* 139 */       ", force: " + (this.stickySessionForce ? 1 : 0) + 
/* 140 */       ", Timeout: " + this.waitWorker + 
/* 141 */       ", Maxtry: " + this.maxRetries;
/*     */   }
/*     */   
/*     */   static BalancerBuilder builder() {
/* 145 */     return new BalancerBuilder();
/*     */   }
/*     */   
/*     */   public static final class BalancerBuilder
/*     */   {
/* 150 */     private String name = "mycluster";
/*     */     private boolean stickySession = true;
/* 152 */     private String stickySessionCookie = "JSESSIONID";
/* 153 */     private String stickySessionPath = "jsessionid";
/*     */     private boolean stickySessionRemove = false;
/*     */     private boolean stickySessionForce = true;
/* 156 */     private int waitWorker = 0;
/* 157 */     private int maxRetries = 1;
/*     */     
/*     */     public String getName() {
/* 160 */       return this.name;
/*     */     }
/*     */     
/*     */     public BalancerBuilder setName(String name) {
/* 164 */       this.name = name;
/* 165 */       return this;
/*     */     }
/*     */     
/*     */     public boolean isStickySession() {
/* 169 */       return this.stickySession;
/*     */     }
/*     */     
/*     */     public BalancerBuilder setStickySession(boolean stickySession) {
/* 173 */       this.stickySession = stickySession;
/* 174 */       return this;
/*     */     }
/*     */     
/*     */     public String getStickySessionCookie() {
/* 178 */       return this.stickySessionCookie;
/*     */     }
/*     */     
/*     */     public BalancerBuilder setStickySessionCookie(String stickySessionCookie) {
/* 182 */       if (stickySessionCookie != null && stickySessionCookie.length() > 30) {
/* 183 */         this.stickySessionCookie = stickySessionCookie.substring(0, 30);
/* 184 */         UndertowLogger.ROOT_LOGGER.stickySessionCookieLengthTruncated(stickySessionCookie, this.stickySessionCookie);
/*     */       } else {
/* 186 */         this.stickySessionCookie = stickySessionCookie;
/*     */       } 
/* 188 */       return this;
/*     */     }
/*     */     
/*     */     public String getStickySessionPath() {
/* 192 */       return this.stickySessionPath;
/*     */     }
/*     */     
/*     */     public BalancerBuilder setStickySessionPath(String stickySessionPath) {
/* 196 */       this.stickySessionPath = stickySessionPath;
/* 197 */       return this;
/*     */     }
/*     */     
/*     */     public boolean isStickySessionRemove() {
/* 201 */       return this.stickySessionRemove;
/*     */     }
/*     */     
/*     */     public BalancerBuilder setStickySessionRemove(boolean stickySessionRemove) {
/* 205 */       this.stickySessionRemove = stickySessionRemove;
/* 206 */       return this;
/*     */     }
/*     */     
/*     */     public boolean isStickySessionForce() {
/* 210 */       return this.stickySessionForce;
/*     */     }
/*     */     
/*     */     public BalancerBuilder setStickySessionForce(boolean stickySessionForce) {
/* 214 */       this.stickySessionForce = stickySessionForce;
/* 215 */       return this;
/*     */     }
/*     */     
/*     */     public int getWaitWorker() {
/* 219 */       return this.waitWorker;
/*     */     }
/*     */     
/*     */     public BalancerBuilder setWaitWorker(int waitWorker) {
/* 223 */       this.waitWorker = waitWorker;
/* 224 */       return this;
/*     */     }
/*     */     
/*     */     public int getMaxRetries() {
/* 228 */       return this.maxRetries;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public BalancerBuilder setMaxRetries(int maxRetries) {
/* 237 */       this.maxRetries = maxRetries;
/* 238 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public int getMaxattempts() {
/* 246 */       return this.maxRetries;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public BalancerBuilder setMaxattempts(int maxattempts) {
/* 254 */       this.maxRetries = maxattempts;
/* 255 */       return this;
/*     */     }
/*     */     
/*     */     public Balancer build() {
/* 259 */       return new Balancer(this);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\mod_cluster\Balancer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */