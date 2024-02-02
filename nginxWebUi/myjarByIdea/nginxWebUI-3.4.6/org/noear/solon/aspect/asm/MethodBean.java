package org.noear.solon.aspect.asm;

public class MethodBean {
   public int access;
   public String methodName;
   public String methodDesc;

   public MethodBean() {
   }

   public MethodBean(int access, String methodName, String methodDesc) {
      this.access = access;
      this.methodName = methodName;
      this.methodDesc = methodDesc;
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (!(obj instanceof MethodBean)) {
         return false;
      } else {
         MethodBean bean = (MethodBean)obj;
         return this.access == bean.access && this.methodName != null && bean.methodName != null && this.methodName.equals(bean.methodName) && this.methodDesc != null && bean.methodDesc != null && this.methodDesc.equals(bean.methodDesc);
      }
   }
}
