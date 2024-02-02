/*     */ package freemarker.cache;
/*     */ 
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.io.UnsupportedEncodingException;
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
/*     */ public class ByteArrayTemplateLoader
/*     */   implements TemplateLoader
/*     */ {
/*  39 */   private final Map<String, ByteArrayTemplateSource> templates = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void putTemplate(String name, byte[] templateContent) {
/*  45 */     putTemplate(name, templateContent, System.currentTimeMillis());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void putTemplate(String name, byte[] templateContent, long lastModified) {
/*  53 */     this.templates.put(name, new ByteArrayTemplateSource(name, templateContent, lastModified));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean removeTemplate(String name) {
/*  63 */     return (this.templates.remove(name) != null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeTemplateSource(Object templateSource) {}
/*     */ 
/*     */   
/*     */   public Object findTemplateSource(String name) {
/*  72 */     return this.templates.get(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLastModified(Object templateSource) {
/*  77 */     return ((ByteArrayTemplateSource)templateSource).lastModified;
/*     */   }
/*     */ 
/*     */   
/*     */   public Reader getReader(Object templateSource, String encoding) throws UnsupportedEncodingException {
/*  82 */     return new InputStreamReader(new ByteArrayInputStream(((ByteArrayTemplateSource)templateSource)
/*  83 */           .templateContent), encoding);
/*     */   }
/*     */   
/*     */   private static class ByteArrayTemplateSource
/*     */   {
/*     */     private final String name;
/*     */     private final byte[] templateContent;
/*     */     private final long lastModified;
/*     */     
/*     */     ByteArrayTemplateSource(String name, byte[] templateContent, long lastModified) {
/*  93 */       if (name == null) {
/*  94 */         throw new IllegalArgumentException("name == null");
/*     */       }
/*  96 */       if (templateContent == null) {
/*  97 */         throw new IllegalArgumentException("templateContent == null");
/*     */       }
/*  99 */       if (lastModified < -1L) {
/* 100 */         throw new IllegalArgumentException("lastModified < -1L");
/*     */       }
/* 102 */       this.name = name;
/* 103 */       this.templateContent = templateContent;
/* 104 */       this.lastModified = lastModified;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 109 */       int prime = 31;
/* 110 */       int result = 1;
/* 111 */       result = 31 * result + ((this.name == null) ? 0 : this.name.hashCode());
/* 112 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 117 */       if (this == obj)
/* 118 */         return true; 
/* 119 */       if (obj == null)
/* 120 */         return false; 
/* 121 */       if (getClass() != obj.getClass())
/* 122 */         return false; 
/* 123 */       ByteArrayTemplateSource other = (ByteArrayTemplateSource)obj;
/* 124 */       if (this.name == null) {
/* 125 */         if (other.name != null)
/* 126 */           return false; 
/* 127 */       } else if (!this.name.equals(other.name)) {
/* 128 */         return false;
/* 129 */       }  return true;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 139 */     StringBuilder sb = new StringBuilder();
/* 140 */     sb.append(TemplateLoaderUtils.getClassNameForToString(this));
/* 141 */     sb.append("(Map { ");
/* 142 */     int cnt = 0;
/* 143 */     for (String name : this.templates.keySet()) {
/* 144 */       cnt++;
/* 145 */       if (cnt != 1) {
/* 146 */         sb.append(", ");
/*     */       }
/* 148 */       if (cnt > 10) {
/* 149 */         sb.append("...");
/*     */         break;
/*     */       } 
/* 152 */       sb.append(StringUtil.jQuote(name));
/* 153 */       sb.append("=...");
/*     */     } 
/* 155 */     if (cnt != 0) {
/* 156 */       sb.append(' ');
/*     */     }
/* 158 */     sb.append("})");
/* 159 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\ByteArrayTemplateLoader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */