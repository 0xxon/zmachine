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

package de.lilawelt.zmachine.client.offline;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.localserver.LocalServer;
import com.google.gwt.gears.client.localserver.ResourceStore;
import com.google.gwt.gears.client.localserver.ResourceStoreUrlCaptureHandler;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

public class Downloader implements EntryPoint {

	private HTML status = new HTML();
	
	public void onModuleLoad() {
		status.setHTML("initializing...");
		
		RootPanel.get().add(status);
		
		Dictionary parameters = Dictionary.getDictionary("Parameters");

		String url = parameters.get("game");
		if ( url == null || url.equals("")) {
			addStatusLine("Internal error");
			return;
		}
		
		doLoad("/games/"+url+".sto");
	}
	
	private void addStatusLine(String line) {
		status.setHTML(status.getHTML()+"<br>"+line);
	}	

	private void doLoad(String url) {
		LocalServer server = Factory.getInstance().createLocalServer();

		addStatusLine("Starting download...");
		ResourceStoreUrlCaptureHandler callback = new ResourceStoreUrlCaptureHandler() {

			public void onCapture(ResourceStoreUrlCaptureEvent event) {
				if ( event.isSuccess() ) {
					addStatusLine("Download finished.");
				} else {
					addStatusLine("Download failed.");
				}

			}
		};


		final ResourceStore resource = server.createStore("games");

		resource.capture(callback, url);


	}
	
}
