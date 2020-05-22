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

import java.util.Collection;
import java.util.Collections;

import parser.ast.Expression;
import parser.ast.ExpressionProb;
import prism.PrismException;
import simulator.SimulatorException;
import dipro.graph.Vertex;

public class UntilFalseProperty extends PrismUntil {

	public UntilFalseProperty() throws PrismException, SimulatorException {
		super(null, null);
	}

	public int check(Vertex vertex) {
		return FALSE;
	}
	
	public double getProbBound() {
		return 1.0d;
	}
	
	public Collection<String> relevantLabels() {
		return Collections.emptyList();
	}
	
	public void constructSolutionPropertiesFile(String fn) {
		
	}
	
	public String toString() {
		return "P=?(true U false)";
	}
	
	public ExpressionProb pctlFormula() {
		throw new UnsupportedOperationException();
	}

	public Expression phi1() {
		return Expression.True();
	}

	public Expression phi2() {
		return Expression.False();
	}

	public boolean isLowerBounded() {
		return false;
	}

	public boolean isUpperBounded() {
		return true;
	}

	public boolean isStrictLowerBounded() {
		return false;
	}

	public boolean isStrictUpperBounded() {
		return false;
	}
}
