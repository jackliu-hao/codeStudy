/*    */ package org.apache.http.message;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import org.apache.http.Header;
/*    */ import org.apache.http.HeaderElement;
/*    */ import org.apache.http.ParseException;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.util.Args;
/*    */ import org.apache.http.util.CharArrayBuffer;
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
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class BasicHeader
/*    */   implements Header, Cloneable, Serializable
/*    */ {
/* 47 */   private static final HeaderElement[] EMPTY_HEADER_ELEMENTS = new HeaderElement[0];
/*    */ 
/*    */   
/*    */   private static final long serialVersionUID = -5427236326487562174L;
/*    */ 
/*    */   
/*    */   private final String name;
/*    */ 
/*    */   
/*    */   private final String value;
/*    */ 
/*    */ 
/*    */   
/*    */   public BasicHeader(String name, String value) {
/* 61 */     this.name = (String)Args.notNull(name, "Name");
/* 62 */     this.value = value;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object clone() throws CloneNotSupportedException {
/* 67 */     return super.clone();
/*    */   }
/*    */ 
/*    */   
/*    */   public HeaderElement[] getElements() throws ParseException {
/* 72 */     if (getValue() != null)
/*    */     {
/* 74 */       return BasicHeaderValueParser.parseElements(getValue(), (HeaderValueParser)null);
/*    */     }
/* 76 */     return EMPTY_HEADER_ELEMENTS;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 81 */     return this.name;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getValue() {
/* 86 */     return this.value;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 92 */     return BasicLineFormatter.INSTANCE.formatHeader((CharArrayBuffer)null, this).toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\message\BasicHeader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */