/*     */ package org.yaml.snakeyaml.nodes;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.yaml.snakeyaml.comments.CommentLine;
/*     */ import org.yaml.snakeyaml.error.Mark;
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
/*     */ public abstract class Node
/*     */ {
/*     */   private Tag tag;
/*     */   private Mark startMark;
/*     */   protected Mark endMark;
/*     */   private Class<? extends Object> type;
/*     */   private boolean twoStepsConstruction;
/*     */   private String anchor;
/*     */   private List<CommentLine> inLineComments;
/*     */   private List<CommentLine> blockComments;
/*     */   private List<CommentLine> endComments;
/*     */   protected boolean resolved;
/*     */   protected Boolean useClassConstructor;
/*     */   
/*     */   public Node(Tag tag, Mark startMark, Mark endMark) {
/*  55 */     setTag(tag);
/*  56 */     this.startMark = startMark;
/*  57 */     this.endMark = endMark;
/*  58 */     this.type = Object.class;
/*  59 */     this.twoStepsConstruction = false;
/*  60 */     this.resolved = true;
/*  61 */     this.useClassConstructor = null;
/*  62 */     this.inLineComments = null;
/*  63 */     this.blockComments = null;
/*  64 */     this.endComments = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Tag getTag() {
/*  75 */     return this.tag;
/*     */   }
/*     */   
/*     */   public Mark getEndMark() {
/*  79 */     return this.endMark;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract NodeId getNodeId();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mark getStartMark() {
/*  91 */     return this.startMark;
/*     */   }
/*     */   
/*     */   public void setTag(Tag tag) {
/*  95 */     if (tag == null) {
/*  96 */       throw new NullPointerException("tag in a Node is required.");
/*     */     }
/*  98 */     this.tag = tag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean equals(Object obj) {
/* 106 */     return super.equals(obj);
/*     */   }
/*     */   
/*     */   public Class<? extends Object> getType() {
/* 110 */     return this.type;
/*     */   }
/*     */   
/*     */   public void setType(Class<? extends Object> type) {
/* 114 */     if (!type.isAssignableFrom(this.type)) {
/* 115 */       this.type = type;
/*     */     }
/*     */   }
/*     */   
/*     */   public void setTwoStepsConstruction(boolean twoStepsConstruction) {
/* 120 */     this.twoStepsConstruction = twoStepsConstruction;
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
/*     */   public boolean isTwoStepsConstruction() {
/* 141 */     return this.twoStepsConstruction;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int hashCode() {
/* 146 */     return super.hashCode();
/*     */   }
/*     */   
/*     */   public boolean useClassConstructor() {
/* 150 */     if (this.useClassConstructor == null) {
/* 151 */       if (!this.tag.isSecondary() && this.resolved && !Object.class.equals(this.type) && !this.tag.equals(Tag.NULL))
/*     */       {
/* 153 */         return true; } 
/* 154 */       if (this.tag.isCompatible(getType()))
/*     */       {
/*     */         
/* 157 */         return true;
/*     */       }
/* 159 */       return false;
/*     */     } 
/*     */     
/* 162 */     return this.useClassConstructor.booleanValue();
/*     */   }
/*     */   
/*     */   public void setUseClassConstructor(Boolean useClassConstructor) {
/* 166 */     this.useClassConstructor = useClassConstructor;
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
/*     */   @Deprecated
/*     */   public boolean isResolved() {
/* 179 */     return this.resolved;
/*     */   }
/*     */   
/*     */   public String getAnchor() {
/* 183 */     return this.anchor;
/*     */   }
/*     */   
/*     */   public void setAnchor(String anchor) {
/* 187 */     this.anchor = anchor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<CommentLine> getInLineComments() {
/* 197 */     return this.inLineComments;
/*     */   }
/*     */   
/*     */   public void setInLineComments(List<CommentLine> inLineComments) {
/* 201 */     this.inLineComments = inLineComments;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<CommentLine> getBlockComments() {
/* 210 */     return this.blockComments;
/*     */   }
/*     */   
/*     */   public void setBlockComments(List<CommentLine> blockComments) {
/* 214 */     this.blockComments = blockComments;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<CommentLine> getEndComments() {
/* 225 */     return this.endComments;
/*     */   }
/*     */   
/*     */   public void setEndComments(List<CommentLine> endComments) {
/* 229 */     this.endComments = endComments;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\nodes\Node.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */