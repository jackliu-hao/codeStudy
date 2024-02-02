/*     */ package javax.activation;
/*     */ 
/*     */ import java.beans.Beans;
/*     */ import java.io.Externalizable;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CommandInfo
/*     */ {
/*     */   private String verb;
/*     */   private String className;
/*     */   
/*     */   public CommandInfo(String verb, String className) {
/*  57 */     this.verb = verb;
/*  58 */     this.className = className;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCommandName() {
/*  67 */     return this.verb;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCommandClass() {
/*  81 */     return this.className;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getCommandObject(DataHandler dh, ClassLoader loader) throws IOException, ClassNotFoundException {
/* 116 */     Object new_bean = null;
/*     */ 
/*     */     
/* 119 */     new_bean = Beans.instantiate(loader, this.className);
/*     */ 
/*     */     
/* 122 */     if (new_bean != null) {
/* 123 */       if (new_bean instanceof CommandObject) {
/* 124 */         ((CommandObject)new_bean).setCommandContext(this.verb, dh);
/* 125 */       } else if (new_bean instanceof Externalizable && 
/* 126 */         dh != null) {
/* 127 */         InputStream is = dh.getInputStream();
/* 128 */         if (is != null) {
/* 129 */           ((Externalizable)new_bean).readExternal(new ObjectInputStream(is));
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 136 */     return new_bean;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\activation\CommandInfo.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */