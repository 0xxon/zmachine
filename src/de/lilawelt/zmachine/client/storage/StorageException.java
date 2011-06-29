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

import com.google.gwt.user.client.rpc.IsSerializable;

public class StorageException extends Exception implements IsSerializable {
	private boolean showMessage;
	
	public StorageException() {		
		showMessage = false;
	}

	public StorageException(String message) {
		super(message);
		showMessage = true;
	}
	
	public StorageException(String message, boolean showMessage) {
		super(message);
		this.showMessage = showMessage;
	}
	
	public boolean ShowMessage() {
		return showMessage;
	}
	
	private static final long serialVersionUID = 1L;
}
