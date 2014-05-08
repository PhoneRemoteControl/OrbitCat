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

import org.json.JSONObject;

public class Message {
    private String date;
    private String body;
    private int type;

    public static final int INBOX_MESSAGE = 0;
    public static final int OUTOX_MESSAGE = 1;
    public static final int DRAFT_MESSAGE = 2;

    public Message(String date, String body) {
        this.date = date;
        this.body = body;
    }
    public String getDate() {
        return date;
    }

    public String getBody() {
        return body;
    }
}
