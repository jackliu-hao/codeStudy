/*    */ package oshi.hardware.platform.unix.freebsd;
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
/*    */ final class FreeBsdDisplay
/*    */   extends AbstractDisplay
/*    */ {
/*    */   FreeBsdDisplay(byte[] edid) {
/* 48 */     super(edid);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static List<Display> getDisplays() {
/* 57 */     return Collections.unmodifiableList(
/* 58 */         (List<? extends Display>)Xrandr.getEdidArrays().stream().map(FreeBsdDisplay::new).collect(Collectors.toList()));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */