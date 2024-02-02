/*     */ package org.apache.maven.model;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Repository
/*     */   extends RepositoryBase
/*     */   implements Serializable, Cloneable
/*     */ {
/*     */   private RepositoryPolicy releases;
/*     */   private RepositoryPolicy snapshots;
/*     */   
/*     */   public Repository clone() {
/*     */     try {
/*  51 */       Repository copy = (Repository)super.clone();
/*     */       
/*  53 */       if (this.releases != null)
/*     */       {
/*  55 */         copy.releases = this.releases.clone();
/*     */       }
/*     */       
/*  58 */       if (this.snapshots != null)
/*     */       {
/*  60 */         copy.snapshots = this.snapshots.clone();
/*     */       }
/*     */       
/*  63 */       return copy;
/*     */     }
/*  65 */     catch (Exception ex) {
/*     */       
/*  67 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/*     */   public RepositoryPolicy getReleases() {
/*  80 */     return this.releases;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RepositoryPolicy getSnapshots() {
/*  91 */     return this.snapshots;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setReleases(RepositoryPolicy releases) {
/* 102 */     this.releases = releases;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSnapshots(RepositoryPolicy snapshots) {
/* 113 */     this.snapshots = snapshots;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\Repository.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */