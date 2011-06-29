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

import java.util.ArrayList;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import de.lilawelt.zmachine.client.machine.Point;

// The text for the windows is saved in an array; 
// Structure:
// 1: Letter or š if special letter
// 2: style or empty
// 3: foreground color or empty
// 4: background color or empty
// 5-...: the raw special character (or empty)

public class UpperWindow extends Composite {
	private VerticalPanel v;
	private ArrayList<HTML> lines;
	private ArrayList<ZCharacter[]> text;
	private int numlines;
	
	private int posx = 0;
	private int posy = 0;
	
	public UpperWindow(int lines) {
		v = new VerticalPanel();
		this.lines = new ArrayList<HTML>();
		text = new ArrayList<ZCharacter[]>();
		
		for (int i = 0; i < lines; i++) {
			this.lines.add(new HTML());
			v.add(this.lines.get(i));
			text.add(new ZCharacter[80]);
			
			for ( int y = 0; y < 80; y++ ) {
				text.get(i)[y] = new ZCharacter();
			}
		}
		
		numlines = lines;
		
		initWidget(v);
	}
	
	public void erase() {
		Log.debug("erase upper window");
		for ( HTML line : this.lines ) {
			line.setHTML("");
		}
		
		for (int i = 0; i < numlines; i++) {
			for ( int y = 0; y < 80; y++ ) {
				text.get(i)[y] = new ZCharacter();
			}
		}
	}
	
	public void showString(String s, int style, int fgcolor, int bgcolor) {			
		String[] ourlines = s.split("\n");
		for (int j = 0; j < ourlines.length; j++ ) {
			for ( int i = 0; i < ourlines[j].length(); i++) {
				boolean doadd = false;
				ZCharacter c = new ZCharacter();
				c.style = style;
				c.fgcolor = fgcolor;
				c.bgcolor = bgcolor;
				if ( i == ourlines[j].indexOf("$@U@$")) {
					// shit
					Log.debug("Long character");
					doadd = true;
					int pos = i-1;
					String addstring = "";
					do {
						pos++;
						addstring += ourlines[j].substring(pos, pos+1);
					} while ( ourlines[j].charAt(pos) != ';');
					ourlines[j] = ourlines[j].substring(0, i) + ourlines[j].substring(pos);
					c.characterText = addstring;
					Log.debug("New Line: "+ourlines[j]);

				} else {
					c.characterText = ourlines[j].substring(i,i+1);
				}
				if (doadd) {
				}
				//Log.debug("Adding: "+c.characterText);
				text.get(posy)[posx] = c;
				posx++;
			}
			if ( j < ourlines.length - 1) {
				posy++;
				posx = 0;
			}
		}
	}
	
	// return the string which has the same style as the current one, starting at position x,y
	private ZString getSameString(final int x, final int y) {
		ZCharacter curchar = text.get(x)[y];
		String curtext = curchar.characterText;
		int fgcolor = curchar.fgcolor;
		int bgcolor = curchar.bgcolor;
		int style = curchar.style;
		
		if ( curtext.equals("") || curtext.equals(" ")) {
			curtext = " ";
		}
		
		int counter = y + 1;
		while(counter < 80) {
			ZCharacter newchar = text.get(x)[counter];
			String newtext = newchar.characterText;
			//Log.debug("Analyzing text: "+newtext);
			int newbgcolor = newchar.bgcolor;
			int newfgcolor = newchar.fgcolor;
			int newstyle = newchar.style;
			
			if ( newtext.equals("") || newtext.equals(" ")) {
				newtext = " ";
			}

			if ( newbgcolor == bgcolor && newfgcolor == fgcolor && newstyle == style ) {
				curtext += newtext;
			} else {
				break;
			}
			
			counter++;
		}
		
		//Log.debug("Returning text: "+curtext);
		ZString z = new ZString();
		z.string = curtext;
		z.realLength = counter - y;
		return z;

	}
	
	public void updateDisplay() {
		for ( int i = 0; i < numlines; i++ ) {
			String linetext = "";
			for ( int y = 0; y < 80; ) {
				ZCharacter curchar = text.get(i)[y];
				int style = curchar.style;
				int fgcolor = curchar.fgcolor;
				int bgcolor = curchar.bgcolor;
				String curtext;
				
				ZString zcurtext = getSameString(i, y);
				curtext = zcurtext.string;
				y = y + zcurtext.realLength;
				//y = y + curtext.length();
				curtext = MachineInterface.htmlEscape(curtext);
				curtext = curtext.replace(" ", "&nbsp;");
				linetext += MachineInterface.styleFormat(curtext, style, fgcolor, bgcolor);
			}

			lines.get(i).setHTML(linetext);
			/* if ( style == 1) {
				Log.debug("Setting background-color to black");
				lines.get(i).getElement().setAttribute("background-color", "black");
			} else {
				lines.get(i).getElement().setAttribute("background-color", "white");
			} */
		}
	}
	
	public String getLines(int line) { // get lines Starting with line i
		String returntext = "";
		for ( int i = line; i < numlines; i++ ) {
			String linetext = "";
			for ( int y = 0; y < 80; y++ ) {
				ZCharacter curchar = text.get(i)[y];
				int style = curchar.style;
				int fgcolor = curchar.fgcolor;
				int bgcolor = curchar.bgcolor;
				String curtext = curchar.characterText;
				if ( curtext.equals("") || curtext.equals(" ")) {
					linetext += MachineInterface.styleFormat("&nbsp;", style, fgcolor, bgcolor);
				} else {
					curtext = MachineInterface.htmlEscape(curtext);
					curtext = curtext.replace(" ", "&nbsp;");
					linetext += MachineInterface.styleFormat(curtext, style, fgcolor, bgcolor);
				}				
			}

			returntext += linetext+"<br>";
		}
		return returntext;

	}
	
	public void setCursor(int x, int y) {
		while ( y > numlines ) {
			HTML a = new HTML();
			this.lines.add(a);
			a.setHTML("test");
			v.add(a);
			text.add(new ZCharacter[80]);
			numlines++;
			
			for ( int l = 0; l < 80; l++ ) {
				text.get(text.size()-1)[l] = new ZCharacter();
			}

		}
		posx = x-1;
		posy = y-1;
	}
	
	public Point getCursor() {
		return new Point(posx, posy);
	}

	public int getNumlines() {
		return numlines;
	}

}
