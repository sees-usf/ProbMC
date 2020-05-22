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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import dipro.graph.Vertex;
import dipro.stoch.StochUntil;
import dipro.util.Safety;

public class MRMCUntil implements StochUntil {

	public static MRMCUntil loadProperty(String propFileName, int probIndex) throws IOException {
		String formula = null;
		double probBound = -1.0d;
		String comparisionOP = null;
		double timeBound = -1.0d;
		String phi1 = null; 
		String phi2 = null; 
		BufferedReader input = new BufferedReader(new FileReader(propFileName));
		int i = -1;
		String line = null;
		while (i < probIndex) {
			line = input.readLine();
			i++;
		}
		formula = line;
		System.out.println("Property: "+formula);
		// parsing
		int a = formula.indexOf('{');
		int b = formula.indexOf('}');
		assert a > 0;
		assert a < b;
		String probStr = formula.substring(a + 1, b);
		if (probStr.contains("=?")) {
			probBound = 1.0f;
			comparisionOP = "=?";
		}
		else {
			a = probStr.indexOf('<');
			b = probStr.indexOf('=');
			assert a != 0;
			int x; 
			if (a > 0) {
				//a = probStr.indexOf('<');
				if(b>0) {
					assert b==a+1;
					comparisionOP = "<=";
					x = b;
				}
				else {
					comparisionOP = "<";
					x = a;
				}
			}
			else {
				a = probStr.indexOf('>');
				assert a>=0;
				if(b>0) {
					assert b==a+1;
					comparisionOP = ">=";
					x=b;
				}
				else {
					comparisionOP = ">";
					x = a;
				}
			}
			probBound = Double.parseDouble(probStr.substring(x + 1));
		}
		
		String buff = formula.split("}")[1].trim();
		String[] tokens = buff.split(" ");
		assert tokens[0].trim().equals("[");
		assert tokens[tokens.length-1].trim().equals("]");
		i = 1;
		while(i<tokens.length) {
			String token = tokens[i].trim();
			i++;
			if(token.length()>0) {
				phi1 = token;
				break;
			}
		}
		while(i<tokens.length) {
			String token = tokens[i].trim();
			i++;
			if(token.length()>0) {
				assert token.charAt(0)=='U';
				if(token.length()>1) {
					// Then there is a time bound
					String[] aa = token.split(",")[1].split("]");
					timeBound = Double.valueOf(aa[0].trim());
				}
				break;
			}
		}
		while(i<tokens.length) {
			String token = tokens[i].trim();
			i++;
			if(token.length()>0) {
				phi2 = token;
				break;
			}
		}
		System.out.println("time bound = "+timeBound);
		System.out.println("prob. bound = "+probBound);
		System.out.println("phi1 = "+phi1);
		System.out.println("phi2 = "+phi2);
		if(timeBound<0) {
			return new MRMCUntil(formula, comparisionOP, probBound, phi1, phi2);
		}
		else {
			return new MRMCTBoundedUntil(formula, comparisionOP, probBound, timeBound, phi1, phi2);
		}
	}

	
	protected String formula;

	protected double probBound;
	
	protected String comparisionOP;

	protected String phi1;

	protected String phi2;

	public MRMCUntil(String formula, String comparisionOP, double probBound, String phi1, String phi2) {
		this.formula = formula;
		this.comparisionOP = comparisionOP;
		this.probBound = probBound;
		this.phi1 = phi1;
		this.phi2 = phi2;
	}

	public MRMCUntil(String comparisionOP, double probBound, String phi1, String phi2) {
		this.comparisionOP = comparisionOP;
		this.probBound = probBound;
		this.phi1 = phi1;
		this.phi2 = phi2;
		this.formula = buildString();
	}
	
	public String toString() {
		return formula;
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
		sb.append(" ");
		sb.append(phi2);
		sb.append(" ]");
		return sb.toString();
	}

	@Override
	public double getProbBound() throws Exception {
		return probBound;
	}

//	@Override
//	public double time() throws Exception {
//		return timeBound;
//	}

	@Override
	public Collection<String> relevantLabels() throws Exception {
		ArrayList<String> l = new ArrayList<String>(2);
		l.add(phi1);
		l.add(phi2);
		return l;
	}

	public int check(Vertex vertex) {
		MRMCState s = (MRMCState) vertex;
		boolean a = (Boolean) s.getLabelValue(phi1);
		boolean b = (Boolean) s.getLabelValue(phi2);
		int r = -1;
		if (b)
			r = Safety.TRUE;
		else if (a && !b)
			r = Safety.FALSE;
		else if (!a && !b)
			r = Safety.NEVER;
		return r;
	}

	@Override
	public boolean isLowerBounded() {
		return comparisionOP.contains(">");
	}

	@Override
	public boolean isUpperBounded() {
		return comparisionOP.contains("<");
	}

	public boolean isStrictLowerBounded() {
		return comparisionOP.equals(">");
	}

	public boolean isStrictUpperBounded() {
		return comparisionOP.equals("<");
	}
}
