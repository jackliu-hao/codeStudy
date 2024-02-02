/*    */ package org.xnio.channels;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.xnio.Option;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface Configurable
/*    */ {
/* 64 */   public static final Configurable EMPTY = new Configurable() {
/*    */       public boolean supportsOption(Option<?> option) {
/* 66 */         return false;
/*    */       }
/*    */       
/*    */       public <T> T getOption(Option<T> option) throws IOException {
/* 70 */         return null;
/*    */       }
/*    */       
/*    */       public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 74 */         return null;
/*    */       }
/*    */     };
/*    */   
/*    */   boolean supportsOption(Option<?> paramOption);
/*    */   
/*    */   <T> T getOption(Option<T> paramOption) throws IOException;
/*    */   
/*    */   <T> T setOption(Option<T> paramOption, T paramT) throws IllegalArgumentException, IOException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\Configurable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */