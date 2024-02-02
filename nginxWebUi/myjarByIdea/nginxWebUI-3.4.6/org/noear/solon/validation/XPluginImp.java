package org.noear.solon.validation;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.validation.annotation.LoginedChecker;
import org.noear.solon.validation.annotation.NoRepeatSubmitChecker;
import org.noear.solon.validation.annotation.NotBlacklistChecker;
import org.noear.solon.validation.annotation.WhitelistChecker;

public class XPluginImp implements Plugin {
   public void start(AopContext context) {
      context.getWrapAsyn(ValidatorFailureHandler.class, (bw) -> {
         ValidatorManager.setFailureHandler((ValidatorFailureHandler)bw.raw());
      });
      context.getWrapAsyn(NoRepeatSubmitChecker.class, (bw) -> {
         ValidatorManager.setNoRepeatSubmitChecker((NoRepeatSubmitChecker)bw.raw());
      });
      context.getWrapAsyn(LoginedChecker.class, (bw) -> {
         ValidatorManager.setLoginedChecker((LoginedChecker)bw.raw());
      });
      context.getWrapAsyn(WhitelistChecker.class, (bw) -> {
         ValidatorManager.setWhitelistChecker((WhitelistChecker)bw.raw());
      });
      context.getWrapAsyn(NotBlacklistChecker.class, (bw) -> {
         ValidatorManager.setNotBlacklistChecker((NotBlacklistChecker)bw.raw());
      });
   }
}
