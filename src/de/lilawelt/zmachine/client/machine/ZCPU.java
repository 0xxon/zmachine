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

import de.lilawelt.zmachine.client.SaveStack;

/**
 * The ZCPU class implements the Central Processing Unit
 * of a ZMachine, and is the ZMachine's interface to the outside
 * world.  With the assistance of other classes in the zmachine
 * package, and of a class supplied by the programmer that implements
 * the ZUserInterface interface, this class loads and executes
 * Z-code programs in the standard Infocom/Inform story-file format.
 *
 * @author Matt Kimmel
 */
public class ZCPU extends CPUBase {
	private static final long serialVersionUID = 1L;

	public ZCPU(ZUserInterface ui) {
		super(ui);
	}
	
	protected ZSaveStack getEmptySaveStack() {
		return new SaveStack();
	}
	
    protected ZSaveStack getSaveStack(String s) {
    	return new SaveStack(s);
    }

	
}
