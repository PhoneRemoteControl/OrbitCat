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

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

import org.phoneremotecontrol.app.sms.Conversation;
import org.phoneremotecontrol.app.sms.SMSUtils;

import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class HttpServer extends NanoHTTPD {
    private static String TAG = "HttpServer";
    Context _context;

    public HttpServer(int port, Context context) {
        super(port);
        _context = context;
        Log.i(TAG, "Initialisation of nanohttpd on port " + port);
    }

    @Override public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();
        Log.i(TAG, method + " '" + uri + "' ");

        String msg;

        if (uri.equals("/sms")) {
            StringBuilder sb = new StringBuilder();
            List<Conversation> list = SMSUtils.getSMSThreadIds(_context);
            sb.append("<html><body><h1>" + _context.getString(R.string.sms_list) +"</h1>\n");

            sb.append("<ul>");

            for (Conversation c : list) {
                sb.append("<li>" + c.getThreadId() + "<ul><li>" + _context.getString(R.string.sms_nb_message) + " : " + c.getMsgCount() + "</li>");
                sb.append("<li>" + _context.getString(R.string.sms_recipient_phone_number) + " : " + c.getContact().getPhoneNumber() + "</li>");
                if (c.getContact().getDisplayName() != null) {
                    sb.append("<li>" + _context.getString(R.string.sms_recipient_name) + " : " + c.getContact().getDisplayName() + "</li>");
                }
                sb.append("</ul>");
            }

            sb.append("</ul>");
            sb.append("</body></html>\n");

            msg = sb.toString();
        } else {
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
        }

        return new NanoHTTPD.Response(msg);
    }
}
