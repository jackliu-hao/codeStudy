/*    */ package org.noear.solon.logging.utils;
/*    */ 
/*    */ import org.slf4j.MDC;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class TagsMDC
/*    */ {
/* 10 */   private static final TagsMetainfo metainfo = new TagsMetainfo();
/*    */   
/*    */   public static TagsMetainfo tag0(String tag0) {
/* 13 */     return metainfo.tag0(tag0);
/*    */   }
/*    */   
/*    */   public static TagsMetainfo tag1(String tag1) {
/* 17 */     return metainfo.tag1(tag1);
/*    */   }
/*    */   
/*    */   public static TagsMetainfo tag2(String tag2) {
/* 21 */     return metainfo.tag2(tag2);
/*    */   }
/*    */   
/*    */   public static TagsMetainfo tag3(String tag3) {
/* 25 */     return metainfo.tag3(tag3);
/*    */   }
/*    */   
/*    */   public static TagsMetainfo tag4(String tag4) {
/* 29 */     return metainfo.tag4(tag4);
/*    */   }
/*    */   
/*    */   public static class TagsMetainfo {
/*    */     public TagsMetainfo tag0(String tag0) {
/* 34 */       MDC.put("tag0", tag0);
/* 35 */       return this;
/*    */     }
/*    */     
/*    */     public TagsMetainfo tag1(String tag1) {
/* 39 */       MDC.put("tag1", tag1);
/* 40 */       return this;
/*    */     }
/*    */     
/*    */     public TagsMetainfo tag2(String tag2) {
/* 44 */       MDC.put("tag2", tag2);
/* 45 */       return this;
/*    */     }
/*    */     
/*    */     public TagsMetainfo tag3(String tag3) {
/* 49 */       MDC.put("tag3", tag3);
/* 50 */       return this;
/*    */     }
/*    */     
/*    */     public TagsMetainfo tag4(String tag4) {
/* 54 */       MDC.put("tag4", tag4);
/* 55 */       return this;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\loggin\\utils\TagsMDC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */