/*    */ package oshi.hardware.platform.windows;
/*    */ 
/*    */ import com.sun.jna.platform.win32.Advapi32Util;
/*    */ import com.sun.jna.platform.win32.Win32Exception;
/*    */ import com.sun.jna.platform.win32.WinReg;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import oshi.annotation.concurrent.Immutable;
/*    */ import oshi.hardware.SoundCard;
/*    */ import oshi.hardware.common.AbstractSoundCard;
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
/*    */ 
/*    */ @Immutable
/*    */ final class WindowsSoundCard
/*    */   extends AbstractSoundCard
/*    */ {
/*    */   private static final String REGISTRY_SOUNDCARDS = "SYSTEM\\CurrentControlSet\\Control\\Class\\{4d36e96c-e325-11ce-bfc1-08002be10318}\\";
/*    */   
/*    */   WindowsSoundCard(String kernelVersion, String name, String codec) {
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
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static List<SoundCard> getSoundCards() {
/* 73 */     List<WindowsSoundCard> soundCards = new ArrayList<>();
/* 74 */     String[] keys = Advapi32Util.registryGetKeys(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Class\\{4d36e96c-e325-11ce-bfc1-08002be10318}\\");
/* 75 */     for (String key : keys) {
/* 76 */       String fullKey = "SYSTEM\\CurrentControlSet\\Control\\Class\\{4d36e96c-e325-11ce-bfc1-08002be10318}\\" + key;
/*    */       try {
/* 78 */         if (Advapi32Util.registryValueExists(WinReg.HKEY_LOCAL_MACHINE, fullKey, "Driver")) {
/* 79 */           soundCards.add(new WindowsSoundCard(
/* 80 */                 Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, fullKey, "Driver") + " " + 
/* 81 */                 Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, fullKey, "DriverVersion"), 
/*    */                 
/* 83 */                 Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, fullKey, "ProviderName") + " " + 
/*    */                 
/* 85 */                 Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, fullKey, "DriverDesc"), 
/*    */                 
/* 87 */                 Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, fullKey, "DriverDesc")));
/*    */         }
/* 89 */       } catch (Win32Exception e) {
/* 90 */         if (e.getErrorCode() != 5)
/*    */         {
/* 92 */           throw e;
/*    */         }
/*    */       } 
/*    */     } 
/* 96 */     return (List)Collections.unmodifiableList(soundCards);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\windows\WindowsSoundCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */