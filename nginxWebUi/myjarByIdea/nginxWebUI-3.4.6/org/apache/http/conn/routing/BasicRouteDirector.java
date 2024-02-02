package org.apache.http.conn.routing;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class BasicRouteDirector implements HttpRouteDirector {
   public int nextStep(RouteInfo plan, RouteInfo fact) {
      Args.notNull(plan, "Planned route");
      int step = true;
      int step;
      if (fact != null && fact.getHopCount() >= 1) {
         if (plan.getHopCount() > 1) {
            step = this.proxiedStep(plan, fact);
         } else {
            step = this.directStep(plan, fact);
         }
      } else {
         step = this.firstStep(plan);
      }

      return step;
   }

   protected int firstStep(RouteInfo plan) {
      return plan.getHopCount() > 1 ? 2 : 1;
   }

   protected int directStep(RouteInfo plan, RouteInfo fact) {
      if (fact.getHopCount() > 1) {
         return -1;
      } else if (!plan.getTargetHost().equals(fact.getTargetHost())) {
         return -1;
      } else if (plan.isSecure() != fact.isSecure()) {
         return -1;
      } else {
         return plan.getLocalAddress() != null && !plan.getLocalAddress().equals(fact.getLocalAddress()) ? -1 : 0;
      }
   }

   protected int proxiedStep(RouteInfo plan, RouteInfo fact) {
      if (fact.getHopCount() <= 1) {
         return -1;
      } else if (!plan.getTargetHost().equals(fact.getTargetHost())) {
         return -1;
      } else {
         int phc = plan.getHopCount();
         int fhc = fact.getHopCount();
         if (phc < fhc) {
            return -1;
         } else {
            for(int i = 0; i < fhc - 1; ++i) {
               if (!plan.getHopTarget(i).equals(fact.getHopTarget(i))) {
                  return -1;
               }
            }

            if (phc > fhc) {
               return 4;
            } else if ((!fact.isTunnelled() || plan.isTunnelled()) && (!fact.isLayered() || plan.isLayered())) {
               if (plan.isTunnelled() && !fact.isTunnelled()) {
                  return 3;
               } else if (plan.isLayered() && !fact.isLayered()) {
                  return 5;
               } else {
                  return plan.isSecure() != fact.isSecure() ? -1 : 0;
               }
            } else {
               return -1;
            }
         }
      }
   }
}
