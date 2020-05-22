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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import parser.ParseException;
import parser.Values;
import parser.ast.Expression;
import parser.ast.ExpressionConstant;
import parser.ast.ExpressionLiteral;
import parser.ast.ExpressionProb;
import parser.ast.ExpressionTemporal;
import parser.ast.LabelList;
import parser.ast.Module;
import parser.ast.ModulesFile;
import parser.ast.PropertiesFile;
import prism.Prism;
import prism.PrismException;
import prism.PrismLangException;
import prism.UndefinedConstants;
import simulator.SimulatorEngine;
import simulator.SimulatorException;
import dipro.graph.Transition;
import dipro.run.AbstractPrismContext;
import dipro.run.PreparedDataFromPrism;
import dipro.run.PrismDefaultContext;
import dipro.run.PrismExplicitContext;
import dipro.run.Registry;
import dipro.run.UnsupportedPropertyException;

class PrismRawModel {

	public static final String UNDEFINED_CONSTANT = new String(
			"UNDEFINED_CONSTANT");

	private AbstractPrismContext context;

	protected Prism prism;

	protected SimulatorEngine engine;

	protected ModulesFile modulesFile;

	protected PropertiesFile propertiesFile;

	protected Properties externalConstantValues;

	protected Values constantValues;

	private List<String> variablesNames;

	private int stateSize;

	// new label index
	private int labelIndex;


	// Hashtable<String, String> labels = new Hashtable<String, String>();
	Hashtable<String, String> subformulaeToLabels = new Hashtable<String, String>();

	PrismRawModel(AbstractPrismContext context, PreparedDataFromPrism preparedData)
	throws InvalidPropertiesFormatException, PrismException,
	ParseException, IOException, SimulatorException {
		this.context = context;
		prism = new Prism(context.getPrismMainLog(), context
				.getPrismTechLog());
		prism.initialise();
		if (context.getDiPro().isPlugin()) {
			modulesFile = context.getConfig().getModel();
			propertiesFile = context.getConfig().getProp();
			constantValues = context.getConfig().getConstantValues();
			if(constantValues == null)
				defineConstants();
			
		} else {
			parseModel();
		}
		engine = new SimulatorEngine();
		engine.startNewPath(modulesFile, propertiesFile, initValues());
		int n = engine.getNumVariables();
		variablesNames = new Vector<String>(n);
		for (int i = 0; i < n; i++)
			variablesNames.add(i, engine.getVariableName(i));
		stateSize = calculateStateSize();
	}
	
	PrismRawModel(AbstractPrismContext context)
			throws InvalidPropertiesFormatException, PrismException,
			ParseException, IOException, SimulatorException {
		this(context, null);
	}

	private void parseModel() throws PrismException, IOException {
		if(context instanceof PrismDefaultContext) {
			modulesFile = prism.parseModelFile(new File(((PrismDefaultContext)context).getModelFileName()));
		}
		else {
			PrismExplicitContext con = (PrismExplicitContext)context;
			modulesFile = prism.parseExplicitModel(new File(con.getTraFileName()), new File(con.getStaFileName()), new File(con.getLabFileName()), con.getModelType());
		}
		propertiesFile = prism.parsePropertiesFile(modulesFile, new File(
				context.getPropFileName()));

		/* load external constant values */
		externalConstantValues = new Properties();
		if (context.getConstFileName() != null) {
			InputStream in = new FileInputStream(context.getConstFileName());
			externalConstantValues.load(in);
			in.close();
		}
		defineConstants();
	}
	
	private int calculateStateSize() throws SimulatorException {
		int size = 0;
		for (int i = 0; i < variablesNames.size(); i++) {
			int type = engine.getVariableType(i);
			switch (type) {
			case Expression.INT:
				size += 4;
				break;
			case Expression.BOOLEAN:
				size += 4;
				break;
			case Expression.DOUBLE:
				size += 8;
				break;
			default:
				throw new IllegalStateException("Invalid Prism data type: "
						+ type);
			}
		}
		return size;
	}

	private void defineConstants() throws PrismException {
		constantValues = PrismRawModel.defineConstants(this.modulesFile,
				this.propertiesFile, this.externalConstantValues);
	}

	static Values defineConstants(ModulesFile mf, PropertiesFile pf,
			Properties externalConstantValues) throws PrismException {
		UndefinedConstants undefinedConstants = new UndefinedConstants(mf, pf);
		int mfn = undefinedConstants.getMFNumUndefined();
		for (int i = 0; i < mfn; i++) {
			String constantName = undefinedConstants.getMFUndefinedName(i);
			String constantValue = externalConstantValues
					.getProperty(constantName);
			undefinedConstants.defineConstant(constantName, constantValue);
		}
		int pfn = undefinedConstants.getPFNumUndefined();
		for (int i = 0; i < pfn; i++) {
			String constantName = undefinedConstants.getPFUndefinedName(i);
			String constantValue = externalConstantValues
					.getProperty(constantName);
			undefinedConstants.defineConstant(constantName, constantValue);
		}
		undefinedConstants.checkAllDefined();
		undefinedConstants.initialiseIterators();
		mf.setUndefinedConstants(undefinedConstants.getMFConstantValues());
		pf.setUndefinedConstants(undefinedConstants.getPFConstantValues());
		Values constantValues = new Values();
		constantValues.addValues(mf.getConstantValues());
		constantValues.addValues(pf.getConstantValues());
		return constantValues;
	}

	Values initValues() throws PrismException {
		return modulesFile.getInitialValues();
	}

	@SuppressWarnings("static-access")
	Collection<PrismTransition> generateOutgoingTransitions(PrismState s) {
		PrismState s1 = s;
//		float totalExitRate = -1.0f;
		List<PrismTransition> outgoingTransitions = new LinkedList<PrismTransition>();
		try {
			engine.restartNewPath(s1.values());
			int numUpdates = engine.getNumUpdates();
			for (int i = 0; i < numUpdates; i++) {
				assert engine.getPathSize() == 1;
				engine.manualUpdate(i);
				Values v = getPathValues(1);
				PrismState s2 = new PrismState(v);
				engine.backtrack(0);
				PrismTransition trans = (PrismTransition) createTransition(s1,
						s2);
				fillTransition(trans, i);
//				if(type() == ModulesFile.STOCHASTIC) {
//					totalExitRate = trans.getProbOrRate();
//				}
				outgoingTransitions.add(trans);
			}
		} catch (SimulatorException e) {
			Registry.getMain().handleFatalError("Prism Simulation Engine Failure!", e);
		}
//		assert type() == ModulesFile.STOCHASTIC || totalExitRate == -1.0;
//		if(type() == ModulesFile.STOCHASTIC) {
//			for(PrismTransition t : outgoingTransitions) {
//				t.transData.setSourceStateTotalExitRate(totalExitRate);
//			}
//		}
		return outgoingTransitions;
	}

	PrismTransition createTransition(PrismState s1, PrismState s2) {
		return new PrismTransition(s1, s2);
	}

	String generateInitString() throws PrismException {
		Values v = initValues();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < v.getNumValues(); i++) {
			if (i != 0)
				sb.append(",");
			sb.append(v.getName(i));
			sb.append("=");
			sb.append(v.getValue(i));
		}
		return sb.toString();
	}

	@SuppressWarnings("static-access")
	protected Values getPathValues(int pathIndex) throws SimulatorException {
		Values v = new Values();
		for (String varName : variablesNames) {
			int j = engine.getIndexOfVar(varName);
			int x = engine.getPathData(j, pathIndex);
			int type = engine.getVariableType(j);
			Object value;
			switch (type) {
			case SimulatorEngine.BOOLEAN:
				value = x == 0 ? new Boolean(false) : new Boolean(true);
				break;
			case SimulatorEngine.INTEGER:
				value = new Integer(x);
				break;
			default:
				throw new SimulatorException("Invalid type for variable "
						+ varName);
			}
			v.addValue(varName, value);
		}
		return v;
	}

	@SuppressWarnings("static-access")
	protected Transition fillTransition(PrismTransition trans, int updateIndex)
			throws SimulatorException {
		String label = engine.getActionLabelOfUpdate(updateIndex);
		String assigDescr = engine
				.getAssignmentDescriptionOfUpdate(updateIndex);
		float p = (float) engine.getProbabilityOfUpdate(updateIndex);
		String moduleName = engine.getModuleNameOfUpdate(updateIndex);
		int actionIndex = engine.getDistributionIndexOfUpdate(updateIndex);
		int moduleIndex = engine.getModuleIndexOfUpdate(updateIndex);
		PrismTransitionData data = new PrismTransitionData(moduleName,
				moduleIndex, label, actionIndex, p, assigDescr);
		trans.setTransitionData(data);
		return trans;
	}

	ModulesFile modulesFile() {
		return modulesFile;
	}

	PropertiesFile propertiesFile() {
		return propertiesFile;
	}

	Properties externalConstantValues() {
		return externalConstantValues;
	}

	Values constantValues() {
		return constantValues;
	}

	int type() {
		return modulesFile.getType();
	}

	public void prepareForMRMCExport(String mfName, String pfName,
			String mrmcPropfName) throws IOException,
			UnsupportedPropertyException, PrismException {
		File mf = new File(mfName);
		mf.createNewFile();
		PrintStream mfStream = new PrintStream(new FileOutputStream(mf));
		switch (modulesFile.getType()) {
		case ModulesFile.NONDETERMINISTIC:
			mfStream.println("nondeterministic\n");
			break;
		case ModulesFile.STOCHASTIC:
			mfStream.println("stochastic\n");
			break;
		case ModulesFile.PROBABILISTIC:
			mfStream.println("probabilistic\n");
			break;
		}

		// Write the constants into the model file
		for (int i = 0; i < constantValues.getNumValues(); i++) {
			String cName = constantValues.getName(i);
			Object cValue = constantValues.getValue(i);
			int cType = constantValues.getType(i);
			StringBuilder line = new StringBuilder("const ");
			switch (cType) {
			case Expression.BOOLEAN:
				line.append("bool ");
				break;
			case Expression.INT:
				line.append("int ");
				break;
			case Expression.DOUBLE:
				line.append("double ");
				break;
			}
			line.append(cName);
			line.append("=");
			line.append(cValue);
			line.append(";");
			mfStream.println(line);
		}
		mfStream.println();
		// Write labels as formulae into the model file
		LabelList labelList = propertiesFile.getLabelList();
		// write labels into property file
		for (int i = 0; i < labelList.size(); i++) {
			StringBuilder sb = new StringBuilder();
			sb.append("formula ");
			String label = labelList.getLabelName(i);
			sb.append(label);
			sb.append(" = ");
			sb.append(labelList.getLabel(i));
			sb.append(";");
			mfStream.println(sb);
		}
		mfStream.println();
		// Write the model into the model file
		for (int i = 0; i < modulesFile.getNumModules(); i++) {
			Module m = modulesFile.getModule(i);
			m.toString();
			mfStream.println(m.toString());
			mfStream.println();
		}
		// close the stream
		mfStream.close();

		File pf = new File(pfName);
		pf.createNewFile();
		PrintStream pfStream = new PrintStream(new FileOutputStream(pf));
		// add the new labels into the labelList and
		// create labels for subformulae
		// for (int i = 0; i < propertiesFile.getNumProperties(); i++) {
		Expression formula = propertiesFile.getProperty(context.getProbIndex());
		String property = writeFormula(formula, false);
		String mrmcProperty = writeFormula(formula, true);
		Enumeration<String> en = subformulaeToLabels.keys();
		while (en.hasMoreElements()) {
			String strSubformula = en.nextElement();
			String label = subformulaeToLabels.get(strSubformula);
			StringBuilder sb = new StringBuilder();
			sb.append("label ");
			sb.append(label);
			sb.append(" = ");
			sb.append(strSubformula);
			sb.append(";");
			pfStream.println(sb);
		}

		// Write the properties into the file
		pfStream.println(property);
		pfStream.close();

		File mrmcPropFN = new File(mrmcPropfName);
		mrmcPropFN.createNewFile();
		PrintStream mrmcPropStream = new PrintStream(new FileOutputStream(
				mrmcPropFN));
		// write into the mrmc property file
		mrmcPropStream.println(mrmcProperty);
		mrmcPropStream.close();
	}

	private String writeFormula(Expression formula, boolean isForMRMC)
			throws UnsupportedPropertyException, PrismException {
		// Consider only PCTLProb formulae
		// Ignore PCTLSS and PCTLReward ones
		if (!(formula instanceof ExpressionProb))
			throw new UnsupportedPropertyException(formula.toString());
		boolean isPCTL = true;
		if(modulesFile.getType() == ModulesFile.STOCHASTIC) {
			isPCTL = false;
		}
		ExpressionProb property = (ExpressionProb)formula;
		ExpressionTemporal pathFormula = (ExpressionTemporal)property.getExpression();
		int op = pathFormula.getOperator();
		assert op == ExpressionTemporal.P_U;
		StringBuffer sb = new StringBuffer();
		sb.append("P");
		if (isForMRMC) {
			sb.append("{ ");
		}
		if (property.getProb() == null) {
			// property.append(((PCTLProb) formula).getRelOp());
			sb.append("=?");
		} else {
			sb.append(property.getRelOp());
			double prob = property.getProb().evaluateDouble(getConstantValues(), null);
			sb.append(prob);
		}
		if (isForMRMC) {
			sb.append(" } ");
		}
		sb.append(" [ ");
		String phi1 = rewriteAsLabel(pathFormula.getOperand1(), isForMRMC);
		String phi2 = rewriteAsLabel(pathFormula.getOperand2(), isForMRMC);
		sb.append(phi1);
		sb.append(" U");
		Expression timeUpperBoundExpr = pathFormula.getUpperBound();
		if (timeUpperBoundExpr != null) {
			if(!isForMRMC) {
				sb.append("<=");
				if(isPCTL) {
					int t = timeUpperBoundExpr.evaluateInt(getConstantValues(), null);
					assert t >= 0;
					sb.append(t);
				}
				else {
					double t = timeUpperBoundExpr.evaluateDouble(getConstantValues(), null);
					assert t >= 0;
					sb.append(t);
				}
				sb.append(" ");
			}
			else {
				sb.append("[0,");
				if(isPCTL) {
					int t = timeUpperBoundExpr.evaluateInt(getConstantValues(), null);
					assert t >= 0;
					sb.append(t);
				}
				else {
					double t = timeUpperBoundExpr.evaluateDouble(getConstantValues(), null);
					assert t >= 0;
					sb.append(t);
				}
				sb.append("] ");
			}
		} else {
			sb.append(" ");
		}
		sb.append(phi2);
		sb.append(" ] ");
		return sb.toString();
	}

//	private String writeProbNext(PCTLFormula formula,
//			PCTLProbNext pctlProbNext, boolean isMRMCPropFile) throws PrismException {
//		StringBuffer property = new StringBuffer();
//		property.append("P");
//		if (isMRMCPropFile) {
//			property.append("{ ");
//		}
//		if (((PCTLProb) formula).getProb() == null)
//			property.append("=?");
//		else {
//			property.append(((PCTLProb) formula).getRelOp());
//			double prob = ((PCTLProb) formula).getProb().evaluateDouble(
//					getConstantValues(), null);
//			property.append(prob);
//		}
//		if (isMRMCPropFile) {
//			property.append(" } ");
//		}
//		property.append(" [ ");
//		property.append("X ");
//		String phi = rewriteAsLabel(pctlProbNext.getOperand(),isMRMCPropFile);
//		property.append(phi);
//		property.append(" ] ");
//		return property.toString();
//	}
//
//	private String writeProbUntil(PCTLFormula formula,
//			PCTLProbUntil pctlProbUntil, boolean isMRMCPropFile) throws PrismException {
//		StringBuffer property = new StringBuffer();
//		property.append("P");
//		if (isMRMCPropFile) {
//			property.append("{ ");
//		}
//		property.append(((PCTLProb) formula).getRelOp());
//		if (((PCTLProb) formula).getProb() == null)
//			property.append("=?");
//		else {
//			property.append(((PCTLProb) formula).getRelOp());
//			double prob = ((PCTLProb) formula).getProb().evaluateDouble(
//					getConstantValues(), null);
//			property.append(prob);
//		}
//		if (isMRMCPropFile) {
//			property.append(" } ");
//		}
//		property.append(" [ ");
//		String phi1 = rewriteAsLabel(pctlProbUntil.getOperand1(), isMRMCPropFile);
//		String phi2 = rewriteAsLabel(pctlProbUntil.getOperand2(), isMRMCPropFile);
//		property.append(phi1);
//		property.append(" U ");
//		property.append(phi2);
//		property.append(" ] ");
//		return property.toString();
//	}
//
//	private String writeProbBoundedUntilFormula(PCTLFormula formula,
//			PCTLProbBoundedUntil pctlProbBoundedUntil, boolean isMRMCPropFile)
//			throws PrismException {
//		StringBuffer property = new StringBuffer();
//		property.append("P");
//		if (isMRMCPropFile) {
//			property.append("{ ");
//		}
//		if (((PCTLProb) formula).getProb() == null) {
//			// property.append(((PCTLProb) formula).getRelOp());
//			property.append("=?");
//		} else {
//			property.append(((PCTLProb) formula).getRelOp());
//			double prob = ((PCTLProb) formula).getProb().evaluateDouble(
//					getConstantValues(), null);
//			property.append(prob);
//		}
//		if (isMRMCPropFile) {
//			property.append(" } ");
//		}
//		property.append(" [ ");
//		String phi1 = rewriteAsLabel(pctlProbBoundedUntil.getOperand1(), isMRMCPropFile);
//		String phi2 = rewriteAsLabel(pctlProbBoundedUntil.getOperand2(), isMRMCPropFile);
//		property.append(phi1);
//		property.append(" U");
//		property.append("[0,");
//		Expression timeUpperBoundExpr = pctlProbBoundedUntil.getUpperBound();
//		assert timeUpperBoundExpr != null;
//		double timeUpperBound = timeUpperBoundExpr.evaluateDouble(
//				getConstantValues(), null);
//		assert timeUpperBound >= 0.0d;
//		property.append(timeUpperBound);
//		property.append("] ");
//		property.append(phi2);
//		property.append(" ] ");
//		return property.toString();
//	}

//	private String checkForNewLabels(PCTLFormula operand, boolean isMRMCPropFile) {
//		// the "operand" can be PCTLExpression, PCTLFormulaUnary,
//		// PCTLFormulaNary, PCTLLabel
//
//		if (operand instanceof PCTLLabel) {
//			if (isMRMCPropFile)
//				// remove the quotes
//				return operand.toString().substring(1,
//						operand.toString().length() - 1);
//		}
//		if (operand instanceof PCTLNot) {
//			// remove the quotes
//			if (isMRMCPropFile)
//				return "!"
//						+ operand.toString().substring(2,
//								operand.toString().length() - 1);
//			else
//				return operand.toString();
//		}
//		if (operand instanceof PCTLExpression) {
//
//			PCTLExpression pctlExpression = (PCTLExpression) operand;
//			if (pctlExpression.getExpression() instanceof ExpressionTrue) {
//				if (isMRMCPropFile)
//					return "tt";
//				else
//					return operand.toString();
//			}
//			if (pctlExpression.getExpression() instanceof ExpressionFalse) {
//				if (isMRMCPropFile)
//					return "ff";
//				else
//					return operand.toString();
//			}
//
//			// check if the label already exists
//			String strExpr = pctlExpression.getExpression().toString();
//			if (!exprToLabels.containsKey(strExpr)) {
//				String newLabel = "\"label" + labelIndex + "\"";
//				labelIndex++;
//				exprToLabels.put(strExpr, newLabel);
//			}
//			String label = exprToLabels.get(strExpr);
//			if (isMRMCPropFile)
//				return label.substring(1, label.length() - 1);
//			else
//				return label;
//			// if
//			// (labels.containsValue(pctlExpression.getExpression().toString())
//			// && !isMRMCPropFile) {
//			// Enumeration<String> keys = labels.keys();
//			// while (keys.hasMoreElements()) {
//			// String key = keys.nextElement();
//			// if (labels.get(key).equals(
//			// pctlExpression.getExpression().toString())) {
//			// return key;
//			// }
//			// }
//			// }
//			//
//			// String key = "\"label" + labelIndex + "\"";
//			// labels.put(key, pctlExpression.getExpression().toString());
//			// labelIndex++;
//			// if (isMRMCPropFile)
//			// return "label" + labelIndex;
//			// else
//			// return key;
//		}
//
//		if (operand instanceof PCTLFormulaNary) {
//			int numOperands = ((PCTLFormulaNary) operand).getNumOperands();
//			StringBuffer sb = new StringBuffer();
//
//			for (int i = 0; i < numOperands; i++) {
//				String val = checkForNewLabels(((PCTLFormulaNary) operand)
//						.getOperand(i), isMRMCPropFile);
//
//				if (i > 0 && i < numOperands) {
//					if (operand instanceof PCTLAnd)
//						sb.append(" & ");
//					if (operand instanceof PCTLOr)
//						sb.append(" | ");
//				}
//				sb.append(val);
//			}
//			return sb.toString();
//		}
//		if (operand instanceof PCTLFormulaUnary) {
//			// do something
//			if (operand instanceof PCTLNot) {
//				return operand.toString();
//			}
//		}
//
//		return operand.toString();
//	}

	
	private String rewriteAsLabel(Expression phi, boolean isForMRMC) throws PrismLangException {
		// the "operand" can be PCTLExpression, PCTLFormulaUnary,
		// PCTLFormulaNary, PCTLLabel
		String label; 
		if (   phi instanceof ExpressionConstant
				|| phi instanceof ExpressionLiteral) {
			boolean b = phi.evaluateBoolean(getConstantValues(), null);
			if(isForMRMC) {
				label = b? "tt" : "ff";
			} 
			else {
				label = b? "true" : "false";
			}
			return label;
		} 
		String strPhi = phi.toString();
		strPhi = replaceLabelsByFormulae(strPhi);
		if (!subformulaeToLabels.containsKey(strPhi)) {
			labelIndex++;
			String newLabel = "\"label_" + labelIndex + "\"";
			subformulaeToLabels.put(strPhi, newLabel);
		}
		label = subformulaeToLabels.get(strPhi);
		if (isForMRMC) {
			// remove the quotes
			label = label.substring(1,label.length()-1);
		}
		return label;
	}
	
	
	private String replaceLabelsByFormulae(String phi) {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<phi.length(); i++) {
			char c = phi.charAt(i);
			if(c!='\"') sb.append(c);
		}
		return sb.toString();
	}
		
	void clear() {
		stateSize = -1;
		variablesNames.clear();
		variablesNames = null;
		constantValues = null;
		if (!context.getDiPro().isPlugin())
			externalConstantValues.clear();
		propertiesFile = null;
		modulesFile = null;
		engine = null;
		prism.closeDown();
		prism = null;
		System.gc();
	}

	Properties getExternalConstantValues() {
		return externalConstantValues;
	}

	Values getConstantValues() {
		return constantValues;
	}

	List<String> getVariablesNames() {
		return variablesNames;
	}

	Class getVariableType(String varName) throws SimulatorException {
		int t = engine.getVariableType(engine.getIndexOfVar(varName));
		switch (t) {
		case SimulatorEngine.BOOLEAN:
			return Boolean.class;
		case SimulatorEngine.INTEGER:
			return Integer.class;
		case SimulatorEngine.DOUBLE:
			return Double.class;
		}
		throw new IllegalArgumentException(varName);
	}

	int getStateSize() {
		return stateSize;
	}
	
//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		sb.append("Model File: ");
//		if(context instanceof PrismContext) {
//			sb.append(((PrismContext)context).getModelFileName());
//		}
//		else {
//			sb.append(((PrismExplicitContext)context).getTraFileName());
//		}
//		Enumeration<?> constants = externalConstantValues.propertyNames();
//		if(constants.hasMoreElements()) sb.append("\nConstants:");
//		while(constants.hasMoreElements()) {
//			String constant = (String)constants.nextElement();
//			String value = externalConstantValues.getProperty(constant);
//			sb.append("\n");
//			sb.append(constant);
//			sb.append(" = ");
//			sb.append(value);
//		}
//		return sb.toString();
//	}
}
