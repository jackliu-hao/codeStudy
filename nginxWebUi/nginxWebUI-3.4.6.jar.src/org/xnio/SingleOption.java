/*    */ package org.xnio;
/*    */ 
/*    */ import org.xnio._private.Messages;
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
/*    */ final class SingleOption<T>
/*    */   extends Option<T>
/*    */ {
/*    */   private static final long serialVersionUID = 2449094406108952764L;
/*    */   private final transient Class<T> type;
/*    */   private final transient Option.ValueParser<T> parser;
/*    */   
/*    */   SingleOption(Class<?> declClass, String name, Class<T> type) {
/* 35 */     super(declClass, name);
/* 36 */     if (type == null) {
/* 37 */       throw Messages.msg.nullParameter("type");
/*    */     }
/* 39 */     this.type = type;
/* 40 */     this.parser = Option.getParser(type);
/*    */   }
/*    */   
/*    */   public T cast(Object o) {
/* 44 */     return this.type.cast(o);
/*    */   }
/*    */   
/*    */   public T parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
/* 48 */     return this.parser.parseValue(string, classLoader);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\SingleOption.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */