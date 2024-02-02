/*     */ package org.apache.commons.compress.harmony.archive.internal.nls;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.util.Locale;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.ResourceBundle;
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
/*     */ public class Messages
/*     */ {
/*  48 */   private static ResourceBundle bundle = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getString(String msg) {
/*  57 */     if (bundle == null) {
/*  58 */       return msg;
/*     */     }
/*     */     try {
/*  61 */       return bundle.getString(msg);
/*  62 */     } catch (MissingResourceException e) {
/*  63 */       return "Missing message: " + msg;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getString(String msg, Object arg) {
/*  75 */     return getString(msg, new Object[] { arg });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getString(String msg, int arg) {
/*  86 */     return getString(msg, new Object[] { Integer.toString(arg) });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getString(String msg, char arg) {
/*  97 */     return getString(msg, new Object[] { String.valueOf(arg) });
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
/*     */   public static String getString(String msg, Object arg1, Object arg2) {
/* 109 */     return getString(msg, new Object[] { arg1, arg2 });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getString(String msg, Object[] args) {
/* 120 */     String format = msg;
/*     */     
/* 122 */     if (bundle != null) {
/*     */       try {
/* 124 */         format = bundle.getString(msg);
/* 125 */       } catch (MissingResourceException missingResourceException) {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 130 */     return format(format, args);
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
/*     */   public static String format(String format, Object[] args) {
/* 146 */     StringBuilder answer = new StringBuilder(format.length() + args.length * 20);
/* 147 */     String[] argStrings = new String[args.length];
/* 148 */     for (int i = 0; i < args.length; i++) {
/* 149 */       if (args[i] == null) {
/* 150 */         argStrings[i] = "<null>";
/*     */       } else {
/* 152 */         argStrings[i] = args[i].toString();
/*     */       } 
/*     */     } 
/* 155 */     int lastI = 0; int j;
/* 156 */     for (j = format.indexOf('{', 0); j >= 0; j = format.indexOf('{', lastI)) {
/* 157 */       if (j != 0 && format.charAt(j - 1) == '\\') {
/*     */         
/* 159 */         if (j != 1) {
/* 160 */           answer.append(format.substring(lastI, j - 1));
/*     */         }
/* 162 */         answer.append('{');
/* 163 */         lastI = j + 1;
/*     */       }
/* 165 */       else if (j > format.length() - 3) {
/*     */         
/* 167 */         answer.append(format.substring(lastI));
/* 168 */         lastI = format.length();
/*     */       } else {
/* 170 */         int argnum = (byte)Character.digit(format.charAt(j + 1), 10);
/* 171 */         if (argnum < 0 || format.charAt(j + 2) != '}') {
/*     */           
/* 173 */           answer.append(format.substring(lastI, j + 1));
/* 174 */           lastI = j + 1;
/*     */         } else {
/*     */           
/* 177 */           answer.append(format.substring(lastI, j));
/* 178 */           if (argnum >= argStrings.length) {
/* 179 */             answer.append("<missing argument>");
/*     */           } else {
/* 181 */             answer.append(argStrings[argnum]);
/*     */           } 
/* 183 */           lastI = j + 3;
/*     */         } 
/*     */       } 
/*     */     } 
/* 187 */     if (lastI < format.length()) {
/* 188 */       answer.append(format.substring(lastI));
/*     */     }
/* 190 */     return answer.toString();
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
/*     */   public static ResourceBundle setLocale(Locale locale, String resource) {
/*     */     try {
/* 203 */       ClassLoader loader = null;
/* 204 */       return AccessController.<ResourceBundle>doPrivileged(() -> ResourceBundle.getBundle(resource, locale, (loader != null) ? loader : ClassLoader.getSystemClassLoader()));
/*     */     }
/* 206 */     catch (MissingResourceException missingResourceException) {
/*     */ 
/*     */       
/* 209 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   static {
/*     */     try {
/* 215 */       bundle = setLocale(Locale.getDefault(), "org.apache.commons.compress.harmony.archive.internal.nls.messages");
/*     */     }
/* 217 */     catch (Throwable e) {
/* 218 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\archive\internal\nls\Messages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */