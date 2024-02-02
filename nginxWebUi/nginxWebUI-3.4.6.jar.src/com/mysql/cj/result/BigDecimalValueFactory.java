/*     */ package com.mysql.cj.result;
/*     */ 
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.nio.ByteBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BigDecimalValueFactory
/*     */   extends AbstractNumericValueFactory<BigDecimal>
/*     */ {
/*     */   int scale;
/*     */   boolean hasScale;
/*     */   
/*     */   public BigDecimalValueFactory(PropertySet pset) {
/*  46 */     super(pset);
/*     */   }
/*     */   
/*     */   public BigDecimalValueFactory(PropertySet pset, int scale) {
/*  50 */     super(pset);
/*  51 */     this.scale = scale;
/*  52 */     this.hasScale = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private BigDecimal adjustResult(BigDecimal d) {
/*  63 */     if (this.hasScale) {
/*     */       try {
/*  65 */         return d.setScale(this.scale);
/*  66 */       } catch (ArithmeticException ex) {
/*     */         
/*  68 */         return d.setScale(this.scale, 4);
/*     */       } 
/*     */     }
/*     */     
/*  72 */     return d;
/*     */   }
/*     */ 
/*     */   
/*     */   public BigDecimal createFromBigInteger(BigInteger i) {
/*  77 */     return adjustResult(new BigDecimal(i));
/*     */   }
/*     */ 
/*     */   
/*     */   public BigDecimal createFromLong(long l) {
/*  82 */     return adjustResult(BigDecimal.valueOf(l));
/*     */   }
/*     */ 
/*     */   
/*     */   public BigDecimal createFromBigDecimal(BigDecimal d) {
/*  87 */     return adjustResult(d);
/*     */   }
/*     */ 
/*     */   
/*     */   public BigDecimal createFromDouble(double d) {
/*  92 */     return adjustResult(BigDecimal.valueOf(d));
/*     */   }
/*     */ 
/*     */   
/*     */   public BigDecimal createFromBit(byte[] bytes, int offset, int length) {
/*  97 */     return new BigDecimal(new BigInteger(ByteBuffer.allocate(length + 1).put((byte)0).put(bytes, offset, length).array()));
/*     */   }
/*     */   
/*     */   public String getTargetTypeName() {
/* 101 */     return BigDecimal.class.getName();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\BigDecimalValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */