/*     */ package oshi.hardware.common;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.GlobalMemory;
/*     */ import oshi.hardware.PhysicalMemory;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.FormatUtil;
/*     */ import oshi.util.ParseUtil;
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
/*     */ @ThreadSafe
/*     */ public abstract class AbstractGlobalMemory
/*     */   implements GlobalMemory
/*     */ {
/*     */   public List<PhysicalMemory> getPhysicalMemory() {
/*  47 */     List<PhysicalMemory> pmList = new ArrayList<>();
/*  48 */     List<String> dmi = ExecutingCommand.runNative("dmidecode --type 17");
/*  49 */     int bank = 0;
/*  50 */     String bankLabel = "unknown";
/*  51 */     String locator = "";
/*  52 */     long capacity = 0L;
/*  53 */     long speed = 0L;
/*  54 */     String manufacturer = "unknown";
/*  55 */     String memoryType = "unknown";
/*  56 */     for (String line : dmi) {
/*  57 */       if (line.trim().contains("DMI type 17")) {
/*     */         
/*  59 */         if (bank++ > 0) {
/*  60 */           if (capacity > 0L) {
/*  61 */             pmList.add(new PhysicalMemory(bankLabel + locator, capacity, speed, manufacturer, memoryType));
/*     */           }
/*  63 */           bankLabel = "unknown";
/*  64 */           locator = "";
/*  65 */           capacity = 0L;
/*  66 */           speed = 0L;
/*     */         }  continue;
/*  68 */       }  if (bank > 0) {
/*  69 */         String[] split = line.trim().split(":");
/*  70 */         if (split.length == 2) {
/*  71 */           switch (split[0]) {
/*     */             case "Bank Locator":
/*  73 */               bankLabel = split[1].trim();
/*     */             
/*     */             case "Locator":
/*  76 */               locator = "/" + split[1].trim();
/*     */             
/*     */             case "Size":
/*  79 */               capacity = ParseUtil.parseDecimalMemorySizeToBinary(split[1].trim());
/*     */             
/*     */             case "Type":
/*  82 */               memoryType = split[1].trim();
/*     */             
/*     */             case "Speed":
/*  85 */               speed = ParseUtil.parseHertz(split[1]);
/*     */             
/*     */             case "Manufacturer":
/*  88 */               manufacturer = split[1].trim();
/*     */           } 
/*     */ 
/*     */ 
/*     */         
/*     */         }
/*     */       } 
/*     */     } 
/*  96 */     if (capacity > 0L) {
/*  97 */       pmList.add(new PhysicalMemory(bankLabel + locator, capacity, speed, manufacturer, memoryType));
/*     */     }
/*  99 */     return pmList;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 104 */     StringBuilder sb = new StringBuilder();
/* 105 */     sb.append("Available: ");
/* 106 */     sb.append(FormatUtil.formatBytes(getAvailable()));
/* 107 */     sb.append("/");
/* 108 */     sb.append(FormatUtil.formatBytes(getTotal()));
/* 109 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\common\AbstractGlobalMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */