/*    */ package org.h2.value;
/*    */ 
/*    */ import java.text.CollationKey;
/*    */ import java.text.Collator;
/*    */ import org.h2.engine.SysProperties;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.util.SmallLRUCache;
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
/*    */ public class CompareModeDefault
/*    */   extends CompareMode
/*    */ {
/*    */   private final Collator collator;
/*    */   private final SmallLRUCache<String, CollationKey> collationKeys;
/*    */   private volatile CompareModeDefault caseInsensitive;
/*    */   
/*    */   protected CompareModeDefault(String paramString, int paramInt) {
/* 26 */     super(paramString, paramInt);
/* 27 */     this.collator = CompareMode.getCollator(paramString);
/* 28 */     if (this.collator == null) {
/* 29 */       throw DbException.getInternalError(paramString);
/*    */     }
/* 31 */     this.collator.setStrength(paramInt);
/* 32 */     int i = SysProperties.COLLATOR_CACHE_SIZE;
/* 33 */     if (i != 0) {
/* 34 */       this.collationKeys = SmallLRUCache.newInstance(i);
/*    */     } else {
/* 36 */       this.collationKeys = null;
/*    */     } 
/*    */   }
/*    */   
/*    */   public int compareString(String paramString1, String paramString2, boolean paramBoolean) {
/*    */     int i;
/* 42 */     if (paramBoolean && getStrength() > 1) {
/* 43 */       CompareModeDefault compareModeDefault = this.caseInsensitive;
/* 44 */       if (compareModeDefault == null) {
/* 45 */         this.caseInsensitive = compareModeDefault = new CompareModeDefault(getName(), 1);
/*    */       }
/* 47 */       return compareModeDefault.compareString(paramString1, paramString2, false);
/*    */     } 
/*    */     
/* 50 */     if (this.collationKeys != null) {
/* 51 */       CollationKey collationKey1 = getKey(paramString1);
/* 52 */       CollationKey collationKey2 = getKey(paramString2);
/* 53 */       i = collationKey1.compareTo(collationKey2);
/*    */     } else {
/* 55 */       i = this.collator.compare(paramString1, paramString2);
/*    */     } 
/* 57 */     return i;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equalsChars(String paramString1, int paramInt1, String paramString2, int paramInt2, boolean paramBoolean) {
/* 63 */     return (compareString(paramString1.substring(paramInt1, paramInt1 + 1), paramString2.substring(paramInt2, paramInt2 + 1), paramBoolean) == 0);
/*    */   }
/*    */ 
/*    */   
/*    */   private CollationKey getKey(String paramString) {
/* 68 */     synchronized (this.collationKeys) {
/* 69 */       CollationKey collationKey = (CollationKey)this.collationKeys.get(paramString);
/* 70 */       if (collationKey == null) {
/* 71 */         collationKey = this.collator.getCollationKey(paramString);
/* 72 */         this.collationKeys.put(paramString, collationKey);
/*    */       } 
/* 74 */       return collationKey;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\CompareModeDefault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */