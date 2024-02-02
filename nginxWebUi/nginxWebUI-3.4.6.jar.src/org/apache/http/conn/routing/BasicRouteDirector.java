/*     */ package org.apache.http.conn.routing;
/*     */ 
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.util.Args;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class BasicRouteDirector
/*     */   implements HttpRouteDirector
/*     */ {
/*     */   public int nextStep(RouteInfo plan, RouteInfo fact) {
/*  55 */     Args.notNull(plan, "Planned route");
/*     */     
/*  57 */     int step = -1;
/*     */     
/*  59 */     if (fact == null || fact.getHopCount() < 1) {
/*  60 */       step = firstStep(plan);
/*  61 */     } else if (plan.getHopCount() > 1) {
/*  62 */       step = proxiedStep(plan, fact);
/*     */     } else {
/*  64 */       step = directStep(plan, fact);
/*     */     } 
/*     */     
/*  67 */     return step;
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
/*     */   protected int firstStep(RouteInfo plan) {
/*  81 */     return (plan.getHopCount() > 1) ? 2 : 1;
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
/*     */   protected int directStep(RouteInfo plan, RouteInfo fact) {
/*  97 */     if (fact.getHopCount() > 1) {
/*  98 */       return -1;
/*     */     }
/* 100 */     if (!plan.getTargetHost().equals(fact.getTargetHost()))
/*     */     {
/* 102 */       return -1;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 111 */     if (plan.isSecure() != fact.isSecure()) {
/* 112 */       return -1;
/*     */     }
/*     */ 
/*     */     
/* 116 */     if (plan.getLocalAddress() != null && !plan.getLocalAddress().equals(fact.getLocalAddress()))
/*     */     {
/*     */       
/* 119 */       return -1;
/*     */     }
/*     */     
/* 122 */     return 0;
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
/*     */   protected int proxiedStep(RouteInfo plan, RouteInfo fact) {
/* 137 */     if (fact.getHopCount() <= 1) {
/* 138 */       return -1;
/*     */     }
/* 140 */     if (!plan.getTargetHost().equals(fact.getTargetHost())) {
/* 141 */       return -1;
/*     */     }
/* 143 */     int phc = plan.getHopCount();
/* 144 */     int fhc = fact.getHopCount();
/* 145 */     if (phc < fhc) {
/* 146 */       return -1;
/*     */     }
/*     */     
/* 149 */     for (int i = 0; i < fhc - 1; i++) {
/* 150 */       if (!plan.getHopTarget(i).equals(fact.getHopTarget(i))) {
/* 151 */         return -1;
/*     */       }
/*     */     } 
/*     */     
/* 155 */     if (phc > fhc)
/*     */     {
/* 157 */       return 4;
/*     */     }
/*     */ 
/*     */     
/* 161 */     if ((fact.isTunnelled() && !plan.isTunnelled()) || (fact.isLayered() && !plan.isLayered()))
/*     */     {
/* 163 */       return -1;
/*     */     }
/*     */     
/* 166 */     if (plan.isTunnelled() && !fact.isTunnelled()) {
/* 167 */       return 3;
/*     */     }
/* 169 */     if (plan.isLayered() && !fact.isLayered()) {
/* 170 */       return 5;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 176 */     if (plan.isSecure() != fact.isSecure()) {
/* 177 */       return -1;
/*     */     }
/*     */     
/* 180 */     return 0;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\routing\BasicRouteDirector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */