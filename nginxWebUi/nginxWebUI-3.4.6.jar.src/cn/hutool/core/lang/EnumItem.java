/*    */ package cn.hutool.core.lang;
/*    */ 
/*    */ import java.io.Serializable;
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
/*    */ public interface EnumItem<E extends EnumItem<E>>
/*    */   extends Serializable
/*    */ {
/*    */   String name();
/*    */   
/*    */   default String text() {
/* 23 */     return name();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   int intVal();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default E[] items() {
/* 35 */     return (E[])getClass().getEnumConstants();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default E fromInt(Integer intVal) {
/* 45 */     if (intVal == null) {
/* 46 */       return null;
/*    */     }
/* 48 */     E[] vs = items();
/* 49 */     for (E enumItem : vs) {
/* 50 */       if (enumItem.intVal() == intVal.intValue()) {
/* 51 */         return enumItem;
/*    */       }
/*    */     } 
/* 54 */     return null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default E fromStr(String strVal) {
/* 64 */     if (strVal == null) {
/* 65 */       return null;
/*    */     }
/* 67 */     E[] vs = items();
/* 68 */     for (E enumItem : vs) {
/* 69 */       if (strVal.equalsIgnoreCase(enumItem.name())) {
/* 70 */         return enumItem;
/*    */       }
/*    */     } 
/* 73 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\EnumItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */