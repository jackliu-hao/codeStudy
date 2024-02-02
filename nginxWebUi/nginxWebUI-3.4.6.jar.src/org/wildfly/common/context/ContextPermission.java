/*     */ package org.wildfly.common.context;
/*     */ 
/*     */ import java.security.Permission;
/*     */ import java.security.PermissionCollection;
/*     */ import org.wildfly.common.Assert;
/*     */ import org.wildfly.common._private.CommonMessages;
/*     */ import org.wildfly.common.annotation.NotNull;
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
/*     */ public final class ContextPermission
/*     */   extends Permission
/*     */ {
/*     */   private static final long serialVersionUID = 2149744699461086708L;
/*     */   private static final int ACTION_GET = 1;
/*     */   private static final int ACTION_GET_PRIV_SUP = 2;
/*     */   private static final int ACTION_GET_GLOBAL_DEF = 4;
/*     */   private static final int ACTION_SET_GLOBAL_DEF = 8;
/*     */   private static final int ACTION_SET_GLOBAL_DEF_SUP = 16;
/*     */   private static final int ACTION_GET_THREAD_DEF = 32;
/*     */   private static final int ACTION_SET_THREAD_DEF = 64;
/*     */   private static final int ACTION_SET_THREAD_DEF_SUP = 128;
/*     */   private static final int ACTION_GET_CLASSLOADER_DEF = 256;
/*     */   private static final int ACTION_SET_CLASSLOADER_DEF = 512;
/*     */   private static final int ACTION_SET_CLASSLOADER_DEF_SUP = 1024;
/*     */   private static final int ACTION_ALL = 2047;
/*     */   static final String STR_GET = "get";
/*     */   static final String STR_GET_PRIV_SUP = "getPrivilegedSupplier";
/*     */   static final String STR_GET_GLOBAL_DEF = "getGlobalDefault";
/*     */   static final String STR_SET_GLOBAL_DEF = "setGlobalDefault";
/*     */   static final String STR_SET_GLOBAL_DEF_SUP = "setGlobalDefaultSupplier";
/*     */   static final String STR_GET_THREAD_DEF = "getThreadDefault";
/*     */   static final String STR_SET_THREAD_DEF = "setThreadDefault";
/*     */   static final String STR_SET_THREAD_DEF_SUP = "setThreadDefaultSupplier";
/*     */   static final String STR_GET_CLASSLOADER_DEF = "getClassLoaderDefault";
/*     */   static final String STR_SET_CLASSLOADER_DEF = "setClassLoaderDefault";
/*     */   static final String STR_SET_CLASSLOADER_DEF_SUP = "setClassLoaderDefaultSupplier";
/*     */   private final transient int actionBits;
/*     */   private transient String actionString;
/*     */   
/*     */   public ContextPermission(String name, String actions) {
/*  93 */     super(name);
/*  94 */     Assert.checkNotNullParam("name", name);
/*  95 */     Assert.checkNotNullParam("actions", actions);
/*  96 */     this.actionBits = parseActions(actions);
/*     */   }
/*     */   
/*     */   ContextPermission(String name, int actionBits) {
/* 100 */     super(name);
/* 101 */     Assert.checkNotNullParam("name", name);
/* 102 */     this.actionBits = actionBits & 0x7FF;
/*     */   }
/*     */   
/*     */   private static int parseActions(String actions) throws IllegalArgumentException {
/* 106 */     int bits = 0;
/* 107 */     int start = 0;
/* 108 */     int idx = actions.indexOf(',');
/* 109 */     if (idx == -1)
/* 110 */       return parseAction(actions); 
/*     */     while (true) {
/* 112 */       bits |= parseAction(actions.substring(start, idx));
/* 113 */       start = idx + 1;
/* 114 */       idx = actions.indexOf(',', start);
/* 115 */       if (idx == -1) {
/* 116 */         bits |= parseAction(actions.substring(start));
/* 117 */         return bits;
/*     */       } 
/*     */     } 
/*     */   } private static int parseAction(String action) {
/* 121 */     switch (action.trim()) { case "get":
/* 122 */         return 1;
/* 123 */       case "getPrivilegedSupplier": return 2;
/* 124 */       case "getGlobalDefault": return 4;
/* 125 */       case "setGlobalDefault": return 8;
/* 126 */       case "setGlobalDefaultSupplier": return 16;
/* 127 */       case "getThreadDefault": return 32;
/* 128 */       case "setThreadDefault": return 64;
/* 129 */       case "setThreadDefaultSupplier": return 128;
/* 130 */       case "getClassLoaderDefault": return 256;
/* 131 */       case "setClassLoaderDefault": return 512;
/* 132 */       case "setClassLoaderDefaultSupplier": return 1024;
/* 133 */       case "*": return 2047;
/* 134 */       case "": return 0; }
/*     */     
/* 136 */     throw CommonMessages.msg.invalidPermissionAction(action);
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
/*     */   public boolean implies(Permission permission) {
/* 149 */     return (permission instanceof ContextPermission && implies((ContextPermission)permission));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean implies(ContextPermission permission) {
/* 160 */     return (this == permission || (permission != null && isSet(this.actionBits, permission.actionBits) && impliesName(permission.getName())));
/*     */   }
/*     */   
/*     */   private boolean impliesName(String otherName) {
/* 164 */     String myName = getName();
/* 165 */     return (myName.equals("*") || myName.equals(otherName));
/*     */   }
/*     */   
/*     */   static boolean isSet(int mask, int test) {
/* 169 */     return ((mask & test) == test);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 179 */     return (obj instanceof ContextPermission && equals((ContextPermission)obj));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(ContextPermission permission) {
/* 189 */     return (this == permission || (permission != null && this.actionBits == permission.actionBits && getName().equals(permission.getName())));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 198 */     return getName().hashCode() * 17 + this.actionBits;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getActions() {
/* 207 */     String actionString = this.actionString;
/* 208 */     if (actionString == null) {
/* 209 */       int actionBits = this.actionBits;
/* 210 */       if (isSet(actionBits, 2047))
/* 211 */         return this.actionString = "*"; 
/* 212 */       if (actionBits == 0) {
/* 213 */         return this.actionString = "";
/*     */       }
/* 215 */       StringBuilder b = new StringBuilder();
/* 216 */       if (isSet(actionBits, 1)) b.append("get").append(','); 
/* 217 */       if (isSet(actionBits, 2)) b.append("getPrivilegedSupplier").append(','); 
/* 218 */       if (isSet(actionBits, 4)) b.append("getGlobalDefault").append(','); 
/* 219 */       if (isSet(actionBits, 8)) b.append("setGlobalDefault").append(','); 
/* 220 */       if (isSet(actionBits, 16)) b.append("setGlobalDefaultSupplier").append(','); 
/* 221 */       if (isSet(actionBits, 32)) b.append("getThreadDefault").append(','); 
/* 222 */       if (isSet(actionBits, 64)) b.append("setThreadDefault").append(','); 
/* 223 */       if (isSet(actionBits, 128)) b.append("setThreadDefaultSupplier").append(','); 
/* 224 */       if (isSet(actionBits, 256)) b.append("getClassLoaderDefault").append(','); 
/* 225 */       if (isSet(actionBits, 512)) b.append("setClassLoaderDefault").append(','); 
/* 226 */       if (isSet(actionBits, 1024)) b.append("setClassLoaderDefaultSupplier").append(','); 
/* 227 */       assert b.length() > 0;
/* 228 */       b.setLength(b.length() - 1);
/* 229 */       return this.actionString = b.toString();
/*     */     } 
/* 231 */     return actionString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public ContextPermission withActions(String actions) {
/* 242 */     return withActionBits(parseActions(actions));
/*     */   }
/*     */   
/*     */   ContextPermission withActionBits(int actionBits) {
/* 246 */     if (isSet(this.actionBits, actionBits)) {
/* 247 */       return this;
/*     */     }
/* 249 */     return new ContextPermission(getName(), this.actionBits | actionBits);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public ContextPermission withoutActions(String actions) {
/* 261 */     return withoutActionBits(parseActions(actions));
/*     */   }
/*     */   
/*     */   ContextPermission withoutActionBits(int actionBits) {
/* 265 */     if ((actionBits & this.actionBits) == 0) {
/* 266 */       return this;
/*     */     }
/* 268 */     return new ContextPermission(getName(), this.actionBits & (actionBits ^ 0xFFFFFFFF));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PermissionCollection newPermissionCollection() {
/* 278 */     return new ContextPermissionCollection();
/*     */   }
/*     */   
/*     */   int getActionBits() {
/* 282 */     return this.actionBits;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\context\ContextPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */