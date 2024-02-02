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
/*    */ final class TypeOption<T>
/*    */   extends Option<Class<? extends T>>
/*    */ {
/*    */   private static final long serialVersionUID = 2449094406108952764L;
/*    */   private final transient Class<T> type;
/*    */   private final transient Option.ValueParser<Class<? extends T>> parser;
/*    */   
/*    */   TypeOption(Class<?> declClass, String name, Class<T> type) {
/* 35 */     super(declClass, name);
/* 36 */     if (type == null) {
/* 37 */       throw Messages.msg.nullParameter("type");
/*    */     }
/* 39 */     this.type = type;
/* 40 */     this.parser = Option.getClassParser(type);
/*    */   }
/*    */   
/*    */   public Class<? extends T> cast(Object o) {
/* 44 */     return ((Class)o).asSubclass(this.type);
/*    */   }
/*    */   
/*    */   public Class<? extends T> parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
/* 48 */     return this.parser.parseValue(string, classLoader);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\TypeOption.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */