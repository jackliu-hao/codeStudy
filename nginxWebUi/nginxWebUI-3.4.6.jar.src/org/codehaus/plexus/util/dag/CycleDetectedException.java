/*    */ package org.codehaus.plexus.util.dag;
/*    */ 
/*    */ import java.util.Iterator;
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
/*    */ public class CycleDetectedException
/*    */   extends Exception
/*    */ {
/*    */   private List cycle;
/*    */   
/*    */   public CycleDetectedException(String message, List cycle) {
/* 29 */     super(message);
/*    */     
/* 31 */     this.cycle = cycle;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List getCycle() {
/* 38 */     return this.cycle;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String cycleToString() {
/* 46 */     StringBuffer buffer = new StringBuffer();
/*    */     
/* 48 */     for (Iterator iterator = this.cycle.iterator(); iterator.hasNext(); ) {
/*    */       
/* 50 */       buffer.append(iterator.next());
/*    */       
/* 52 */       if (iterator.hasNext())
/*    */       {
/* 54 */         buffer.append(" --> ");
/*    */       }
/*    */     } 
/* 57 */     return buffer.toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 62 */     return super.getMessage() + " " + cycleToString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\dag\CycleDetectedException.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */