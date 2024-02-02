package ch.qos.logback.core.pattern.parser;

public class CompositeNode extends SimpleKeywordNode {
   Node childNode;

   CompositeNode(String keyword) {
      super(2, keyword);
   }

   public Node getChildNode() {
      return this.childNode;
   }

   public void setChildNode(Node childNode) {
      this.childNode = childNode;
   }

   public boolean equals(Object o) {
      if (!super.equals(o)) {
         return false;
      } else if (!(o instanceof CompositeNode)) {
         return false;
      } else {
         CompositeNode r = (CompositeNode)o;
         return this.childNode != null ? this.childNode.equals(r.childNode) : r.childNode == null;
      }
   }

   public int hashCode() {
      return super.hashCode();
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      if (this.childNode != null) {
         buf.append("CompositeNode(" + this.childNode + ")");
      } else {
         buf.append("CompositeNode(no child)");
      }

      buf.append(this.printNext());
      return buf.toString();
   }
}
