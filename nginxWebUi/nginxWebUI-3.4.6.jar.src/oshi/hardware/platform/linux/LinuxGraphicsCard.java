/*     */ package oshi.hardware.platform.linux;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.hardware.GraphicsCard;
/*     */ import oshi.hardware.common.AbstractGraphicsCard;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.tuples.Pair;
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
/*     */ @Immutable
/*     */ final class LinuxGraphicsCard
/*     */   extends AbstractGraphicsCard
/*     */ {
/*     */   LinuxGraphicsCard(String name, String deviceId, String vendor, String versionInfo, long vram) {
/*  59 */     super(name, deviceId, vendor, versionInfo, vram);
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
/*     */   public static List<GraphicsCard> getGraphicsCards() {
/*  71 */     List<LinuxGraphicsCard> cardList = getGraphicsCardsFromLspci();
/*  72 */     if (cardList.isEmpty()) {
/*  73 */       cardList = getGraphicsCardsFromLshw();
/*     */     }
/*     */     
/*  76 */     return (List)Collections.unmodifiableList(cardList);
/*     */   }
/*     */ 
/*     */   
/*     */   private static List<LinuxGraphicsCard> getGraphicsCardsFromLspci() {
/*  81 */     List<LinuxGraphicsCard> cardList = new ArrayList<>();
/*     */     
/*  83 */     List<String> lspci = ExecutingCommand.runNative("lspci -vnnm");
/*  84 */     String name = "unknown";
/*  85 */     String deviceId = "unknown";
/*  86 */     String vendor = "unknown";
/*  87 */     List<String> versionInfoList = new ArrayList<>();
/*  88 */     boolean found = false;
/*  89 */     String lookupDevice = null;
/*  90 */     for (String line : lspci) {
/*  91 */       String[] split = line.trim().split(":", 2);
/*  92 */       String prefix = split[0];
/*     */       
/*  94 */       if (prefix.equals("Class") && line.contains("VGA")) {
/*  95 */         found = true;
/*  96 */       } else if (prefix.equals("Device") && !found && split.length > 1) {
/*  97 */         lookupDevice = split[1].trim();
/*     */       } 
/*  99 */       if (found) {
/* 100 */         if (split.length < 2) {
/*     */           
/* 102 */           cardList.add(new LinuxGraphicsCard(name, deviceId, vendor, 
/* 103 */                 versionInfoList.isEmpty() ? "unknown" : String.join(", ", (Iterable)versionInfoList), 
/* 104 */                 queryLspciMemorySize(lookupDevice)));
/* 105 */           versionInfoList.clear();
/* 106 */           found = false; continue;
/*     */         } 
/* 108 */         if (prefix.equals("Device")) {
/* 109 */           Pair<String, String> pair = ParseUtil.parseLspciMachineReadable(split[1].trim());
/* 110 */           if (pair != null) {
/* 111 */             name = (String)pair.getA();
/* 112 */             deviceId = "0x" + (String)pair.getB();
/*     */           }  continue;
/* 114 */         }  if (prefix.equals("Vendor")) {
/* 115 */           Pair<String, String> pair = ParseUtil.parseLspciMachineReadable(split[1].trim());
/* 116 */           if (pair != null) {
/* 117 */             vendor = (String)pair.getA() + " (0x" + (String)pair.getB() + ")"; continue;
/*     */           } 
/* 119 */           vendor = split[1].trim(); continue;
/*     */         } 
/* 121 */         if (prefix.equals("Rev:")) {
/* 122 */           versionInfoList.add(line.trim());
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 128 */     if (found) {
/* 129 */       cardList.add(new LinuxGraphicsCard(name, deviceId, vendor, 
/* 130 */             versionInfoList.isEmpty() ? "unknown" : String.join(", ", (Iterable)versionInfoList), 
/* 131 */             queryLspciMemorySize(lookupDevice)));
/*     */     }
/* 133 */     return cardList;
/*     */   }
/*     */   
/*     */   private static long queryLspciMemorySize(String lookupDevice) {
/* 137 */     long vram = 0L;
/*     */ 
/*     */     
/* 140 */     List<String> lspciMem = ExecutingCommand.runNative("lspci -v -s " + lookupDevice);
/* 141 */     for (String mem : lspciMem) {
/* 142 */       if (mem.contains(" prefetchable")) {
/* 143 */         vram += ParseUtil.parseLspciMemorySize(mem);
/*     */       }
/*     */     } 
/* 146 */     return vram;
/*     */   }
/*     */ 
/*     */   
/*     */   private static List<LinuxGraphicsCard> getGraphicsCardsFromLshw() {
/* 151 */     List<LinuxGraphicsCard> cardList = new ArrayList<>();
/* 152 */     List<String> lshw = ExecutingCommand.runNative("lshw -C display");
/* 153 */     String name = "unknown";
/* 154 */     String deviceId = "unknown";
/* 155 */     String vendor = "unknown";
/* 156 */     List<String> versionInfoList = new ArrayList<>();
/* 157 */     long vram = 0L;
/* 158 */     int cardNum = 0;
/* 159 */     for (String line : lshw) {
/* 160 */       String[] split = line.trim().split(":");
/* 161 */       if (split[0].startsWith("*-display")) {
/*     */         
/* 163 */         if (cardNum++ > 0) {
/* 164 */           cardList.add(new LinuxGraphicsCard(name, deviceId, vendor, 
/* 165 */                 versionInfoList.isEmpty() ? "unknown" : String.join(", ", (Iterable)versionInfoList), vram));
/* 166 */           versionInfoList.clear();
/*     */         }  continue;
/* 168 */       }  if (split.length == 2) {
/* 169 */         String prefix = split[0];
/* 170 */         if (prefix.equals("product")) {
/* 171 */           name = split[1].trim(); continue;
/* 172 */         }  if (prefix.equals("vendor")) {
/* 173 */           vendor = split[1].trim(); continue;
/* 174 */         }  if (prefix.equals("version")) {
/* 175 */           versionInfoList.add(line.trim()); continue;
/* 176 */         }  if (prefix.startsWith("resources")) {
/* 177 */           vram = ParseUtil.parseLshwResourceString(split[1].trim());
/*     */         }
/*     */       } 
/*     */     } 
/* 181 */     cardList.add(new LinuxGraphicsCard(name, deviceId, vendor, 
/* 182 */           versionInfoList.isEmpty() ? "unknown" : String.join(", ", (Iterable)versionInfoList), vram));
/* 183 */     return cardList;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\linux\LinuxGraphicsCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */