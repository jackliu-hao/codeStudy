/*    */ package org.apache.commons.compress.changes;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ public class ChangeSetResults
/*    */ {
/* 28 */   private final List<String> addedFromChangeSet = new ArrayList<>();
/* 29 */   private final List<String> addedFromStream = new ArrayList<>();
/* 30 */   private final List<String> deleted = new ArrayList<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void deleted(String fileName) {
/* 37 */     this.deleted.add(fileName);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void addedFromStream(String fileName) {
/* 46 */     this.addedFromStream.add(fileName);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void addedFromChangeSet(String fileName) {
/* 55 */     this.addedFromChangeSet.add(fileName);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List<String> getAddedFromChangeSet() {
/* 63 */     return this.addedFromChangeSet;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List<String> getAddedFromStream() {
/* 71 */     return this.addedFromStream;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List<String> getDeleted() {
/* 79 */     return this.deleted;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   boolean hasBeenAdded(String fileName) {
/* 88 */     return (this.addedFromChangeSet.contains(fileName) || this.addedFromStream.contains(fileName));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\changes\ChangeSetResults.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */