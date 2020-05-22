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

import java.util.HashMap;

import dipro.util.DiProException;

public class PiApprox implements PiInterface {

	private float epsilon;
	private HashMap<Integer, Float> probMap;
	private int minRecordedK;
	private int maxRecordedK;

	public PiApprox(float epsilon) {
		this.epsilon = epsilon;
		probMap = new HashMap<Integer, Float>();
		maxRecordedK = -1;
		minRecordedK = -1;
	}

	/**
	 * Pre-condition: k is greater than the lastly set probability.
	 * 
	 * @throws DiProException
	 */
	@Override
	public void setProb(int k, float p) throws DiProException {
		// System.out.println(this+": Trans. prob. entry ("+k+", "+p+")");
		if (probMap.isEmpty()) {
			probMap.put(k, p);
			minRecordedK = k;
			maxRecordedK = k;
		} else {
			assert maxRecordedK >= 0;
			assert k > maxRecordedK;
			float q = getLastProb(k);
			if (Math.abs(p - q) > epsilon) {
				probMap.put(k, p);
				maxRecordedK = k;
			} else {
				// System.out.println(this+": Ignore Transient prob. entry:
				// ("+k+", "+p+")");
			}
		}
	}

	@Override
	public float getProb(int k) throws DiProException {
		if (minRecordedK < 0)
			return 0;
		assert minRecordedK >= 0 && maxRecordedK >= 0;
		if (k < minRecordedK)
			return 0;
		Float f = probMap.get(k);
		if (f != null)
			return f.floatValue();
		float p1 = getLastProb(k);
		float p2;
		if (k > maxRecordedK)
			p2 = p1;
		else
			p2 = getNextProb(k);
		float p = Math.min(p1, p2) - epsilon;
		p = Math.max(p, 0.0f);
		return p;
	}

	private float getLastProb(int k) throws DiProException {
		for (int i = k - 1; i >= 0; i--) {
			Float f = probMap.get(i);
			if (f != null)
				return f.floatValue();
		}
		System.out.println(probMap);
		throw new DiProException("Inconsist transient probabilities object, k="
				+ k + " minRecordedK = " + minRecordedK);
	}

	private float getNextProb(int k) throws DiProException {
		assert k <= maxRecordedK;
		if (k == maxRecordedK) {
			Float f = probMap.get(k);
			assert f != null;
			return f.floatValue();
		}
		for (int i = k + 1; true; i++) {
			Float f = probMap.get(i);
			if (f != null)
				return f.floatValue();
		}
	}

	@Override
	public int memory() {
		int memory = 0;
		// size for minRecordedK and maxRecordedK
		memory = memory + 2 * 4;
		// # x Integer and Float
		memory = memory + probMap.size() * (4 + 4);
		return memory;
	}

}
