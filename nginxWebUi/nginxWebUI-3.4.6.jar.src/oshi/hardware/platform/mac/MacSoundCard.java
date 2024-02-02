/*    */ package oshi.hardware.platform.mac;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import oshi.annotation.concurrent.Immutable;
/*    */ import oshi.hardware.SoundCard;
/*    */ import oshi.hardware.common.AbstractSoundCard;
/*    */ import oshi.util.FileUtil;
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
/*    */ final class MacSoundCard
/*    */   extends AbstractSoundCard
/*    */ {
/*    */   private static final String APPLE = "Apple Inc.";
/*    */   
/*    */   MacSoundCard(String kernelVersion, String name, String codec) {
/* 55 */     super(kernelVersion, name, codec);
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
/* 66 */     List<MacSoundCard> soundCards = new ArrayList<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 75 */     String manufacturer = "Apple Inc.";
/* 76 */     String kernelVersion = "AppleHDAController";
/* 77 */     String codec = "AppleHDACodec";
/*    */     
/* 79 */     boolean version = false;
/* 80 */     String versionMarker = "<key>com.apple.driver.AppleHDAController</key>";
/*    */ 
/*    */     
/* 83 */     for (String checkLine : FileUtil.readFile("/System/Library/Extensions/AppleHDA.kext/Contents/Info.plist")) {
/* 84 */       if (checkLine.contains(versionMarker)) {
/* 85 */         version = true;
/*    */         continue;
/*    */       } 
/* 88 */       if (version) {
/*    */         
/* 90 */         kernelVersion = "AppleHDAController " + ParseUtil.getTextBetweenStrings(checkLine, "<string>", "</string>");
/* 91 */         version = false;
/*    */       } 
/*    */     } 
/* 94 */     soundCards.add(new MacSoundCard(kernelVersion, manufacturer, codec));
/*    */     
/* 96 */     return (List)Collections.unmodifiableList(soundCards);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\mac\MacSoundCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */