/*    */ package cn.hutool.core.io.resource;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.util.Collection;
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
/*    */ public class MultiFileResource
/*    */   extends MultiResource
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public MultiFileResource(Collection<File> files) {
/* 21 */     super(new Resource[0]);
/* 22 */     add(files);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MultiFileResource(File... files) {
/* 30 */     super(new Resource[0]);
/* 31 */     add(files);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MultiFileResource add(File... files) {
/* 41 */     for (File file : files) {
/* 42 */       add(new FileResource(file));
/*    */     }
/* 44 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MultiFileResource add(Collection<File> files) {
/* 54 */     for (File file : files) {
/* 55 */       add(new FileResource(file));
/*    */     }
/* 57 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public MultiFileResource add(Resource resource) {
/* 62 */     return (MultiFileResource)super.add(resource);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\resource\MultiFileResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */