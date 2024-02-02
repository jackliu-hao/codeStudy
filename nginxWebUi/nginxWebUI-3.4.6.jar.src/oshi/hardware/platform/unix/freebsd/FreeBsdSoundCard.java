/*    */ package oshi.hardware.platform.unix.freebsd;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import oshi.annotation.concurrent.Immutable;
/*    */ import oshi.hardware.SoundCard;
/*    */ import oshi.hardware.common.AbstractSoundCard;
/*    */ import oshi.util.ExecutingCommand;
/*    */ import oshi.util.ParseUtil;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Immutable
/*    */ final class FreeBsdSoundCard
/*    */   extends AbstractSoundCard
/*    */ {
/*    */   private static final String LSHAL = "lshal";
/*    */   
/*    */   FreeBsdSoundCard(String kernelVersion, String name, String codec) {
/* 57 */     super(kernelVersion, name, codec);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static List<SoundCard> getSoundCards() {
/* 68 */     Map<String, String> vendorMap = new HashMap<>();
/* 69 */     Map<String, String> productMap = new HashMap<>();
/* 70 */     vendorMap.clear();
/* 71 */     productMap.clear();
/* 72 */     List<String> sounds = new ArrayList<>();
/* 73 */     String key = "";
/* 74 */     for (String line : ExecutingCommand.runNative("lshal")) {
/* 75 */       line = line.trim();
/* 76 */       if (line.startsWith("udi =")) {
/*    */         
/* 78 */         key = ParseUtil.getSingleQuoteStringValue(line); continue;
/* 79 */       }  if (!key.isEmpty() && !line.isEmpty()) {
/* 80 */         if (line.contains("freebsd.driver =") && "pcm".equals(ParseUtil.getSingleQuoteStringValue(line))) {
/* 81 */           sounds.add(key); continue;
/* 82 */         }  if (line.contains("info.product")) {
/* 83 */           productMap.put(key, ParseUtil.getStringBetween(line, '\'')); continue;
/* 84 */         }  if (line.contains("info.vendor")) {
/* 85 */           vendorMap.put(key, ParseUtil.getStringBetween(line, '\''));
/*    */         }
/*    */       } 
/*    */     } 
/* 89 */     List<SoundCard> soundCards = new ArrayList<>();
/* 90 */     for (String _key : sounds) {
/* 91 */       soundCards.add(new FreeBsdSoundCard(productMap.get(_key), (String)vendorMap.get(_key) + " " + (String)productMap.get(_key), productMap
/* 92 */             .get(_key)));
/*    */     }
/* 94 */     return Collections.unmodifiableList(soundCards);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdSoundCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */