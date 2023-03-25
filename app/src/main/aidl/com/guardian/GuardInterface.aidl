package com.guardian;

interface GuardInterface {

   int getCheckTime();

   void setCheckTime(int checktime);

   void startTimerCheck(long time);

   void setOpenGuardianStatues(boolean isOpen);

   boolean getOpenGuardianStatues();

   void setOpenPower(boolean isOpenPower);

   boolean getOpenPower();
}
