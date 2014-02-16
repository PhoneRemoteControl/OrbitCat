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

public class Contact {
    private String _phoneNumber;
    private String _displayName;

    public Contact(String phoneNumber, String displayName) {
        _phoneNumber = phoneNumber;
        _displayName = displayName;
    }

    public String getPhoneNumber() {
        return _phoneNumber;
    }

    public String getDisplayName() {
        return _displayName;
    }

    public String toString() {
        return "[phoneNumber = " + _phoneNumber + ", displayName  = " + _displayName + "]";
    }
}
