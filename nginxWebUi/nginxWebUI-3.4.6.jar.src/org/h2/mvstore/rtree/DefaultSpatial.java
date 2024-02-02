/*    */ package org.h2.mvstore.rtree;
/*    */ 
/*    */ import java.util.Arrays;
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
/*    */ final class DefaultSpatial
/*    */   implements Spatial
/*    */ {
/*    */   private final long id;
/*    */   private final float[] minMax;
/*    */   
/*    */   public DefaultSpatial(long paramLong, float... paramVarArgs) {
/* 27 */     this.id = paramLong;
/* 28 */     this.minMax = paramVarArgs;
/*    */   }
/*    */   
/*    */   private DefaultSpatial(long paramLong, DefaultSpatial paramDefaultSpatial) {
/* 32 */     this.id = paramLong;
/* 33 */     this.minMax = (float[])paramDefaultSpatial.minMax.clone();
/*    */   }
/*    */ 
/*    */   
/*    */   public float min(int paramInt) {
/* 38 */     return this.minMax[paramInt + paramInt];
/*    */   }
/*    */ 
/*    */   
/*    */   public void setMin(int paramInt, float paramFloat) {
/* 43 */     this.minMax[paramInt + paramInt] = paramFloat;
/*    */   }
/*    */ 
/*    */   
/*    */   public float max(int paramInt) {
/* 48 */     return this.minMax[paramInt + paramInt + 1];
/*    */   }
/*    */ 
/*    */   
/*    */   public void setMax(int paramInt, float paramFloat) {
/* 53 */     this.minMax[paramInt + paramInt + 1] = paramFloat;
/*    */   }
/*    */ 
/*    */   
/*    */   public Spatial clone(long paramLong) {
/* 58 */     return new DefaultSpatial(paramLong, this);
/*    */   }
/*    */ 
/*    */   
/*    */   public long getId() {
/* 63 */     return this.id;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isNull() {
/* 68 */     return (this.minMax.length == 0);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equalsIgnoringId(Spatial paramSpatial) {
/* 73 */     return Arrays.equals(this.minMax, ((DefaultSpatial)paramSpatial).minMax);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\rtree\DefaultSpatial.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */