/*     */ package freemarker.cache;
/*     */ 
/*     */ import freemarker.template.utility.NullArgumentException;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ public class MultiTemplateLoader
/*     */   implements StatefulTemplateLoader
/*     */ {
/*     */   private final TemplateLoader[] templateLoaders;
/*  41 */   private final Map<String, TemplateLoader> lastTemplateLoaderForName = new ConcurrentHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean sticky = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MultiTemplateLoader(TemplateLoader[] templateLoaders) {
/*  54 */     NullArgumentException.check("templateLoaders", templateLoaders);
/*  55 */     this.templateLoaders = (TemplateLoader[])templateLoaders.clone();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object findTemplateSource(String name) throws IOException {
/*  61 */     TemplateLoader lastTemplateLoader = null;
/*  62 */     if (this.sticky) {
/*     */ 
/*     */       
/*  65 */       lastTemplateLoader = this.lastTemplateLoaderForName.get(name);
/*  66 */       if (lastTemplateLoader != null) {
/*  67 */         Object source = lastTemplateLoader.findTemplateSource(name);
/*  68 */         if (source != null) {
/*  69 */           return new MultiSource(source, lastTemplateLoader);
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  78 */     for (TemplateLoader templateLoader : this.templateLoaders) {
/*  79 */       if (lastTemplateLoader != templateLoader) {
/*  80 */         Object source = templateLoader.findTemplateSource(name);
/*  81 */         if (source != null) {
/*  82 */           if (this.sticky) {
/*  83 */             this.lastTemplateLoaderForName.put(name, templateLoader);
/*     */           }
/*  85 */           return new MultiSource(source, templateLoader);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/*  90 */     if (this.sticky) {
/*  91 */       this.lastTemplateLoaderForName.remove(name);
/*     */     }
/*     */     
/*  94 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLastModified(Object templateSource) {
/*  99 */     return ((MultiSource)templateSource).getLastModified();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Reader getReader(Object templateSource, String encoding) throws IOException {
/* 105 */     return ((MultiSource)templateSource).getReader(encoding);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeTemplateSource(Object templateSource) throws IOException {
/* 111 */     ((MultiSource)templateSource).close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resetState() {
/* 119 */     this.lastTemplateLoaderForName.clear();
/* 120 */     for (TemplateLoader loader : this.templateLoaders) {
/* 121 */       if (loader instanceof StatefulTemplateLoader) {
/* 122 */         ((StatefulTemplateLoader)loader).resetState();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static final class MultiSource
/*     */   {
/*     */     private final Object source;
/*     */     
/*     */     private final TemplateLoader loader;
/*     */ 
/*     */     
/*     */     MultiSource(Object source, TemplateLoader loader) {
/* 137 */       this.source = source;
/* 138 */       this.loader = loader;
/*     */     }
/*     */     
/*     */     long getLastModified() {
/* 142 */       return this.loader.getLastModified(this.source);
/*     */     }
/*     */ 
/*     */     
/*     */     Reader getReader(String encoding) throws IOException {
/* 147 */       return this.loader.getReader(this.source, encoding);
/*     */     }
/*     */ 
/*     */     
/*     */     void close() throws IOException {
/* 152 */       this.loader.closeTemplateSource(this.source);
/*     */     }
/*     */     
/*     */     Object getWrappedSource() {
/* 156 */       return this.source;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 161 */       if (o instanceof MultiSource) {
/* 162 */         MultiSource m = (MultiSource)o;
/* 163 */         return (m.loader.equals(this.loader) && m.source.equals(this.source));
/*     */       } 
/* 165 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 170 */       return this.loader.hashCode() + 31 * this.source.hashCode();
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 175 */       return this.source.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 186 */     StringBuilder sb = new StringBuilder();
/* 187 */     sb.append("MultiTemplateLoader(");
/* 188 */     for (int i = 0; i < this.templateLoaders.length; i++) {
/* 189 */       if (i != 0) {
/* 190 */         sb.append(", ");
/*     */       }
/* 192 */       sb.append("loader").append(i + 1).append(" = ").append(this.templateLoaders[i]);
/*     */     } 
/* 194 */     sb.append(")");
/* 195 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTemplateLoaderCount() {
/* 204 */     return this.templateLoaders.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateLoader getTemplateLoader(int index) {
/* 214 */     return this.templateLoaders[index];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSticky() {
/* 223 */     return this.sticky;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSticky(boolean sticky) {
/* 234 */     this.sticky = sticky;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\MultiTemplateLoader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */