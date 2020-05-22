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

package dipro.h.cluster;

import prism.PrismException;
import dipro.alg.BF;
import dipro.graph.Vertex;
import dipro.h.Heuristic;
import dipro.h.PrismHeuristic;
import dipro.run.Context;
import dipro.run.PrismDefaultContext;
import dipro.stoch.prism.PrismState;

public class ClusterMinimumHeuristic extends PrismHeuristic {

	//Constants
	protected int N;
	protected int left_mx; 
	protected int right_mx;

	//minimum QOS requires 3/4*N connected workstations operational
	protected int k;

	//rates
	protected double ws_rate;
	protected double line_rate; // = 0.0002;
	protected double Toleft_rate; // = 0.00025;
	protected double Toright_rate; // = 0.00025;
	protected double inspect_rate;
	protected double repairWS_rate;
	protected double repairS_rate;
	protected double repairL_rate;
	
	protected OneClusterFailureH h1;
	protected OneClusterFailureH h2; 
	protected BothClusterFailureH h3; 
	
	public ClusterMinimumHeuristic(Context settings, BF alg) throws PrismException {
		super(settings, alg);
		N = (Integer)getConstValue("N");
		//k=floor(0.75*N);
		k = (Integer)getConstValue("k");
		left_mx = (Integer)getConstValue("left_mx");
		right_mx = (Integer)getConstValue("right_mx");
		ws_rate = 0.002d;
		line_rate= (Double)getConstValue("line_rate");
		Toleft_rate= (Double)getConstValue("Toleft_rate");
		Toright_rate= (Double)getConstValue("Toright_rate");
		inspect_rate = 10.0;
		repairWS_rate = 2.0;
		repairS_rate = 0.25;
		repairL_rate = 0.125;
		
		 h1 = new OneClusterFailureH(settings, alg); 
		 h2 = new OneClusterFailureH(settings, alg); 
		 h2.setLeft(false); 
		 h3 = new BothClusterFailureH(settings, alg); 
	}
	
	public double evaluate(Vertex v) throws Exception {
		// label "minimum" = (left_n>=k & Toleft_n) | (right_n>=k & Toright_n) | ((left_n+right_n)>=k & Toleft_n & line_n & Toright_n);
		// !"minimum" =  (left_n<k | !Toleft_n) & (right_n<k | !Toright_n) & ((left_n+right_n)<k | !Toleft_n | !line_n | !Toright_n);
//		double h = Math.min(h1.evaluate(v), Math.min(h2.evaluate(v), h3.evaluate(v)));
		double h = h1.evaluate(v) * h2.evaluate(v) * h3.evaluate(v);
//		System.out.println(v+": h="+h);
		return h;
	}

	protected double e(int ws, boolean r, boolean line_n, boolean toLeft_n, boolean toRight_n) {
//		return (n * ws_rate +line_rate+Toleft_rate+Toright_rate);
		double e = 0.0;
		e = e + ws*ws_rate;
		if(r) e = e + repairWS_rate;
		else e = e + inspect_rate;
		if(line_n) e = e + line_rate;
		else {
			if(r) e = e + repairL_rate;
			else e = e + inspect_rate;
		}
		if(toLeft_n) e = e + Toleft_rate;
		else {
			if(r) e = e + repairS_rate;
			else e = e + inspect_rate;
		}
		if(toRight_n) e = e + Toright_rate;
		else {
			if(r) e = e + repairS_rate;
			else e = e + inspect_rate;
		}
		return e;
	}
	
	class OneClusterFailureH extends PrismHeuristic {
		
		boolean left = true;
		
		protected OneClusterFailureH(Context settings, BF alg) {
			super(settings, alg);
		}
		
		void setLeft(boolean left) {
			this.left = left; 
		}
		
		public double evaluate(Vertex v) throws PrismException {
			PrismState s = (PrismState)v; 
			int n = (Integer)getValue(s, (left? "left_n":"right_n"));
			int n2 = (Integer)getValue(s, (!left? "left_n":"right_n"));
			boolean r = (Boolean)getValue(s, "r");
			boolean line_n = (Boolean)getValue(s, "line_n");
			boolean Toleft_n = (Boolean)getValue(s,"Toleft_n");
			boolean Toright_n = (Boolean)getValue(s, "Toright_n");
			boolean swOn = left? Toleft_n : Toright_n; 
			if(n<k || !swOn) return 1.0d;
			double h1 = (left? Toleft_rate: Toright_rate)/e(n+n2, r, line_n, Toleft_n, Toright_n);
			double h2 = 1.0d;
			for(int i= n; i>=k; i--) {
				
				double e = e(i+n2,r, line_n, Toleft_n, Toright_n);
				h2 = h2 * (i*ws_rate/e);
			}
//			System.out.println(s+": h1="+h1+" h2="+h2);
			double h = Math.min(1.0d, h1+h2);
//			double h = Math.max(h1, h2);
			assert h>=0.0d && h<=1.0d;
			return h;
		}
	}
	
	class BothClusterFailureH extends PrismHeuristic {

		protected BothClusterFailureH(Context settings, BF alg) {
			super(settings, alg);
		}
		
		public double evaluate(Vertex v) throws PrismException {
			PrismState s =(PrismState)v;
			int left_n = s.values().getIntValueOf("left_n"); 
			int right_n = s.values().getIntValueOf("right_n");
			int n = left_n + right_n; 
			boolean r = (Boolean)getValue(s, "r");
			boolean line_n = (Boolean)getValue(s, "line_n");
			boolean Toleft_n = (Boolean)getValue(s,"Toleft_n");
			boolean Toright_n = (Boolean)getValue(s, "Toright_n");
			if(n<k || (!Toleft_n && !Toright_n) || !line_n) return 1;
			double h1 = line_n? 1.0d: line_rate / e(n, r, line_n, Toleft_n, Toright_n);
			double h2 = Toleft_n? 1.0d: Toleft_rate / e(n, r, line_n, Toleft_n, Toright_n);
			double h3 = Toright_n? 1.0d: Toright_rate / e(n, r, line_n, Toleft_n, Toright_n);
			double h4 = 1.0d;
			for(int i= n; i>=k; i--) {
				h4 = h4 * (i*ws_rate/e(i, r, line_n, Toleft_n, Toright_n));
			}
			double h5 = Math.min(h2, h3);
//			System.out.println(s+": h1="+h1+" h5="+h5+" h4="+h4);
			double h = Math.min(1.0d, h1+h5+h4);
//			double h = Math.max(Math.max(h1, h2), Math.max(h3, h4));
			assert h>=0.0d && h<=1.0d;
			return h;
		}
	}
}
