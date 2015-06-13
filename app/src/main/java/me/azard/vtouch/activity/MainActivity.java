package me.azard.vtouch.activity;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import me.azard.vtouch.R;

import me.azard.vtouch.network.imp.ClientManager;
import me.azard.vtouch.protocol.intf.IProtocolController;
import me.azard.vtouch.protocol.intf.IProtocolCallBack;
import me.azard.vtouch.protocol.imp.WifiProtocolController;
import me.azard.vtouch.network.intf.IServerCallBack;
import me.azard.vtouch.event.Nexus5;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    private ClientManager mClientManager;
    private IProtocolController mIProtocolController;
    private Nexus5 mNexus5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout)findViewById(R.id.drawer_layout));

        mClientManager = new ClientManager();
        mNexus5 = new Nexus5();


        mIProtocolController = new WifiProtocolController(this.getBaseContext());
        mIProtocolController.registerNetworkEvent("Connected", new IServerCallBack() {
            @Override
            public void execute(long cid) {
                Log.d("AzardDebug", "in Connected");
            }
        });
        mIProtocolController.registerNetworkEvent("Disconnected", new IServerCallBack() {
            @Override
            public void execute(long cid) {
                Log.d("AzardDebug", "in Disconnected");
            }
        });
        mIProtocolController.registerMusicEvent("playMusic", new IProtocolCallBack() {
            @Override
            public void execute(Long cid, String argument) {
                int res = Integer.valueOf(argument);
                if (res == 7) {
                    mNexus5.touch(0, 208, 346);
                } else if (res == 6) {
                    mNexus5.touch(1, 174, 733);
                } else if (res == 1) {
                    mNexus5.touch(2, 173, 1124);
                } else if (res == 2) {
                    mNexus5.touch(3, 242, 1468);
                }
                Log.d("AzardDebug", "in playmusic:" + argument);
            }
        });

        mIProtocolController.registerMusicEvent("stopMusic",new IProtocolCallBack() {
            @Override
            public void execute(Long cid, String argument) {
                int res=Integer.valueOf(argument);
                if (res == 7) {
                    mNexus5.release(0);
                } else if (res == 6) {
                    mNexus5.release(1);
                } else if (res == 1) {
                    mNexus5.release(2);
                } else if (res == 2){
                    mNexus5.release(3);
                }
                Log.d("AzardDebug", "in stopmusic:" + argument);
            }
        });


    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, FunctionFragment.newInstance(position + 1))
                        .commit();
                break;

            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, TouchSettingFragment.newInstance(position + 1))
                        .commit();
                break;

        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ClientManager getClientManager() {
        return mClientManager;
    }

    public IProtocolController getProtocolController(){
        return mIProtocolController;
    }


    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_function, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        public MainActivity getMainActivity(){
            return (MainActivity)getActivity();
        }
    }
}
