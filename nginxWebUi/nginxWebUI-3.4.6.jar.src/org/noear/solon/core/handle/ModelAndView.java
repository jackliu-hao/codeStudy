/*    */ package org.noear.solon.core.handle;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.LinkedHashMap;
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
/*    */ 
/*    */ 
/*    */ public class ModelAndView
/*    */   implements Serializable
/*    */ {
/*    */   private String view;
/*    */   
/*    */   public ModelAndView() {}
/*    */   
/* 35 */   private Map<String, Object> model = new LinkedHashMap<>();
/*    */ 
/*    */   
/*    */   public ModelAndView(String view) {
/* 39 */     this();
/* 40 */     this.view = view;
/*    */   }
/*    */   public ModelAndView(String view, Map<String, ?> model) {
/* 43 */     this(view);
/*    */     
/* 45 */     if (model != null) {
/* 46 */       this.model.putAll(model);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String view() {
/* 53 */     return this.view;
/*    */   }
/*    */   public ModelAndView view(String view) {
/* 56 */     this.view = view;
/* 57 */     return this;
/*    */   }
/*    */   
/*    */   public Map<String, Object> model() {
/* 61 */     return this.model;
/*    */   }
/*    */   public void put(String key, Object value) {
/* 64 */     this.model.put(key, value);
/*    */   }
/*    */   
/*    */   public void putIfAbsent(String key, Object value) {
/* 68 */     this.model.putIfAbsent(key, value);
/*    */   }
/*    */   
/*    */   public void putAll(Map<String, ?> keyValues) {
/* 72 */     this.model.putAll(keyValues);
/*    */   }
/*    */   
/*    */   public void clear() {
/* 76 */     this.model.clear();
/* 77 */     this.view = null;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 82 */     return (this.view == null && this.model.size() == 0);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\ModelAndView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */