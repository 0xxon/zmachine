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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;

import de.lilawelt.zmachine.client.offline.OfflineMenu;
import de.lilawelt.zmachine.client.offline.OfflineMenuImpl;
import de.lilawelt.zmachine.client.storage.StorageService;
import de.lilawelt.zmachine.client.storage.StorageServiceAsync;


public class Machine implements EntryPoint {
	private static Machine singleton;
	//private DockPanel outer;
	private MachineInterface mi;
	private Composite s = null;
	private RootPanel r;
	
	//private Label bottom;
	
	public void onModuleLoad() {
		singleton = this;
				
		Dictionary parameters = Dictionary.getDictionary("Parameters");
		String selector = parameters.get("selector");
		
		
		Element loading = DOM.getElementById("loading");
		if ( loading != null ) {
			DOM.removeChild(RootPanel.getBodyElement(), loading);
		}	
		
		
		/* Does not work in internet explorer.
		try {
			String base = parameters.get("base");
			r = RootPanel.get(base);
		} catch (Exception e) {
			r = RootPanel.get();
		} */
		
		
		r = RootPanel.get();
		if ( selector.equals("base") ) {
			s = new StorySelector();
			r.add(s);
		} else if ( selector.equals("url") ){
			String url = parameters.get("url");
			String storyuid = parameters.get("uid");
			loadStory("/Proxy?url="+url, storyuid);
		} else if ( selector.equals("urlselector") ) {
			s = new UrlSelector();
			r.add(s);
		} else if ( selector.equals("playstory") ) {
			String storyname = parameters.get("name"); 
			String storyuid = parameters.get("uid");
			Log.debug("Loading: "+storyname);
			loadStory("/store/"+storyname+".sto", storyuid);
		} else if ( selector.equals("offline")) {
			OfflineMenuImpl menu = OfflineMenu.get();
			menu.initialize();
			s = menu;
			r.add(s);
		}
		
		ContextMenu c = new ContextMenu();
		Event.addNativePreviewHandler(c);
	}
		
	public void loadStory(String story, String storyUid) {
		
		if ( s != null )
			r.remove(s);
		s = null;
				
		Log.debug("Ok, machine interface is starting up");
		Log.debug("Story: "+story+" uid: "+storyUid);
		mi = new MachineInterface();
		
		r.add(mi);
				
		mi.startStory(story, storyUid);
	}
	
	public void start() {
		mi.getCpu().initialize();
		mi.getCpu().setupRun();
		continueExecution();
	}

	public void continueExecution() {
		Log.debug("Continuing execution...");
		//try {
			mi.getCpu().decodeLoop();
		//} catch (BreakException e) {
		//	Log.debug("Caught error "+e.toString());
		//	Window.alert("Sorry, a fatal error occured");
		//}
	}
	
	public static Machine get() {
		return singleton;
	}


	public MachineInterface getMi() {
		return mi;
	}	
	
	  public static StorageServiceAsync RpcRequest() {
		  // set up the rpc service
		  StorageServiceAsync s =
			     (StorageServiceAsync) GWT.create(StorageService.class);
		  
	      ServiceDefTarget target = (ServiceDefTarget) s;
	      String moduleRelativeURL = GWT.getModuleBaseURL() + "../StorageService";
	      target.setServiceEntryPoint(moduleRelativeURL);
		  return s;
	  }

	
}
