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

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony.Threads;
import android.util.Log;

import org.phoneremotecontrol.app.contacts.Contact;
import org.phoneremotecontrol.app.contacts.ContactUtils;

import java.util.ArrayList;
import java.util.List;

public class SMSUtils {

    private static String TAG = "SMSUtils";
    private static String URI_CANNONICAL_ADDRESSES = "content://mms-sms/canonical-addresses";
    private static String URI_CANNONICAL_ADDRESS = "content://mms-sms/canonical-address";
    private static String URI_CONVERSATIONS = "content://mms-sms/conversations?simple=true";

    public static List<Conversation> getSMSThreadIds(Context context) {

        List<Conversation> list = new ArrayList<Conversation>();

        final String[] allThreadProjections = {
                Threads._ID, Threads.MESSAGE_COUNT, Threads.RECIPIENT_IDS
        };
        Cursor cursor = context.getContentResolver().query(Uri.parse(URI_CONVERSATIONS), allThreadProjections, null, null, null);

        while (cursor.moveToNext()) {
            long threadId = cursor.getLong(0);
            long msgCount = cursor.getLong(1);
            long rec = cursor.getLong(2);

            String phoneNumber = getNumberForId(rec, context);
            Contact contact = ContactUtils.getContactFromPhoneNumber(phoneNumber, context);
            list.add(new Conversation(threadId, msgCount, contact));
            Log.d(TAG, "Found " + threadId  + " " + msgCount + " " + rec);
        }
        cursor.close();

        return list;
    }

    public static String getNumberForId(long recipientId, Context context) {
        Cursor cursor = context.getContentResolver().query(Uri.parse(URI_CANNONICAL_ADDRESS + "/" + recipientId), null, null, null, null);

        String number = null;
        if (cursor.moveToNext()) {
            number = cursor.getString(0);
        }

        cursor.close();
        return number;
    }
}
