/*     */ package freemarker.cache;
/*     */ 
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public class StringTemplateLoader
/*     */   implements TemplateLoader
/*     */ {
/*  64 */   private final Map<String, StringTemplateSource> templates = new HashMap<>();
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
/*     */   public void putTemplate(String name, String templateContent) {
/*  78 */     putTemplate(name, templateContent, System.currentTimeMillis());
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
/*     */   public void putTemplate(String name, String templateContent, long lastModified) {
/* 101 */     this.templates.put(name, new StringTemplateSource(name, templateContent, lastModified));
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
/*     */   public boolean removeTemplate(String name) {
/* 117 */     return (this.templates.remove(name) != null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeTemplateSource(Object templateSource) {}
/*     */ 
/*     */   
/*     */   public Object findTemplateSource(String name) {
/* 126 */     return this.templates.get(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLastModified(Object templateSource) {
/* 131 */     return ((StringTemplateSource)templateSource).lastModified;
/*     */   }
/*     */ 
/*     */   
/*     */   public Reader getReader(Object templateSource, String encoding) {
/* 136 */     return new StringReader(((StringTemplateSource)templateSource).templateContent);
/*     */   }
/*     */   
/*     */   private static class StringTemplateSource {
/*     */     private final String name;
/*     */     private final String templateContent;
/*     */     private final long lastModified;
/*     */     
/*     */     StringTemplateSource(String name, String templateContent, long lastModified) {
/* 145 */       if (name == null) {
/* 146 */         throw new IllegalArgumentException("name == null");
/*     */       }
/* 148 */       if (templateContent == null) {
/* 149 */         throw new IllegalArgumentException("source == null");
/*     */       }
/* 151 */       if (lastModified < -1L) {
/* 152 */         throw new IllegalArgumentException("lastModified < -1L");
/*     */       }
/* 154 */       this.name = name;
/* 155 */       this.templateContent = templateContent;
/* 156 */       this.lastModified = lastModified;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 161 */       int prime = 31;
/* 162 */       int result = 1;
/* 163 */       result = 31 * result + ((this.name == null) ? 0 : this.name.hashCode());
/* 164 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 169 */       if (this == obj)
/* 170 */         return true; 
/* 171 */       if (obj == null)
/* 172 */         return false; 
/* 173 */       if (getClass() != obj.getClass())
/* 174 */         return false; 
/* 175 */       StringTemplateSource other = (StringTemplateSource)obj;
/* 176 */       if (this.name == null) {
/* 177 */         if (other.name != null)
/* 178 */           return false; 
/* 179 */       } else if (!this.name.equals(other.name)) {
/* 180 */         return false;
/* 181 */       }  return true;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String toString() {
/* 187 */       return this.name;
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
/*     */   public String toString() {
/* 199 */     StringBuilder sb = new StringBuilder();
/* 200 */     sb.append(TemplateLoaderUtils.getClassNameForToString(this));
/* 201 */     sb.append("(Map { ");
/* 202 */     int cnt = 0;
/* 203 */     for (String name : this.templates.keySet()) {
/* 204 */       cnt++;
/* 205 */       if (cnt != 1) {
/* 206 */         sb.append(", ");
/*     */       }
/* 208 */       if (cnt > 10) {
/* 209 */         sb.append("...");
/*     */         break;
/*     */       } 
/* 212 */       sb.append(StringUtil.jQuote(name));
/* 213 */       sb.append("=...");
/*     */     } 
/* 215 */     if (cnt != 0) {
/* 216 */       sb.append(' ');
/*     */     }
/* 218 */     sb.append("})");
/* 219 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\StringTemplateLoader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */