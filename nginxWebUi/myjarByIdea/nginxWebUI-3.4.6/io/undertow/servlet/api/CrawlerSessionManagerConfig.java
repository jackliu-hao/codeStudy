package io.undertow.servlet.api;

public class CrawlerSessionManagerConfig {
   public static final String DEFAULT_CRAWLER_REGEX = ".*[bB]ot.*|.*Yahoo! Slurp.*|.*Feedfetcher-Google.*";
   private final String crawlerUserAgents;
   private final int sessionInactiveInterval;

   public CrawlerSessionManagerConfig() {
      this(60, ".*[bB]ot.*|.*Yahoo! Slurp.*|.*Feedfetcher-Google.*");
   }

   public CrawlerSessionManagerConfig(int sessionInactiveInterval) {
      this(sessionInactiveInterval, ".*[bB]ot.*|.*Yahoo! Slurp.*|.*Feedfetcher-Google.*");
   }

   public CrawlerSessionManagerConfig(String crawlerUserAgents) {
      this(60, crawlerUserAgents);
   }

   public CrawlerSessionManagerConfig(int sessionInactiveInterval, String crawlerUserAgents) {
      this.sessionInactiveInterval = sessionInactiveInterval;
      this.crawlerUserAgents = crawlerUserAgents;
   }

   public String getCrawlerUserAgents() {
      return this.crawlerUserAgents;
   }

   public int getSessionInactiveInterval() {
      return this.sessionInactiveInterval;
   }
}
