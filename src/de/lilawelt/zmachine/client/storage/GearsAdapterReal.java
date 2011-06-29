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

import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.GearsException;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.DatabaseException;
import com.google.gwt.gears.client.database.ResultSet;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The class, where we use gears to store all our stuff.
 * 
 * @author johanna
 *
 */
public class GearsAdapterReal extends GearsAdapterImpl {
	private Database db;
	boolean initialized = false;
	
	public boolean Initialize() throws StorageException {
		if ( initialized )
			return true;
		
		Factory f = Factory.getInstance();
		if ( f == null ) 
			throw new StorageException("Gears could not be initialized");
		
		if ( ( !f.hasPermission() ) && ( !f.getPermission() ) )
			throw new StorageException("Sorry, this site may not access your gears installation");	
		
		Log.debug("Trying gears init...");
		
	    // Create the database if it doesn't exist.
	    try {
	      db = f.createDatabase();
	      db.open("textadventure-saves");
	      // The 'int' type will store up to 8 byte ints depending on the magnitude of the 
	      // value added.
	      db.execute("create table if not exists saves (savename text, gameuid text, savedate date, savedata blob)");
	    } catch (GearsException e) {
			throw new StorageException("Sorry, an error was encountered while initializing gears: "+e.getMessage());
	    }
	    
	    Log.debug("Dine with gears init");
	    
	    initialized = true;
	    return true;
	}
	
	public void save(String SaveName, String GameUid, String SaveData,
			AsyncCallback<String> callback) {
		try {
			db.execute("delete from saves where savename = ? AND gameUid = ?", SaveName, GameUid);
			db.execute("insert into saves (savename, gameuid, savedate, savedata) values (?, ?, DATETIME('NOW'), ?)", SaveName, GameUid, SaveData);
			callback.onSuccess("");
		} catch (DatabaseException e) {
			callback.onFailure(new Throwable(e.toString()));
		}
	}
	
	public void restore(String SaveName, String GameUid,
			AsyncCallback<String> callback) {
		Log.debug("Got restore with saveName: "+SaveName+" and GameUid: "+GameUid);
		try {
			ResultSet rs = db.execute("Select savedata from saves where savename = ? AND gameuid = ?", SaveName, GameUid);
			if ( !rs.isValidRow() ) {
				Window.alert("no such save found");
				callback.onFailure(new Throwable("no such save found"));
			} else {
				callback.onSuccess(rs.getFieldAsString(0));
			}
			
		} catch (DatabaseException e) {
			callback.onFailure(new Throwable(e.toString()));
		}
	}

	public void getSavedGames(String GameUid, AsyncCallback<List<String>> callback) {
		List<String> SavedGames = new LinkedList<String>();
		
		if (!initialized) {
			try {
				Initialize();
			} catch (StorageException e) {
				callback.onFailure(e);
				return;
			}
		}
		
		try {
			ResultSet rs = db.execute("Select savename from saves where gameuid = ?", GameUid);
			for (int i = 0; rs.isValidRow(); ++i, rs.next()) {
				SavedGames.add(rs.getFieldAsString(0));
				Log.debug("Added saved game: "+rs.getFieldAsString(0));
			}
			Log.debug("Firing saved-games callback");
			callback.onSuccess(SavedGames);
		} catch (DatabaseException e) {
			callback.onFailure(new Throwable(e.toString()));
		}
		
	}

}
