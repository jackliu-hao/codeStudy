/*     */ package com.cym.utils;
/*     */ 
/*     */ import cn.hutool.core.date.DateUtil;
/*     */ import com.cym.ext.NetworkInfo;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.util.Date;
/*     */ import java.util.Formatter;
/*     */ import java.util.Optional;
/*     */ import java.util.Properties;
/*     */ import java.util.StringTokenizer;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NetWorkUtil
/*     */ {
/*     */   private static final int SLEEP_TIME = 2000;
/*  22 */   static Logger logger = LoggerFactory.getLogger(NetWorkUtil.class);
/*     */   
/*     */   public static NetworkInfo getNetworkDownUp() {
/*  25 */     Properties props = System.getProperties();
/*  26 */     String os = props.getProperty("os.name").toLowerCase();
/*  27 */     os = os.toLowerCase().startsWith("win") ? "windows" : "linux";
/*  28 */     Process pro = null;
/*  29 */     Runtime r = Runtime.getRuntime();
/*  30 */     BufferedReader input = null;
/*  31 */     NetworkInfo networkInfo = new NetworkInfo();
/*     */     
/*     */     try {
/*  34 */       String command = "windows".equals(os) ? "netstat -e" : "ifconfig";
/*  35 */       pro = r.exec(command);
/*  36 */       input = new BufferedReader(new InputStreamReader(pro.getInputStream()));
/*  37 */       long[] result1 = readInLine(input, os);
/*  38 */       Thread.sleep(2000L);
/*  39 */       pro.destroy();
/*  40 */       input.close();
/*  41 */       pro = r.exec(command);
/*  42 */       input = new BufferedReader(new InputStreamReader(pro.getInputStream()));
/*  43 */       long[] result2 = readInLine(input, os);
/*  44 */       networkInfo.setReceive(formatNumber((result2[0] - result1[0]) / 2048.0D));
/*  45 */       networkInfo.setSend(formatNumber((result2[1] - result1[1]) / 2048.0D));
/*     */ 
/*     */       
/*  48 */       if (networkInfo.getReceive().doubleValue() < 0.0D) {
/*  49 */         networkInfo.setReceive(Double.valueOf(0.0D - networkInfo.getReceive().doubleValue()));
/*     */       }
/*  51 */       if (networkInfo.getSend().doubleValue() < 0.0D) {
/*  52 */         networkInfo.setSend(Double.valueOf(0.0D - networkInfo.getSend().doubleValue()));
/*     */       }
/*  54 */     } catch (Exception e) {
/*  55 */       logger.error(e.getMessage(), e);
/*     */     } finally {
/*  57 */       if (input != null) {
/*     */         try {
/*  59 */           input.close();
/*  60 */         } catch (IOException e) {
/*  61 */           logger.error(e.getMessage(), e);
/*     */         } 
/*     */       }
/*  64 */       Optional.<Process>ofNullable(pro).ifPresent(p -> p.destroy());
/*     */     } 
/*  66 */     networkInfo.setTime(DateUtil.format(new Date(), "HH:mm:ss"));
/*  67 */     return networkInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   private static long[] readInLine(BufferedReader input, String osType) {
/*  72 */     long[] arr = new long[2];
/*  73 */     StringTokenizer tokenStat = null;
/*     */     try {
/*  75 */       if (osType.equals("linux")) {
/*  76 */         long rx = 0L, tx = 0L;
/*  77 */         String line = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  85 */         while ((line = input.readLine()) != null) {
/*  86 */           if (line.indexOf("RX packets") >= 0) {
/*  87 */             rx += formatLong(line); continue;
/*  88 */           }  if (line.indexOf("TX packets") >= 0) {
/*  89 */             tx += formatLong(line);
/*     */           }
/*     */         } 
/*     */         
/*  93 */         arr[0] = rx;
/*  94 */         arr[1] = tx;
/*     */       } else {
/*  96 */         input.readLine();
/*  97 */         input.readLine();
/*  98 */         input.readLine();
/*  99 */         input.readLine();
/* 100 */         tokenStat = new StringTokenizer(input.readLine());
/* 101 */         tokenStat.nextToken();
/* 102 */         arr[0] = Long.parseLong(tokenStat.nextToken());
/* 103 */         arr[1] = Long.parseLong(tokenStat.nextToken());
/*     */       } 
/* 105 */     } catch (Exception e) {
/* 106 */       logger.error(e.getMessage(), e);
/*     */     } 
/* 108 */     return arr;
/*     */   }
/*     */   
/*     */   private static long formatLong(String line) {
/* 112 */     line = line.replace("RX packets", "").replace("TX packets", "").replace(":", "").trim().split(" ")[0];
/* 113 */     return Long.parseLong(line) * 1024L;
/*     */   }
/*     */   
/*     */   private static Double formatNumber(double f) {
/* 117 */     return Double.valueOf(Double.parseDouble((new Formatter()).format("%.2f", new Object[] { Double.valueOf(f) }).toString()));
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 121 */     String line = "RX packets:8889 errors:0 dropped:0 overruns:0 frame:0";
/* 122 */     System.out.println(formatLong(line));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cy\\utils\NetWorkUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */