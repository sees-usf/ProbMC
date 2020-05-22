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

package dipro.h.embedded;

import prism.PrismException;
import dipro.alg.BF;
import dipro.graph.Vertex;
import dipro.run.Context;

/** 
 * fail_sensors = i=2&s<MIN_SENSORS
 * @author hxa
 *
 */
public class DownHeuristic extends EmbeddedHeuristic {

	protected FailSensorsHeuristic sH;
	protected FailActuatorsHeuristic aH;
	protected FailIOHeuristic ioH;
	protected FailMainHeuristic mH;
	
	public DownHeuristic(Context settings, BF alg) throws PrismException {
		super(settings, alg);
		sH  = new FailSensorsHeuristic(settings, alg);
		aH  = new FailActuatorsHeuristic(settings, alg);
		ioH  = new FailIOHeuristic(settings, alg);
		mH  = new FailMainHeuristic(settings, alg);
	}

	/** This method computes the heuristic value of the given state. 
	 * The current heuristic computes an estimate of each failure mode: 
	 * sensor failure, actuator failure, input/output processor failure, 
	 * and main processor failure. The total heuristic estimate is the 
	 * defined as the sum of the individual estimates. The sum is 
	 * justified by the fact that all failure modes are independent of 
	 * each other. 
	 * @param the considered state
	 * @return the heuristic value of the state
	 */
	@Override
	public double evaluate(Vertex v) throws Exception {
		double hS = sH.evaluate(v);
		double hA = aH.evaluate(v);
		double hIO = ioH.evaluate(v);
		double hM = mH.evaluate(v);
//		double x = Math.max(sH.evaluate(v), aH.evaluate(v));
//		double y = Math.max(ioH.evaluate(v), mH.evaluate(v));
//		double h = Math.max(x,y);
		double h = hS + hA + hIO + hM;
//		System.out.println(v+": hS="+hS+" hA="+hA+" hIO="+hIO+" hM="+hM+" -> h="+h);
		h = h > 1.0f? 1.0f: h;
		return h;
	}
}
