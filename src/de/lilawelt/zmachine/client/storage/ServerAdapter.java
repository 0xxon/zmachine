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

import de.lilawelt.zmachine.client.Machine;


public class ServerAdapter implements StorageServiceAsync {

	public void getSavedGames(String GameUid,
			AsyncCallback<List<String>> callback) {
		Machine.RpcRequest().getSavedGames(GameUid, callback);
	}

	public void restore(String SaveName, String GameUid,
			AsyncCallback<String> callback) {
		Machine.RpcRequest().restore(SaveName, GameUid, callback);
		
	}

	public void save(String SaveName, String GameUid, String SaveData,
			AsyncCallback<String> callback) {
		Machine.RpcRequest().save(SaveName, GameUid, SaveData, callback);
	}

}
