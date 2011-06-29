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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.GearsException;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.ResultSet;
import com.google.gwt.gears.client.localserver.LocalServer;
import com.google.gwt.gears.client.localserver.ManagedResourceStore;
import com.google.gwt.gears.offline.client.Offline;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;

import de.lilawelt.zmachine.client.Machine;

public class OfflineMenuImplReal extends OfflineMenuImpl {
	Label statusLabel = new Label();
	boolean showing = false;
	Factory f;
	
	public OfflineMenuImplReal() {
		super();
	}
	
	@Override
	public void initialize() {
		p.add(statusLabel, DockPanel.CENTER);
		
		f = Factory.getInstance();
		if ( f == null ) {
			statusLabel.setText("May not access gears");
			return;
		}
		
		if ( ( !f.hasPermission() ) && ( !f.getPermission() ) ) {
			statusLabel.setText("May not access gears");
			return;
		}
		
	    // Draw a different interface if the application can be served offline.
		try {
			LocalServer server = Factory.getInstance().createLocalServer();
			// This check to see if the host page can be served locally
			if (server.canServeLocally("/zmachine/hosted.html")) {
				createManagedResourceStoreOffline();
				createManagedResourceStore();
				showGames();
			} else {
				createManagedResourceStoreOffline();
				createManagedResourceStore();
			}
		} catch (GearsException ex) {
			statusLabel.setText("Fatal error: "+ex.getMessage());
			return;
		}

	}
	
	public void showGames() {
		if ( showing ) {
			return;
		}
		
		showing = true;
		
		p.remove(statusLabel);
		
		Label title = new Label("Available games:");
		p.add(title, DockPanel.NORTH);
		
		HTMLTable h = new FlexTable();
		h.setText(0, 0, "Game Name:");
		
		p.add(h, DockPanel.NORTH);
		
		try {
			Database db = f.createDatabase();
			db.open("textadventure-saves");
			
			ResultSet rs = db.execute("Select gamename, gameuid from offlinegames;");
			for (int i = 0; rs.isValidRow(); ++i, rs.next()) {
				
				final String name = rs.getFieldAsString(0);
				final String uid = rs.getFieldAsString(1);
				
				Hyperlink link = new Hyperlink();
				link.setText(rs.getFieldAsString(0));
				link.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						Machine.get().loadStory("/games/"+name+".sto", uid);
					} });
				h.setWidget(i+1, 0, link);
			}
		} catch (Exception e) {
			Window.alert("Error: "+e.getMessage());
		}
		
	}

	private void createManagedResourceStoreOffline() {
		ManagedResourceStore m = f.createLocalServer().createManagedStore("offline");
		m.setManifestUrl("/offline.manifest");
		m.checkForUpdate();
	}

	private void createManagedResourceStore() {
		try {
			final ManagedResourceStore managedResourceStore = Offline.getManagedResourceStore();

			new Timer() {
				final String oldVersion = managedResourceStore.getCurrentVersion();
				String transferringData = "Transferring data";

				@Override
				public void run() {
					switch (managedResourceStore.getUpdateStatus()) {
					case ManagedResourceStore.UPDATE_OK:
						if (managedResourceStore.getCurrentVersion().equals(oldVersion)) {
							statusLabel.setText("No update was available.");
						} else {
							statusLabel.setText("Download successfull.");
							showGames();
						}
						break;
					case ManagedResourceStore.UPDATE_CHECKING:
					case ManagedResourceStore.UPDATE_DOWNLOADING:
						transferringData += ".";
						statusLabel.setText(transferringData);
						schedule(500);
						break;
					case ManagedResourceStore.UPDATE_FAILED:
						statusLabel.setText(managedResourceStore.getLastErrorMessage());
						break;
					}
				}
			}.schedule(500);

		} catch (GearsException e) {
			statusLabel.setText("");
			Window.alert(e.getMessage());
		}
	}

}
