/*    */ package io.undertow.servlet.api;
/*    */ 
/*    */ import javax.servlet.DispatcherType;
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
/*    */ public class FilterMappingInfo
/*    */ {
/*    */   private final String filterName;
/*    */   private final MappingType mappingType;
/*    */   private final String mapping;
/*    */   private final DispatcherType dispatcher;
/*    */   
/*    */   public FilterMappingInfo(String filterName, MappingType mappingType, String mapping, DispatcherType dispatcher) {
/* 34 */     this.filterName = filterName;
/* 35 */     this.mappingType = mappingType;
/* 36 */     this.mapping = mapping;
/* 37 */     this.dispatcher = dispatcher;
/*    */   }
/*    */   
/*    */   public MappingType getMappingType() {
/* 41 */     return this.mappingType;
/*    */   }
/*    */   
/*    */   public String getMapping() {
/* 45 */     return this.mapping;
/*    */   }
/*    */   
/*    */   public DispatcherType getDispatcher() {
/* 49 */     return this.dispatcher;
/*    */   }
/*    */   
/*    */   public String getFilterName() {
/* 53 */     return this.filterName;
/*    */   }
/*    */   
/*    */   public enum MappingType {
/* 57 */     URL,
/* 58 */     SERVLET;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\FilterMappingInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */