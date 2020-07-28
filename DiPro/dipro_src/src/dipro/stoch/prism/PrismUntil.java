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

//Focuses on the bounded feature of PRISM
package dipro.stoch.prism;

import java.io.File;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.Vector;

import parser.Values;
import parser.ast.Expression;
import parser.ast.ExpressionBinaryOp;
import parser.ast.ExpressionConstant;
import parser.ast.ExpressionFunc;
import parser.ast.ExpressionLabel;
import parser.ast.ExpressionLiteral;
import parser.ast.ExpressionProb;
import parser.ast.ExpressionTemporal;
import parser.ast.ExpressionUnaryOp;
import parser.ast.ExpressionVar;
import parser.ast.LabelList;
import prism.PrismException;
//import simulator.SimulatorException;
import dipro.graph.Vertex;
import dipro.stoch.StochUntil;
import dipro.util.DiProException;
import dipro.util.Safety;
//Focuses on the Until function of PRISM and how to deal with that
public class PrismUntil implements StochUntil {

	protected ExpressionProb formula;
	protected PrismModel model;

	PrismUntil(PrismModel pm, ExpressionProb formula) throws PrismException {
		this.model = pm;
		this.formula = formula;
	}

	//Loads the properties from the properties file
	public static PrismUntil loadProperty(PrismModel pm, int propertyIndex) throws DiProException, PrismException {
		Expression f = pm.propertiesFile().getProperty(propertyIndex);
		if(!isSupportedProperty(f)) {
			throw new DiProException("The property is not supported: " + f);
		}
		ExpressionProb formula = (ExpressionProb) f;
		PrismUntil property;
			
		ExpressionTemporal pathFormula = (ExpressionTemporal) formula.getExpression();
		
		int op = pathFormula.getOperator();
		assert op == ExpressionTemporal.P_U;
		Expression upperBound = pathFormula.getUpperBound();
		if (upperBound != null) {
			property = new PrismTBoundedUntil(pm, formula);
		} else {
			property = new PrismUntil(pm, formula);
		}
		System.out.println("PrismUntil - loadProperty");
		return property;
	}

	public static boolean isSupportedProperty(Expression f) {
		if (!(f instanceof ExpressionProb)) {
			return false;
		}
		ExpressionProb formula = (ExpressionProb) f;
		if (formula.getRelOp().equals("min=") || formula.getRelOp().equals(">=") || formula.getRelOp().equals(">"))
		{
			return false;
		}
		ExpressionTemporal pathFormula = (ExpressionTemporal) formula
				.getExpression();
		int op = pathFormula.getOperator();
		if (op != ExpressionTemporal.P_U) {
			return false;
		}
		if(pathFormula.getUpperBound() == null){
			if(pathFormula.getLowerBound() == null){
			}else{
				return false;				
			}
		}
		if(formula.getFilter() != null){
			return formula.getFilter().minRequested();
		}
		return true;
	}
	
	public ExpressionProb pctlFormula() {
		return formula;
	}

	public Expression phi1() {
		return ((ExpressionTemporal) formula.getExpression()).getOperand1();
	}

	public Expression phi2() {
		return ((ExpressionTemporal) formula.getExpression()).getOperand2();
	}

	public boolean isLowerBounded() {
		String relOp = formula.getRelOp();
		return relOp.equals(">") || relOp.equals(">=");
	}

	public boolean isUpperBounded() {
		String relOp = formula.getRelOp();
		return relOp.equals("<") || relOp.equals("<=");
	}

	public boolean isStrictLowerBounded() {
		String relOp = formula.getRelOp();
		return relOp.equals(">");
	}

	public boolean isStrictUpperBounded() {
		String relOp = formula.getRelOp();
		return relOp.equals("<");
	}

	// public boolean isUnbounded() {
	// return !isLowerBounded() && !isUpperBounded();
	// }
	//	
	// public boolean isMaxProb() {
	// String relOp = formula.getRelOp();
	// return relOp.equals("<") || relOp.equals("<=") || relOp.equals("max=");
	// }
	//	
	// public boolean isMinProb() {
	// String relOp = formula.getRelOp();
	// return relOp.equals(">") || relOp.equals(">=") || relOp.equals("min=");
	// }

	public double getProbBound() throws PrismException, DiProException {
		if (!isUpperBounded() && !isLowerBounded())
			throw new DiProException(
					"The property does not specify a probability bound.");
		Expression prob = formula.getProb();
		if (prob == null)
			return 1.0d;
		double p = prob.evaluateDouble(model.constantValues(), null);
		return p;
	}

	// public float probLowerBound() throws PrismException {
	// Expression exp =
	// ((PCTLProbBoundedUntil)formula.getOperand()).getLowerBound();
	// float p = exp.evaluateDouble(model.getRawModel().constantValues(), null);
	// return p;
	// }

	public int check(Vertex vertex) {
		PrismState s = (PrismState) vertex;
		System.out.println("check - PrismUntil");
		try {
			boolean a = evaluate(phi1(), s);
			boolean b = evaluate(phi2(), s);
			int result = b ? TRUE : a ? FALSE : NEVER;
			/*
			 * < ForDebugging > if(result==NEVER)
			 * System.out.println("NEVER: "+vertex); /* </ ForDebugging >
			 */
			return result;
		} catch (Exception e) {
			RuntimeException re = new RuntimeException(e);
			re.setStackTrace(e.getStackTrace());
			throw re;
		}
	}

	protected boolean evaluate(Expression phi, PrismState s)
			throws DiProException, PrismException {
		return evaluate(phi, s.values);
	}

	protected boolean evaluate(Expression phi, Values varValues)
			throws PrismException, DiProException {
		// This function is only defined for boolean expressions.
		//assert phi.getType() == Expression.BOOLEAN;
		boolean value;
		if (phi instanceof ExpressionUnaryOp) {
			int op = ((ExpressionUnaryOp) phi).getOperator();
			switch (op) {
			case ExpressionUnaryOp.NOT:
				value = !evaluate(((ExpressionUnaryOp) phi).getOperand(),
						varValues);
				break;
			case ExpressionUnaryOp.PARENTH:
				value = evaluate(((ExpressionUnaryOp) phi).getOperand(),
						varValues);
				break;
			default:
				// The operator left is ExpressionUnaryOp.MINUS which
				// can't occur in a boolean expression.
				throw new DiProException("Invalid subformula " + phi);
			}
			return value;
		}
		if (phi instanceof ExpressionBinaryOp) {
			Expression expr1 = ((ExpressionBinaryOp) phi).getOperand1();
			Expression expr2 = ((ExpressionBinaryOp) phi).getOperand2();
			int op = ((ExpressionBinaryOp) phi).getOperator();
			switch (op) {
			case ExpressionBinaryOp.EQ:
			case ExpressionBinaryOp.GE:
			case ExpressionBinaryOp.GT:
			case ExpressionBinaryOp.LE:
			case ExpressionBinaryOp.LT:
			case ExpressionBinaryOp.NE:
				value = phi.evaluateBoolean(model.constantValues(), varValues);
				break;
			case ExpressionBinaryOp.IMPLIES:
				value = !evaluate(expr1, varValues)
						|| evaluate(expr2, varValues);
				break;
			case ExpressionBinaryOp.AND:
				value = evaluate(expr1, varValues)
						&& evaluate(expr2, varValues);
				break;
			case ExpressionBinaryOp.OR:
				value = evaluate(expr1, varValues)
						|| evaluate(expr2, varValues);
				break;
			// Other operators (ExpressionBinaryOp.PLUS, MINUS, TIMES and DEVID)
			// can't occur in a boolean expression.
			default:
				throw new DiProException("Invalid subformula " + phi);
			}
			return value;
		}
		if (phi instanceof ExpressionConstant
				|| phi instanceof ExpressionLiteral
				|| phi instanceof ExpressionFunc
				|| phi instanceof ExpressionVar) {
			System.out.println("Other things- evaluate - PRISMUNTIL");
			value = phi.evaluateBoolean(model.constantValues(), varValues);
			return value;
		}
		if (phi instanceof ExpressionLabel) {
			System.out.println("Properties - -  - " + prismModel().propertiesFile().getLabelList());
			//System.out.println("Expression Label - PrismUntil" + prismModel().propertiesFile().getLabelList().size() + ((ExpressionLabel) phi).getName());
			int i = prismModel().propertiesFile().getLabelList().getLabelIndex(((ExpressionLabel) phi).getName());
			Expression expr = prismModel().propertiesFile().getLabelList().getLabel(i); 
			Object o = evaluate(expr, varValues);
			value = (Boolean) o;
			return value;
		}
		throw new DiProException("Invalid subformula " + phi);
	}

	/**
	 * Creates the file containing the safety propery adopted to the solution
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
		out.print(" U ");
		out.print("(" + phi2().toString() + ")");
		out.println(" & " + Safety.TARGET_FLAG_NAME + "=1 ]");
		out.close();
	}

	protected void writeLabels(PrintStream out) {
		LabelList l = model.propertiesFile().getLabelList();
		for (int i = 0; i < l.size(); i++) {
			out.println("label \"" + l.getLabelName(i) + "\" = "
					+ l.getLabel(i).toString() + ";");
		}
	}

	protected void writeConstantValues(PrintStream out) throws PrismException {
		Values v = model.constantValues();
		int n = v.getNumValues();
		for (int i = 0; i < n; i++) {
			String name = v.getName(i);
			String value;
			String type;
			int t = v.getType(i);
			switch (t) {
			case Expression.INT:
				type = "int";
				value = Integer.toString(v.getIntValue(i));
				break;
			case Expression.BOOLEAN:
				type = "bool";
				value = Boolean.toString(v.getBooleanValue(i));
				break;
			case Expression.DOUBLE:
				type = "double";
				DecimalFormatSymbols symb = new DecimalFormatSymbols();
				symb.setDecimalSeparator('.');
				DecimalFormat form = new DecimalFormat("#0.0################",
						symb);
				value = form.format(v.getDoubleValue(i));
				break;
			default:
				throw new IllegalStateException("Invalid prism type code: " + t);
			}
			out.println("const " + type + " " + name + " = " + value + ";");
		}
	}

	public PrismModel prismModel() {
		return model;
	}

	// public String generateInitString() throws PrismException {
	// StringBuilder sb = new
	// StringBuilder(model.getRawModel().generateInitString());
	// sb.append(",");
	// sb.append(Safety.TARGET_FLAG_NAME);
	// sb.append("=0");
	// return sb.toString();
	// }

	// /** Sets the name of the file which will contain the safety
	// * property which is equivalent to this property but
	// * adapted to the output of the search algorithm.
	// * @param the name of the file
	// * @see #constructSolutionPropertiesFile()
	// * @see #dsgPropFileName
	// * @see #dsgPropFileName()
	// */
	// public void setDSGPropFileName(String name) {
	// dsgPropFileName = name;
	// }

	// /** Returns the name of the file which will contain the safety
	// * property which is equivalent to this property but
	// * adapted to the output of the search algorithm.
	// * @see #constructSolutionPropertiesFile()
	// * @see #setDSGPropFileName(String)
	// * @see #dsgPropFileName
	// */
	// public String dsgPropFileName() {
	// return dsgPropFileName;
	// }

	/**
	 * This method implements the abstract methode
	 * {@link Safety#relevantLabels()} for Prism Until formulas. The result is
	 * here a Collection containing the names of all variable occuring the
	 * formula.
	 * 
	 * @return a Collection containing the names of all variable occuring the
	 *         formula.
	 */
	public Collection<String> relevantLabels() throws PrismException {
		Vector<String> varNames = new Vector<String>();
		relevantLabels(formula, varNames);
		return varNames;
	}

	private void relevantLabels(Expression formula, Collection<String> labels) {
		// if (formula instanceof PCTLExpression) {
		// relevantLabels(((PCTLExpression) formula).getExpression(), labels);
		// return;
		// }
		// if (formula instanceof PCTLLabel) {
		// int i = prismModel().propertiesFile().getLabelList().getLabelIndex(
		// ((PCTLLabel) formula).getName());
		// Expression expr = prismModel().propertiesFile().getLabelList()
		// .getLabel(i);
		// relevantLabels(expr, labels);
		// return;
		// }
		// if (formula instanceof PCTLFormulaBinary) {
		// relevantLabels(((PCTLFormulaBinary) formula).getOperand1(), labels);
		// relevantLabels(((PCTLFormulaBinary) formula).getOperand2(), labels);
		// return;
		// }
		// if (formula instanceof PCTLFormulaUnary) {
		// relevantLabels(((PCTLFormulaUnary) formula).getOperand(), labels);
		// return;
		// }
		// if (formula instanceof PCTLFormulaNary) {
		// int n = ((PCTLFormulaNary) formula).getNumOperands();
		// for (int i = 0; i < n; i++) {
		// relevantLabels(((PCTLFormulaNary) formula).getOperand(i),
		// labels);
		// }
		// return;
		// }
	}

	// private void relevantLabels(Expression expr, Collection<String> labels) {
	// if (expr instanceof ExpressionVar) {
	// String name = ((ExpressionVar) expr).getName();
	// if (!labels.contains(name))
	// labels.add(name);
	// return;
	// }
	// if (expr instanceof ExpressionBinary) {
	// relevantLabels(((ExpressionBinary) expr).getOperand1(), labels);
	// relevantLabels(((ExpressionBinary) expr).getOperand2(), labels);
	// return;
	// }
	// if (expr instanceof ExpressionUnary) {
	// relevantLabels(((ExpressionUnary) expr).getOperand(), labels);
	// return;
	// }
	// if (expr instanceof ExpressionNary) {
	// int n = ((ExpressionNary) expr).getNumOperands();
	// for (int i = 0; i < n; i++) {
	// relevantLabels(((ExpressionNary) expr).getOperand(i), labels);
	// }
	// return;
	// }
	// }

	public String toString() {
		return formula.toString();
	}
}
