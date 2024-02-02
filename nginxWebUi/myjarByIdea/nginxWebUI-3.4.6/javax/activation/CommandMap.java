package javax.activation;

public abstract class CommandMap {
   private static CommandMap defaultCommandMap = null;

   public static CommandMap getDefaultCommandMap() {
      if (defaultCommandMap == null) {
         defaultCommandMap = new MailcapCommandMap();
      }

      return defaultCommandMap;
   }

   public static void setDefaultCommandMap(CommandMap commandMap) {
      SecurityManager security = System.getSecurityManager();
      if (security != null) {
         try {
            security.checkSetFactory();
         } catch (SecurityException var3) {
            if (CommandMap.class.getClassLoader() != commandMap.getClass().getClassLoader()) {
               throw var3;
            }
         }
      }

      defaultCommandMap = commandMap;
   }

   public abstract CommandInfo[] getPreferredCommands(String var1);

   public CommandInfo[] getPreferredCommands(String mimeType, DataSource ds) {
      return this.getPreferredCommands(mimeType);
   }

   public abstract CommandInfo[] getAllCommands(String var1);

   public CommandInfo[] getAllCommands(String mimeType, DataSource ds) {
      return this.getAllCommands(mimeType);
   }

   public abstract CommandInfo getCommand(String var1, String var2);

   public CommandInfo getCommand(String mimeType, String cmdName, DataSource ds) {
      return this.getCommand(mimeType, cmdName);
   }

   public abstract DataContentHandler createDataContentHandler(String var1);

   public DataContentHandler createDataContentHandler(String mimeType, DataSource ds) {
      return this.createDataContentHandler(mimeType);
   }

   public String[] getMimeTypes() {
      return null;
   }
}
