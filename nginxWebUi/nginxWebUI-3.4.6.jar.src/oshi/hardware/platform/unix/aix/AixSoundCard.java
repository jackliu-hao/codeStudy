/*    */ package oshi.hardware.platform.unix.aix;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import java.util.function.Supplier;
/*    */ import oshi.annotation.concurrent.Immutable;
/*    */ import oshi.hardware.SoundCard;
/*    */ import oshi.hardware.common.AbstractSoundCard;
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
/*    */ 
/*    */ @Immutable
/*    */ final class AixSoundCard
/*    */   extends AbstractSoundCard
/*    */ {
/*    */   AixSoundCard(String kernelVersion, String name, String codec) {
/* 54 */     super(kernelVersion, name, codec);
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
/*    */   public static List<SoundCard> getSoundCards(Supplier<List<String>> lscfg) {
/* 66 */     List<SoundCard> soundCards = new ArrayList<>();
/* 67 */     for (String line : lscfg.get()) {
/* 68 */       String s = line.trim();
/* 69 */       if (s.startsWith("paud")) {
/* 70 */         String[] split = ParseUtil.whitespaces.split(s, 3);
/* 71 */         if (split.length == 3) {
/* 72 */           soundCards.add(new AixSoundCard("unknown", split[2], "unknown"));
/*    */         }
/*    */       } 
/*    */     } 
/* 76 */     return Collections.unmodifiableList(soundCards);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\aix\AixSoundCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */