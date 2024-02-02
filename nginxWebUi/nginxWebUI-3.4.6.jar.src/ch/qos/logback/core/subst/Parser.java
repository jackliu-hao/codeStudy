/*     */ package ch.qos.logback.core.subst;
/*     */ 
/*     */ import ch.qos.logback.core.CoreConstants;
/*     */ import ch.qos.logback.core.spi.ScanException;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Parser
/*     */ {
/*     */   final List<Token> tokenList;
/*  38 */   int pointer = 0;
/*     */   
/*     */   public Parser(List<Token> tokenList) {
/*  41 */     this.tokenList = tokenList;
/*     */   }
/*     */   
/*     */   public Node parse() throws ScanException {
/*  45 */     if (this.tokenList == null || this.tokenList.isEmpty())
/*  46 */       return null; 
/*  47 */     return E();
/*     */   }
/*     */   
/*     */   private Node E() throws ScanException {
/*  51 */     Node t = T();
/*  52 */     if (t == null) {
/*  53 */       return null;
/*     */     }
/*  55 */     Node eOpt = Eopt();
/*  56 */     if (eOpt != null) {
/*  57 */       t.append(eOpt);
/*     */     }
/*  59 */     return t;
/*     */   }
/*     */ 
/*     */   
/*     */   private Node Eopt() throws ScanException {
/*  64 */     Token next = peekAtCurentToken();
/*  65 */     if (next == null) {
/*  66 */       return null;
/*     */     }
/*  68 */     return E();
/*     */   }
/*     */   private Node T() throws ScanException {
/*     */     Node innerNode;
/*     */     Token right;
/*     */     Node curlyLeft, v;
/*  74 */     Token w, t = peekAtCurentToken();
/*     */     
/*  76 */     switch (t.type) {
/*     */       case LITERAL:
/*  78 */         advanceTokenPointer();
/*  79 */         return makeNewLiteralNode(t.payload);
/*     */       case CURLY_LEFT:
/*  81 */         advanceTokenPointer();
/*  82 */         innerNode = C();
/*  83 */         right = peekAtCurentToken();
/*  84 */         expectCurlyRight(right);
/*  85 */         advanceTokenPointer();
/*  86 */         curlyLeft = makeNewLiteralNode(CoreConstants.LEFT_ACCOLADE);
/*  87 */         curlyLeft.append(innerNode);
/*  88 */         curlyLeft.append(makeNewLiteralNode(CoreConstants.RIGHT_ACCOLADE));
/*  89 */         return curlyLeft;
/*     */       case START:
/*  91 */         advanceTokenPointer();
/*  92 */         v = V();
/*  93 */         w = peekAtCurentToken();
/*  94 */         expectCurlyRight(w);
/*  95 */         advanceTokenPointer();
/*  96 */         return v;
/*     */     } 
/*  98 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private Node makeNewLiteralNode(String s) {
/* 103 */     return new Node(Node.Type.LITERAL, s);
/*     */   }
/*     */ 
/*     */   
/*     */   private Node V() throws ScanException {
/* 108 */     Node e = E();
/* 109 */     Node variable = new Node(Node.Type.VARIABLE, e);
/* 110 */     Token t = peekAtCurentToken();
/* 111 */     if (isDefaultToken(t)) {
/* 112 */       advanceTokenPointer();
/* 113 */       Node def = E();
/* 114 */       variable.defaultPart = def;
/*     */     } 
/* 116 */     return variable;
/*     */   }
/*     */ 
/*     */   
/*     */   private Node C() throws ScanException {
/* 121 */     Node e0 = E();
/* 122 */     Token t = peekAtCurentToken();
/* 123 */     if (isDefaultToken(t)) {
/* 124 */       advanceTokenPointer();
/* 125 */       Node literal = makeNewLiteralNode(":-");
/* 126 */       e0.append(literal);
/* 127 */       Node e1 = E();
/* 128 */       e0.append(e1);
/*     */     } 
/* 130 */     return e0;
/*     */   }
/*     */   
/*     */   private boolean isDefaultToken(Token t) {
/* 134 */     return (t != null && t.type == Token.Type.DEFAULT);
/*     */   }
/*     */   
/*     */   void advanceTokenPointer() {
/* 138 */     this.pointer++;
/*     */   }
/*     */   
/*     */   void expectNotNull(Token t, String expected) {
/* 142 */     if (t == null) {
/* 143 */       throw new IllegalArgumentException("All tokens consumed but was expecting \"" + expected + "\"");
/*     */     }
/*     */   }
/*     */   
/*     */   void expectCurlyRight(Token t) throws ScanException {
/* 148 */     expectNotNull(t, "}");
/* 149 */     if (t.type != Token.Type.CURLY_RIGHT) {
/* 150 */       throw new ScanException("Expecting }");
/*     */     }
/*     */   }
/*     */   
/*     */   Token peekAtCurentToken() {
/* 155 */     if (this.pointer < this.tokenList.size()) {
/* 156 */       return this.tokenList.get(this.pointer);
/*     */     }
/* 158 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\subst\Parser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */