/*    */ package com.cym.ext;
/*    */ 
/*    */ import java.util.List;
/*    */ 
/*    */ public class Tree {
/*    */   String name;
/*    */   String value;
/*    */   List<Tree> children;
/*    */   
/*    */   public String getName() {
/* 11 */     return this.name;
/*    */   }
/*    */   
/*    */   public void setName(String name) {
/* 15 */     this.name = name;
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 19 */     return this.value;
/*    */   }
/*    */   
/*    */   public void setValue(String value) {
/* 23 */     this.value = value;
/*    */   }
/*    */   
/*    */   public List<Tree> getChildren() {
/* 27 */     return this.children;
/*    */   }
/*    */   
/*    */   public void setChildren(List<Tree> children) {
/* 31 */     this.children = children;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\ext\Tree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */