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
// Original license of the ZAX-Sourcefile:
/**
 * Copyright (c) 2008 Matthew E. Kimmel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.lilawelt.zmachine.client.machine;

import java.io.Serializable;

public abstract class ZMemory extends Object implements Serializable {
	private static final long serialVersionUID = 1L;
	protected ZUserInterface zui;
    protected byte[] data;
    protected int dataLength;
    protected static final String tab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";  

    abstract public void initialize(ZUserInterface ui,String storyFile);
    
    // Fetch a byte from the specified address
    int fetchByte(int addr)
    {
        if (addr > (dataLength - 1))
            zui.fatal("Memory fault: address " + addr);
        int i = (data[addr] & 0xff);
        return i;
    }

    // Store a byte at the specified address
    void putByte(int addr,int b)
    {
        if (addr > (dataLength - 1))
            zui.fatal("Memory fault: address " + addr);
        data[addr] = (byte)(b & 0xff);
    }

    // Fetch a word from the specified address
    int fetchWord(int addr)
    {
        int i;

        if (addr > (dataLength - 1))
            zui.fatal("Memory fault: address " + addr);
        i = (((data[addr] << 8) | (data[addr+1] & 0xff)) & 0xffff);
        return i;
    }

    // Store a word at the specified address
    void putWord(int addr,int w)
    {
        if (addr > (dataLength - 1))
            zui.fatal("Memory fault: address " + addr);
        data[addr] = (byte)((w >> 8) & 0xff);
        data[addr+1] = (byte)(w & 0xff);
    }

	// Dump the specified amount of memory, starting at the specified address,
	// to the specified DataOutputStream.
	StringBuffer dumpMemory(int addr,int len) 
	{
		StringBuffer out = new StringBuffer();
		for ( int i = 0; i < len; i++) {
			int b = fetchByte(addr+i);
			if ( b < 127 ) {
				out.append((char) b);
			} else if ( b < 254 ) {
				out.append((char) 127);
				b -= 127;
				out.append((char) b);
			} else {
				out.append((char) 127);
				out.append((char) 127);
				b -= 127;
				b -= 127;
				out.append((char) b);
			}
		}
		return out;
	}

	// Read in memory stored by dumpMemory.
	void readMemory(String dis,int addr,int len)
	{
		int position = 0;
		for ( int i = 0; i < len; i++ ) {
			char c = dis.charAt(position);
			if ( (int) c == 127 ) {
				position++;
				if ( (int) dis.charAt(position) == 127 ) {
					position++;
					data[addr+i] = 127;
					data[addr+i] += 127;
				} else {
					data[addr+i] = 127;
				}
				data[addr+i] += dis.charAt(position);
			}  else {
				data[addr+i] = (byte) c;
			}
			position++;
		}
	}  
	
}
