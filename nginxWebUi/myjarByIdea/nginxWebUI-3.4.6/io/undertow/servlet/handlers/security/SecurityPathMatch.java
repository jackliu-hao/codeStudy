package io.undertow.servlet.handlers.security;

import io.undertow.servlet.api.SingleConstraintMatch;
import io.undertow.servlet.api.TransportGuaranteeType;

class SecurityPathMatch {
   private final TransportGuaranteeType transportGuaranteeType;
   private final SingleConstraintMatch mergedConstraint;

   SecurityPathMatch(TransportGuaranteeType transportGuaranteeType, SingleConstraintMatch mergedConstraint) {
      this.transportGuaranteeType = transportGuaranteeType;
      this.mergedConstraint = mergedConstraint;
   }

   TransportGuaranteeType getTransportGuaranteeType() {
      return this.transportGuaranteeType;
   }

   SingleConstraintMatch getMergedConstraint() {
      return this.mergedConstraint;
   }
}
