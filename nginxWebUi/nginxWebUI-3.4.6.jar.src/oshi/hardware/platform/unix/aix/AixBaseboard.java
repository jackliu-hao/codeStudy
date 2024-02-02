/*    */ package oshi.hardware.platform.unix.aix;
/*    */ 
/*    */ import java.util.List;
/*    */ import java.util.function.Supplier;
/*    */ import oshi.annotation.concurrent.Immutable;
/*    */ import oshi.driver.unix.aix.Lscfg;
/*    */ import oshi.hardware.common.AbstractBaseboard;
/*    */ import oshi.util.Util;
/*    */ import oshi.util.tuples.Triplet;
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
/*    */ final class AixBaseboard
/*    */   extends AbstractBaseboard
/*    */ {
/*    */   private static final String IBM = "IBM";
/*    */   private final String model;
/*    */   private final String serialNumber;
/*    */   private final String version;
/*    */   
/*    */   AixBaseboard(Supplier<List<String>> lscfg) {
/* 48 */     Triplet<String, String, String> msv = Lscfg.queryBackplaneModelSerialVersion(lscfg.get());
/* 49 */     this.model = Util.isBlank((String)msv.getA()) ? "unknown" : (String)msv.getA();
/* 50 */     this.serialNumber = Util.isBlank((String)msv.getB()) ? "unknown" : (String)msv.getB();
/* 51 */     this.version = Util.isBlank((String)msv.getC()) ? "unknown" : (String)msv.getC();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getManufacturer() {
/* 56 */     return "IBM";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getModel() {
/* 61 */     return this.model;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getSerialNumber() {
/* 66 */     return this.serialNumber;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getVersion() {
/* 71 */     return this.version;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\aix\AixBaseboard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */