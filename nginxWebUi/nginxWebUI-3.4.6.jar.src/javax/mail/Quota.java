/*     */ package javax.mail;
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
/*     */ public class Quota
/*     */ {
/*     */   public String quotaRoot;
/*     */   public Resource[] resources;
/*     */   
/*     */   public static class Resource
/*     */   {
/*     */     public String name;
/*     */     public long usage;
/*     */     public long limit;
/*     */     
/*     */     public Resource(String name, long usage, long limit) {
/*  80 */       this.name = name;
/*  81 */       this.usage = usage;
/*  82 */       this.limit = limit;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Quota(String quotaRoot) {
/* 103 */     this.quotaRoot = quotaRoot;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setResourceLimit(String name, long limit) {
/* 113 */     if (this.resources == null) {
/* 114 */       this.resources = new Resource[1];
/* 115 */       this.resources[0] = new Resource(name, 0L, limit);
/*     */       return;
/*     */     } 
/* 118 */     for (int i = 0; i < this.resources.length; i++) {
/* 119 */       if ((this.resources[i]).name.equalsIgnoreCase(name)) {
/* 120 */         (this.resources[i]).limit = limit;
/*     */         return;
/*     */       } 
/*     */     } 
/* 124 */     Resource[] ra = new Resource[this.resources.length + 1];
/* 125 */     System.arraycopy(this.resources, 0, ra, 0, this.resources.length);
/* 126 */     ra[ra.length - 1] = new Resource(name, 0L, limit);
/* 127 */     this.resources = ra;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\Quota.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */