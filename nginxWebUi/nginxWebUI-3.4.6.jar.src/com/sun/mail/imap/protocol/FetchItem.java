/*    */ package com.sun.mail.imap.protocol;
/*    */ 
/*    */ import com.sun.mail.iap.ParsingException;
/*    */ import javax.mail.FetchProfile;
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
/*    */ 
/*    */ 
/*    */ public abstract class FetchItem
/*    */ {
/*    */   private String name;
/*    */   private FetchProfile.Item fetchProfileItem;
/*    */   
/*    */   public FetchItem(String name, FetchProfile.Item fetchProfileItem) {
/* 61 */     this.name = name;
/* 62 */     this.fetchProfileItem = fetchProfileItem;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 66 */     return this.name;
/*    */   }
/*    */   
/*    */   public FetchProfile.Item getFetchProfileItem() {
/* 70 */     return this.fetchProfileItem;
/*    */   }
/*    */   
/*    */   public abstract Object parseItem(FetchResponse paramFetchResponse) throws ParsingException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\FetchItem.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */