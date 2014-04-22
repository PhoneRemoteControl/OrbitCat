/*
 * This file is part of the PhoneRemoteControl application.
 *
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
package org.phoneremotecontrol.app.sms;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.phoneremotecontrol.app.R;
import org.phoneremotecontrol.app.http.HttpWorker;

import java.util.List;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;

public class SMSHttpWorker implements HttpWorker {
    private Context _context;
    private String _location;

    public SMSHttpWorker(Context context, String location) {
        _context = context;
        _location = location;
    }

    @Override
    public String getLocation() {
        return _location;
    }

    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        List<Conversation> list = SMSUtils.getSMSThreadIds(_context);
        JSONArray conversationArray = new JSONArray();

        for (Conversation c : list) {
            try {
                conversationArray.put(c.toJSON());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String msg = conversationArray.toString();
        Response response = new Response(msg);
        response.setMimeType("application/json");
        return new Response(msg);
    }

    public String toString() {
        return "SMSHTTPWorker at " + _location;
    }
}
