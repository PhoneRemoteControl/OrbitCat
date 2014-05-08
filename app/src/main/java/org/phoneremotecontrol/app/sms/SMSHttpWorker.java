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
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.phoneremotecontrol.app.contacts.Contact;
import org.phoneremotecontrol.app.contacts.ContactException;
import org.phoneremotecontrol.app.contacts.ContactUtils;
import org.phoneremotecontrol.app.http.HttpServerService;
import org.phoneremotecontrol.app.http.HttpWorker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;

public class SMSHttpWorker implements HttpWorker {
    private static final String TAG = "SMSHttpWorker";
    private Context context;
    private String location;
    private File cacheDir;
    private String cacheLocation;

    public SMSHttpWorker(Context context, String location) {
        this.context = context;
        this.location = location;
        initCache();
    }

    @Override
    public String getLocation() {
        return location;
    }

    private void initCache() {
        cacheLocation = "/workers" + location;
        cacheDir = new File(HttpServerService.getRootDir(context), cacheLocation);
        cacheDir.mkdirs();
        Log.d(TAG, "Init cache for " + location + " on " + cacheDir + " at location " + cacheLocation);
    }

    private Response serveConversations() {
        Log.d(TAG, "Serving conversations ...");
        List<Conversation> list = SMSUtils.getSMSThreadIds(context);
        JSONArray conversationArray = new JSONArray();

        File imagePath = new File(cacheDir, "img");
        if (!imagePath.isDirectory()) {
            imagePath.mkdir();
        }

        for (Conversation c : list) {
            File contactImage = null;
            try {
                contactImage = copyContactImage(c.getContact(), imagePath);
            } catch (ContactException e) {
                Log.d(TAG, "Fail to copy contact image for " + e.getMessage());
            }

            JSONObject jsonObject = null;
            try {
                jsonObject = c.toJSON();

                if (contactImage != null) {
                    jsonObject.put("imagePath", cacheLocation + "/img/" + contactImage.getName());
                }
            } catch (JSONException e) {
                Log.e(TAG, "Unable to serialize JSON for " + c);
            }

            conversationArray.put(jsonObject);
        }

        String msg = conversationArray.toString();
        Response response = new Response(msg);
        response.setMimeType("application/json");
        return response;
    }

    private Response serveMessages(long id) {
        Log.d(TAG, "Serving messages for " + id);

        List<Message> messagesList = SMSUtils.getMessageForThread(id, 20, 0, context);
        JSONArray messageArray = new JSONArray();

        for (Message m : messagesList) {
            JSONObject obj = null;
            try {
                obj = m.toJSON();
            } catch (JSONException e) {
                Log.e(TAG, "Unable to serialize JSON for " + m);
            }
            messageArray.put(obj);
        }

        String msg = messageArray.toString();
        Response response = new Response(msg);
        response.setMimeType("application/json");
        return response;
    }

    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        String[] splittedLocation = session.getUri().split("/", 0);
        if (splittedLocation.length == 2) {
            return serveConversations();
        } else if (splittedLocation.length == 3) {
            long id = Long.parseLong(splittedLocation[2]);
            return serveMessages(id);
        } else {
            Log.d(TAG, "Incorrect URL");
        }

        return new Response("");
    }

    private File copyContactImage(Contact c, File f) throws ContactException {
        File outFile;
        try {
            outFile = new File(f, c.getId() + ".jpg");
            Log.e(TAG, f.getPath() + " " + outFile);
            InputStream in = ContactUtils.getContactPhotoStream(c, context);
            if (in == null) {
                throw new ContactException("No input stream for " + c);
            }

            OutputStream out = new FileOutputStream(outFile);
            copyFile(in, out);
        } catch (Exception e) {
            throw new ContactException("Unable to copy contact image to " + f, e);
        }
        return outFile;
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public String toString() {
        return "SMSHTTPWorker at " + location;
    }
}
