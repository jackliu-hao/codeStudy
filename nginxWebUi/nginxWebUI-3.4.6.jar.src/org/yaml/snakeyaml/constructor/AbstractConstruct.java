/*    */ package org.yaml.snakeyaml.constructor;
/*    */ 
/*    */ import org.yaml.snakeyaml.error.YAMLException;
/*    */ import org.yaml.snakeyaml.nodes.Node;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AbstractConstruct
/*    */   implements Construct
/*    */ {
/*    */   public void construct2ndStep(Node node, Object data) {
/* 35 */     if (node.isTwoStepsConstruction()) {
/* 36 */       throw new IllegalStateException("Not Implemented in " + getClass().getName());
/*    */     }
/* 38 */     throw new YAMLException("Unexpected recursive structure for Node: " + node);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\constructor\AbstractConstruct.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */