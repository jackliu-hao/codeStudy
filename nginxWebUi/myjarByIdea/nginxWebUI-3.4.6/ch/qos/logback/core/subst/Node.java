package ch.qos.logback.core.subst;

public class Node {
   Type type;
   Object payload;
   Object defaultPart;
   Node next;

   public Node(Type type, Object payload) {
      this.type = type;
      this.payload = payload;
   }

   public Node(Type type, Object payload, Object defaultPart) {
      this.type = type;
      this.payload = payload;
      this.defaultPart = defaultPart;
   }

   void append(Node newNode) {
      if (newNode != null) {
         Node n;
         for(n = this; n.next != null; n = n.next) {
         }

         n.next = newNode;
      }
   }

   public String toString() {
      switch (this.type) {
         case LITERAL:
            return "Node{type=" + this.type + ", payload='" + this.payload + "'}";
         case VARIABLE:
            StringBuilder payloadBuf = new StringBuilder();
            StringBuilder defaultPartBuf2 = new StringBuilder();
            if (this.defaultPart != null) {
               this.recursive((Node)this.defaultPart, defaultPartBuf2);
            }

            this.recursive((Node)this.payload, payloadBuf);
            String r = "Node{type=" + this.type + ", payload='" + payloadBuf.toString() + "'";
            if (this.defaultPart != null) {
               r = r + ", defaultPart=" + defaultPartBuf2.toString();
            }

            r = r + '}';
            return r;
         default:
            return null;
      }
   }

   public void dump() {
      System.out.print(this.toString());
      System.out.print(" -> ");
      if (this.next != null) {
         this.next.dump();
      } else {
         System.out.print(" null");
      }

   }

   void recursive(Node n, StringBuilder sb) {
      for(Node c = n; c != null; c = c.next) {
         sb.append(c.toString()).append(" --> ");
      }

      sb.append("null ");
   }

   public void setNext(Node n) {
      this.next = n;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Node node = (Node)o;
         if (this.type != node.type) {
            return false;
         } else {
            label48: {
               if (this.payload != null) {
                  if (this.payload.equals(node.payload)) {
                     break label48;
                  }
               } else if (node.payload == null) {
                  break label48;
               }

               return false;
            }

            if (this.defaultPart != null) {
               if (!this.defaultPart.equals(node.defaultPart)) {
                  return false;
               }
            } else if (node.defaultPart != null) {
               return false;
            }

            if (this.next != null) {
               if (!this.next.equals(node.next)) {
                  return false;
               }
            } else if (node.next != null) {
               return false;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.type != null ? this.type.hashCode() : 0;
      result = 31 * result + (this.payload != null ? this.payload.hashCode() : 0);
      result = 31 * result + (this.defaultPart != null ? this.defaultPart.hashCode() : 0);
      result = 31 * result + (this.next != null ? this.next.hashCode() : 0);
      return result;
   }

   static enum Type {
      LITERAL,
      VARIABLE;
   }
}
