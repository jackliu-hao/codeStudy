/*     */ package ch.qos.logback.core.subst;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Node
/*     */ {
/*     */   Type type;
/*     */   Object payload;
/*     */   Object defaultPart;
/*     */   Node next;
/*     */   
/*     */   enum Type
/*     */   {
/*  19 */     LITERAL, VARIABLE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Node(Type type, Object payload) {
/*  28 */     this.type = type;
/*  29 */     this.payload = payload;
/*     */   }
/*     */   
/*     */   public Node(Type type, Object payload, Object defaultPart) {
/*  33 */     this.type = type;
/*  34 */     this.payload = payload;
/*  35 */     this.defaultPart = defaultPart;
/*     */   }
/*     */   
/*     */   void append(Node newNode) {
/*  39 */     if (newNode == null)
/*     */       return; 
/*  41 */     Node n = this;
/*     */     while (true) {
/*  43 */       if (n.next == null) {
/*  44 */         n.next = newNode;
/*     */         return;
/*     */       } 
/*  47 */       n = n.next;
/*     */     } 
/*     */   } public String toString() {
/*     */     StringBuilder payloadBuf;
/*     */     StringBuilder defaultPartBuf2;
/*     */     String r;
/*  53 */     switch (this.type) {
/*     */       case LITERAL:
/*  55 */         return "Node{type=" + this.type + ", payload='" + this.payload + "'}";
/*     */       case VARIABLE:
/*  57 */         payloadBuf = new StringBuilder();
/*  58 */         defaultPartBuf2 = new StringBuilder();
/*  59 */         if (this.defaultPart != null) {
/*  60 */           recursive((Node)this.defaultPart, defaultPartBuf2);
/*     */         }
/*  62 */         recursive((Node)this.payload, payloadBuf);
/*  63 */         r = "Node{type=" + this.type + ", payload='" + payloadBuf.toString() + "'";
/*  64 */         if (this.defaultPart != null)
/*  65 */           r = r + ", defaultPart=" + defaultPartBuf2.toString(); 
/*  66 */         r = r + '}';
/*  67 */         return r;
/*     */     } 
/*  69 */     return null;
/*     */   }
/*     */   
/*     */   public void dump() {
/*  73 */     System.out.print(toString());
/*  74 */     System.out.print(" -> ");
/*  75 */     if (this.next != null) {
/*  76 */       this.next.dump();
/*     */     } else {
/*  78 */       System.out.print(" null");
/*     */     } 
/*     */   }
/*     */   
/*     */   void recursive(Node n, StringBuilder sb) {
/*  83 */     Node c = n;
/*  84 */     while (c != null) {
/*  85 */       sb.append(c.toString()).append(" --> ");
/*  86 */       c = c.next;
/*     */     } 
/*  88 */     sb.append("null ");
/*     */   }
/*     */   
/*     */   public void setNext(Node n) {
/*  92 */     this.next = n;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  97 */     if (this == o)
/*  98 */       return true; 
/*  99 */     if (o == null || getClass() != o.getClass()) {
/* 100 */       return false;
/*     */     }
/* 102 */     Node node = (Node)o;
/*     */     
/* 104 */     if (this.type != node.type)
/* 105 */       return false; 
/* 106 */     if ((this.payload != null) ? !this.payload.equals(node.payload) : (node.payload != null))
/* 107 */       return false; 
/* 108 */     if ((this.defaultPart != null) ? !this.defaultPart.equals(node.defaultPart) : (node.defaultPart != null))
/* 109 */       return false; 
/* 110 */     if ((this.next != null) ? !this.next.equals(node.next) : (node.next != null)) {
/* 111 */       return false;
/*     */     }
/* 113 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 118 */     int result = (this.type != null) ? this.type.hashCode() : 0;
/* 119 */     result = 31 * result + ((this.payload != null) ? this.payload.hashCode() : 0);
/* 120 */     result = 31 * result + ((this.defaultPart != null) ? this.defaultPart.hashCode() : 0);
/* 121 */     result = 31 * result + ((this.next != null) ? this.next.hashCode() : 0);
/* 122 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\subst\Node.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */