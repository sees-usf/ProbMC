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

import dipro.stoch.StochTBoundedUntil;

public class MRMCTBoundedUntil extends MRMCUntil implements StochTBoundedUntil {

	protected double timeBound;
	
	public MRMCTBoundedUntil(String formula, String comparisionOP, double probBound, double timeBound, String phi1, String phi2) {
		super(formula, comparisionOP, probBound, phi1, phi2);
		this.timeBound = timeBound;
	}
	
	public MRMCTBoundedUntil(String comparisionOP, double probBound, double timeBound, String phi1, String phi2) {
		super(null, comparisionOP, probBound, phi1, phi2);
		this.timeBound = timeBound;
		this.formula = buildString();
	}

	public double timeBound() throws Exception {
		return timeBound;
	}
	
	public String buildString() {
		// P{ <p } [ phi1 U[0,t] phi2 ]
		StringBuffer sb = new StringBuffer("P{ ");
		if (probBound < 0)
			sb.append(" =? ");
		else {
			sb.append("<=");
			sb.append(probBound);
		}
		sb.append(" } [ ");
		sb.append(phi1);
		sb.append(" U");
		if (timeBound > 0) {
			sb.append("[0,");
			sb.append(timeBound);
			sb.append("]");
		}
		sb.append(" ");
		sb.append(phi2);
		sb.append(" ]");
		return sb.toString();
	}

	public MRMCUntil createTimeUnboundedFormula() {
		MRMCUntil f = new MRMCUntil(comparisionOP, probBound, phi1, phi2);
		return f;
	}
}
