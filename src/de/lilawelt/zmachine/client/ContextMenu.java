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

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

public class ContextMenu implements NativePreviewHandler, CloseHandler<PopupPanel> {
	private final PopupPanel p = new PopupPanel(true);
	private boolean active = false;
	private boolean initialized = false;

	public void onPreviewNativeEvent(NativePreviewEvent preview) {
		NativeEvent event = preview.getNativeEvent();		
		if ( event.getButton() != NativeEvent.BUTTON_RIGHT ) {
			return;
		}
		
		if ( active )
			return;		
		if ( !initialized ) {
			createPopupMenu();
			initialized = true;
			p.addCloseHandler(this);
		}

		int x = event.getClientX();
		int y = event.getClientY();
		p.setPopupPosition(x, y);
		p.show();
		active = true;

		event.preventDefault();
		preview.cancel();
	}

	private void createPopupMenu() {

		Command close = new Command() {
			public void execute() {
				p.hide();
			}
		};
		
		Command contact = new Command() {
			public void execute() {
				PopupPanel contact = new PopupPanel(true);
				p.hide();
				HTML text = new HTML("Contact:<br><br>Johanna Amann<br>Seehang 2a<br>82335 Berg<br><br>johanna@0xxon.net<br><a href='http://z-machine.lilawelt.de' target='_blank'>Z-Machine.lilawelt.de</a>");
				contact.add(text);
				contact.center();
				contact.show();
			}
		};
		
		Command version = new Command() {
			public void execute() {
				PopupPanel version = new PopupPanel(true);
				p.hide();
				HTML text = new HTML("Version 0.3, all rights reserved<br><br>" +
								"This interpreter is based on ZAX by<br>Matt Kimmel which was released under the<br>MIT LICENSE. See license terms <a href='http://z-machine.lilawelt.de/stuff/zax-LICENSE.txt' target='_blank'>here</a>.");
				version.add(text);
				version.center();
				version.show();
			}
		};


		MenuBar popupMenuBar = new MenuBar(true);
		MenuItem versionItem = new MenuItem("About", true, version);
		MenuItem contactItem = new MenuItem("Contact", true, contact);
		MenuItem closeItem = new MenuItem("Close", true, close);

		popupMenuBar.addItem(versionItem);
		popupMenuBar.addItem(contactItem);
		popupMenuBar.addItem(closeItem);

		popupMenuBar.setVisible(true);
		p.add(popupMenuBar);

	}
	
	public void onClose(CloseEvent<PopupPanel> event) {
		active = false;
	}

}
