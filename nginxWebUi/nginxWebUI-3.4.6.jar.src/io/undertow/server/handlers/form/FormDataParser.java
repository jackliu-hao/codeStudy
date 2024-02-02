/*    */ package io.undertow.server.handlers.form;
/*    */ 
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.util.AttachmentKey;
/*    */ import java.io.Closeable;
/*    */ import java.io.IOException;
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
/*    */ public interface FormDataParser
/*    */   extends Closeable
/*    */ {
/* 40 */   public static final AttachmentKey<FormData> FORM_DATA = AttachmentKey.create(FormData.class);
/*    */   
/*    */   void parse(HttpHandler paramHttpHandler) throws Exception;
/*    */   
/*    */   FormData parseBlocking() throws IOException;
/*    */   
/*    */   void close() throws IOException;
/*    */   
/*    */   void setCharacterEncoding(String paramString);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\form\FormDataParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */