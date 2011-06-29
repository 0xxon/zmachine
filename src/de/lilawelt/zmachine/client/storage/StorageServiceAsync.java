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

package de.lilawelt.zmachine.client.storage;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * This interface brings an abstraction to saving to our nice little project
 * each save-type simply implements this interface and then can be sent
 * away between each of the subroutines.
 * 
 * Yay.
 * 
 * @author johanna
 *
 */
public interface StorageServiceAsync {
		
	//public boolean Initialize() throws StorageException; // initialize - true on success.
	
	// get list of games already saved for our current identifier
	public void getSavedGames(String GameUid, AsyncCallback<List<String>> callback);
			
	public void save(String SaveName, String GameUid, String SaveData, AsyncCallback<String> callback); 

	public void restore(String SaveName, String GameUid, AsyncCallback<String> callback);
	
}
