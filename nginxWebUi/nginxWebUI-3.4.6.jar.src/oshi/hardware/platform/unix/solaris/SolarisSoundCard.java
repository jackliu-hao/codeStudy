/*    */ package oshi.hardware.platform.unix.solaris;
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
/*    */ final class SolarisSoundCard
/*    */   extends AbstractSoundCard
/*    */ {
/*    */   private static final String LSHAL = "lshal";
/*    */   private static final String DEFAULT_AUDIO_DRIVER = "audio810";
/*    */   
/*    */   SolarisSoundCard(String kernelVersion, String name, String codec) {
/* 58 */     super(kernelVersion, name, codec);
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
/* 69 */     Map<String, String> vendorMap = new HashMap<>();
/* 70 */     Map<String, String> productMap = new HashMap<>();
/* 71 */     List<String> sounds = new ArrayList<>();
/* 72 */     String key = "";
/* 73 */     for (String line : ExecutingCommand.runNative("lshal")) {
/* 74 */       line = line.trim();
/* 75 */       if (line.startsWith("udi =")) {
/*    */         
/* 77 */         key = ParseUtil.getSingleQuoteStringValue(line); continue;
/* 78 */       }  if (!key.isEmpty() && !line.isEmpty()) {
/* 79 */         if (line.contains("info.solaris.driver =") && "audio810"
/* 80 */           .equals(ParseUtil.getSingleQuoteStringValue(line))) {
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
/* 91 */       soundCards.add(new SolarisSoundCard((String)productMap.get(_key) + " " + "audio810", (String)vendorMap
/* 92 */             .get(_key) + " " + (String)productMap.get(_key), productMap.get(_key)));
/*    */     }
/* 94 */     return Collections.unmodifiableList(soundCards);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\solaris\SolarisSoundCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */