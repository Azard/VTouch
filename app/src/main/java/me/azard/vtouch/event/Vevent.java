package me.azard.vtouch.event;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Vevent {

    public void executeCommand(String command) throws InterruptedException, IOException, TimeoutException, RootDeniedException {
        CommandCapture cmd = new CommandCapture(0, command);
        RootTools.getShell(true).add(cmd);
    }

    public void sendevent(String event_num, int param_1, int param_2, int param_3) {
        try {
            executeCommand(String.format("sendevent /dev/input/%s %d %d %d", event_num, param_1, param_2, param_3));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (RootDeniedException e) {
            e.printStackTrace();
        }
    }
}
