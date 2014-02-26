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

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import org.phoneremotecontrol.app.http.HttpServer;
import org.phoneremotecontrol.app.sms.SMSHttpWorker;

import java.io.IOException;

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
        HttpServer _httpServer;
        EditText portEditText;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            setupListeners(rootView);

            return rootView;
        }

        private void setupListeners(View rootView) {
            portEditText = (EditText) rootView.findViewById(R.id.edit_port);
            Switch switchState = (Switch) rootView.findViewById(R.id.btn_state);

            SMSHttpWorker smsWorker = new SMSHttpWorker(getActivity().getApplicationContext(), "/sms");

            _httpServer = new HttpServer(Integer.parseInt(portEditText.getText().toString()),
                    getActivity().getApplicationContext().getCacheDir());
            _httpServer.addWorker(smsWorker);
            switchState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        try {
                            if (_httpServer.isAlive()) {
                                _httpServer.stop();
                            }
                            SMSHttpWorker smsWorker = new SMSHttpWorker(getActivity().getApplicationContext(), "/sms");
                            _httpServer = new HttpServer(Integer.parseInt(portEditText.getText().toString()),
                                    getActivity().getApplicationContext().getCacheDir());
                            _httpServer.addWorker(smsWorker);
                            _httpServer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        _httpServer.stop();
                    }
                }
            });
        }
    }

}
