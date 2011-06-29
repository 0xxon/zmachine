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

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import de.lilawelt.zmachine.client.storage.StorageException;
import de.lilawelt.zmachine.client.storage.StorageServiceAsync;

public class FileSelectorImpl extends Composite {
	
	VerticalPanel p;
	TextBox name;
    ListBox lb;
    StorageServiceAsync storage;
    Button ok;
    Button cancel;
    HorizontalPanel hp;
    String suggested;
    String data;
    final AsyncCallback<String> callback;
    final PopupPanel root;
    boolean savePanel;
	
	public FileSelectorImpl(String suggestedName, String saveData, AsyncCallback<String> callback, StorageServiceAsync s, boolean save, PopupPanel rootPanel) {
		p = new VerticalPanel();
		
		suggested = suggestedName;
		data = saveData;
		this.callback = callback;
		this.root = rootPanel;
		storage = s;
		savePanel = save;
		
		Init();
	}		
		
	private void Init() {
		
		cancel = new Button("Cancel", new ClickHandler() {
		      public void onClick(ClickEvent event) {
		    	  Log.debug("Pressed cancel");
		    	  if ( callback == null )
		    		  Log.debug("Callback is null?!?");
		    	  StorageException e = new StorageException("Pressed cancel", false);
		    	  callback.onFailure(e);
		    	  root.hide();		    	  
		        }		      
		      });
		
		
		p.add(new HTML("Please wait, loading..."));
		root.center();
		
		AsyncCallback<List<String>> l = new AsyncCallback<List<String>>() {

			public void onFailure(final Throwable caught) {
				
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						public void execute() {		
							p.clear();
							p.add(new HTML("An error occured: "+caught.getMessage()));
							p.add(cancel);
							root.center();
						}
		    	  });

			}

			public void onSuccess(List<String> result) {
				p.clear();
				Initialize();
				
				for (String name : result ) {
					Log.debug("I am adding: "+name);
					lb.addItem(name);
				}				
				
				root.center();
			}
		};
		
		storage.getSavedGames(Machine.get().getMi().getStoryUid(), l);

		
		
		initWidget(p);
	}
	
	private void Initialize() {
		name = new TextBox();
		lb = new ListBox();
		hp = new HorizontalPanel();
		
		if ( suggested != null ) {
			name.setText(suggested);
		}
		
		if ( savePanel ) {
			ok = new Button("Save", new ClickHandler() {
				public void onClick(ClickEvent event) {

					Log.debug("pressed ok");
					storage.save(name.getText(), Machine.get().getMi().getStoryUid(), data, callback);
					Log.debug("Sent to storage");
					root.hide();
				}

			});

		} else {
			
			ok = new Button("Restore", new ClickHandler() {
				public void onClick(ClickEvent event) {

					Log.debug("pressed ok");
					storage.restore(lb.getItemText(lb.getSelectedIndex()), Machine.get().getMi().getStoryUid(), callback);
					root.hide();
					
				}

			});

		}
		hp.add(ok);
		hp.add(cancel);
		
		lb.setWidth("100px");
		
		lb.setVisibleItemCount(5);
				
		 lb.addChangeHandler(new ChangeHandler(){
			    public void onChange(ChangeEvent event){
			      // Get the index of the selected item
			       int itemSelected = lb.getSelectedIndex();

			      // Get the string value of the item that has been selected
			       name.setText(lb.getValue(itemSelected));

			    }
			}); 
		
		if ( callback != null )
			p.add(new Label("Select old savegame or enter savegame name:"));
		else 
			p.add(new Label("Select game to restore"));
		
		p.add(lb);
		if ( callback != null )
			p.add(name);
		p.add(hp);
		
	}

}
