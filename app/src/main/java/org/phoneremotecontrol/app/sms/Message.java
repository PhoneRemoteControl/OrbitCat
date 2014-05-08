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

import android.provider.Telephony.Sms;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {
    private String date;
    private String body;
    private int type;
    private String realType;
    private boolean seen;

    public static final String INBOX_MESSAGE = "inbox";
    public static final String OUTBOX_MESSAGE = "outbox";
    public static final String DRAFT_MESSAGE = "draft";
    public static final String SENT_MESSAGE = "sent";

    public Message(String date, String body, int type, boolean seen) {
        this.date = date;
        this.body = body;
        this.type = type;
        this.seen = seen;
        realType = toRealType(type);
    }

    public String toRealType(int type) {
        switch (type) {
            case Sms.MESSAGE_TYPE_INBOX:
                return INBOX_MESSAGE;
            case Sms.MESSAGE_TYPE_OUTBOX:
                return  OUTBOX_MESSAGE;
            case Sms.MESSAGE_TYPE_DRAFT:
                return DRAFT_MESSAGE;
            case Sms.MESSAGE_TYPE_SENT:
                return SENT_MESSAGE;
            default:
                return "";
        }
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("date", date);
        obj.put("body", body);
        obj.put("type", realType);
        obj.put("seen", seen);

        return obj;
    }
    public String getDate() {
        return date;
    }

    public String getBody() {
        return body;
    }
}
