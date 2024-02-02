/*    */ package ch.qos.logback.core.net;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.InvalidClassException;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.io.ObjectStreamClass;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ public class HardenedObjectInputStream
/*    */   extends ObjectInputStream
/*    */ {
/*    */   final List<String> whitelistedClassNames;
/* 24 */   static final String[] JAVA_PACKAGES = new String[] { "java.lang", "java.util" };
/*    */   
/*    */   public HardenedObjectInputStream(InputStream in, String[] whilelist) throws IOException {
/* 27 */     super(in);
/*    */     
/* 29 */     this.whitelistedClassNames = new ArrayList<String>();
/* 30 */     if (whilelist != null) {
/* 31 */       for (int i = 0; i < whilelist.length; i++) {
/* 32 */         this.whitelistedClassNames.add(whilelist[i]);
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public HardenedObjectInputStream(InputStream in, List<String> whitelist) throws IOException {
/* 38 */     super(in);
/*    */     
/* 40 */     this.whitelistedClassNames = new ArrayList<String>();
/* 41 */     this.whitelistedClassNames.addAll(whitelist);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected Class<?> resolveClass(ObjectStreamClass anObjectStreamClass) throws IOException, ClassNotFoundException {
/* 47 */     String incomingClassName = anObjectStreamClass.getName();
/*    */     
/* 49 */     if (!isWhitelisted(incomingClassName)) {
/* 50 */       throw new InvalidClassException("Unauthorized deserialization attempt", anObjectStreamClass.getName());
/*    */     }
/*    */     
/* 53 */     return super.resolveClass(anObjectStreamClass);
/*    */   }
/*    */   
/*    */   private boolean isWhitelisted(String incomingClassName) {
/* 57 */     for (int i = 0; i < JAVA_PACKAGES.length; i++) {
/* 58 */       if (incomingClassName.startsWith(JAVA_PACKAGES[i]))
/* 59 */         return true; 
/*    */     } 
/* 61 */     for (String whiteListed : this.whitelistedClassNames) {
/* 62 */       if (incomingClassName.equals(whiteListed))
/* 63 */         return true; 
/*    */     } 
/* 65 */     return false;
/*    */   }
/*    */   
/*    */   protected void addToWhitelist(List<String> additionalAuthorizedClasses) {
/* 69 */     this.whitelistedClassNames.addAll(additionalAuthorizedClasses);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\HardenedObjectInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */