package cn.hutool.jwt;

import java.util.Date;

public interface RegisteredPayload<T extends RegisteredPayload<T>> {
   String ISSUER = "iss";
   String SUBJECT = "sub";
   String AUDIENCE = "aud";
   String EXPIRES_AT = "exp";
   String NOT_BEFORE = "nbf";
   String ISSUED_AT = "iat";
   String JWT_ID = "jti";

   default T setIssuer(String issuer) {
      return this.setPayload("iss", issuer);
   }

   default T setSubject(String subject) {
      return this.setPayload("sub", subject);
   }

   default T setAudience(String... audience) {
      return this.setPayload("aud", audience);
   }

   default T setExpiresAt(Date expiresAt) {
      return this.setPayload("exp", expiresAt);
   }

   default T setNotBefore(Date notBefore) {
      return this.setPayload("nbf", notBefore);
   }

   default T setIssuedAt(Date issuedAt) {
      return this.setPayload("iat", issuedAt);
   }

   default T setJWTId(String jwtId) {
      return this.setPayload("jti", jwtId);
   }

   T setPayload(String var1, Object var2);
}
