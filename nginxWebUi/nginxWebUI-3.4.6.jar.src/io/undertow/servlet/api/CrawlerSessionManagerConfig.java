/*    */ package io.undertow.servlet.api;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CrawlerSessionManagerConfig
/*    */ {
/*    */   public static final String DEFAULT_CRAWLER_REGEX = ".*[bB]ot.*|.*Yahoo! Slurp.*|.*Feedfetcher-Google.*";
/*    */   private final String crawlerUserAgents;
/*    */   private final int sessionInactiveInterval;
/*    */   
/*    */   public CrawlerSessionManagerConfig() {
/* 32 */     this(60, ".*[bB]ot.*|.*Yahoo! Slurp.*|.*Feedfetcher-Google.*");
/*    */   }
/*    */   
/*    */   public CrawlerSessionManagerConfig(int sessionInactiveInterval) {
/* 36 */     this(sessionInactiveInterval, ".*[bB]ot.*|.*Yahoo! Slurp.*|.*Feedfetcher-Google.*");
/*    */   }
/*    */   
/*    */   public CrawlerSessionManagerConfig(String crawlerUserAgents) {
/* 40 */     this(60, crawlerUserAgents);
/*    */   }
/*    */   
/*    */   public CrawlerSessionManagerConfig(int sessionInactiveInterval, String crawlerUserAgents) {
/* 44 */     this.sessionInactiveInterval = sessionInactiveInterval;
/* 45 */     this.crawlerUserAgents = crawlerUserAgents;
/*    */   }
/*    */   
/*    */   public String getCrawlerUserAgents() {
/* 49 */     return this.crawlerUserAgents;
/*    */   }
/*    */   
/*    */   public int getSessionInactiveInterval() {
/* 53 */     return this.sessionInactiveInterval;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\CrawlerSessionManagerConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */