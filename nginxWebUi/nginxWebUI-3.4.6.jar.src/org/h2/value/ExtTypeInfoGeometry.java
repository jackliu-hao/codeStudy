/*    */ package org.h2.value;
/*    */ 
/*    */ import java.util.Objects;
/*    */ import org.h2.util.geometry.EWKTUtils;
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
/*    */ public final class ExtTypeInfoGeometry
/*    */   extends ExtTypeInfo
/*    */ {
/*    */   private final int type;
/*    */   private final Integer srid;
/*    */   
/*    */   static StringBuilder toSQL(StringBuilder paramStringBuilder, int paramInt, Integer paramInteger) {
/* 22 */     if (paramInt == 0 && paramInteger == null) {
/* 23 */       return paramStringBuilder;
/*    */     }
/* 25 */     paramStringBuilder.append('(');
/* 26 */     if (paramInt == 0) {
/* 27 */       paramStringBuilder.append("GEOMETRY");
/*    */     } else {
/* 29 */       EWKTUtils.formatGeometryTypeAndDimensionSystem(paramStringBuilder, paramInt);
/*    */     } 
/* 31 */     if (paramInteger != null) {
/* 32 */       paramStringBuilder.append(", ").append(paramInteger.intValue());
/*    */     }
/* 34 */     return paramStringBuilder.append(')');
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
/*    */   public ExtTypeInfoGeometry(int paramInt, Integer paramInteger) {
/* 47 */     this.type = paramInt;
/* 48 */     this.srid = paramInteger;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 53 */     return 31 * ((this.srid == null) ? 0 : this.srid.hashCode()) + this.type;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object paramObject) {
/* 58 */     if (this == paramObject) {
/* 59 */       return true;
/*    */     }
/* 61 */     if (paramObject == null || paramObject.getClass() != ExtTypeInfoGeometry.class) {
/* 62 */       return false;
/*    */     }
/* 64 */     ExtTypeInfoGeometry extTypeInfoGeometry = (ExtTypeInfoGeometry)paramObject;
/* 65 */     return (this.type == extTypeInfoGeometry.type && Objects.equals(this.srid, extTypeInfoGeometry.srid));
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 70 */     return toSQL(paramStringBuilder, this.type, this.srid);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getType() {
/* 80 */     return this.type;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Integer getSrid() {
/* 89 */     return this.srid;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ExtTypeInfoGeometry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */