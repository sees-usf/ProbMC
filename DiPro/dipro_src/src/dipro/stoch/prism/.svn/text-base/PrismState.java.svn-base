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

package dipro.stoch.prism;

import java.util.Hashtable;

import parser.Values;
import prism.PrismException;
import dipro.graph.State;

public class PrismState implements State {

//	private static int stateIds =0;
//	private int id; 	
	protected Values values;

	
	public PrismState(Values v) {
//		this.id = stateIds++;
		values = v;
	}

	public Object element() {
		return values;
	}

	public Values values() {
		return values;
	}

	@Override
	public Object getLabelValue(String label) throws PrismException {
		return values.getValueOf(label);
	}

	/**
	 * TODO We do not have any access on the internal Vectors of the value
	 * object. Thus, at the moment we have to copy the Vector of values to a
	 * temporal Vector v and call the function v.hashCode().
	 */
	@Override
	public int hashCode() {
		int n = values.getNumValues();
		Hashtable<String, Object> t = new Hashtable<String, Object>();
		for (int i = 0; i < n; i++) {
			String s = values.getName(i);
			Object v = values.getValue(i);
			t.put(s, v);
		}
		return t.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final PrismState other = (PrismState) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else {
			if (!values.equals(other.values))
				return false;
		}
		return true;
	}

	public String toString() {
		return "[" + values.toString() + "]";
	}
	
//	public String toString() {
//		return "["+id+"]";
//	}
}
