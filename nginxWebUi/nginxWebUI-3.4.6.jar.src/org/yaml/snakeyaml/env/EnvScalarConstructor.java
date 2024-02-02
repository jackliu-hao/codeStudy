/*    */ package org.yaml.snakeyaml.env;
/*    */ 
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ import org.yaml.snakeyaml.constructor.AbstractConstruct;
/*    */ import org.yaml.snakeyaml.constructor.Constructor;
/*    */ import org.yaml.snakeyaml.error.MissingEnvironmentVariableException;
/*    */ import org.yaml.snakeyaml.nodes.Node;
/*    */ import org.yaml.snakeyaml.nodes.ScalarNode;
/*    */ import org.yaml.snakeyaml.nodes.Tag;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class EnvScalarConstructor
/*    */   extends Constructor
/*    */ {
/* 34 */   public static final Tag ENV_TAG = new Tag("!ENV");
/*    */ 
/*    */   
/* 37 */   public static final Pattern ENV_FORMAT = Pattern.compile("^\\$\\{\\s*((?<name>\\w+)((?<separator>:?(-|\\?))(?<value>\\S+)?)?)\\s*\\}$");
/*    */   
/*    */   public EnvScalarConstructor() {
/* 40 */     this.yamlConstructors.put(ENV_TAG, new ConstructEnv());
/*    */   }
/*    */   
/*    */   private class ConstructEnv extends AbstractConstruct {
/*    */     public Object construct(Node node) {
/* 45 */       String val = EnvScalarConstructor.this.constructScalar((ScalarNode)node);
/* 46 */       Matcher matcher = EnvScalarConstructor.ENV_FORMAT.matcher(val);
/* 47 */       matcher.matches();
/* 48 */       String name = matcher.group("name");
/* 49 */       String value = matcher.group("value");
/* 50 */       String separator = matcher.group("separator");
/* 51 */       return EnvScalarConstructor.this.apply(name, separator, (value != null) ? value : "", EnvScalarConstructor.this.getEnv(name));
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     private ConstructEnv() {}
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String apply(String name, String separator, String value, String environment) {
/* 65 */     if (environment != null && !environment.isEmpty()) return environment;
/*    */     
/* 67 */     if (separator != null) {
/*    */       
/* 69 */       if (separator.equals("?") && 
/* 70 */         environment == null) {
/* 71 */         throw new MissingEnvironmentVariableException("Missing mandatory variable " + name + ": " + value);
/*    */       }
/* 73 */       if (separator.equals(":?")) {
/* 74 */         if (environment == null)
/* 75 */           throw new MissingEnvironmentVariableException("Missing mandatory variable " + name + ": " + value); 
/* 76 */         if (environment.isEmpty())
/* 77 */           throw new MissingEnvironmentVariableException("Empty mandatory variable " + name + ": " + value); 
/*    */       } 
/* 79 */       if (separator.startsWith(":")) {
/* 80 */         if (environment == null || environment.isEmpty()) {
/* 81 */           return value;
/*    */         }
/* 83 */       } else if (environment == null) {
/* 84 */         return value;
/*    */       } 
/*    */     } 
/* 87 */     return "";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getEnv(String key) {
/* 97 */     return System.getenv(key);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\env\EnvScalarConstructor.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */