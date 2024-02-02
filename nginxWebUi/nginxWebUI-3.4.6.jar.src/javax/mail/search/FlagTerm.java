/*     */ package javax.mail.search;
/*     */ 
/*     */ import javax.mail.Flags;
/*     */ import javax.mail.Message;
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
/*     */ public final class FlagTerm
/*     */   extends SearchTerm
/*     */ {
/*     */   protected boolean set;
/*     */   protected Flags flags;
/*     */   private static final long serialVersionUID = -142991500302030647L;
/*     */   
/*     */   public FlagTerm(Flags flags, boolean set) {
/*  79 */     this.flags = flags;
/*  80 */     this.set = set;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Flags getFlags() {
/*  87 */     return (Flags)this.flags.clone();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getTestSet() {
/*  94 */     return this.set;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean match(Message msg) {
/*     */     try {
/* 106 */       Flags f = msg.getFlags();
/* 107 */       if (this.set) {
/* 108 */         if (f.contains(this.flags)) {
/* 109 */           return true;
/*     */         }
/* 111 */         return false;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 118 */       Flags.Flag[] sf = this.flags.getSystemFlags();
/*     */ 
/*     */       
/* 121 */       for (int i = 0; i < sf.length; i++) {
/* 122 */         if (f.contains(sf[i]))
/*     */         {
/* 124 */           return false;
/*     */         }
/*     */       } 
/* 127 */       String[] s = this.flags.getUserFlags();
/*     */ 
/*     */       
/* 130 */       for (int j = 0; j < s.length; j++) {
/* 131 */         if (f.contains(s[j]))
/*     */         {
/* 133 */           return false;
/*     */         }
/*     */       } 
/* 136 */       return true;
/*     */     }
/* 138 */     catch (Exception e) {
/* 139 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 147 */     if (!(obj instanceof FlagTerm))
/* 148 */       return false; 
/* 149 */     FlagTerm ft = (FlagTerm)obj;
/* 150 */     return (ft.set == this.set && ft.flags.equals(this.flags));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 157 */     return this.set ? this.flags.hashCode() : (this.flags.hashCode() ^ 0xFFFFFFFF);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\search\FlagTerm.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */