/***
 *  Copyright (C) 2011 Johanna Amann
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ***/


package de.lilawelt.zmachine.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

/**
 * Stack for saving our savegame stuff. This is a Javascript overlay object, which is 
 * exclusively used through SaveStack.
 * 
 * @author johanna
 *
 */
public class SaveStackNative extends JavaScriptObject {
	protected SaveStackNative() { }
		
	public final native int length() /*-{ return this.length; }-*/;
	public final native int get(int i) /*-{ return this[i]; }-*/;
	public final native void push(int i) /*-{ this[this.length] = i; }-*/;
	
	public final native void saveString(String name, String value) /*-{ this[name] = value; }-*/;
	public final native String getString(String name) /*-{ return this[name]; }-*/;
	
	public final String toJSON() {          
        return new JSONObject(this).toString();
	}
	
	public static final native SaveStackNative fromJson(String input) /*-{ return eval('(' + input + ')') }-*/;  
}
