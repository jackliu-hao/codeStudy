package cn.hutool.jwt;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.NoneJWTSigner;
import java.util.Date;

public class JWTValidator {
   private final JWT jwt;

   public static JWTValidator of(String token) {
      return new JWTValidator(JWT.of(token));
   }

   public static JWTValidator of(JWT jwt) {
      return new JWTValidator(jwt);
   }

   public JWTValidator(JWT jwt) {
      this.jwt = jwt;
   }

   public JWTValidator validateAlgorithm() throws ValidateException {
      return this.validateAlgorithm((JWTSigner)null);
   }

   public JWTValidator validateAlgorithm(JWTSigner signer) throws ValidateException {
      validateAlgorithm(this.jwt, signer);
      return this;
   }

   public JWTValidator validateDate() throws ValidateException {
      return this.validateDate(DateUtil.beginOfSecond(DateUtil.date()));
   }

   public JWTValidator validateDate(Date dateToCheck) throws ValidateException {
      validateDate(this.jwt.getPayload(), dateToCheck, 0L);
      return this;
   }

   public JWTValidator validateDate(Date dateToCheck, long leeway) throws ValidateException {
      validateDate(this.jwt.getPayload(), dateToCheck, leeway);
      return this;
   }

   private static void validateAlgorithm(JWT jwt, JWTSigner signer) throws ValidateException {
      String algorithmId = jwt.getAlgorithm();
      if (null == signer) {
         signer = jwt.getSigner();
      }

      if (StrUtil.isEmpty(algorithmId)) {
         if (null != signer && !(signer instanceof NoneJWTSigner)) {
            throw new ValidateException("No algorithm defined in header!");
         }
      } else if (null == signer) {
         throw new IllegalArgumentException("No Signer for validate algorithm!");
      } else {
         String algorithmIdInSigner = signer.getAlgorithmId();
         if (!StrUtil.equals(algorithmId, algorithmIdInSigner)) {
            throw new ValidateException("Algorithm [{}] defined in header doesn't match to [{}]!", new Object[]{algorithmId, algorithmIdInSigner});
         } else if (!jwt.verify(signer)) {
            throw new ValidateException("Signature verification failed!");
         }
      }
   }

   private static void validateDate(JWTPayload payload, Date now, long leeway) throws ValidateException {
      if (null == now) {
         now = DateUtil.date();
         ((Date)now).setTime(((Date)now).getTime() / 1000L * 1000L);
      }

      Date notBefore = payload.getClaimsJson().getDate("nbf");
      validateNotAfter("nbf", notBefore, (Date)now, leeway);
      Date expiresAt = payload.getClaimsJson().getDate("exp");
      validateNotBefore("exp", expiresAt, (Date)now, leeway);
      Date issueAt = payload.getClaimsJson().getDate("iat");
      validateNotAfter("iat", issueAt, (Date)now, leeway);
   }

   private static void validateNotAfter(String fieldName, Date dateToCheck, Date now, long leeway) throws ValidateException {
      if (null != dateToCheck) {
         if (leeway > 0L) {
            now = DateUtil.date(((Date)now).getTime() + leeway * 1000L);
         }

         if (dateToCheck.after((Date)now)) {
            throw new ValidateException("'{}':[{}] is after now:[{}]", new Object[]{fieldName, DateUtil.date(dateToCheck), DateUtil.date((Date)now)});
         }
      }
   }

   private static void validateNotBefore(String fieldName, Date dateToCheck, Date now, long leeway) throws ValidateException {
      if (null != dateToCheck) {
         if (leeway > 0L) {
            now = DateUtil.date(((Date)now).getTime() - leeway * 1000L);
         }

         if (dateToCheck.before((Date)now)) {
            throw new ValidateException("'{}':[{}] is before now:[{}]", new Object[]{fieldName, DateUtil.date(dateToCheck), DateUtil.date((Date)now)});
         }
      }
   }
}
