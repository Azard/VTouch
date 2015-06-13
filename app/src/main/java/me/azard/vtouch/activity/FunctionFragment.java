package me.azard.vtouch.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.azard.vtouch.R;

import me.azard.vtouch.network.imp.ClientManager;
import me.azard.vtouch.protocol.intf.IProtocolController;
import me.azard.vtouch.protocol.intf.IStartWifiCallBack;


public class FunctionFragment extends MainActivity.PlaceholderFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private IProtocolController mIProtocolController;
    private ClientManager mClientManager;
    public View rootView;


    public static FunctionFragment newInstance(int sectionNumber) {
        FunctionFragment fragment = new FunctionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_function, container, false);
        // 绑定Button事件
        rootView.findViewById(R.id.bigbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_left_cnt();
                set_right_cnt();
                btnFunctionWifiOn(v);
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

    // 手套图标开启连接/关闭连接
    public void set_left_cnt()
    {
        rootView.findViewById(R.id.leftglove).setBackground(getResources().getDrawable(R.drawable.left_glove_cnt));
    }
    public void set_right_cnt()
    {
        rootView.findViewById(R.id.rightglove).setBackground(getResources().getDrawable(R.drawable.right_glove_cnt));
    }
    public void set_left_dis()
    {
        rootView.findViewById(R.id.leftglove).setBackground(getResources().getDrawable(R.drawable.left_glove_dis));
    }
    public void set_right_dis()
    {
        rootView.findViewById(R.id.rightglove).setBackground(getResources().getDrawable(R.drawable.right_glove_dis));
    }

}
