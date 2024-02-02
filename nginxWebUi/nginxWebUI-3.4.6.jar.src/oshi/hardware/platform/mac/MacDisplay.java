/*    */ package oshi.hardware.platform.mac;
/*    */ 
/*    */ import com.sun.jna.Pointer;
/*    */ import com.sun.jna.platform.mac.CoreFoundation;
/*    */ import com.sun.jna.platform.mac.IOKit;
/*    */ import com.sun.jna.platform.mac.IOKitUtil;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ import oshi.annotation.concurrent.Immutable;
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
/*    */ @Immutable
/*    */ final class MacDisplay
/*    */   extends AbstractDisplay
/*    */ {
/* 51 */   private static final Logger LOG = LoggerFactory.getLogger(MacDisplay.class);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   MacDisplay(byte[] edid) {
/* 60 */     super(edid);
/* 61 */     LOG.debug("Initialized MacDisplay");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static List<Display> getDisplays() {
/* 70 */     List<Display> displays = new ArrayList<>();
/*    */     
/* 72 */     IOKit.IOIterator serviceIterator = IOKitUtil.getMatchingServices("IODisplayConnect");
/* 73 */     if (serviceIterator != null) {
/* 74 */       CoreFoundation.CFStringRef cfEdid = CoreFoundation.CFStringRef.createCFString("IODisplayEDID");
/* 75 */       IOKit.IORegistryEntry sdService = serviceIterator.next();
/* 76 */       while (sdService != null) {
/*    */         
/* 78 */         IOKit.IORegistryEntry properties = sdService.getChildEntry("IOService");
/* 79 */         if (properties != null) {
/*    */           
/* 81 */           CoreFoundation.CFTypeRef edidRaw = properties.createCFProperty(cfEdid);
/* 82 */           if (edidRaw != null) {
/* 83 */             CoreFoundation.CFDataRef edid = new CoreFoundation.CFDataRef(edidRaw.getPointer());
/*    */             
/* 85 */             int length = edid.getLength();
/* 86 */             Pointer p = edid.getBytePtr();
/* 87 */             displays.add(new MacDisplay(p.getByteArray(0L, length)));
/* 88 */             edid.release();
/*    */           } 
/* 90 */           properties.release();
/*    */         } 
/*    */         
/* 93 */         sdService.release();
/* 94 */         sdService = serviceIterator.next();
/*    */       } 
/* 96 */       serviceIterator.release();
/* 97 */       cfEdid.release();
/*    */     } 
/* 99 */     return Collections.unmodifiableList(displays);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\mac\MacDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */