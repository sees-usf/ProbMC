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

package dipro.util;

import java.util.Collection;
import java.util.Collections;

import dipro.graph.Vertex;

public interface Proposition {
	/**
	 * This constant is delivered by the method
	 * {@link #check(Vertex) check(Vertex)} as a result if and only if the
	 * current vertex is not a target and it will never lead to one.
	 */
	public static final int NEVER = -1;

	/**
	 * This constant is delivered by the method
	 * {@link #check(Vertex) check(Vertex)} as a result if and only if the
	 * current vertex is not a target but it might lead to one.
	 */
	public static final int FALSE = 0;

	/**
	 * This constant is delivered by the method
	 * {@link #check(Vertex) check(Vertex)} as a result if and only if the
	 * current vertex is a target.
	 */
	public static final int TRUE = 1;

	/**
	 * Check the current state on being a target state.
	 * 
	 * @param vertex
	 *            the vertex which has to be checked
	 * @return {@link #NEVER NEVER} if and only if the current vertex is not a
	 *         target vertex and it will never lead to a target,
	 *         {@link #FALSE FALSE} if and only if the current vertex is not a
	 *         target vertex but it might lead to a target, or
	 *         {@link #TRUE TRUE} if and only if the current state is a target
	 *         state.
	 */
	public int check(Vertex vertex);

	/**
	 * Delivers the names of labels, e.g. variables, which are relevant for this
	 * safety property.
	 * 
	 * @return a collection containing the names of all relavent labels.
	 * @throws Exception
	 */
	public abstract Collection<String> relevantLabels() throws Exception;

	public final Proposition FALSE_PROP = new Proposition() {
		public int check(Vertex vertex) {
			return FALSE;
		}

		@Override
		public Collection<String> relevantLabels() throws Exception {
			return Collections.EMPTY_LIST;
		}
	};

	public final Proposition TRUE_PROP = new Proposition() {
		public int check(Vertex vertex) {
			return TRUE;
		}

		@Override
		public Collection<String> relevantLabels() throws Exception {
			return Collections.EMPTY_LIST;
		}
	};
}
