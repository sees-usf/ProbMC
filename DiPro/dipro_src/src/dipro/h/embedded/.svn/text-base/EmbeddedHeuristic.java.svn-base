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
import dipro.h.PrismHeuristic;
import dipro.run.Context;

public class EmbeddedHeuristic extends PrismHeuristic {

	//constants
	protected int MAX_COUNT;
	protected int  MIN_SENSORS;
	protected int  MIN_ACTUATORS;

	// rates
	protected double  lambda_p; //= 1/(365*24*60*60); // 1 year
	protected double  lambda_s; //= 1/(30*24*60*60); // 1 month
	protected double  lambda_a; //= 1/(2*30*24*60*60); // 2 months
	protected double  tau; //= 1/60; // 1 min
	protected double  delta_f; //= 1/(24*60*60); // 1 day
	protected double  delta_r; //= 1/30; // 30 secs
	
	//state variables
	//s : [0..3] init 3; // number of sensors working
	protected static final String str_s = "s";
	//i : [0..2] init 2; // 2=ok, 1=transient fault, 0=failed
	protected static final String str_i = "i";
	//a : [0..2] init 2; // number of actuators working
	protected static final String str_a = "a";
	//m : [0..1] init 1; // 1=ok, 0=failed
	protected static final String str_m = "m";
	//count : [0..MAX_COUNT+1] init 0; // number of consecutive skipped cycles
	protected static final String str_count = "count";
	//o : [0..2] init 2; // 2=ok, 1=transient fault, 0=failed
	protected static final String str_o = "o";
	// flags
	// main processor has processed data from input processor
	// and sent corresponding instructions to output processor (since last timeout)
	//comp : bool init true;
	protected static final String str_comp = "comp";
	// input processor has data ready to send
	//reqi : bool init true;
	protected static final String str_reqi = "reqi";
	// output processor has instructions ready to be processed
	//reqo : bool init false;
	protected static final String str_reqo = "reqo";
	
	public EmbeddedHeuristic(Context settings, BF alg) throws PrismException {
		super(settings, alg);
		MAX_COUNT = (Integer) getConstValue("MAX_COUNT");
		MIN_SENSORS = (Integer) getConstValue("MIN_SENSORS");
		MIN_ACTUATORS = (Integer) getConstValue("MIN_ACTUATORS");
		lambda_p= (Double) getConstValue("lambda_p");
		lambda_s= (Double) getConstValue("lambda_s");
		lambda_a= (Double) getConstValue("lambda_a");
		tau= (Double) getConstValue("tau");
		delta_f= (Double) getConstValue("delta_f");
		delta_r=(Double) getConstValue("delta_r");
	}
	
	/** Estimate of the total exit rate in a state with the given variable values. 
	 * 
	 * @param s the value of the variable s (sensor status) in the state
	 * @param i the value of the variable i (input processor) in the state
	 * @param o the value of the variable o (output processor) in the state
	 * @param a the value of the variable a (actuator status) in the state
	 * @param m the value of the variable m (main processor) in the state
	 * @return an under-estimate of the total exit rate in a state with the given variable values
	 * @throws PrismException
	 */
	protected double e(int s, int i, int o, int a, int m) throws PrismException {
		//int count = v.values().getIntValueOf(str_count);
		double e = 0.0d;
		//s>1 -> s*lambda_s
		if(s>1) e+= s*lambda_s;
		//i>0 & s>=MIN_SENSORS -> lambda_p 
		if(i>0 && s>=MIN_SENSORS) e+=lambda_p;
		//i=2 & s>=MIN_SENSORS -> delta_f
		if(i==2 && s>=MIN_SENSORS) e+=delta_f;
		//i=1 & s>=MIN_SENSORS -> delta_r
		if(i==1 && s>=MIN_SENSORS) e+=delta_r;
		//For o, similar to i
		if(o>0 && s>=MIN_ACTUATORS) e+=lambda_p;
		if(o==2 && s>=MIN_ACTUATORS) e+=delta_f;
		if(o==1 && s>=MIN_ACTUATORS) e+=delta_r;
		//a>0 -> a*lambda_a :
		if(a>0) e+=a*lambda_a;
		//m=1 -> lambda_p :
		if(m==1) e+=lambda_p;
		//comp -> tau : 
		//!comp -> tau :
		e+=2*tau;

		return e;
	}
}
