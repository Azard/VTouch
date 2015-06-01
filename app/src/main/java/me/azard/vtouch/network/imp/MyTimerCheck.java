package me.azard.vtouch.network.imp;


public abstract class MyTimerCheck {
    private int mCount = 0;
    private int mTimeOutCount = 10;
    private int mSleepTime = 1000; // 1s
    private Object mTag=null;
    private boolean mExitFlag = false;
    private Thread mThread = null;
    /**
     * Do not process UI work in this.
     */
    public abstract void doTimerCheckWork();

    public abstract void doTimeOutWork();
    public MyTimerCheck(Object tag) {
        mThread = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (!mExitFlag) {
                    mCount++;
                    if (mCount < mTimeOutCount) {
                        doTimerCheckWork();
                        try {
                            mThread.sleep(mSleepTime);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            exit();
                        }
                    } else {
                        doTimeOutWork();
                    }
                }
            }
        });

    }

    public void waitStop()
    {
       try{
        if (mThread!=null)
            mThread.join();
        }
       catch(InterruptedException e)
       {
           return;
       }
    }
    /**
     * start
     * @param timeOutCount  How many times will check?
     * @param sleepTime ms, Every check sleep time.
     */
    public void start(int timeOutCount, int sleepTime) {
        mTimeOutCount = timeOutCount;
        mSleepTime = sleepTime;

        mThread.start();
    }
    public void start(int latancy) {
        mTimeOutCount = 10;
        mSleepTime = latancy/10;

        mThread.start();
    }
    public void exit() {
        mExitFlag = true;
    }

}