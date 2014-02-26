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

package org.phoneremotecontrol.app.http;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.SimpleWebServer;

public class HttpServer extends SimpleWebServer {
    private static String TAG = "HttpServer";
    private List<HttpWorker> _workerList;

    public HttpServer(int port, File root) {
        super(null, port, root, true);
        _workerList = new ArrayList<HttpWorker>();
        Log.d(TAG, "Initialisation of nanohttpd on port " + port + " with document root : " + root);
    }

    public boolean addWorker(HttpWorker worker) {
        if (worker != null && getWorkerForIdenticalLocation(worker.getLocation()) == null) {
            _workerList.add(worker);
            return true;
        }
        return false;
    }

    public HttpWorker getWorkerForIdenticalLocation(String location) {
        for (HttpWorker worker : _workerList) {
            if (worker.getLocation().equals(location)) {
                return worker;
            }
        }
        return null;
    }

    public HttpWorker getWorkerForLocation(String location) {
        String[] splittedLocation = location.split("/", 0);
        HttpWorker found = null;

        for (int i = splittedLocation.length; i > 0 && found == null; i--) {
            for (HttpWorker worker : _workerList) {
                String[] splittedWorker = worker.getLocation().split("/", 0);
                if (splittedWorker.length == i) {
                    boolean match = true;
                    for (int j = 0; j < splittedWorker.length; j++) {
                        Log.d(TAG, "Comparing " + splittedWorker[j] + " and " + splittedLocation[j]);
                        if (!splittedWorker[j].equals(splittedLocation[j])) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        found = worker;
                        break;
                    }
                }
            }
        }

        return found;
    }

    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();
        Log.i(TAG, method + " '" + uri + "' ");

        HttpWorker worker = getWorkerForLocation(uri);
        if (worker != null) {
            Log.d(TAG, "Found worker for request " + uri + " : " + worker);
            return worker.serve(session);
        }
        String msg;

        msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null)
            msg +=
                    "<form action='?' method='get'>\n" +
                            "  <p>Your name: <input type='text' name='username'></p>\n" +
                            "</form>\n";
        else
            msg += "<p>Hello, " + parms.get("username") + "!</p>";

        msg += "</body></html>\n";

        return new NanoHTTPD.Response(msg);
    }

}
