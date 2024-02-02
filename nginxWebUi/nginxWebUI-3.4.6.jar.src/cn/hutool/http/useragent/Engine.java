/*    */ package cn.hutool.http.useragent;
/*    */ 
/*    */ import cn.hutool.core.collection.CollUtil;
/*    */ import cn.hutool.core.util.ReUtil;
/*    */ import java.util.List;
/*    */ import java.util.regex.Pattern;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Engine
/*    */   extends UserAgentInfo
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 19 */   public static final Engine Unknown = new Engine("Unknown", null);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 24 */   public static final List<Engine> engines = CollUtil.newArrayList((Object[])new Engine[] { new Engine("Trident", "trident"), new Engine("Webkit", "webkit"), new Engine("Chrome", "chrome"), new Engine("Opera", "opera"), new Engine("Presto", "presto"), new Engine("Gecko", "gecko"), new Engine("KHTML", "khtml"), new Engine("Konqueror", "konqueror"), new Engine("MIDP", "MIDP") });
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private final Pattern versionPattern;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Engine(String name, String regex) {
/* 45 */     super(name, regex);
/* 46 */     this.versionPattern = Pattern.compile(name + "[/\\- ]([\\d\\w.\\-]+)", 2);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getVersion(String userAgentString) {
/* 57 */     if (isUnknown()) {
/* 58 */       return null;
/*    */     }
/* 60 */     return ReUtil.getGroup1(this.versionPattern, userAgentString);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\htt\\useragent\Engine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */