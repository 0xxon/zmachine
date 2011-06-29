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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TabPanel;

import de.lilawelt.zmachine.client.storage.GearsAdapter;
import de.lilawelt.zmachine.client.storage.GearsAdapterImpl;
import de.lilawelt.zmachine.client.storage.ServerAdapter;

public class FileSelector extends PopupPanel {
	
	public FileSelector(String suggestedName, String saveData, AsyncCallback<String> callback, boolean save) {
		super(false); // do not auto-hide if user clicks outside
		Log.debug("FileSelector for save");
		
		GearsAdapterImpl g  = GearsAdapter.get();
		Log.debug("Created Gears Interface");
		ServerAdapter p = new ServerAdapter();
		
		if ( callback == null ) 
			Log.debug("null callback in FileSelector 2");

		FileSelectorImpl gearsSelector = new FileSelectorImpl(suggestedName, saveData, callback, g, save, this);
		Log.debug("Created Gears FileSelectorImpl");
		FileSelectorImpl phpSelector = new FileSelectorImpl(suggestedName, saveData, callback, p, save, this);
		
		
		TabPanel choices = new TabPanel();
		choices.add(phpSelector, "Server");
		choices.add(gearsSelector, "Local");
		choices.selectTab(0);
		Log.debug("Created TabPabel");
		
	    setWidget(choices);
	}

}
