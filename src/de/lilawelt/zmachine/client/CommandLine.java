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

import static com.google.gwt.event.dom.client.KeyCodes.KEY_ALT;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_BACKSPACE;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_CTRL;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_DOWN;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_END;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_ENTER;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_ESCAPE;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_HOME;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_LEFT;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_PAGEDOWN;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_PAGEUP;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_RIGHT;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_TAB;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_UP;

import java.util.LinkedList;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;

import de.lilawelt.zmachine.client.MachineInterface.READMODES;

public class CommandLine {
	
	private LinkedList<String> history;
	private int currPos;
	
	public CommandLine() {
		history = new LinkedList<String>();
	}
	
	/**
	 * This is called, when a new readline event is started
	 */
	public void start() {
		currPos = history.size();
	}
	
	public boolean HandleReadLineEvent(NativePreviewEvent preview, final MachineInterface mi) {
		NativeEvent event = preview.getNativeEvent();
		if (event.getMetaKey() || event.getCtrlKey() || event.getAltKey()) {
			// we do not handle this, give it to the browser
			return false;
		}

		Character keyCode = (char) event.getKeyCode();
		if ( keyCode == 0 ) {
			keyCode = (char) event.getCharCode();
		}
		Log.debug("Got keypress event: "+event.getKeyCode());
		
		// backspace and keys is handled via down-event, everything else via keypress:
		if ( preview.getTypeInt() == Event.ONKEYDOWN && ( event.getKeyCode() != KEY_BACKSPACE && event.getKeyCode() != KEY_LEFT && event.getKeyCode() != KEY_RIGHT && event.getKeyCode() != KEY_UP && event.getKeyCode() != KEY_DOWN ) ) {
			return false;
		} else if ( preview.getTypeInt() == Event.ONKEYPRESS && ( event.getKeyCode() == KEY_BACKSPACE || event.getKeyCode() == KEY_LEFT || event.getKeyCode() == KEY_RIGHT || event.getKeyCode() == KEY_UP || event.getKeyCode() == KEY_DOWN)) {
			return false;
		}

		switch (keyCode) {
		case KEY_ALT:
		case KEY_LEFT:
		case KEY_PAGEUP:
		case KEY_RIGHT:
		case KEY_PAGEDOWN:
		case KEY_TAB:
		case KEY_END:
		case KEY_ESCAPE:
		case KEY_HOME:
		case KEY_CTRL:
			// case KeyboardListener.MODIFIER_CTRL:
			// case KeyboardListener.MODIFIER_ALT:
			// case KeyboardListener.MODIFIER_META:
			return false;
		}
		
		if ( keyCode > 1000 ) {
			return false;
		}
		
		boolean terminate = false;
		
		//Log.debug("I am here");
		if ( keyCode == KEY_ENTER ) {
			terminate = true;
		} else if ( mi.getTermChars() != null ) {
			//Log.debug("and here");
			for ( Integer c : mi.getTermChars() ) {
				Log.debug("Trying additional terminating characters...: "+c.intValue()+" (code) "+event.getKeyCode());
				if ( c.equals(event.getKeyCode()) ) {
					Log.debug("Found terminating character");
					terminate = true;
				}
			}
		}

		if (keyCode == KEY_UP) {
			Log.debug("Handled KEY_UP");
			// we have no history or are at the last element.
			if ( history.size() == 0 || currPos == 0 ) {
				return true;
			}
			
			currPos--;
			mi.lastLineSb = new StringBuffer(history.get(currPos));
			mi.lastLine.setHTML(mi.lastLineBase + MachineInterface.htmlEscape(mi.lastLineSb.toString()));
			
			Log.debug("Now at position: "+currPos);
			
			return true;
		} else if ( keyCode == KEY_DOWN ) { 
			Log.debug("Handled KEY_DOWN");
			if ( history.size() == 0 || currPos >= history.size() ) {
				return true;
			}
			
			currPos++;
			Log.debug("Now at position: "+currPos);
			
			if ( currPos == history.size()) {
				mi.lastLineSb = new StringBuffer();
				mi.lastLine.setHTML(mi.lastLineBase);
			}
			
			mi.lastLineSb = new StringBuffer(history.get(currPos));
			mi.lastLine.setHTML(mi.lastLineBase + MachineInterface.htmlEscape(mi.lastLineSb.toString()));
			
			return true;
		} else if (keyCode == KEY_BACKSPACE) {
			if ( mi.lastLineSb.length() > 0 ) {
				mi.lastLineSb.deleteCharAt(mi.lastLineSb.length() - 1);
				mi.lastLine.setHTML(mi.lastLineBase + MachineInterface.htmlEscape(mi.lastLineSb.toString()));
			}
		} else if (terminate) {
			
			if ( mi.readMode == READMODES.READLINE_TIMED ) {
				mi.readTimer.cancel();
				mi.readTimer = null;
			} 
			
			history.add(mi.lastLineSb.toString()); // add it to history
			
			mi.lastLineBase = mi.lastLineBase + MachineInterface.htmlEscape(mi.lastLineSb.toString() + "\n");
			mi.lastLine.setHTML(mi.lastLineBase);
			final int lastkey = keyCode;
			Log.debug("trying to continue execution...1");
			mi.previewHandler.removeHandler();
			mi.previewHandler = null;
			Log.debug("Queueing");
			mi.readMode = READMODES.ERROR;
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				public void execute() {
					Log.debug("trying to continue execution...2");
					mi.getCpu().zop_read_continue(mi.lastLineSb.toString(), lastkey);
					mi.lastLineSb = null;
					Log.debug("trying to continue execution...3");
					Machine.get().continueExecution();
				}
			});
		} else {
			String key = keyCode.toString();
			mi.lastLineSb.append(key);
			mi.lastLine.setHTML(mi.lastLineBase + MachineInterface.htmlEscape(mi.lastLineSb.toString()));
		}
		return true;
	} 

}
