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

package dipro.h;

import prism.PrismException;
import dipro.alg.BF;
import dipro.graph.Vertex;
import dipro.run.Context;
import dipro.run.PrismDefaultContext;
import dipro.stoch.prism.PrismState;

public class PrismHeuristic extends Heuristic {

	public PrismHeuristic(Context settings, BF alg) {
		super(settings, alg);
	}

	public double evaluate(Vertex v) throws Exception {
		return 1.0d;
	}

	protected Object getConstValue(String constName) throws PrismException {
		try {
			return prismContext().getPrismModel().constantValues().getValueOf(
					constName);
		} catch (Exception e) {
			throw new PrismException("Error while loading value for constant "
					+ constName);
		}
	}

	protected Object getValue(PrismState s, String name) throws PrismException {
		return s.values().getValueOf(name);
	}

	protected PrismDefaultContext prismContext() {
		return (PrismDefaultContext) context;
	}
}
