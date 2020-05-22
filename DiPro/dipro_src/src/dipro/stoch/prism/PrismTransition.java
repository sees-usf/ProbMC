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

import dipro.graph.Vertex;
import dipro.stoch.StochasticTransition;

public class PrismTransition extends StochasticTransition {

	// protected Pair<PrismState, PrismState> states;
	protected PrismTransitionData transData;

	/**
	 * Standard constructor. Transition is initialized when being created by
	 * this constructor.
	 * 
	 * @param s1 -
	 *            the origin
	 * @param s2 -
	 *            the destination
	 */
	PrismTransition(PrismState s1, PrismState s2) {
		this(s1, s2, null);
	}

	PrismTransition(PrismState s1, PrismState s2, PrismTransitionData transData) {
		// states = new Pair<PrismState, PrismState>(s1,s2);
		super(s1, s2);
		this.transData = transData;
	}

	// public boolean equals(Object o) {
	// if(!(o instanceof PrismTransition)) return false;
	// PrismTransition t = (PrismTransition) o;
	// return t.source().equals(source()) && t.target().equals(target());
	// }

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(transData.toString());
		return sb.toString();
	}

	public Object element() {
		return transData;
	}

	void setTransitionData(PrismTransitionData transData) {
		this.transData = transData;
	}

	public PrismTransitionData getTransitionData() {
		return transData;
	}

	@Override
	public void setSource(Vertex s) {
		assert s instanceof PrismState;
		PrismState v = (PrismState) source();
		assert v == null || v.equals(s);
		super.setSource(s);
	}

	@Override
	public void setTarget(Vertex s) {
		assert s instanceof PrismState;
		PrismState v = (PrismState) target();
		assert v == null || v.equals(s);
		super.setTarget(s);
	}

	@Override
	public float getProbOrRate() {
		return transData.getProbOrRate();
	}

	@Override
	public void setProbOrRate(float p) {
		if (transData == null) {
			transData = new PrismTransitionData();
		}
		transData.probOrRate = p;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((transData == null) ? 0 : transData.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PrismTransition other = (PrismTransition) obj;
		if (transData == null) {
			if (other.transData != null)
				return false;
		} else if (!transData.equals(other.transData))
			return false;
		return true;
	}
}
