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

public abstract class ZRandom extends Object implements Serializable {
	private static final long serialVersionUID = 1L;

	// The initialize function performs necessary initialization
    // (if any).  It is passed the ZUserInterface object for this
    // ZMachine.
    public abstract void initialize(ZUserInterface ui);
    {
        //zui = ui;
        //rnd = new Random(); // not needed for gwt
    }

    // Seed the random number generator with s.  If s == 0, use
    // an outside source.
    public abstract void seed(int s);
    {
        /* if (s == 0)
            rnd = new Random();
        else
            rnd = new Random((long)s); */ // we do not need seeds
    }

    // Get a random number between 1 and s, inclusive.
    public abstract int getRandom(int s);
    //{
	//	return (Math.abs(Random.nextInt()) % s) + 1;
    //}
}
