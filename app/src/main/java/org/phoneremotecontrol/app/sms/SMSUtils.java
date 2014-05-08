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
import android.provider.Telephony;
import android.provider.Telephony.Threads;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;

import org.phoneremotecontrol.app.contacts.Contact;
import org.phoneremotecontrol.app.contacts.ContactUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SMSUtils {

    private static String TAG = "SMSUtils";
    private static String URI_CANNONICAL_ADDRESS = "content://mms-sms/canonical-address";
    private static String URI_CONVERSATIONS = "content://mms-sms/conversations?simple=true";
    private static String URI_INBOX = "content://mms-sms/inbox";

    public static List<Conversation> getSMSThreadIds(Context context) {

        List<Conversation> list = new ArrayList<Conversation>();

        final String[] allThreadProjections = {
                Threads._ID, Threads.MESSAGE_COUNT, Threads.RECIPIENT_IDS, Threads.SNIPPET
        };
        Cursor cursor = context.getContentResolver().query(Uri.parse(URI_CONVERSATIONS), allThreadProjections, null, null, null);

        while (cursor.moveToNext()) {
            long threadId = cursor.getLong(cursor.getColumnIndex(Threads._ID));
            long msgCount = cursor.getLong(cursor.getColumnIndex(Threads.MESSAGE_COUNT));
            long rec = cursor.getLong(cursor.getColumnIndex(Threads.RECIPIENT_IDS));
            String snippet = cursor.getString(cursor.getColumnIndex(Threads.SNIPPET));

            String phoneNumber = getNumberForId(rec, context);
            Contact contact = ContactUtils.getContactFromPhoneNumber(phoneNumber, context);
            list.add(new Conversation(threadId, msgCount, contact, snippet));
            Log.d(TAG, "Found " + threadId  + " " + msgCount + " " + rec);
        }
        cursor.close();

        return list;
    }

    public static List<Message> getMessageForThread(long threadId, int number, Context context) {
        return getMessageForThread(threadId, number, 0, context);
    }

    public static List<Message> getMessageForThread(long threadId, int number, int offset, Context context) {
        String uri = "content://sms/conversations/" + threadId;
        final String[] projection = new String[]{Telephony.Sms.DATE, Telephony.Sms.BODY, Telephony.Sms.TYPE, Telephony.Sms.SEEN};
        Cursor cursor = context.getContentResolver().query(Uri.parse(uri), projection, null, null, Telephony.Sms.DATE + " DESC limit " + number + " offset " + offset);

        List<Message> msgList = new ArrayList<Message>();

        while(cursor.moveToNext()) {
            long date = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE));
            String formattedDate = formatTimeStampString(context, date, true);
            String body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
            int type = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.TYPE));
            int seen = cursor.getInt(cursor.getColumnIndex(Telephony.Sms.SEEN));

            Message message = new Message(formattedDate, body, type, seen != 0);
            msgList.add(message);
        }
        cursor.close();

        return msgList;
    }

    // Taken from CyanogenMod's android_packages_apps_Mms
    public static String formatTimeStampString(Context context, long when, boolean fullFormat) {
        Time then = new Time();
        then.set(when);
        Time now = new Time();
        now.setToNow();

        // Basic settings for formatDateTime() we want for all cases.
        int format_flags = DateUtils.FORMAT_NO_NOON_MIDNIGHT |
                DateUtils.FORMAT_ABBREV_ALL |
                DateUtils.FORMAT_CAP_AMPM;

        // If the message is from a different year, show the date and year.
        if (then.year != now.year) {
            format_flags |= DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE;
        } else if (then.yearDay != now.yearDay) {
            // If it is from a different day than today, show only the date.
            format_flags |= DateUtils.FORMAT_SHOW_DATE;
        } else {
            // Otherwise, if the message is from today, show the time.
            format_flags |= DateUtils.FORMAT_SHOW_TIME;
        }

        // If the caller has asked for full details, make sure to show the date
        // and time no matter what we've determined above (but still make showing
        // the year only happen if it is a different year from today).
        if (fullFormat) {
            format_flags |= (DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
        }

        return DateUtils.formatDateTime(context, when, format_flags);
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
