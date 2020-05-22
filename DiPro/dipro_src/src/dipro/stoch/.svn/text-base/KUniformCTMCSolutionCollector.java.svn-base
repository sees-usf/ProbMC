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

import java.io.File;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Set;

import parser.ast.Expression;
import parser.ast.ModulesFile;
import parser.ast.PropertiesFile;
import prism.Model;
import prism.Prism;
import prism.Result;
import dipro.alg.BF;
import dipro.alg.KSPAlgorithm;
import dipro.alg.BF.SearchMark;
import dipro.graph.DirectedEdge;
import dipro.graph.Vertex;
import dipro.run.Config;
import dipro.run.PrismDefaultContext;
import dipro.util.Attribute;
import dipro.util.Trace;

public class KUniformCTMCSolutionCollector extends KDTMCSolutionCollector {

	public final Attribute ACCURATE_TRACE_PROB = new Attribute(
			"ACCURATE_TRACE_PROB");

	public KUniformCTMCSolutionCollector(KSPAlgorithm alg) throws Exception {
		super(alg);
	}

	
	protected void processTrace(BF.SearchMark targetMark) throws Exception {
		if(probabilityUpperBound==-1.0d) computeProbUpperBound();
		Trace trace = constructSolTrace(targetMark);
		double traceProb = modelCheckTrace(trace);
		if (traceProb > bestTraceValue) {
			bestTraceValue = traceProb;
		}
		trace.set(ACCURATE_TRACE_PROB, new Double(traceProb));
		trace.setSolutionValue(bestTraceValue);
		solutionProb = solutionProb + traceProb;
		boolean enough = false;
		if(isStrictBound) enough = enough || (solutionProb >= probabilityUpperBound);
		else enough = enough || (solutionProb > probabilityUpperBound);
		if (enough) {
			String comp = isStrictBound? " < " : " <= ";
			if (getConfig().logLevel >= Config.ALG_LOG_NORMAL) {
				alg.log("Counterexample found, CX probability is enough= "+solutionProb+comp+probabilityUpperBound+"\n"+
						"Thus, request termination.");
			}
			alg.requestTermination();
		} 
//		if (trace.length() > 0) {
//			Vertex v = trace.getFirstVertex();
//			incrementSolCardinality(v, traceProb);
//			solutionGraph.addVertex(v);
//		}
//		Iterator<DirectedEdge> edges = trace.getEdges();
//		while (edges.hasNext()) {
//			DirectedEdge e = edges.next();
//			incrementSolCardinality(e, traceProb);
//			incrementSolCardinality(e.target(), traceProb);
//			solutionGraph.addEdge(e);
//		}
		Set<Vertex> vs = trace.getVertexSet();
		for(Vertex v: vs) {
			incrementSolCardinality(v, traceProb);
			solutionGraph.addVertex(v);
		}
		Set<DirectedEdge> es = trace.getEdgeSet();
		for(DirectedEdge e: es) {
			incrementSolCardinality(e, traceProb);
			solutionGraph.addEdge(e);
		}
		
		recordTrace(trace);
		//traceRecorder.record(trace);
		trace.clear();
		trace = null;
	}

	protected double modelCheckTrace(Trace trace) throws Exception {
		double p = 0.0d;
		if (alg.getContext() instanceof PrismDefaultContext) {
			p = prismModelCheckTrace(trace);
		} else {
			p = mrmcModelCheckTrace(trace);
		}
		return p;
	}

	protected double prismModelCheckTrace(Trace trace) throws Exception {
		File sFile = new File("trace.temp.s");
		File tFile = new File("trace.temp.t");
		File lFile = new File("trace.temp.l");
		PrintStream out = new PrintStream(lFile);
		out.println("0=\"init\" 1=\"deadlock\"");
		out.println("0: 0");
		out.close();
		
		out = new PrintStream(sFile);
		out.println("(id)");
		int sinkId = trace.length();
		for (int i = 0; i <= trace.length(); i++) {
			out.println(i + ":(" + i + ")");
		}
		out.close();
		out = new PrintStream(tFile);
		out.println();
		DecimalFormatSymbols symb = new DecimalFormatSymbols();
		symb.setDecimalSeparator('.');
		DecimalFormat form = new DecimalFormat(
				"######0.0############################################################",
				symb);
		for (int i = 0; i < trace.length() - 1; i++) {
			StochasticTransition transition = (StochasticTransition) trace
					.getEdge(i);
			Vertex vertex = transition.source();
			assert vertex.equals(trace.getVertex(i));
			double q = ((UniformCTMC) alg.getContext().getGraph())
					.getUniformRate();
			double totalExit = q;
			SearchMark mark = ((KSPAlgorithm) alg).getBasicSearchAlgorithm()
					.getSearchTree().isExplored(vertex);
//			StochasticTransition loop = (StochasticTransition) mark
//					.get(((KSearch) alg).getBasicSearchAlgorithm().SELF_LOOP);
//			if (loop != null) {
//				totalExit = totalExit - loop.getProbOrRate();
//			}
			double prob = transition.getProbOrRate();
			double toSink = Math.abs(totalExit - prob);
			// toSink =
			// Math.round(toSink/alg.getConfig().epsilon)*alg.getConfig().epsilon;
			out.println(i + " " + (i + 1) + " " + form.format(prob));
			if (toSink != 0) {
				out.println(i + " " + sinkId + " " + form.format(toSink));
			}
		}
		out.close();
		StochUntil prop = (StochUntil) alg.getContext().getProperty();
		StringBuilder sb = new StringBuilder("P=? [ true U");
		if (prop instanceof StochTBoundedUntil) {
			double t = ((StochTBoundedUntil) prop).timeBound();
			sb.append("<=");
			sb.append(t);
		}
		sb.append(" id=");
		sb.append(trace.length() - 1);
		sb.append(" ]");
		File pFile = new File("trace.temp.csl");
		out = new PrintStream(pFile);
		out.println(sb);
		out.close();
		PrismDefaultContext pc = (PrismDefaultContext) alg.getContext();
		Prism prism = pc.getPrism();
		ModulesFile mf = prism.parseExplicitModel(sFile, tFile, lFile, ModulesFile.STOCHASTIC);
		PropertiesFile pf = prism.parsePropertiesFile(mf, pFile);
		// pf.setUndefinedConstants(ps.getPrismModel().constantValues());
		Expression f = pf.getProperty(0);
		Model m = prism.buildExplicitModel();
		Result result = prism.modelCheck(m, pf, f);
		double prob = ((Double)result.getResult()).doubleValue();
		pc.getPrismMainLog().flush();
		pc.getPrismTechLog().flush();
		m.clear();
		m = null;
		pf = null;
		mf = null;
		prism = null;
		sFile = null;
		tFile = null;
		System.gc();
		return prob;
	}

	protected double mrmcModelCheckTrace(Trace trace) throws Exception {
		// TODO Auto-generated method stub
		return 0.0d;
	}
}
