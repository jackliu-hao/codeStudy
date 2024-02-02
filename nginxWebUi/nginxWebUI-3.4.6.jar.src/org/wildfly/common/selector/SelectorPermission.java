/*     */ package org.wildfly.common.selector;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InvalidObjectException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.security.BasicPermission;
/*     */ import java.security.Permission;
/*     */ import org.wildfly.common.Assert;
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
/*     */ public final class SelectorPermission
/*     */   extends BasicPermission
/*     */ {
/*     */   private static final long serialVersionUID = -7156787601824624014L;
/*     */   private static final int ACTION_GET = 1;
/*     */   private static final int ACTION_SET = 2;
/*     */   private static final int ACTION_CHANGE = 4;
/*     */   private final int actions;
/*     */   
/*     */   public SelectorPermission(String name, String actions) {
/*  45 */     super(name);
/*  46 */     Assert.checkNotNullParam("name", name);
/*  47 */     Assert.checkNotNullParam("actions", actions);
/*  48 */     String[] actionArray = actions.split("\\s*,\\s*");
/*  49 */     int q = 0;
/*  50 */     for (String action : actionArray) {
/*  51 */       if (action.equalsIgnoreCase("get")) {
/*  52 */         q |= 0x1;
/*  53 */       } else if (action.equalsIgnoreCase("set")) {
/*  54 */         q |= 0x2;
/*  55 */       } else if (action.equalsIgnoreCase("change")) {
/*  56 */         q |= 0x4;
/*  57 */       } else if (action.equals("*")) {
/*  58 */         q |= 0x7;
/*     */         break;
/*     */       } 
/*     */     } 
/*  62 */     this.actions = q;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getActions() {
/*  67 */     int maskedActions = this.actions & 0x7;
/*  68 */     switch (maskedActions) { case 0:
/*  69 */         return "";
/*  70 */       case 1: return "get";
/*  71 */       case 2: return "set";
/*  72 */       case 3: return "get,set";
/*  73 */       case 4: return "change";
/*  74 */       case 5: return "get,change";
/*  75 */       case 6: return "set,change";
/*  76 */       case 7: return "get,set,change"; }
/*  77 */      throw Assert.impossibleSwitchCase(maskedActions);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean implies(Permission p) {
/*  82 */     return (p instanceof SelectorPermission && implies((SelectorPermission)p));
/*     */   }
/*     */   
/*     */   public boolean implies(SelectorPermission p) {
/*  86 */     return (p != null && (p.actions & this.actions) == p.actions && super.implies(p));
/*     */   }
/*     */   
/*     */   public boolean equals(Object p) {
/*  90 */     return (p instanceof SelectorPermission && equals((SelectorPermission)p));
/*     */   }
/*     */   
/*     */   public boolean equals(SelectorPermission p) {
/*  94 */     return (p != null && p.actions == this.actions && super.equals(p));
/*     */   }
/*     */   
/*     */   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
/*  98 */     ois.defaultReadObject();
/*  99 */     int actions = this.actions;
/* 100 */     if ((actions & 0x7) != actions)
/* 101 */       throw new InvalidObjectException("Invalid permission actions"); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\selector\SelectorPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */