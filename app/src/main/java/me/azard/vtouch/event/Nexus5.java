package me.azard.vtouch.event;

public class Nexus5 {

    Vevent vevent = new Vevent();

    static int EV_SYN = 0;
    static int EV_ABS = 3;

    static int SYN_REPORT = 0;
    static int ABS_MT_SLOT = 47;
    static int ABS_MT_TOUCH_MAJOR = 48;
    static int ABS_MT_POSITION_X = 53;
    static int ABS_MT_POSITION_Y = 54;
    static int ABS_MT_TRACKING_ID = 57;
    static int ABS_MT_PRESSURE = 58;


    public void touch(int finger_index, int x, int y) {
        vevent.sendevent("event1", EV_ABS, ABS_MT_SLOT, finger_index);
        vevent.sendevent("event1", EV_ABS, ABS_MT_TRACKING_ID, finger_index);
        vevent.sendevent("event1", EV_ABS, ABS_MT_POSITION_X, x);
        vevent.sendevent("event1", EV_ABS, ABS_MT_POSITION_Y, y);
        vevent.sendevent("event1", EV_SYN, SYN_REPORT, 0);
    }

    public void release(int finger_index) {
        vevent.sendevent("event1", EV_ABS, ABS_MT_SLOT, finger_index);
        vevent.sendevent("event1", EV_ABS, ABS_MT_TRACKING_ID, -1);
        vevent.sendevent("event1", EV_SYN, SYN_REPORT, 0);
    }

}
