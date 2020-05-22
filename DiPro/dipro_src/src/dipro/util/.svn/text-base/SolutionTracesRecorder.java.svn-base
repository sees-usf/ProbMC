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

package dipro.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class SolutionTracesRecorder {

	public final static int NO_TRACES = -1;
	public final static int CX_FILE = 0;
	public final static int CX_XML_FILE = 1;
	public final static int DIAG_PATH = 2;
	
	protected String cxFileName;
	protected PrintStream cxOut;
	protected int n;
	protected double prob;
	private int recordType, pathCount;
	private DiagnosticPath path;
	
	/**
	 * Used to save the space (in Bytes) which would be needed to keep all
	 * traces in the memory.
	 */
	protected int memory;

//	public SolutionTracesRecorder() {
//		n = 0;
//		cxFileName = null;
//		cxOut = null;
//		recordType = -1;
//	}

//	public SolutionTracesRecorder(String cxFileName) {
//		this(cxFileName, CX_FILE);
//	}

	public SolutionTracesRecorder(String cxFileName, int recordType) {
		this.cxFileName = cxFileName;
		this.recordType = recordType;
		cxOut = null;
		pathCount = 0;
		prob = 0.0;
		n = 0;
	}

	public void record(Trace trace) throws FileNotFoundException{
		computeMemoryAndProbability(trace);
		
		if (recordType == CX_FILE)
			writeCXFile(trace);
		else if (recordType == CX_XML_FILE || recordType == DIAG_PATH)
			writeCX_XMLFile(trace);
	}
	
	public void computeMemoryAndProbability(Trace trace){
		n++;
		/* m is used to compute the memory used to store the new trace */
		int m = 0;
		/*
		 * The memory used to store the trace itself = (the number of edges) *
		 * (4 = the size of a reference)
		 */
		m = m + trace.length() * 4;
		memory = memory + m;	
		prob += trace.getSolutionValue();

	}

	public DiagnosticPath record(Trace trace, ArrayList<String> ignoreList) {
		computeMemoryAndProbability(trace);
		
		path = new DiagnosticPath(pathCount++);
		path.setProbability(trace.getSolutionValue());
		path.buildTraceFromString(trace.toXMLString(),ignoreList);

		return path;
	}

	public int getNumRecordedTraces() {
		return n;
	}

	public void close() {
		if (cxOut != null){
			 if (recordType == CX_XML_FILE || recordType == DIAG_PATH){
				 cxOut.println("</Counterexamples>");
			 }
			cxOut.close();
		}
		
	}

	/**
	 * Returns the space which would be needed to keep all traces in the memory.
	 * 
	 * @return the memory used to store all traces in Bytes.
	 */
	public int getUsedMemory() {
		return memory;
	}

	private void writeCXFile(Trace trace) throws FileNotFoundException {
		if (cxOut == null) {
			if (cxFileName == null)
				return;
			cxOut = new PrintStream(new FileOutputStream(cxFileName+".traces.txt"));
			cxOut.println("Counterexample file");
			cxOut.println();
		}
		cxOut.println();
		cxOut.println("--------");
		cxOut.println("Diagnostic Path ");
		cxOut.println(trace.toString());
		cxOut.println("--------");
	}

	private void writeCX_XMLFile(Trace trace) throws FileNotFoundException {
		if (cxOut == null) {
			if (cxFileName == null)
				return;
			cxOut = new PrintStream(new FileOutputStream(cxFileName+".traces.xml"));
			cxOut.println("<Counterexamples>");
		}
		cxOut.println();
		cxOut.println("<DiagnosticPath>");
		cxOut.println("<Probability>" + trace.getSolutionValue()
				+ "</Probability>");
		cxOut.println("<Trace>" + trace.toXMLString() + "</Trace>");
		cxOut.println("</DiagnosticPath>");
	}

	
	protected int getType(){
		return recordType;
	}

	protected int setType(){
		return recordType;
	}
}
