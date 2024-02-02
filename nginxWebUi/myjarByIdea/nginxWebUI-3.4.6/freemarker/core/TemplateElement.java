package freemarker.core;

import freemarker.template.SimpleSequence;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateSequenceModel;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import javax.swing.tree.TreeNode;

/** @deprecated */
@Deprecated
public abstract class TemplateElement extends TemplateObject implements TreeNode {
   private static final int INITIAL_REGULATED_CHILD_BUFFER_CAPACITY = 6;
   private TemplateElement parent;
   private TemplateElement[] childBuffer;
   private int childCount;
   private int index;

   abstract TemplateElement[] accept(Environment var1) throws TemplateException, IOException;

   public final String getDescription() {
      return this.dump(false);
   }

   public final String getCanonicalForm() {
      return this.dump(true);
   }

   final String getChildrenCanonicalForm() {
      return getChildrenCanonicalForm(this.childBuffer);
   }

   static String getChildrenCanonicalForm(TemplateElement[] children) {
      if (children == null) {
         return "";
      } else {
         StringBuilder sb = new StringBuilder();
         TemplateElement[] var2 = children;
         int var3 = children.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            TemplateElement child = var2[var4];
            if (child == null) {
               break;
            }

            sb.append(child.getCanonicalForm());
         }

         return sb.toString();
      }
   }

   boolean isShownInStackTrace() {
      return false;
   }

   abstract boolean isNestedBlockRepeater();

   protected abstract String dump(boolean var1);

   public TemplateNodeModel getParentNode() {
      return null;
   }

   public String getNodeNamespace() {
      return null;
   }

   public String getNodeType() {
      return "element";
   }

   public TemplateSequenceModel getChildNodes() {
      if (this.childBuffer == null) {
         return new SimpleSequence(0);
      } else {
         SimpleSequence seq = new SimpleSequence(this.childCount);

         for(int i = 0; i < this.childCount; ++i) {
            seq.add(this.childBuffer[i]);
         }

         return seq;
      }
   }

   public String getNodeName() {
      String classname = this.getClass().getName();
      int shortNameOffset = classname.lastIndexOf(46) + 1;
      return classname.substring(shortNameOffset);
   }

   public boolean isLeaf() {
      return this.childCount == 0;
   }

   /** @deprecated */
   @Deprecated
   public boolean getAllowsChildren() {
      return !this.isLeaf();
   }

   /** @deprecated */
   @Deprecated
   public int getIndex(TreeNode node) {
      for(int i = 0; i < this.childCount; ++i) {
         if (this.childBuffer[i].equals(node)) {
            return i;
         }
      }

      return -1;
   }

   public int getChildCount() {
      return this.childCount;
   }

   public Enumeration children() {
      return (Enumeration)(this.childBuffer != null ? new _ArrayEnumeration(this.childBuffer, this.childCount) : Collections.enumeration(Collections.EMPTY_LIST));
   }

   /** @deprecated */
   @Deprecated
   public TreeNode getChildAt(int index) {
      if (this.childCount == 0) {
         throw new IndexOutOfBoundsException("Template element has no children");
      } else {
         try {
            return this.childBuffer[index];
         } catch (ArrayIndexOutOfBoundsException var3) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.childCount);
         }
      }
   }

   public void setChildAt(int index, TemplateElement element) {
      if (index < this.childCount && index >= 0) {
         this.childBuffer[index] = element;
         element.index = index;
         element.parent = this;
      } else {
         throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.childCount);
      }
   }

   /** @deprecated */
   @Deprecated
   public TreeNode getParent() {
      return this.parent;
   }

   final TemplateElement getParentElement() {
      return this.parent;
   }

   final void setChildBufferCapacity(int capacity) {
      int ln = this.childCount;
      TemplateElement[] newChildBuffer = new TemplateElement[capacity];

      for(int i = 0; i < ln; ++i) {
         newChildBuffer[i] = this.childBuffer[i];
      }

      this.childBuffer = newChildBuffer;
   }

   final void addChild(TemplateElement nestedElement) {
      this.addChild(this.childCount, nestedElement);
   }

   final void addChild(int index, TemplateElement nestedElement) {
      int childCount = this.childCount;
      TemplateElement[] childBuffer = this.childBuffer;
      if (childBuffer == null) {
         childBuffer = new TemplateElement[6];
         this.childBuffer = childBuffer;
      } else if (childCount == childBuffer.length) {
         this.setChildBufferCapacity(childCount != 0 ? childCount * 2 : 1);
         childBuffer = this.childBuffer;
      }

      for(int i = childCount; i > index; --i) {
         TemplateElement movedElement = childBuffer[i - 1];
         movedElement.index = i;
         childBuffer[i] = movedElement;
      }

      nestedElement.index = index;
      nestedElement.parent = this;
      childBuffer[index] = nestedElement;
      this.childCount = childCount + 1;
   }

   final TemplateElement getChild(int index) {
      return this.childBuffer[index];
   }

   final TemplateElement[] getChildBuffer() {
      return this.childBuffer;
   }

   final void setChildren(TemplateElements buffWithCnt) {
      TemplateElement[] childBuffer = buffWithCnt.getBuffer();
      int childCount = buffWithCnt.getCount();

      for(int i = 0; i < childCount; ++i) {
         TemplateElement child = childBuffer[i];
         child.index = i;
         child.parent = this;
      }

      this.childBuffer = childBuffer;
      this.childCount = childCount;
   }

   final void copyFieldsFrom(TemplateElement that) {
      super.copyFieldsFrom(that);
      this.parent = that.parent;
      this.index = that.index;
      this.childBuffer = that.childBuffer;
      this.childCount = that.childCount;
   }

   final int getIndex() {
      return this.index;
   }

   final void setFieldsForRootElement() {
      this.index = 0;
      this.parent = null;
   }

   TemplateElement postParseCleanup(boolean stripWhitespace) throws ParseException {
      int childCount = this.childCount;
      if (childCount != 0) {
         int i;
         TemplateElement te;
         for(i = 0; i < childCount; te.index = i++) {
            te = this.childBuffer[i];
            te = te.postParseCleanup(stripWhitespace);
            this.childBuffer[i] = te;
            te.parent = this;
         }

         for(i = 0; i < childCount; ++i) {
            te = this.childBuffer[i];
            if (te.isIgnorable(stripWhitespace)) {
               --childCount;

               TemplateElement te2;
               for(int j = i; j < childCount; te2.index = j++) {
                  te2 = this.childBuffer[j + 1];
                  this.childBuffer[j] = te2;
               }

               this.childBuffer[childCount] = null;
               this.childCount = childCount;
               --i;
            }
         }

         if (childCount == 0) {
            this.childBuffer = null;
         } else if (childCount < this.childBuffer.length && childCount <= this.childBuffer.length * 3 / 4) {
            TemplateElement[] trimmedChildBuffer = new TemplateElement[childCount];

            for(int i = 0; i < childCount; ++i) {
               trimmedChildBuffer[i] = this.childBuffer[i];
            }

            this.childBuffer = trimmedChildBuffer;
         }
      }

      return this;
   }

   boolean isIgnorable(boolean stripWhitespace) {
      return false;
   }

   TemplateElement prevTerminalNode() {
      TemplateElement prev = this.previousSibling();
      if (prev != null) {
         return prev.getLastLeaf();
      } else {
         return this.parent != null ? this.parent.prevTerminalNode() : null;
      }
   }

   TemplateElement nextTerminalNode() {
      TemplateElement next = this.nextSibling();
      if (next != null) {
         return next.getFirstLeaf();
      } else {
         return this.parent != null ? this.parent.nextTerminalNode() : null;
      }
   }

   TemplateElement previousSibling() {
      if (this.parent == null) {
         return null;
      } else {
         return this.index > 0 ? this.parent.childBuffer[this.index - 1] : null;
      }
   }

   TemplateElement nextSibling() {
      if (this.parent == null) {
         return null;
      } else {
         return this.index + 1 < this.parent.childCount ? this.parent.childBuffer[this.index + 1] : null;
      }
   }

   private TemplateElement getFirstChild() {
      return this.childCount == 0 ? null : this.childBuffer[0];
   }

   private TemplateElement getLastChild() {
      int childCount = this.childCount;
      return childCount == 0 ? null : this.childBuffer[childCount - 1];
   }

   private TemplateElement getFirstLeaf() {
      TemplateElement te;
      for(te = this; !te.isLeaf() && !(te instanceof Macro) && !(te instanceof BlockAssignment); te = te.getFirstChild()) {
      }

      return te;
   }

   private TemplateElement getLastLeaf() {
      TemplateElement te;
      for(te = this; !te.isLeaf() && !(te instanceof Macro) && !(te instanceof BlockAssignment); te = te.getLastChild()) {
      }

      return te;
   }

   boolean isOutputCacheable() {
      return false;
   }

   boolean isChildrenOutputCacheable() {
      int ln = this.childCount;

      for(int i = 0; i < ln; ++i) {
         if (!this.childBuffer[i].isOutputCacheable()) {
            return false;
         }
      }

      return true;
   }

   boolean heedsOpeningWhitespace() {
      return false;
   }

   boolean heedsTrailingWhitespace() {
      return false;
   }
}
