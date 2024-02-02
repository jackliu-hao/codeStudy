package cn.hutool.core.bean.copier;

import cn.hutool.core.lang.copier.Copier;
import cn.hutool.core.util.ObjectUtil;
import java.util.function.Supplier;

public abstract class AbsCopier<S, T> implements Copier<T> {
   protected final S source;
   protected final T target;
   protected final CopyOptions copyOptions;

   public AbsCopier(S source, T target, CopyOptions copyOptions) {
      this.source = source;
      this.target = target;
      this.copyOptions = (CopyOptions)ObjectUtil.defaultIfNull(copyOptions, (Supplier)(CopyOptions::create));
   }
}
