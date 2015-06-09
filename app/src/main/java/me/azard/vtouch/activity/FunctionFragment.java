package me.azard.vtouch.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.io.DataOutputStream;

import me.azard.vtouch.R;

import me.azard.vtouch.network.imp.ClientManager;
import me.azard.vtouch.protocol.intf.IProtocolController;
import me.azard.vtouch.protocol.intf.IStartWifiCallBack;
import me.azard.vtouch.ui.MyGifView;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;
import java.util.concurrent.TimeoutException;



public class FunctionFragment extends MainActivity.PlaceholderFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private IProtocolController mIProtocolController;
    private ClientManager mClientManager;


    public static FunctionFragment newInstance(int sectionNumber) {
        FunctionFragment fragment = new FunctionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_function, container, false);
        // 绑定Button事件
        rootView.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {btnFunctionWifiOn(v);}
        });
        rootView.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFunctionShell(v);
            }
        });

        rootView.findViewById(R.id.bigbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.iv).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.leftglove).setBackground(getResources().getDrawable(R.drawable.left_glove_cnt));
                rootView.findViewById(R.id.rightglove).setBackground(getResources().getDrawable(R.drawable.right_glove_cnt));
                //btnFunctionWifiOn(v);
                //rootView.findViewById(R.id.iv).setVisibility(View.INVISIBLE);
            }
        });



        mClientManager = getMainActivity().getClientManager();
        return rootView;
    }

    // 开启WiFi AP
    public void btnFunctionWifiOn(View v) {


        getMainActivity().getClientManager().clear();
        final ProgressDialog waitDialog = ProgressDialog.show(v.getContext(), "开启WiFi热点", "请稍等...", true);
        waitDialog.show();
        mIProtocolController = getMainActivity().getProtocolController();
        mIProtocolController.stopServer();
        mIProtocolController.startApaAndServer("MusicGloves", "12345678", 5000, 8081, new IStartWifiCallBack() {
            @Override
            public void execute() {
                waitDialog.dismiss();
            }
        }, new IStartWifiCallBack() {
            @Override
            public void execute() {
                waitDialog.dismiss();
            }
        });



    }



    public void btnFunctionShell(View v) {


        try {
            executeCommand("sendevent /dev/input/event1 3 57 319");
            executeCommand("sendevent /dev/input/event1 3 53 76");
            executeCommand("sendevent /dev/input/event1 3 54 171");
            executeCommand("sendevent /dev/input/event1 3 58 48");
            executeCommand("sendevent /dev/input/event1 3 48 4");
            executeCommand("sendevent /dev/input/event1 0 0 0");
            executeCommand("sendevent /dev/input/event1 3 57 -1");
            executeCommand("sendevent /dev/input/event1 0 0 0");
            //Runtime.getRuntime().exec("input keyevent 3");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (RootDeniedException e) {
            e.printStackTrace();
        }



        /*
        try {
            executeCommand("sendevent /dev/input/event0 1 116 1");
            executeCommand("sendevent /dev/input/event0 0 0 0");
            executeCommand("sendevent /dev/input/event0 1 116 0");
            executeCommand("sendevent /dev/input/event0 0 0 0");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (RootDeniedException e) {
            e.printStackTrace();
        }
        */


    }

    public void executeCommand(String command) throws InterruptedException, IOException, TimeoutException, RootDeniedException {
        CommandCapture cmd = new CommandCapture(0, command);
        RootTools.getShell(true).add(cmd);
    }

}
