/*    */ package oshi.hardware.platform.unix.aix;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import java.util.function.Function;
/*    */ import java.util.stream.Collectors;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.driver.unix.Xrandr;
/*    */ import oshi.hardware.Display;
/*    */ import oshi.hardware.common.AbstractDisplay;
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
/*    */ @ThreadSafe
/*    */ final class AixDisplay
/*    */   extends AbstractDisplay
/*    */ {
/*    */   AixDisplay(byte[] edid) {
/* 48 */     super(edid);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static List<Display> getDisplays() {
/* 57 */     return 
/* 58 */       Collections.unmodifiableList((List<? extends Display>)Xrandr.getEdidArrays().stream().map(AixDisplay::new).collect(Collectors.toList()));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\aix\AixDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */