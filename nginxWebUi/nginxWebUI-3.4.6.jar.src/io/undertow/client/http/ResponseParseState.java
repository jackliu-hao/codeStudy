/*    */ package io.undertow.client.http;
/*    */ 
/*    */ import io.undertow.util.HttpString;
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
/*    */ class ResponseParseState
/*    */ {
/*    */   public static final int VERSION = 0;
/*    */   public static final int STATUS_CODE = 1;
/*    */   public static final int REASON_PHRASE = 2;
/*    */   public static final int AFTER_REASON_PHRASE = 3;
/*    */   public static final int HEADER = 4;
/*    */   public static final int HEADER_VALUE = 5;
/*    */   public static final int PARSE_COMPLETE = 6;
/*    */   int state;
/*    */   int parseState;
/*    */   HttpString current;
/*    */   byte[] currentBytes;
/*    */   int pos;
/* 66 */   final StringBuilder stringBuilder = new StringBuilder();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   byte leftOver;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   HttpString nextHeader;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   ResponseParseState() {
/* 84 */     this.parseState = 0;
/* 85 */     this.pos = 0;
/*    */   }
/*    */   
/*    */   public boolean isComplete() {
/* 89 */     return (this.state == 6);
/*    */   }
/*    */   
/*    */   public final void parseComplete() {
/* 93 */     this.state = 6;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\http\ResponseParseState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */