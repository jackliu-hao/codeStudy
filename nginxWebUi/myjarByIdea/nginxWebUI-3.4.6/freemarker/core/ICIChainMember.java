package freemarker.core;

interface ICIChainMember {
   int getMinimumICIVersion();

   Object getPreviousICIChainMember();
}
