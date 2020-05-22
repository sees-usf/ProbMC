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

package dipro.stoch;

public class Pi implements PiInterface {

	protected float[] probs;
	protected int offset;

	public Pi(int begin, int end) {
		offset = begin;
		probs = new float[end - begin];
	}

	// public boolean isCovered(int k) {
	// return k>=0 && k <= offset + probs.length;
	// }

	public int time() {
		return offset + probs.length - 1;
	}

	public float getProb(int k) {
		if (k < offset)
			return 0.0f;
		return probs[k - offset];
	}

	public boolean isEmpty() {
		return offset + probs.length <= 0;
	}

	public void setProb(int k, float p) {
		assert k >= 0 && k <= offset + probs.length;
		if (k < offset && p > 0.0d)
			throw new IllegalStateException("Prob. for time points < " + offset
					+ " must be 0.");
		probs[k - offset] = p;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("(");
		if (offset > 1)
			sb.append("..., 0.0, ");
		if (offset == 1)
			sb.append("0.0, ");
		int end = Math.min(20, probs.length - 1);
		for (int i = 0; i <= end; i++) {
			sb.append(probs[i]);
			if (i < end)
				sb.append(", ");
		}
		if (end < probs.length - 1)
			sb.append(", ...");
		sb.append(")");
		return sb.toString();
	}

	@Override
	public int memory() {
		// 1 x Integer (offset) + # x Float (probabilities array)
		return 4 + probs.length * 4;
	}
}
