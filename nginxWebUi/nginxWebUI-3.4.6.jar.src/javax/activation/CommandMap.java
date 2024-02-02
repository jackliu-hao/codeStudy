/*     */ package javax.activation;
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
/*     */ public abstract class CommandMap
/*     */ {
/*  40 */   private static CommandMap defaultCommandMap = null;
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
/*     */   public static CommandMap getDefaultCommandMap() {
/*  60 */     if (defaultCommandMap == null) {
/*  61 */       defaultCommandMap = new MailcapCommandMap();
/*     */     }
/*  63 */     return defaultCommandMap;
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
/*     */   public static void setDefaultCommandMap(CommandMap commandMap) {
/*  75 */     SecurityManager security = System.getSecurityManager();
/*  76 */     if (security != null)
/*     */       
/*     */       try {
/*  79 */         security.checkSetFactory();
/*  80 */       } catch (SecurityException ex) {
/*     */ 
/*     */ 
/*     */         
/*  84 */         if (CommandMap.class.getClassLoader() != commandMap.getClass().getClassLoader())
/*     */         {
/*  86 */           throw ex;
/*     */         }
/*     */       }  
/*  89 */     defaultCommandMap = commandMap;
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
/*     */   public CommandInfo[] getPreferredCommands(String mimeType, DataSource ds) {
/* 117 */     return getPreferredCommands(mimeType);
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
/*     */   public CommandInfo[] getAllCommands(String mimeType, DataSource ds) {
/* 145 */     return getAllCommands(mimeType);
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
/*     */   public CommandInfo getCommand(String mimeType, String cmdName, DataSource ds) {
/* 174 */     return getCommand(mimeType, cmdName);
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
/*     */   public DataContentHandler createDataContentHandler(String mimeType, DataSource ds) {
/* 206 */     return createDataContentHandler(mimeType);
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
/*     */   public String[] getMimeTypes() {
/* 218 */     return null;
/*     */   }
/*     */   
/*     */   public abstract CommandInfo[] getPreferredCommands(String paramString);
/*     */   
/*     */   public abstract CommandInfo[] getAllCommands(String paramString);
/*     */   
/*     */   public abstract CommandInfo getCommand(String paramString1, String paramString2);
/*     */   
/*     */   public abstract DataContentHandler createDataContentHandler(String paramString);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\activation\CommandMap.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */