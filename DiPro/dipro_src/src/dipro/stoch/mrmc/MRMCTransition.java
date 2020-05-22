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

package dipro.stoch.mrmc;

import dipro.graph.Vertex;
import dipro.stoch.StochasticTransition;

public class MRMCTransition extends StochasticTransition {

	private float weight;

	public MRMCTransition(MRMCState s1, MRMCState s2) {
		this(s1, s2, -1.0f);
	}

	public MRMCTransition(MRMCState s1, MRMCState s2, float weight) {
		super(s1, s2);
		this.weight = weight;
	}

	/**
	 * @return Return the weight of the edge
	 */
	public float getWeight() {
		return weight;
	}

	@Override
	public void setSource(Vertex v) {
		assert v instanceof MRMCState;
		MRMCState state = (MRMCState) source();
		assert state != null && state.equals(v);
		super.setSource(v);
	}

	@Override
	public void setTarget(Vertex v) {
		assert v instanceof MRMCState;
		MRMCState state = (MRMCState) target();
		assert state != null && state.equals(v);
		super.setTarget(v);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + Float.floatToIntBits(weight);
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
		final MRMCTransition other = (MRMCTransition) obj;
		if (Float.floatToIntBits(weight) != Float.floatToIntBits(other.weight))
			return false;
		return true;
	}

	public String toString() {
		return source() + " -> " + target();
	}

	@Override
	public float getProbOrRate() {
		return weight;
	}

	@Override
	public void setProbOrRate(float p) {
		this.weight = p;
	}
}
