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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;

public class OfflineMenuImpl extends Composite {
	DockPanel p = new DockPanel();
	
	public OfflineMenuImpl() {
		initWidget(p);
	}
	
	public void initialize() {
		HTML main = new HTML("Sorry, internal error.");

		p.add(main, DockPanel.CENTER);
	}
	

}
