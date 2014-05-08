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

package org.phoneremotecontrol.app.sms;

import org.json.JSONException;
import org.json.JSONObject;
import org.phoneremotecontrol.app.contacts.Contact;

public class Conversation {
    private long threadId;
    private long msgCount;
    private Contact contact;
    private String snippet;

    public Conversation(long threadId, long msgCount, Contact contact, String snippet) {
        this.threadId = threadId;
        this.msgCount = msgCount;
        this.contact = contact;
        this.snippet = snippet;
    }

    public String toString() {
        return "[threadId = " + getThreadId() + ", msgCount = " + getMsgCount() + ", contact = " + getContact() + "]";
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("threadId", threadId);
        obj.put("msgCount", msgCount);
        obj.put("contact", contact.toJSON());
        obj.put("snippet", snippet);
        return obj;
    }

    public long getThreadId() {
        return threadId;
    }

    public long getMsgCount() {
        return msgCount;
    }

    public Contact getContact() {
        return contact;
    }

    public String getSnippet() {
        return snippet;
    }
}
