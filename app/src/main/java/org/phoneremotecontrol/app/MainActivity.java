/*
 * This file is part of the PhoneRemoteControl application.
 *
 * Copyright (C) 2014 Florent Rochette (Florent38) <florent dot rochette at gmail dot com>
 * Copyright (C) 2014 Pierre-Antoine Forestier (Freakfonk) <freakfonk at gmail dot com>
 * Copyright (C) 2014 Yoann Laissus (Arakmar) <yoann dot laissus at gmail dot com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.phoneremotecontrol.app;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import org.phoneremotecontrol.app.http.HttpServer;
import org.phoneremotecontrol.app.http.HttpServerService;
import org.phoneremotecontrol.app.http.HttpServerService.LocalBinder;
import org.phoneremotecontrol.app.network.NetworkUtils;
import org.phoneremotecontrol.app.sms.SMSHttpWorker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        HttpServerService _httpServerService;
        boolean _bound = false;
        EditText portEditText;
        Switch switchState;
        RadioGroup radioGroup;
        Map<String, String> ipMap;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            initInterfacesList(rootView);
            setupListeners(rootView);

            return rootView;
        }

        private void initInterfacesList(View rootView) {
            radioGroup = (RadioGroup) rootView.findViewById(R.id.rb_group);
            ipMap = NetworkUtils.getIPv4Addresses();

            for (String intName : ipMap.keySet()) {
                RadioButton rb = new RadioButton(getActivity());
                rb.setText(intName + " : " + ipMap.get(intName));
                radioGroup.addView(rb);
            }

            if (!ipMap.isEmpty()) {
                radioGroup.check(1);
            }
        }

        private void setupListeners(View rootView) {
            portEditText = (EditText) rootView.findViewById(R.id.edit_port);
            switchState = (Switch) rootView.findViewById(R.id.btn_state);

            switchState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Intent serviceIntent = new Intent(getActivity(), HttpServerService.class);
                    switchState.setEnabled(false);
                    if (isChecked) {
                        // Send the port to the service
                        int port = Integer.parseInt(portEditText.getText().toString());
                        int hostId = radioGroup.getCheckedRadioButtonId();
                        String host = (String)ipMap.values().toArray()[hostId -1];
                        serviceIntent.putExtra("http_port", port);
                        serviceIntent.putExtra("http_host", host);
                        // Start the service and bind it to be notified if it's closed externally
                        getActivity().getApplicationContext().startService(serviceIntent);
                        getActivity().bindService(serviceIntent, mConnection, 0);

                    } else {
                        getActivity().getApplicationContext().stopService(serviceIntent);
                    }
                }
            });
        }

        private void refreshState() {
            switchState.setEnabled(true);
            switchState.setChecked(_bound);
            radioGroup.setEnabled(!_bound);
        }

        private ServiceConnection mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                _bound = true;
                refreshState();
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                _bound = false;
                refreshState();
            }
        };

    }

}
