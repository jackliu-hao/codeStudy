/*     */ package com.mysql.cj.conf.url;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.ConnectionUrl;
/*     */ import com.mysql.cj.conf.ConnectionUrlParser;
/*     */ import com.mysql.cj.conf.HostInfo;
/*     */ import com.mysql.cj.conf.HostsListView;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.stream.Collectors;
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
/*     */ public class XDevApiConnectionUrl
/*     */   extends ConnectionUrl
/*     */ {
/*     */   private static final int DEFAULT_PORT = 33060;
/*     */   private boolean prioritySorted = false;
/*     */   private boolean hasDuplicatedPriorities = false;
/*     */   
/*     */   public XDevApiConnectionUrl(ConnectionUrlParser connStrParser, Properties info) {
/*  71 */     super(connStrParser, info);
/*  72 */     this.type = ConnectionUrl.Type.XDEVAPI_SESSION;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  80 */     boolean first = true;
/*  81 */     String user = null;
/*  82 */     String password = null;
/*  83 */     boolean hasPriority = false;
/*  84 */     Set<Integer> priorities = new HashSet<>();
/*  85 */     for (HostInfo hi : this.hosts) {
/*  86 */       if (first) {
/*  87 */         first = false;
/*  88 */         user = hi.getUser();
/*  89 */         password = hi.getPassword();
/*  90 */         hasPriority = hi.getHostProperties().containsKey(PropertyKey.PRIORITY.getKeyName());
/*     */       } else {
/*  92 */         if (!StringUtils.nullSafeEqual(user, hi.getUser()) || !StringUtils.nullSafeEqual(password, hi.getPassword())) {
/*  93 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  94 */               Messages.getString("ConnectionString.14", new Object[] { ConnectionUrl.Type.XDEVAPI_SESSION.getScheme() }));
/*     */         }
/*  96 */         if (hasPriority ^ hi.getHostProperties().containsKey(PropertyKey.PRIORITY.getKeyName())) {
/*  97 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  98 */               Messages.getString("ConnectionString.15", new Object[] { ConnectionUrl.Type.XDEVAPI_SESSION.getScheme() }));
/*     */         }
/*     */       } 
/* 101 */       if (hasPriority) {
/*     */         try {
/* 103 */           int priority = Integer.parseInt(hi.getProperty(PropertyKey.PRIORITY.getKeyName()));
/* 104 */           if (priority < 0 || priority > 100) {
/* 105 */             throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 106 */                 Messages.getString("ConnectionString.16", new Object[] { ConnectionUrl.Type.XDEVAPI_SESSION.getScheme() }));
/*     */           }
/* 108 */           if (priorities.contains(Integer.valueOf(priority))) {
/* 109 */             this.hasDuplicatedPriorities = true; continue;
/*     */           } 
/* 111 */           priorities.add(Integer.valueOf(priority));
/*     */         }
/* 113 */         catch (NumberFormatException e) {
/* 114 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 115 */               Messages.getString("ConnectionString.16", new Object[] { ConnectionUrl.Type.XDEVAPI_SESSION.getScheme() }));
/*     */         } 
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 121 */     if (hasPriority) {
/* 122 */       this.prioritySorted = true;
/* 123 */       this.hosts.sort(
/* 124 */           Comparator.comparing(hi -> Integer.valueOf(Integer.parseInt((String)hi.getHostProperties().get(PropertyKey.PRIORITY.getKeyName())))).reversed());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void preprocessPerTypeHostProperties(Map<String, String> hostProps) {
/* 130 */     if (hostProps.containsKey(PropertyKey.ADDRESS.getKeyName())) {
/* 131 */       String address = hostProps.get(PropertyKey.ADDRESS.getKeyName());
/* 132 */       ConnectionUrlParser.Pair<String, Integer> hostPortPair = ConnectionUrlParser.parseHostPortPair(address);
/* 133 */       String host = StringUtils.safeTrim((String)hostPortPair.left);
/* 134 */       Integer port = (Integer)hostPortPair.right;
/* 135 */       if (!StringUtils.isNullOrEmpty(host) && !hostProps.containsKey(PropertyKey.HOST.getKeyName())) {
/* 136 */         hostProps.put(PropertyKey.HOST.getKeyName(), host);
/*     */       }
/* 138 */       if (port.intValue() != -1 && !hostProps.containsKey(PropertyKey.PORT.getKeyName())) {
/* 139 */         hostProps.put(PropertyKey.PORT.getKeyName(), port.toString());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getDefaultPort() {
/* 146 */     return 33060;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void fixProtocolDependencies(Map<String, String> hostProps) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public List<HostInfo> getHostsList(HostsListView view) {
/* 156 */     if (this.prioritySorted) {
/* 157 */       if (this.hasDuplicatedPriorities) {
/*     */         
/* 159 */         Map<Integer, List<HostInfo>> hostsByPriority = (Map<Integer, List<HostInfo>>)this.hosts.stream().collect(Collectors.groupingBy(hi -> Integer.valueOf((String)hi.getHostProperties().get(PropertyKey.PRIORITY.getKeyName()))));
/* 160 */         this
/*     */           
/* 162 */           .hosts = (List)hostsByPriority.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey).reversed()).map(Map.Entry::getValue).peek(Collections::shuffle).flatMap(Collection::stream).collect(Collectors.toList());
/*     */       } 
/*     */     } else {
/* 165 */       Collections.shuffle(this.hosts);
/*     */     } 
/* 167 */     return super.getHostsList(view);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\con\\url\XDevApiConnectionUrl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */