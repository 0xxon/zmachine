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

import java.util.Vector;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import static com.google.gwt.event.dom.client.KeyCodes.*; 

import de.lilawelt.zmachine.client.machine.Dimension;
import de.lilawelt.zmachine.client.machine.Point;
import de.lilawelt.zmachine.client.machine.ZCPU;
import de.lilawelt.zmachine.client.machine.ZUserInterface;

/**
 * @author johanna
 *
 */
public class MachineInterface extends Composite implements ZUserInterface, NativePreviewHandler {

	private ZCPU cpu;

	private Dimension screenSize;
	private int curWindow; // The current window.
	private int version = 0; // version of the storyfile
	//private int moreLines = 0; // Number of lines before next MORE

	// private List<Label> lines = new LinkedList();
	private DockPanel outer;
	private ScrollPanel inner;
	private HorizontalPanel statusbar;
	private UpperWindow upperwindow;
	private VerticalPanel v;
	public HTML lastLine;
	public String lastLineBase;
	public StringBuffer lastLineSb;
	private String story; // the url to the story, we are playing.
	private String storyUid; // an unique identifier of the story we are playing
	private HandlerRegistration resize; // our window resize listener
	//private int curFont;
	private int curStyle;
	private int curFgColor;
	private int curBgColor;
	private int maxWidth;
	private int splitNewWindow = -10; // remember if we still have to split a window. -10 if everything is ok.
	private Vector<Integer> termChars; // terminating characters
	static public int defaultFgColor = 9;
	static public int defaultBgColor = 2;
	public HandlerRegistration previewHandler = null;
	private CommandLine commline;

	
    public Timer readTimer; // timer for timed reads
	
	public enum READMODES {ERROR, READCHAR, READLINE, READCHAR_TIMED, READLINE_TIMED}
	public READMODES readMode;

	MachineInterface() {
		outer = new DockPanel();

		initWidget(outer);
	}

	
	public void updateDisplay() {
		if ( upperwindow != null )
			upperwindow.updateDisplay();
		
	}
	
	public void startStory(String story, String storyUid) {
		Log.debug("initializing cpu...");
		cpu = new ZCPU(this);

		this.story = story;
		this.storyUid = storyUid;
		cpu.loadStory(story);
		
	}

	public ZCPU getCpu() {
		return cpu;
	}

	public boolean defaultFontProportional() {
		Log.debug("method defaultFontProportional");
		return false;
	}

	public void eraseLine(int s) {
		// TODO Auto-generated method stub
		Log.debug("Unimplemented method eraseLine");

	}

	public void eraseWindow(int window) {
		Log.debug("method eraseWindow for window "+window);
		if ( window == 0 ) {
			v.clear();
			v.getElement().setAttribute("style", "background-color:"+codeToColor(curBgColor));
			lastLineBase = "";
		} else if ( window == 1 ) {
			upperwindow.erase();
		} else if ( window == -1 ) {
			splitScreen(0);
			v.clear();
			v.getElement().setAttribute("style", "background-color:"+codeToColor(curBgColor));
		}

	}

	public void fatal(String errmsg) {
		outer.clear();
		PopupPanel popup = new PopupPanel(false);
		popup.setTitle("Fatal error:");
		popup.setWidget(new Label(errmsg));
		popup.center();
		popup.show();
		throw new RuntimeException(errmsg);
	}

	public Point getCursorPosition() {
		Log.debug("method getCursorPosition");
		if ( curWindow == 1 ) {
			return upperwindow.getCursor();
		} else {
			Log.error("getCursorPosition for window 0");
			return null;
		}
	}

	public int getDefaultBackground() {
		Log.debug("method getDefaultBackground");
		return defaultBgColor;
	}

	public int getDefaultForeground() {
		Log.debug("method getDefaultForeground");
		return defaultFgColor;
	}
	
	public void getFilename(String suggestedName, AsyncCallback<String> callback) {
		Log.debug("method getFileName for restore");
		
		if ( callback == null )
			Log.debug("Null callback in MachineInterfece");
		
	    FileSelector s = new FileSelector(suggestedName, "", callback, false);
	    
	    s.show();
	    
	    s.center();

	}


	public void getFilename(String suggestedName, String saveData, AsyncCallback<String> callback) {
		Log.debug("method getFileName for save");
	    
	    FileSelector s = new FileSelector(suggestedName,  saveData, callback, true);
	    s.show();
	    
	    s.center();	    	    
	}

	public Dimension getFontSize() {
		Log.debug("Call to getFontSize");
		return new Dimension(1, 1);
	}

	public Dimension getScreenCharacters() {
		Log.debug("Call to getScreenCharacters");
		return screenSize;
	}

	public Dimension getScreenUnits() {
		Log.debug("Call to getScreenUnits");
		return screenSize;
	}

	public Dimension getWindowSize(int window) {
		Log.debug("Unimplemented method getWindowSize for window "+window);
		if ( window == 1) {
			return new Dimension(upperwindow.getNumlines(), 80);
		} else {
			return new Dimension(255, 80);
		}
	}

	public boolean hasBoldface() {
		Log.debug("method hasBoldFace");
		return true;
	}

	public boolean hasColors() {
		Log.debug("method hasColors");
		return true;
	}

	public boolean hasFixedWidth() {
		Log.debug("method hasFixedWidth");
		return true;
	}

	public boolean hasItalic() {
		Log.debug("method hasItalic");
		return true;
	}

	public boolean hasStatusLine() {
		Log.debug("method hasStatusLine");
		if ((version >= 1) && (version <= 3))
			return true;
		else
			return false;
	}

	public boolean hasTimedInput() {
		Log.debug("method hasTimedInput");
		return true;
	}

	public boolean hasUpperWindow() {
		Log.debug("method hasUpperWindow");
		if (version >= 3)
			return true;
		else
			return false;
	}
	
	static public String codeToColor(int num) {
		switch (num) {
			case 2: return "black";
			case 3: return "red";
			case 4: return "#7CFC00"; // bright green
			case 5: return "yellow";
			case 6: return "blue";
			case 7: return "magenta";
			case 8: return "cyan";
			case 9: return "white";
			default: return "";
		}
	}
	
	static public String styleFormat(String s, int style, int fgcolor, int bgcolor) {
		String attributes = "";
		//Log.debug("styleFormat with s="+s+" style="+style+" fgcolor="+fgcolor+" bgcolor="+bgcolor);
		
		if ( fgcolor == -1 ) {
			fgcolor = defaultFgColor;
		}
		if ( bgcolor == -1 ) {
			bgcolor = defaultBgColor;
		}
		
		//if ( fgcolor != -1 && bgcolor != -1 ) {
			//if ( fgcolor != defaultFgColor ) {
				attributes += "color:"+codeToColor(fgcolor);
			//}
			//if ( bgcolor != defaultBgColor ) {
				attributes += ";background-color:"+codeToColor(bgcolor);
			//}
		//}
		
		if ( style == 0 ) {
		} else if ( style == 1 ) { // reverse video
			if ( fgcolor == -1 || bgcolor == -1 ) 
				attributes = "background-color:black;color:white;font-size:small";
			else
				attributes = ";background-color:"+codeToColor(fgcolor)+";color:"+codeToColor(bgcolor)+";font-size:small";
		} else if ( style == 2) {
			attributes +=  ";font-weight:bold";
		} else if ( style == 4 ) {
			attributes += ";font-style:italic";
		} else {
			//Log.error("Unknown font style: "+style);
		}
		
		
		if ( attributes.equals("") ) {
			return s;
		} else {
			return "<a style='"+attributes+"'>"+s+"</a>";
		}
	}
	
	/**
	 * get the width of a scrollbar.
	 * @return
	 */
	public static native int scrollbarSize() /*-{
		var i = document.createElement('p');
		i.style.width = '100%';

		i.style.height = '200px';

		var o = document.createElement('div');
		o.style.position = 'absolute';
		o.style.top = '0px';
		o.style.left = '0px';
		o.style.visibility = 'hidden';
		o.style.width = '200px';
		o.style.height = '150px';
		o.style.overflow = 'hidden';
		o.appendChild(i);

		document.body.appendChild(o);
		var w1 = i.offsetWidth;
		var h1 = i.offsetHeight;
		o.style.overflow = 'scroll';
		var w2 = i.offsetWidth;
		var h2 = i.offsetHeight;
		if (w1 == w2) w2 = o.clientWidth;
		if (h1 == h2) h2 = o.clientWidth;

		document.body.removeChild(o);

		//window.scrollbarWidth = w1-w2;
		//window.scrollbarHeight = h1-h2;

		return (w1-w2);
	}-*/;

	public void initialize(int ver) {
		version = ver;
		Log.debug("method initialize");
		Log.debug("game version: " + ver);

		Window.enableScrolling(false);
		
		commline = new CommandLine();
		
		// outer.setHeight("100%");
		// outer.setWidth("100%");
		v = new VerticalPanel();
		inner = new ScrollPanel();
		inner.add(v);
		//DOM.setStyleAttribute(inner.getElement(), "overflowX", "hidden");
		//DOM.setStyleAttribute(inner.getElement(), "overflowY", "hidden");
		outer.add(inner, DockPanel.SOUTH);
				
		screenSize = new Dimension(255, 80);
		curWindow = 0;
		
		upperwindow = new UpperWindow(0);
		
		setColor(1,1); // set colors to default.

		String testhtml = "";
		for ( int i = 0; i < 80; i++ ) 
			testhtml += "W";
		
		if ( version > 3 ) {
			HTML testWidth;
			testWidth = new HTML(testhtml);
			outer.add(testWidth, DockPanel.NORTH);
			maxWidth = testWidth.getOffsetWidth() + scrollbarSize();
			outer.remove(testWidth);
		} 
		
		ResizeHandler resizehandler = new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				correctWindowSizes();
				
				/*
				int height = event.getHeight();
				int offset = 0;
				int swidth;
				if ( version <= 3  ) 
					swidth = Window.getClientWidth();
				else
					swidth = maxWidth;
				if ( statusbar != null ) {
					offset += statusbar.getOffsetHeight();
				}
				if ( upperwindow != null ) {
					offset += upperwindow.getOffsetHeight();
				}
				outer.setHeight(height + "px");
				inner.setHeight((height - offset) + "px");
				outer.setWidth(swidth + "px");
				inner.setWidth(swidth + "px");
				//v.setWidth(swidth + "px"); */
			}
		};
				
		Window.addResizeHandler(resizehandler);
		
		correctWindowSizes();
		
	    /* Timer t = new Timer() {
	        public void run() {
	        	correctWindowSizes();
	        }
	      };

	      t.schedule(4000); */
	}

	private void correctWindowSizes() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				
				int height = Window.getClientHeight();
				int offset = 0;
				int swidth;
				if ( version <= 3  ) 
					swidth = Window.getClientWidth();
				else
					swidth = maxWidth;
				if ( statusbar != null ) {
					offset += statusbar.getOffsetHeight();
				}
				if ( upperwindow != null ) {
					offset += upperwindow.getOffsetHeight();
				}
				outer.setHeight(height + "px");
				inner.setHeight((height - offset) + "px");
				outer.setWidth(swidth + "px");
				inner.setWidth(swidth + "px");
				//v.setWidth(swidth + "px"); 				
			}
		});

	}
	
	public void quit() {
		Log.debug("method quit");
		outer.clear();
		PopupPanel popup = new PopupPanel(false);
		popup.setWidget(new Label("Game quit."));
		popup.center();
		popup.show();
	}

	public void readChar(int time) {
		Log.debug("method readChar with time: "+time);
		if ( time == 0 ) {
			readMode = READMODES.READCHAR;
			assert(previewHandler == null);
			previewHandler = Event.addNativePreviewHandler(this);
			updateDisplay();
		} else {
			readMode = READMODES.READCHAR_TIMED;
			readTimer = new Timer() {

				public void run() {
					Log.debug("Timer expired");
					timerComplete();
					
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						public void execute() {
							Log.debug("trying to continue execution...2");
							if ( getCpu().zop_read_char_timed_continue(-1) ) {
								Log.debug("trying to continue execution...3");
								Machine.get().continueExecution();
							} else {
								Log.debug("Execution breaks");
							}
						}
					});

				}
				
			};
			readTimer.schedule(time*100);
			
			assert(previewHandler == null);
			previewHandler = Event.addNativePreviewHandler(this);
			updateDisplay();
		}
	}
	
	private void timerComplete() {
		//DOM.removeEventPreview(this);
		previewHandler.removeHandler();
		previewHandler = null;
	}

	public void readLine(int time, boolean continued) {
		Log.debug("method readLine with time: " + time);
		if ( continued == false  || time == 0) {
			lastLineSb = new StringBuffer();
		} else {
			lastLine.setHTML(lastLineBase + htmlEscape(lastLineSb.toString()));
		}
		
		commline.start();
		
		if ( time == 0 ) {
			lastLineSb = new StringBuffer();
			readMode = READMODES.READLINE;
			assert(previewHandler == null);
			previewHandler = Event.addNativePreviewHandler(this);
		}  else {
			readMode = READMODES.READLINE_TIMED;
			readTimer = new Timer() {

				public void run() {
					Log.debug("Timer expired");
					lastLine.setHTML(htmlEscape(lastLineBase));
					timerComplete();
					
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						public void execute() {
							Log.debug("trying to continue execution...2");
							if ( getCpu().zop_read_timed_continue(lastLineSb.toString()) ) {
								Log.debug("trying to continue execution...3");
								Machine.get().continueExecution();
							} else {
								Log.debug("Execution breaks");
							}
						}
					});

				}
				
			};
			readTimer.schedule(time*100);
			
			assert(previewHandler == null);
			previewHandler = Event.addNativePreviewHandler(this);
			updateDisplay();
		}
		
		updateDisplay();
	}

	public void restart() {
		Log.debug("method restart");
		outer.clear();
		inner = null;
		statusbar = null;
		upperwindow = null;
		v = null;
		resize.removeHandler();
		cpu = null;
		cpu = new ZCPU(this);
		cpu.loadStory(story);
	}

	public void scrollWindow(int lines) {
		Log.debug("Unimplemented method scrollWindow");

	}

	public void setColor(int fg, int bg) {
		Log.debug("method setColor with foreground: "+fg+" and background: "+bg);
		if ( fg > 1 ) {
			curFgColor = fg;
		} else if ( fg == 1 ) {
			curFgColor = defaultFgColor; // default = black
		}
		if ( bg > 1 ) {
			curBgColor = bg;
		} else if ( bg == 1 ) {
			curBgColor = defaultBgColor; // default = white
		}
	}

	public void setCurrentWindow(int window) {
		Log.debug("method setCurrentWindow with window "+window);
		curWindow = window;
	}

	public void setCursorPosition(int x, int y) {
		Log.debug("method setCursorPosition to x="+x+" y="+y);
		if ( curWindow == 0 ) {
			Log.error("Set Cursor for window 0?");
		} else {
			reallySplitScreen();
			upperwindow.setCursor(x, y);
		}
	}

	public void setFont(int font) {
		Log.debug("Unimplemented method setFont to "+font);
		//curFont = font;
	}

	public void setTerminatingCharacters(Vector<Integer> chars) {
		String out = "";
		for ( Integer c : chars ) {
			out += c.toString();
			out += ", ";
		}
		Log.debug("method setTerminatingCharacters with chars: " + out +".");

		termChars = chars;
	}

	public void setTextStyle(int style) {
		Log.debug("method setTextStyle with style: "+style);
		curStyle = style;
	}

	public void showStatusBar(String s, int a, int b, boolean flag) {

		if (statusbar == null) {
			statusbar = new HorizontalPanel();
			statusbar.setStyleName("statusBar");
			statusbar.add(new Label());
			statusbar.add(new Label());
			statusbar.setWidth("100%");
			statusbar.getWidget(0).setStyleName("statusBar-left");
			statusbar.getWidget(1).setStyleName("statusBar-right");
			outer.add(statusbar, DockPanel.NORTH);
			
			correctWindowSizes();
		}

		// String text = s+ " ";
		String text = "";
		if (flag) {
			text += " Time: " + a + ":";
			if (b < 10) {
				text += "0";
			}
			text += b;
		} else {
			text += "Score: " + a + " Turns: " + b;
		}
		Log.debug("method showStatusbar: " + text);
		// statusbar.setText(text);

		Label l1 = (Label) statusbar.getWidget(0);
		Label l2 = (Label) statusbar.getWidget(1);

		l1.setText(s);
		l2.setText(text);

	}
	
	
	
	/* private native String fixSpaces(String name) /-{

	  // ...implemented with JavaScript
	  var re = /(\w)&nbsp;(\w)/;
	  return name.replace(re, '$1 $2');

	}-/; */
	
	public Vector<Integer> getTermChars() {
		return termChars;
	}


	static public String htmlEscape(String s) {
		String text = s;
		text = text.replace("&", "&amp;");
		text = text.replace("<", "&lt;");
		text = text.replace(">", "&gt;");
		//text = text.replace(" ", "&nbsp;");
		text = text.replace("  ", " &nbsp;");
		text = text.replace("$@U@$", "&#"); // unicode marker
		//text = fixSpaces(text);
		text = text.replace("\n", "<br>");

		return text;
	}

	public void showString(String s) {
		Log.debug("method showString (length "+s.length()+"): " + s);
		reallySplitScreen();
		
		if ( curWindow == 0 ) {
			String text = s;
			text = htmlEscape(text);
			Log.debug("method showString escaped " + text);
			text = styleFormat(text, curStyle, curFgColor, curBgColor);
			if ( lastLineBase == null || lastLineBase.equals("")) {
				lastLineBase = text;
				lastLine = new HTML();
				v.add(lastLine);
			} else {
				lastLineBase += text;
			}
			
			/* if ( lastLineBase.equals("<br>")) {
				Log.debug("Line is a newline");
				lastLineBase = "";
				lastLine.setHTML("&nbsp");
				lastLine = new HTML();
				v.add(lastLine);
			} */
			
			lastLineBase = lastLineBase.replaceAll("<br></a>", "</a><br>");
			
			int pos = lastLineBase.lastIndexOf("<br>");
			if ( pos > 1 ) {
				Log.debug("We have a break at position "+pos+" with length "+lastLineBase.length());
				Log.debug("Full text: "+lastLineBase);
				String beforeline = lastLineBase.substring(0, pos+4);
				Log.debug("Beforeline: "+beforeline);
				lastLine.setHTML(beforeline);
				if ( lastLineBase.length() > pos+4) {
					Log.debug("Long enough for split");
					lastLineBase = lastLineBase.substring(pos+4);
					Log.debug("Base after: "+lastLineBase);
				} else {
					Log.debug("Too short for split");
					lastLineBase = "";
				}
				lastLine = new HTML("&nbsp");
				v.add(lastLine);
			}
			
			lastLine.setHTML(lastLineBase);
			
			// RootPanel.get("bottom").getElement().scrollIntoView();
		} else if ( curWindow == 1 ) {
			upperwindow.showString(s, curStyle, curFgColor, curBgColor);
		} else {
			Log.debug("Show string did not understand.");
		}
		inner.scrollToBottom(); 

	}

	public void splitScreen(int lines) {
		Log.debug("implemented method splitScreen with " + lines + " lines");
		splitNewWindow = lines;
	}
	
	public void reallySplitScreen() {
		int lines = splitNewWindow;
		splitNewWindow = -10;
		if ( lines == -10 )
			return;
		Log.debug("Delayed splitscreen with "+lines+" lines executed");
		{
			Log.debug("Widget count: "+v.getWidgetCount()+" lines: "+lines);
			if ( v.getWidgetCount() <= 1 ) {
				// no text as of yet...
				//Log.debug("Writing to lower window: "+upperwindow.getLines(lines));
				HTML line = new HTML(upperwindow.getLines(lines));
				v.add(line);
			}
			//lastLineBase = "";
			if ( upperwindow != null ) {
				
				if ( lines == upperwindow.getNumlines() ) {
					return;
				}
				outer.remove(upperwindow); // remove the old upper window
				upperwindow = null; 
			}
			
			upperwindow = new UpperWindow(lines);
			outer.add(upperwindow, DockPanel.NORTH);
		}
		
		correctWindowSizes();
	}
			
	public boolean HandleReadCharEvent(NativePreviewEvent preview) {
		NativeEvent event = preview.getNativeEvent();

		if (event.getMetaKey() || event.getCtrlKey() || event.getAltKey()) {
			// we do not handle this, give it to the browser
			return false;
		}
		
		// backspace and keys is handled via down-event, everything else via keypress:
		if ( preview.getTypeInt() == Event.ONKEYDOWN && ( event.getKeyCode() != KEY_BACKSPACE && event.getKeyCode() != KEY_LEFT && event.getKeyCode() != KEY_RIGHT && event.getKeyCode() != KEY_UP && event.getKeyCode() != KEY_DOWN ) ) {
			return false;
		} else if ( preview.getTypeInt() == Event.ONKEYPRESS && ( event.getKeyCode() == KEY_BACKSPACE || event.getKeyCode() == KEY_LEFT || event.getKeyCode() == KEY_RIGHT || event.getKeyCode() == KEY_UP || event.getKeyCode() == KEY_DOWN)) {
			return false;
		}

				
		int key = event.getKeyCode();
		Character keyCode = (char) event.getKeyCode();
		if ( keyCode == 0 ) {
			keyCode = (char) event.getCharCode();
		}
		Log.debug("KeyCode "+key);
		
		if ( key == KEY_UP ) {
			keyCode = 129;
			Log.debug("up");
		} else if ( key == KEY_DOWN ) {
			keyCode = 130;
		} else if ( key == KEY_LEFT ) {
			keyCode = 131;
		} else if ( key == KEY_RIGHT ) {
			keyCode = 132;
		}
				
		if ( readMode == READMODES.READCHAR_TIMED ) {
			readTimer.cancel(); // remove unneeded timer.
			readTimer = null;
		}

		final int lastkey = keyCode;
		Log.debug("trying to continue execution...1");
		previewHandler.removeHandler();
		previewHandler = null;
		readMode = READMODES.ERROR;
		Log.debug("Queueing");
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				Log.debug("trying to continue execution...2");
				getCpu().zop_read_char_continue(lastkey);
				Log.debug("trying to continue execution...3");
				Machine.get().continueExecution();
			}
		});

		return true;
	}	
	
	public String getStoryUid() {
		return storyUid;
	}


	public void onPreviewNativeEvent(NativePreviewEvent event) {
		boolean consume = false;
		
		//Log.debug("Got event");
		
		if (event.getTypeInt() == Event.ONKEYPRESS || event.getTypeInt() == Event.ONKEYDOWN ) {
			if ( readMode == READMODES.READLINE || readMode == READMODES.READLINE_TIMED ) {
				consume = commline.HandleReadLineEvent(event, this);
			} else if ( readMode == READMODES.READCHAR || readMode == READMODES.READCHAR_TIMED ) {
				consume =  HandleReadCharEvent(event);
			} else {
				fatal("Unknown readmode");
			}
		} 
		
		if ( consume ) {
			event.cancel();
		} else {
			
		}

	}



		
}
