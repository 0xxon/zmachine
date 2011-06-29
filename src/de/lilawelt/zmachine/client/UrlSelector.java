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
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UrlSelector extends Composite {
	
	private VerticalPanel main;
	private TextBox storylist;
	
	UrlSelector() {
		main = new VerticalPanel();
		
		main.add(new Label("Please insert the story url you want to play"));
		
		storylist = new TextBox();
		storylist.setWidth("400px");
		
		Button ok = new Button("ok");
		ok.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				String getlocation = (GWT.getModuleBaseURL()+"/Proxy?url="+URL.encode(storylist.getText()));
				Log.debug("Getting from: "+getlocation);
				Machine.get().loadStory(getlocation, ""); // not good, all savegames in one place.
			}			
			
		});
		
		main.add(storylist);
		main.add(ok);
				
		initWidget(main);
		
	}

}
