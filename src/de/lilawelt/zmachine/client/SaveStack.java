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

import de.lilawelt.zmachine.client.machine.ZSaveStack;

/**
 * Wrapper for SaveStackNative.
 * 
 * Implements more operations (at the moment only pop).
 * 
 * @author johanna
 *
 */
public class SaveStack implements ZSaveStack {
	private SaveStackNative s;
	private int counter = 0;
	
	public SaveStack() {
		s = (SaveStackNative) SaveStackNative.createArray();
	}
	
	public SaveStack(String s) {
		this.s = SaveStackNative.fromJson(s);
	}
	
	public int length() { return s.length(); }
	public int get(int i) { return s.get(i); }
	public void push(int i) { s.push(i); }
	public void saveString(String name, String value) { s.saveString(name, value); }
	public String getString(String name) { return s.getString(name); }
	public int pop() {
		//assert(counter < length());
		return s.get(counter++);
	}
	public void seek(int pos) { counter = pos; }
	
	public String toJSON() { return s.toJSON(); }
}
