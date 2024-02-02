/*    */ package oshi.hardware.common;
/*    */ 
/*    */ import oshi.annotation.concurrent.Immutable;
/*    */ import oshi.hardware.SoundCard;
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
/*    */ public abstract class AbstractSoundCard
/*    */   implements SoundCard
/*    */ {
/*    */   private String kernelVersion;
/*    */   private String name;
/*    */   private String codec;
/*    */   
/*    */   protected AbstractSoundCard(String kernelVersion, String name, String codec) {
/* 50 */     this.kernelVersion = kernelVersion;
/* 51 */     this.name = name;
/* 52 */     this.codec = codec;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getDriverVersion() {
/* 57 */     return this.kernelVersion;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 62 */     return this.name;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getCodec() {
/* 67 */     return this.codec;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 72 */     StringBuilder builder = new StringBuilder();
/* 73 */     builder.append("SoundCard@");
/* 74 */     builder.append(Integer.toHexString(hashCode()));
/* 75 */     builder.append(" [name=");
/* 76 */     builder.append(this.name);
/* 77 */     builder.append(", kernelVersion=");
/* 78 */     builder.append(this.kernelVersion);
/* 79 */     builder.append(", codec=");
/* 80 */     builder.append(this.codec);
/* 81 */     builder.append(']');
/* 82 */     return builder.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\common\AbstractSoundCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */