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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import dipro.util.Proposition;

public class SetPredicate implements Proposition {

	HashSet<Integer> vertexIds;

	public SetPredicate() {
		vertexIds = new HashSet<Integer>();
	}

	public void add(int vertexId) {
		vertexIds.add(vertexId);
	}

	public int check(Vertex vertex) {
		Integer id = ((DefaultVertex) vertex).getId();
		if (vertexIds.contains(id)) {
			return TRUE;
		}
		return FALSE;
	}

	public String toString() {
		return vertexIds.toString();
	}

	public Set<Integer> getVertexIds() {
		return vertexIds;
	}

	@Override
	public Collection<String> relevantLabels() throws Exception {
		ArrayList<String> l = new ArrayList<String>(1);
		l.add(DefaultVertex.VERTEX_ID_LABEL_NAME);
		return l;
	}
}
