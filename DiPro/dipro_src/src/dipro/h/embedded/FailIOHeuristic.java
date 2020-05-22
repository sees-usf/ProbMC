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
import dipro.stoch.UniformCTMC;
import dipro.stoch.prism.PrismState;

/** 
 * fail_sensors = i=2&s<MIN_SENSORS
 * @author hxa
 *
 */
public class FailIOHeuristic extends EmbeddedHeuristic {


	public FailIOHeuristic(Context settings, BF alg) throws PrismException {
		super(settings, alg);
	}

	public double evaluate(Vertex v) throws Exception {
		int s = ((PrismState) v).values().getIntValueOf(str_s);
		int i = ((PrismState) v).values().getIntValueOf(str_i);
		int o = ((PrismState) v).values().getIntValueOf(str_o);
		int a = ((PrismState) v).values().getIntValueOf(str_a);
		int m = ((PrismState) v).values().getIntValueOf(str_m);
		int count = ((PrismState) v).values().getIntValueOf(str_count);
		double e = e(s, i, o, a, m);
		if(count==MAX_COUNT+1) return 1.0d;
		double h1 = i>0 ?  (lambda_p)/e : 1.0;
		double h2 = o>0 ?  (lambda_p)/e : 1.0;
		//hf: Failure of I- or O- processor
		double hf = Math.min(1.0d, h1+h2);
		
		// h2: transient fault of O- or I-Processor until MAX_COUNT timeouts
		h1 = i==2? delta_f/e : 1.0d;
		h2 = o==2? delta_f/e : 1.0d;
		double ht = Math.min(1.0d, h1+h2);
		for(int k=count; k<=MAX_COUNT; k++) {
			e = e(s,1,1,a,m);
			ht = ht*tau/e;
		}
		double h = Math.min(1.0d, ht+hf);
		return h;
	}
	
//	public double evaluate(Vertex v) throws Exception {
//		int i = ((PrismState) v).values().getIntValueOf(str_i);
//		int o = ((PrismState) v).values().getIntValueOf(str_o);
//		int count = ((PrismState) v).values().getIntValueOf(str_count);
//		if(count==MAX_COUNT+1) return 1.0d;
//		double hi = 1.0d; 
//		double ho = 1.0d;
//		if(i>0) hi = 0.5d;
//		if(o>0) ho = 0.5d;
//		double hf = Math.max(hi, ho);
//		if(hf==1.0d) return hf;
//		hi = 0.9d; 
//		ho = 0.9d;
//		if(i==2) hi = 0.5d;
//		if(o==2) ho = 0.5d;
//		double ht = Math.max(hi, ho);
//		ht = Math.pow(ht, MAX_COUNT+1-count);
//		double h = Math.max(hf, ht);
//		return h;
//	}
}
