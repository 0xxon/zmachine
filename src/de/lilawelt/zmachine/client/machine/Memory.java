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

package de.lilawelt.zmachine.client.machine;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

import de.lilawelt.zmachine.client.Machine;

public class Memory extends ZMemory {	
	private static final long serialVersionUID = 1L;

	// The initialize routine sets things up and loads a game
    // into memory.  It is passed the ZUserInterface object
    // for this ZMachine and the filename of the story-file.
    public void initialize(ZUserInterface ui,String storyFile)
    {
    	Log.debug("Trying to load story from " + storyFile);
    	
    	final PopupPanel p = new PopupPanel();
    	p.add(new HTML("Please wait, loading game..."));
    	p.center();
    	p.show();
    	
    	zui = ui;
    	RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(storyFile));
    	
    	try {
    	  builder.sendRequest(null, new RequestCallback() {
    	    public void onError(Request request, Throwable exception) {
    	       // Couldn't connect to server (could be timeout, SOP violation, etc.)
                Log.debug("Server connect failed");
                zui.fatal("Could not connect to server: "+ exception.getMessage());
    	    }

    	    public void onResponseReceived(Request request, Response response) {
    	      if (200 == response.getStatusCode()) {
    	    	  String story = response.getText();
    	    	  Log.debug("Text length: "+story.length());
    	    	  
    	    	  //String[] tokens = story.split(" ");
    	    	  
    	    	  dataLength = story.length();
    	    	   Log.debug("Initial length: "+dataLength);
    	    	  data = new byte[dataLength];
    	    	  int count = 0;
    	    	  for ( int i = 0; i < dataLength-3; i++ ) {
    	    		  char e1 = story.charAt(i++);
    	    		  //if ( i < 100 )
    	    		  //Log.debug("Charvalue at pos "+i+": "+e1);
    	    		  if ( e1 == '\n' || e1 == '\r') {
    	    			  e1 = story.charAt(i++);
        	    		  if ( e1 == '\n' || e1 == '\r') {
        	    			  e1 = story.charAt(i++);
        	    		  }
    	    		  }
    	    		  e1 = (char) tab.indexOf(e1);
    	    		  char e2 = story.charAt(i++); 
    	    		  if ( e2 == '\n') {
    	    			  e2 = story.charAt(i++);
    	    		  }
    	    		  e2 = (char) tab.indexOf(e2);
    	    		  char e3 = story.charAt(i++);
    	    		  if ( e3 == '\n') {
    	    			  e3 = story.charAt(i++);
    	    		  }
    	    		  e3 = (char) tab.indexOf(e3);
    	    		  char e4 = story.charAt(i);
    	    		  if ( e4 == '\n') {
    	    			  e4 = story.charAt(++i);
    	    		  }
    	    		  e4 = (char) tab.indexOf(e4);

    	    		  
    	    		  //Log.debug("Values: "+e1+" "+e2+" "+e3+" "+e4);

    	    		  byte c1 = (byte) ((e1 << 2) + (e2 >> 4));
    	    	      byte c2 = (byte) (((e2 & 15) << 4) + (e3 >> 2));
    	    	      byte c3 = (byte) (((e3 & 3) << 6) + e4);

    	    	      //c1 =  (byte) ( e2 >> 4);
    	    	      
    	    		  data[count++] = c1;
    	    	      if (e3 != 64)
    	    	    	  data[count++] += c2;
    	    	      if (e4 != 64)
    	    	    	  data[count++] += c3;
    	    		  
    	    	  } 
    	    	  /* data = new byte[dataLength];
    	    	  int count = 0;
    	    	  for ( int i = 0; i < dataLength; i++ ) {
    	    		  char c = story.charAt(i);
    	    		  if ( (int) c == 127 ) {
    	    			  i++;
    	    			  if ( (int) story.charAt(i) == 127 ) {
    	    				  i++;
    	    				  data[count] = 127;
    	    				  data[count] += 127;
    	    			  } else {
    	    				  data[count] = 127;
    	    			  }
    	    			  data[count] += story.charAt(i);
    	    		  }  else {
    	    			  data[count] = (byte) c;
    	    		  }
    	    		  //Log.debug("Text item: "+(int)c);
    	    		  count++;
    	    	  }  */
    	    	  
    	    	  dataLength = count;
    	    	  Log.debug("Loaded "+count+" bytes of story.");
    	    	  /* Log.debug("byte0: "+fetchByte(0));
    	    	  Log.debug("byte1: "+fetchByte(1));
    	    	  for ( int i = 0; i < dataLength; i++ ) {
					if ( i != fetchByte(i))
    	    		  Log.debug("Data "+i+": "+fetchByte(i));
    	    	  } */
    	    	  p.hide();
    	    	  Machine.get().start();
    	      } else {
                  Log.debug("Server returned error on load: " + response.getText());
                  zui.fatal("Server returned error on load: " + response.getText());
    	      }
    	    }

    	  });
    	} catch (RequestException e) {
    	  // Couldn't connect to server   
            zui.fatal("I/O error loading storyfile.");
    	}
    	
    	Log.debug("Initialized memory");
    	
    }


}
