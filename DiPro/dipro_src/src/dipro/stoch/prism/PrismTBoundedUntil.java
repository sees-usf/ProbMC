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

import java.io.File;
import java.io.PrintStream;

import parser.ast.Expression;
import parser.ast.ExpressionProb;
import parser.ast.ExpressionTemporal;
import prism.PrismException;
//import simulator.SimulatorException;
import dipro.stoch.StochTBoundedUntil;
import dipro.util.Safety;

public class PrismTBoundedUntil extends PrismUntil implements
		StochTBoundedUntil {

	private double timeBound; 
	
	PrismTBoundedUntil(PrismModel pm, ExpressionProb formula) throws PrismException{
		super(pm, formula);
		Expression expr = ((ExpressionTemporal)formula.getExpression()).getUpperBound();
		assert expr != null;
		Object result = expr.evaluate(model.constantValues());
		if(result instanceof Integer) {
			timeBound = ((Integer)result).doubleValue();
		}
		else {
			assert result instanceof Double;
			timeBound = ((Double)result).doubleValue();
		}
	}

	@Override
	public double timeBound() throws Exception {
		return timeBound;
//		assert formula.getOperand() instanceof PCTLProbBoundedUntil;
//		return ((PCTLProbBoundedUntil) formula.getOperand()).getUpperBound()
//				.evaluateDouble(model.getRawModel().constantValues(), null);
	}

	/**
	 * Creates the file containing the safety property adopted to the solution
	 * delivered by the search algorithm. The file is stored under the name
	 * given by {@link #dsgPropFileName}.
	 * 
	 * @throws Exception
	 * @see #dsgPropFileName
	 * @see #setDSGPropFileName(String)
	 * @see #dsgPropFileName()
	 * @throws Exception
	 */
	public void constructSolutionPropertiesFile(String fn) throws Exception {
		File pf = new File(fn);
		pf.createNewFile();
		PrintStream out = new PrintStream(pf);
		out.println("// Constants");
		writeConstantValues(out);
		out.println("\n// Labels");
		writeLabels(out);
		out.println("\n// Properties");
		out.print("P=? [ ");
		out.print(phi1().toString());
		out.print(" U<=");
		double t = timeBound();
		if (t == (int) t)
			out.print((int) t);
		else
			out.print(t);
		out.print(" (" + phi2().toString() + ")");
		out.println(" & " + Safety.TARGET_FLAG_NAME + "=1 ]");
		out.close();
	}

}
