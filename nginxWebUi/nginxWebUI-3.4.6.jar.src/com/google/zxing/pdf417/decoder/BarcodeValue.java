/*    */ package com.google.zxing.pdf417.decoder;
/*    */ 
/*    */ import com.google.zxing.pdf417.PDF417Common;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
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
/*    */ final class BarcodeValue
/*    */ {
/* 31 */   private final Map<Integer, Integer> values = new HashMap<>();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void setValue(int value) {
/*    */     Integer confidence;
/* 38 */     if ((confidence = this.values.get(Integer.valueOf(value))) == null) {
/* 39 */       confidence = Integer.valueOf(0);
/*    */     }
/* 41 */     confidence = Integer.valueOf(confidence.intValue() + 1);
/* 42 */     this.values.put(Integer.valueOf(value), confidence);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   int[] getValue() {
/* 50 */     int maxConfidence = -1;
/* 51 */     Collection<Integer> result = new ArrayList<>();
/* 52 */     for (Iterator<Map.Entry<Integer, Integer>> iterator = this.values.entrySet().iterator(); iterator.hasNext(); ) {
/* 53 */       Map.Entry<Integer, Integer> entry; if (((Integer)(entry = iterator.next()).getValue()).intValue() > maxConfidence) {
/* 54 */         maxConfidence = ((Integer)entry.getValue()).intValue();
/* 55 */         result.clear();
/* 56 */         result.add(entry.getKey()); continue;
/* 57 */       }  if (((Integer)entry.getValue()).intValue() == maxConfidence) {
/* 58 */         result.add(entry.getKey());
/*    */       }
/*    */     } 
/* 61 */     return PDF417Common.toIntArray(result);
/*    */   }
/*    */   
/*    */   public Integer getConfidence(int value) {
/* 65 */     return this.values.get(Integer.valueOf(value));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\pdf417\decoder\BarcodeValue.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */