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

import java.io.PrintStream;

import dipro.alg.BF;
import dipro.run.Registry;

public class AlgReporter extends Thread {

	protected BF alg;
	protected PrintStream out;
//	private long reportTime;

	public AlgReporter(BF alg, PrintStream out) {
		this.alg = alg;
		this.out = out;
		out.println(header());
//		this.reportTime = 0l;
	}

//	public void update(Observable o, Object arg) {
////		BF alg = (BF) o;
//		assert o == alg;
//		try {
//			report();
//		} catch (Exception e) {
//			Registry.getMain().handleError(e);
//		}
//	}

	public void run() {
		Object lock = new Object();
		int lastIter = -1;
		while(	alg.getStatus() != BF.TERMINATED &&
				alg.getStatus() != BF.CLEANED_UP) {
			if(	alg.getStatus() != BF.NOT_INITIALIZED) {
				synchronized(alg) {
					try {
						int i = alg.getNumIterations();
						if(lastIter< i) {
							report();
							lastIter = i;
						}
					} catch (Exception e) {
						Registry.getMain().handleError("Failure in reporting experimental data!", e);
					}
				}
				synchronized (lock) {
					try {
						lock.wait(10000);
					} catch (InterruptedException e) {
						Registry.getMain().handleError("Failure in reporting experimental data!", e);
					}
				}
			}
		}
	}

	protected String header() {
		return "I\t S\t E\t V\t N\t X\t CX_S\t CX_E\t RT\t Mem";
	}

	public void report() throws Exception {
//		long ttt = System.currentTimeMillis();
		long i = alg.getNumIterations();
		int s = alg.numVertices();
		int e = alg.numEdges();
		double v = alg.getSolutionValue(); 
		int x = alg.getSolutionSize();
		int n = alg.getNumSolutionTraces();
		int x1 = alg.getNumSolutionVertices();
		int x2 = alg.getNumSolutionEdges();
		long rt = alg.computeRuntime();
//		int am = alg.computeCurrentSearchMemory();
//		int amx = alg.getSearchMemoryPeak();
//		int mm = alg.getModelMemory();
//		int sm = alg.getSolutionMemory();
		int m = alg.getMemory();
		out.print(i);
		out.print("\t " + s);
		out.print("\t " + e);
		out.print("\t " + v);
//		out.print("\t " + av);
		out.print("\t " + n);
		out.print("\t " + x);
		out.print("\t " + x1);
		out.print("\t " + x2);
		out.print("\t " + rt);
//		out.print("\t " + am);
//		out.print("\t " + amx);
//		out.print("\t " + mm);
//		out.print("\t " + sm);
		out.print("\t " + m);
		out.println();
//		reportTime = reportTime + System.currentTimeMillis() - ttt;
//		System.out.println("Runtime = "+rt+", offtime = "+alg.getOffTime()+", Report Time = "+reportTime);
	}
}
