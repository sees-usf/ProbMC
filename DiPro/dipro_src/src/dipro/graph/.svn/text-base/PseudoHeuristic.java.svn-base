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

package dipro.graph;

import java.io.BufferedReader;
import java.io.FileReader;

import dipro.alg.BF;
import dipro.h.Heuristic;
import dipro.run.Context;

public class PseudoHeuristic extends Heuristic {

	private String dirName;

	public PseudoHeuristic(Context context, BF alg) {
		super(context, alg);
		dirName = ((ExternDirectedGraph) this.context.getGraph()).getDirName();
	}

	@Override
	public double evaluate(Vertex v) throws Exception {
		int id = ((DefaultVertex) v).getId();
		int x = id / 1000;
		x = x * 1000;
		BufferedReader in = new BufferedReader(new FileReader(dirName
				+ "/heuristic_" + x + ".txt"));
		String line = in.readLine();
		double h = -1.0d;
		while (line != null) {
			String[] tokens = line.split("\t");
			int i = Integer.parseInt(tokens[0]);
			if (id == i) {
				h = Double.parseDouble(tokens[1]);
				break;
			}
			line = in.readLine();
		}
		in.close();
		assert h != -1.0d;
		return h;
	}

}
