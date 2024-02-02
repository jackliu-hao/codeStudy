/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.SimpleSequence;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateNodeModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import java.io.IOException;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import javax.swing.tree.TreeNode;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public abstract class TemplateElement
/*     */   extends TemplateObject
/*     */   implements TreeNode
/*     */ {
/*     */   private static final int INITIAL_REGULATED_CHILD_BUFFER_CAPACITY = 6;
/*     */   private TemplateElement parent;
/*     */   private TemplateElement[] childBuffer;
/*     */   private int childCount;
/*     */   private int index;
/*     */   
/*     */   abstract TemplateElement[] accept(Environment paramEnvironment) throws TemplateException, IOException;
/*     */   
/*     */   public final String getDescription() {
/* 104 */     return dump(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getCanonicalForm() {
/* 112 */     return dump(true);
/*     */   }
/*     */   
/*     */   final String getChildrenCanonicalForm() {
/* 116 */     return getChildrenCanonicalForm(this.childBuffer);
/*     */   }
/*     */   
/*     */   static String getChildrenCanonicalForm(TemplateElement[] children) {
/* 120 */     if (children == null) {
/* 121 */       return "";
/*     */     }
/* 123 */     StringBuilder sb = new StringBuilder();
/* 124 */     for (TemplateElement child : children) {
/* 125 */       if (child == null) {
/*     */         break;
/*     */       }
/* 128 */       sb.append(child.getCanonicalForm());
/*     */     } 
/* 130 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isShownInStackTrace() {
/* 138 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   abstract boolean isNestedBlockRepeater();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract String dump(boolean paramBoolean);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateNodeModel getParentNode() {
/* 163 */     return null;
/*     */   }
/*     */   
/*     */   public String getNodeNamespace() {
/* 167 */     return null;
/*     */   }
/*     */   
/*     */   public String getNodeType() {
/* 171 */     return "element";
/*     */   }
/*     */   
/*     */   public TemplateSequenceModel getChildNodes() {
/* 175 */     if (this.childBuffer != null) {
/* 176 */       SimpleSequence seq = new SimpleSequence(this.childCount);
/* 177 */       for (int i = 0; i < this.childCount; i++) {
/* 178 */         seq.add(this.childBuffer[i]);
/*     */       }
/* 180 */       return (TemplateSequenceModel)seq;
/*     */     } 
/* 182 */     return (TemplateSequenceModel)new SimpleSequence(0);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getNodeName() {
/* 187 */     String classname = getClass().getName();
/* 188 */     int shortNameOffset = classname.lastIndexOf('.') + 1;
/* 189 */     return classname.substring(shortNameOffset);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLeaf() {
/* 195 */     return (this.childCount == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public boolean getAllowsChildren() {
/* 203 */     return !isLeaf();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public int getIndex(TreeNode node) {
/* 211 */     for (int i = 0; i < this.childCount; i++) {
/* 212 */       if (this.childBuffer[i].equals(node)) {
/* 213 */         return i;
/*     */       }
/*     */     } 
/* 216 */     return -1;
/*     */   }
/*     */   
/*     */   public int getChildCount() {
/* 220 */     return this.childCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration children() {
/* 228 */     return (this.childBuffer != null) ? new _ArrayEnumeration((Object[])this.childBuffer, this.childCount) : 
/*     */       
/* 230 */       Collections.enumeration(Collections.EMPTY_LIST);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public TreeNode getChildAt(int index) {
/* 239 */     if (this.childCount == 0) {
/* 240 */       throw new IndexOutOfBoundsException("Template element has no children");
/*     */     }
/*     */     try {
/* 243 */       return this.childBuffer[index];
/* 244 */     } catch (ArrayIndexOutOfBoundsException e) {
/*     */       
/* 246 */       throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.childCount);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setChildAt(int index, TemplateElement element) {
/* 251 */     if (index < this.childCount && index >= 0) {
/* 252 */       this.childBuffer[index] = element;
/* 253 */       element.index = index;
/* 254 */       element.parent = this;
/*     */     } else {
/* 256 */       throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.childCount);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public TreeNode getParent() {
/* 268 */     return this.parent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final TemplateElement getParentElement() {
/* 275 */     return this.parent;
/*     */   }
/*     */   
/*     */   final void setChildBufferCapacity(int capacity) {
/* 279 */     int ln = this.childCount;
/* 280 */     TemplateElement[] newChildBuffer = new TemplateElement[capacity];
/* 281 */     for (int i = 0; i < ln; i++) {
/* 282 */       newChildBuffer[i] = this.childBuffer[i];
/*     */     }
/* 284 */     this.childBuffer = newChildBuffer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final void addChild(TemplateElement nestedElement) {
/* 291 */     addChild(this.childCount, nestedElement);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final void addChild(int index, TemplateElement nestedElement) {
/* 298 */     int childCount = this.childCount;
/*     */     
/* 300 */     TemplateElement[] childBuffer = this.childBuffer;
/* 301 */     if (childBuffer == null) {
/* 302 */       childBuffer = new TemplateElement[6];
/* 303 */       this.childBuffer = childBuffer;
/* 304 */     } else if (childCount == childBuffer.length) {
/* 305 */       setChildBufferCapacity((childCount != 0) ? (childCount * 2) : 1);
/* 306 */       childBuffer = this.childBuffer;
/*     */     } 
/*     */ 
/*     */     
/* 310 */     for (int i = childCount; i > index; i--) {
/* 311 */       TemplateElement movedElement = childBuffer[i - 1];
/* 312 */       movedElement.index = i;
/* 313 */       childBuffer[i] = movedElement;
/*     */     } 
/* 315 */     nestedElement.index = index;
/* 316 */     nestedElement.parent = this;
/* 317 */     childBuffer[index] = nestedElement;
/* 318 */     this.childCount = childCount + 1;
/*     */   }
/*     */   
/*     */   final TemplateElement getChild(int index) {
/* 322 */     return this.childBuffer[index];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final TemplateElement[] getChildBuffer() {
/* 330 */     return this.childBuffer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final void setChildren(TemplateElements buffWithCnt) {
/* 339 */     TemplateElement[] childBuffer = buffWithCnt.getBuffer();
/* 340 */     int childCount = buffWithCnt.getCount();
/* 341 */     for (int i = 0; i < childCount; i++) {
/* 342 */       TemplateElement child = childBuffer[i];
/* 343 */       child.index = i;
/* 344 */       child.parent = this;
/*     */     } 
/* 346 */     this.childBuffer = childBuffer;
/* 347 */     this.childCount = childCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final void copyFieldsFrom(TemplateElement that) {
/* 354 */     copyFieldsFrom(that);
/* 355 */     this.parent = that.parent;
/* 356 */     this.index = that.index;
/* 357 */     this.childBuffer = that.childBuffer;
/* 358 */     this.childCount = that.childCount;
/*     */   }
/*     */   
/*     */   final int getIndex() {
/* 362 */     return this.index;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final void setFieldsForRootElement() {
/* 370 */     this.index = 0;
/* 371 */     this.parent = null;
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
/*     */   TemplateElement postParseCleanup(boolean stripWhitespace) throws ParseException {
/* 386 */     int childCount = this.childCount;
/* 387 */     if (childCount != 0) {
/* 388 */       int i; for (i = 0; i < childCount; i++) {
/* 389 */         TemplateElement te = this.childBuffer[i];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 403 */         te = te.postParseCleanup(stripWhitespace);
/* 404 */         this.childBuffer[i] = te;
/* 405 */         te.parent = this;
/* 406 */         te.index = i;
/*     */       } 
/* 408 */       for (i = 0; i < childCount; i++) {
/* 409 */         TemplateElement te = this.childBuffer[i];
/* 410 */         if (te.isIgnorable(stripWhitespace)) {
/* 411 */           childCount--;
/*     */           
/* 413 */           for (int j = i; j < childCount; j++) {
/* 414 */             TemplateElement te2 = this.childBuffer[j + 1];
/* 415 */             this.childBuffer[j] = te2;
/* 416 */             te2.index = j;
/*     */           } 
/* 418 */           this.childBuffer[childCount] = null;
/* 419 */           this.childCount = childCount;
/* 420 */           i--;
/*     */         } 
/*     */       } 
/* 423 */       if (childCount == 0) {
/* 424 */         this.childBuffer = null;
/* 425 */       } else if (childCount < this.childBuffer.length && childCount <= this.childBuffer.length * 3 / 4) {
/*     */         
/* 427 */         TemplateElement[] trimmedChildBuffer = new TemplateElement[childCount];
/* 428 */         for (int j = 0; j < childCount; j++) {
/* 429 */           trimmedChildBuffer[j] = this.childBuffer[j];
/*     */         }
/* 431 */         this.childBuffer = trimmedChildBuffer;
/*     */       } 
/*     */     } 
/* 434 */     return this;
/*     */   }
/*     */   
/*     */   boolean isIgnorable(boolean stripWhitespace) {
/* 438 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   TemplateElement prevTerminalNode() {
/* 445 */     TemplateElement prev = previousSibling();
/* 446 */     if (prev != null)
/* 447 */       return prev.getLastLeaf(); 
/* 448 */     if (this.parent != null) {
/* 449 */       return this.parent.prevTerminalNode();
/*     */     }
/* 451 */     return null;
/*     */   }
/*     */   
/*     */   TemplateElement nextTerminalNode() {
/* 455 */     TemplateElement next = nextSibling();
/* 456 */     if (next != null)
/* 457 */       return next.getFirstLeaf(); 
/* 458 */     if (this.parent != null) {
/* 459 */       return this.parent.nextTerminalNode();
/*     */     }
/* 461 */     return null;
/*     */   }
/*     */   
/*     */   TemplateElement previousSibling() {
/* 465 */     if (this.parent == null) {
/* 466 */       return null;
/*     */     }
/* 468 */     return (this.index > 0) ? this.parent.childBuffer[this.index - 1] : null;
/*     */   }
/*     */   
/*     */   TemplateElement nextSibling() {
/* 472 */     if (this.parent == null) {
/* 473 */       return null;
/*     */     }
/* 475 */     return (this.index + 1 < this.parent.childCount) ? this.parent.childBuffer[this.index + 1] : null;
/*     */   }
/*     */   
/*     */   private TemplateElement getFirstChild() {
/* 479 */     return (this.childCount == 0) ? null : this.childBuffer[0];
/*     */   }
/*     */   
/*     */   private TemplateElement getLastChild() {
/* 483 */     int childCount = this.childCount;
/* 484 */     return (childCount == 0) ? null : this.childBuffer[childCount - 1];
/*     */   }
/*     */   
/*     */   private TemplateElement getFirstLeaf() {
/* 488 */     TemplateElement te = this;
/* 489 */     while (!te.isLeaf() && !(te instanceof Macro) && !(te instanceof BlockAssignment))
/*     */     {
/* 491 */       te = te.getFirstChild();
/*     */     }
/* 493 */     return te;
/*     */   }
/*     */   
/*     */   private TemplateElement getLastLeaf() {
/* 497 */     TemplateElement te = this;
/* 498 */     while (!te.isLeaf() && !(te instanceof Macro) && !(te instanceof BlockAssignment))
/*     */     {
/* 500 */       te = te.getLastChild();
/*     */     }
/* 502 */     return te;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isOutputCacheable() {
/* 510 */     return false;
/*     */   }
/*     */   
/*     */   boolean isChildrenOutputCacheable() {
/* 514 */     int ln = this.childCount;
/* 515 */     for (int i = 0; i < ln; i++) {
/* 516 */       if (!this.childBuffer[i].isOutputCacheable()) {
/* 517 */         return false;
/*     */       }
/*     */     } 
/* 520 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean heedsOpeningWhitespace() {
/* 528 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean heedsTrailingWhitespace() {
/* 536 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TemplateElement.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */