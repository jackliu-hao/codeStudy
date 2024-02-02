/*    */ package io.undertow.util;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ public class PathTemplateMatch
/*    */ {
/* 30 */   public static final AttachmentKey<PathTemplateMatch> ATTACHMENT_KEY = AttachmentKey.create(PathTemplateMatch.class);
/*    */   
/*    */   private final String matchedTemplate;
/*    */   private final Map<String, String> parameters;
/*    */   
/*    */   public PathTemplateMatch(String matchedTemplate, Map<String, String> parameters) {
/* 36 */     this.matchedTemplate = matchedTemplate;
/* 37 */     this.parameters = parameters;
/*    */   }
/*    */   
/*    */   public String getMatchedTemplate() {
/* 41 */     return this.matchedTemplate;
/*    */   }
/*    */   
/*    */   public Map<String, String> getParameters() {
/* 45 */     return this.parameters;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\PathTemplateMatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */