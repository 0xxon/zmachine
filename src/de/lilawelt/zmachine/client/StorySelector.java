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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class StorySelector extends Composite implements ChangeHandler {
	
	private VerticalPanel main;
	private String[] storys = {"====", "delusions", "jigsaw", "varicella", "gussdeath", "curses", "moonglow", "lost", "abent"};
	private String[] uids = {"", "cd27aca506af18126a017a9276e87f0c945438a5",
			"7d1b600427282835be083360fe2a1241ec439e1c",
			"3c452acb6f7373243ab7b610362b2556cfa1fd56",
			"dc2bddb88cd4f8fbfa1db2a7672f4568bb283f3b",
			"a1b96df5b58ceb173fd46d5ec358d2b7f4a610ba",
			"19dd167ce9d7444896544c327b2917ca99c44139",
			"602711a4c7bd913ec39fa657178a811e6a66b9db",
			"2fc0f5e67fa95ba48c7f9ccd7058d55bf5d7c000"
	};
	
	private ListBox storylist;
	
	StorySelector() {
		main = new VerticalPanel();
		
		main.add(new Label("Please select the story, you want to play"));
		
		storylist = new ListBox();
		
		for ( String story : storys ) {
			storylist.addItem(story);
		}
		
		main.add(storylist);
		
		storylist.addChangeHandler(this);
		
		initWidget(main);
		
	}

	public void onChange(ChangeEvent event) {
		if ( storylist.getSelectedIndex() > 0 ) {
			Machine.get().loadStory("/basegames/"+storylist.getItemText(storylist.getSelectedIndex()), uids[storylist.getSelectedIndex()]);
		}

	}

}
