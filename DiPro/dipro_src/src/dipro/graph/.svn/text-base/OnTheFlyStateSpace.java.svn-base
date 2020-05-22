//==============================================================================
//	
//	Copyright (c) 2008-
//
//	Chair for Software Engineering - University of Konstanz
//	Prof. Dr. Stefan Leue
//	www.se.inf.uni-konstanz.de
//
//	Authors of this File:
//	* Husain Aljazzar (University of Konstanz)
//	* Florian Leitner-Fischer (University of Konstanz)
//	* Dimitar Simeonov (University of Konstanz)
//------------------------------------------------------------------------------
//	
// This file is part of DiPro.
//
//    DiPro is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    DiPro is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with DiPro.  If not, see <http://www.gnu.org/licenses/>.
//	
//==============================================================================

package dipro.graph;

import java.util.Collection;
import java.util.Iterator;


/**
 * This represents the statespace.
 * 
 */
public abstract class OnTheFlyStateSpace implements StateSpace {

	public Iterator<? extends Transition> outgoingEdges(Vertex v) {
		Collection<? extends Transition> trans = generateOutgoingTransitions((State) v);
		return trans.iterator();
	}

	// ////////////////////////////////////////////////////////////
	// Abstract Methods
	// ////////////////////////////////////////////////////////////
	public abstract Collection<? extends Transition> generateOutgoingTransitions(
			State s);

	// /////////////////////////////////////////////////////////////
	// Methods which are unspupported by default
	// /////////////////////////////////////////////////////////////
	public int numVertices() {
		throw new UnsupportedOperationException();
	}

	public int numEdges() {
		throw new UnsupportedOperationException();
	}

	public Iterator<? extends State> vertices() {
		throw new UnsupportedOperationException();
	}

	public Iterator<? extends Transition> edges() {
		throw new UnsupportedOperationException();
	}

	public int degree(Vertex v) {
		throw new UnsupportedOperationException();
	}

	public Iterator<? extends Transition> incomingEdges(Vertex v) {
		throw new UnsupportedOperationException();
	}
}
