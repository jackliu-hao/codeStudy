/*    */ package org.h2.value;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.Comparator;
/*    */ import java.util.Locale;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.util.JdbcUtils;
/*    */ import org.h2.util.StringUtils;
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
/*    */ public class CompareModeIcu4J
/*    */   extends CompareMode
/*    */ {
/*    */   private final Comparator<String> collator;
/*    */   private volatile CompareModeIcu4J caseInsensitive;
/*    */   
/*    */   protected CompareModeIcu4J(String paramString, int paramInt) {
/* 27 */     super(paramString, paramInt);
/* 28 */     this.collator = getIcu4jCollator(paramString, paramInt);
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareString(String paramString1, String paramString2, boolean paramBoolean) {
/* 33 */     if (paramBoolean && getStrength() > 1) {
/* 34 */       CompareModeIcu4J compareModeIcu4J = this.caseInsensitive;
/* 35 */       if (compareModeIcu4J == null) {
/* 36 */         this.caseInsensitive = compareModeIcu4J = new CompareModeIcu4J(getName(), 1);
/*    */       }
/* 38 */       return compareModeIcu4J.compareString(paramString1, paramString2, false);
/*    */     } 
/* 40 */     return this.collator.compare(paramString1, paramString2);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equalsChars(String paramString1, int paramInt1, String paramString2, int paramInt2, boolean paramBoolean) {
/* 46 */     return (compareString(paramString1.substring(paramInt1, paramInt1 + 1), paramString2.substring(paramInt2, paramInt2 + 1), paramBoolean) == 0);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private static Comparator<String> getIcu4jCollator(String paramString, int paramInt) {
/*    */     try {
/* 53 */       Comparator<String> comparator = null;
/* 54 */       Class clazz = JdbcUtils.loadUserClass("com.ibm.icu.text.Collator");
/*    */       
/* 56 */       Method method = clazz.getMethod("getInstance", new Class[] { Locale.class });
/*    */       
/* 58 */       int i = paramString.length();
/* 59 */       if (i == 2) {
/* 60 */         Locale locale = new Locale(StringUtils.toLowerEnglish(paramString), "");
/* 61 */         if (compareLocaleNames(locale, paramString)) {
/* 62 */           comparator = (Comparator)method.invoke(null, new Object[] { locale });
/*    */         }
/* 64 */       } else if (i == 5) {
/*    */         
/* 66 */         int j = paramString.indexOf('_');
/* 67 */         if (j >= 0) {
/* 68 */           String str1 = StringUtils.toLowerEnglish(paramString.substring(0, j));
/* 69 */           String str2 = paramString.substring(j + 1);
/* 70 */           Locale locale = new Locale(str1, str2);
/* 71 */           if (compareLocaleNames(locale, paramString)) {
/* 72 */             comparator = (Comparator)method.invoke(null, new Object[] { locale });
/*    */           }
/*    */         } 
/*    */       } 
/* 76 */       if (comparator == null) {
/* 77 */         for (Locale locale : (Locale[])clazz.getMethod("getAvailableLocales", new Class[0])
/* 78 */           .invoke(null, new Object[0])) {
/* 79 */           if (compareLocaleNames(locale, paramString)) {
/* 80 */             comparator = (Comparator)method.invoke(null, new Object[] { locale });
/*    */             break;
/*    */           } 
/*    */         } 
/*    */       }
/* 85 */       if (comparator == null) {
/* 86 */         throw DbException.getInvalidValueException("collator", paramString);
/*    */       }
/* 88 */       clazz.getMethod("setStrength", new Class[] { int.class }).invoke(comparator, new Object[] { Integer.valueOf(paramInt) });
/* 89 */       return comparator;
/* 90 */     } catch (Exception exception) {
/* 91 */       throw DbException.convert(exception);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\CompareModeIcu4J.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */