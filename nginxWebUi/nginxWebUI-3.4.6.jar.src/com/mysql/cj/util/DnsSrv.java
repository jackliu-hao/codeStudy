/*     */ package com.mysql.cj.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.Random;
/*     */ import java.util.stream.Collectors;
/*     */ import javax.naming.NamingException;
/*     */ import javax.naming.directory.Attribute;
/*     */ import javax.naming.directory.Attributes;
/*     */ import javax.naming.directory.DirContext;
/*     */ import javax.naming.directory.InitialDirContext;
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
/*     */ public class DnsSrv
/*     */ {
/*     */   public static class SrvRecord
/*     */     implements Comparable<SrvRecord>
/*     */   {
/*     */     private final int priority;
/*     */     private final int weight;
/*     */     private final int port;
/*     */     private final String target;
/*     */     
/*     */     public static SrvRecord buildFrom(String srvLine) {
/*  53 */       String[] srvParts = srvLine.split("\\s+");
/*  54 */       if (srvParts.length == 4) {
/*  55 */         int priority = Integer.parseInt(srvParts[0]);
/*  56 */         int weight = Integer.parseInt(srvParts[1]);
/*  57 */         int port = Integer.parseInt(srvParts[2]);
/*  58 */         String target = srvParts[3].replaceFirst("\\.$", "");
/*  59 */         return new SrvRecord(priority, weight, port, target);
/*     */       } 
/*  61 */       return null;
/*     */     }
/*     */     
/*     */     public SrvRecord(int priority, int weight, int port, String target) {
/*  65 */       this.priority = priority;
/*  66 */       this.weight = weight;
/*  67 */       this.port = port;
/*  68 */       this.target = target;
/*     */     }
/*     */     
/*     */     public int getPriority() {
/*  72 */       return this.priority;
/*     */     }
/*     */     
/*     */     public int getWeight() {
/*  76 */       return this.weight;
/*     */     }
/*     */     
/*     */     public int getPort() {
/*  80 */       return this.port;
/*     */     }
/*     */     
/*     */     public String getTarget() {
/*  84 */       return this.target;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/*  89 */       return String.format("{\"Priority\": %d, \"Weight\": %d, \"Port\": %d, \"Target\": \"%s\"}", new Object[] { Integer.valueOf(getPriority()), Integer.valueOf(getWeight()), Integer.valueOf(getPort()), getTarget() });
/*     */     }
/*     */ 
/*     */     
/*     */     public int compareTo(SrvRecord o) {
/*  94 */       int priorityDiff = getPriority() - o.getPriority();
/*  95 */       return (priorityDiff == 0) ? (getWeight() - o.getWeight()) : priorityDiff;
/*     */     }
/*     */   }
/*     */   
/*     */   public static List<SrvRecord> lookupSrvRecords(String serviceName) throws NamingException {
/* 100 */     List<SrvRecord> srvRecords = new ArrayList<>();
/*     */     
/* 102 */     Properties environment = new Properties();
/* 103 */     environment.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
/* 104 */     DirContext context = new InitialDirContext(environment);
/* 105 */     Attributes dnsEntries = context.getAttributes(serviceName, new String[] { "SRV" });
/* 106 */     if (dnsEntries != null) {
/* 107 */       Attribute hosts = dnsEntries.get("SRV");
/* 108 */       if (hosts != null) {
/* 109 */         for (int i = 0; i < hosts.size(); i++) {
/* 110 */           srvRecords.add(SrvRecord.buildFrom((String)hosts.get(i)));
/*     */         }
/*     */       }
/*     */     } 
/*     */     
/* 115 */     return sortSrvRecords(srvRecords);
/*     */   }
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
/*     */   public static List<SrvRecord> sortSrvRecords(List<SrvRecord> srvRecords) {
/* 128 */     List<SrvRecord> srvRecordsSortedNatural = (List<SrvRecord>)srvRecords.stream().sorted().collect(Collectors.toList());
/*     */ 
/*     */     
/* 131 */     Random random = new Random(System.nanoTime());
/* 132 */     List<SrvRecord> srvRecordsSortedRfc2782 = new ArrayList<>();
/*     */     
/* 134 */     List<Integer> priorities = (List<Integer>)srvRecordsSortedNatural.stream().map(SrvRecord::getPriority).distinct().collect(Collectors.toList());
/* 135 */     for (Integer priority : priorities) {
/* 136 */       List<SrvRecord> srvRecordsSamePriority = (List<SrvRecord>)srvRecordsSortedNatural.stream().filter(s -> (s.getPriority() == priority.intValue())).collect(Collectors.toList());
/* 137 */       while (srvRecordsSamePriority.size() > 1) {
/* 138 */         int recCount = srvRecordsSamePriority.size();
/* 139 */         int sumOfWeights = 0;
/* 140 */         int[] weights = new int[recCount];
/* 141 */         for (int i = 0; i < recCount; i++) {
/* 142 */           sumOfWeights += ((SrvRecord)srvRecordsSamePriority.get(i)).getWeight();
/* 143 */           weights[i] = sumOfWeights;
/*     */         } 
/* 145 */         int selection = random.nextInt(sumOfWeights + 1);
/* 146 */         int pos = 0;
/* 147 */         for (; pos < recCount && weights[pos] < selection; pos++);
/*     */         
/* 149 */         srvRecordsSortedRfc2782.add(srvRecordsSamePriority.remove(pos));
/*     */       } 
/* 151 */       srvRecordsSortedRfc2782.add(srvRecordsSamePriority.get(0));
/*     */     } 
/*     */     
/* 154 */     return srvRecordsSortedRfc2782;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\c\\util\DnsSrv.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */