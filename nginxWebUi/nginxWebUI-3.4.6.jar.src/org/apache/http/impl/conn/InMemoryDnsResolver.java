/*    */ package org.apache.http.impl.conn;
/*    */ 
/*    */ import java.net.InetAddress;
/*    */ import java.net.UnknownHostException;
/*    */ import java.util.Arrays;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.commons.logging.LogFactory;
/*    */ import org.apache.http.conn.DnsResolver;
/*    */ import org.apache.http.util.Args;
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
/*    */ public class InMemoryDnsResolver
/*    */   implements DnsResolver
/*    */ {
/* 48 */   private final Log log = LogFactory.getLog(InMemoryDnsResolver.class);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private final Map<String, InetAddress[]> dnsMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public InMemoryDnsResolver() {
/* 61 */     this.dnsMap = (Map)new ConcurrentHashMap<String, InetAddress>();
/*    */   }
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
/*    */   public void add(String host, InetAddress... ips) {
/* 75 */     Args.notNull(host, "Host name");
/* 76 */     Args.notNull(ips, "Array of IP addresses");
/* 77 */     this.dnsMap.put(host, ips);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public InetAddress[] resolve(String host) throws UnknownHostException {
/* 85 */     InetAddress[] resolvedAddresses = this.dnsMap.get(host);
/* 86 */     if (this.log.isInfoEnabled()) {
/* 87 */       this.log.info("Resolving " + host + " to " + Arrays.deepToString((Object[])resolvedAddresses));
/*    */     }
/* 89 */     if (resolvedAddresses == null) {
/* 90 */       throw new UnknownHostException(host + " cannot be resolved");
/*    */     }
/* 92 */     return resolvedAddresses;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\InMemoryDnsResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */