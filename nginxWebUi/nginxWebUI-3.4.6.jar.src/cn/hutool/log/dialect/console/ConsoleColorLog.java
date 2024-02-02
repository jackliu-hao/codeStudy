/*    */ package cn.hutool.log.dialect.console;
/*    */ 
/*    */ import cn.hutool.core.date.DateUtil;
/*    */ import cn.hutool.core.lang.ansi.AnsiColor;
/*    */ import cn.hutool.core.lang.ansi.AnsiEncoder;
/*    */ import cn.hutool.core.util.ClassUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.log.level.Level;
/*    */ import java.util.function.Function;
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
/*    */ public class ConsoleColorLog
/*    */   extends ConsoleLog
/*    */ {
/* 23 */   private static final AnsiColor COLOR_CLASSNAME = AnsiColor.CYAN;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 28 */   private static final AnsiColor COLOR_TIME = AnsiColor.WHITE;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 33 */   private static final AnsiColor COLOR_NONE = AnsiColor.DEFAULT;
/*    */   static {
/* 35 */     colorFactory = (level -> {
/*    */         switch (level) {
/*    */           case DEBUG:
/*    */           case INFO:
/*    */             return AnsiColor.GREEN;
/*    */           case WARN:
/*    */             return AnsiColor.YELLOW;
/*    */           case ERROR:
/*    */             return AnsiColor.RED;
/*    */           case TRACE:
/*    */             return AnsiColor.MAGENTA;
/*    */         } 
/*    */         return COLOR_NONE;
/*    */       });
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private static Function<Level, AnsiColor> colorFactory;
/*    */ 
/*    */   
/*    */   public static void setColorFactory(Function<Level, AnsiColor> colorFactory) {
/* 57 */     ConsoleColorLog.colorFactory = colorFactory;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ConsoleColorLog(String name) {
/* 66 */     super(name);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ConsoleColorLog(Class<?> clazz) {
/* 75 */     super(clazz);
/*    */   }
/*    */ 
/*    */   
/*    */   public synchronized void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
/* 80 */     if (false == isEnabled(level)) {
/*    */       return;
/*    */     }
/*    */     
/* 84 */     String template = AnsiEncoder.encode(new Object[] { COLOR_TIME, "[%s]", colorFactory.apply(level), "[%-5s]%s", COLOR_CLASSNAME, "%-30s: ", COLOR_NONE, "%s%n" });
/* 85 */     System.out.format(template, new Object[] { DateUtil.now(), level.name(), " - ", ClassUtil.getShortClassName(getName()), StrUtil.format(format, arguments) });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\console\ConsoleColorLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */