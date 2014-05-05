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

package org.phoneremotecontrol.app.contacts;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ContactUtils {
    private static String TAG = "ContactUtils";

    public static Contact getContactFromPhoneNumber(String phoneNumber, Context context) {
        Uri contactUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        final String[] contactProjection = new String[] {PhoneLookup._ID, PhoneLookup.DISPLAY_NAME};

        Cursor c = context.getContentResolver().query(contactUri, contactProjection, null, null, null);
        Contact contact = new Contact(phoneNumber, phoneNumber);

        try {
            if (c.moveToFirst()) {
                long id = c.getLong(c.getColumnIndex(PhoneLookup._ID));
                String name = c.getString(c.getColumnIndex(PhoneLookup.DISPLAY_NAME));

                contact = new Contact(id, phoneNumber, name);
            }
        } catch (Exception e) {
            Log.d(TAG, "Unable to retrieve contact with phone number " + phoneNumber);
        } finally {
            c.close();
        }

        return contact;
    }

    public static InputStream getContactPhotoStream(Contact contact, Context context) {
        if (contact == null) {
            return null;
        }
        Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contact.getId());
        return Contacts.openContactPhotoInputStream(context.getContentResolver(), contactUri);
    }
}
