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

package dipro.alg;

import dipro.util.Proposition;

public class BFStar extends BF {

	int counter = 0; 
	int distThreshold = 0;
	
	protected BFStar() {
		super();
	}

	protected void handleRelaxation(RelaxationInfo relax) throws Exception {
		logRelaxation(relax);
//		switch (relax.flag()) {
//		case RelaxationInfo.NEW_VERTEX:
//			searchTree.incrementNumEdges(1);
//			break;
//		default:
//			break;
//		}
	}

	protected void vertexToExpand(SearchMark uMark) throws Exception {
		super.vertexToExpand(uMark);
		int x = property.check(uMark.vertex());
		if (x == Proposition.TRUE) {
			newTargetFound(uMark);
		}
		/* < ForDebugging > 
		if(uMark.f() >= distThreshold) {
			USRoadNode n1 = (USRoadNode)start;
			USRoadNode n2 =(USRoadNode)uMark.v;
			if(n2.getLongitude()>= -73800000 && n2.getLatitude()<= 40800000 && n2.getLatitude()>= 40600000) {
//			if(node.getLatitude()<= 40700000) {
//			if(n2.getLatitude()<= 30000000) {
				double ad = AirLineHeuristic.computeAirlineDistanceCosineLow(n1, n2);
//				System.out.println((uMark.f()/10)+": "+uMark.v+" (air distance = "+ad+")");
				double lat = n2.getLatitude()/1000000.0d;
				double lon = n2.getLongitude()/1000000.0d;
				System.out.println((uMark.f()/10)+": \t"+n2.getId()+" \t"+lat+" \t"+lon+" \t"+ad);
				counter++;
			}
//			else {
//				System.out.println("No "+uMark.v);
//			}
			if(counter>=20) {
				System.out.println();
				distThreshold = distThreshold + 100000;
				counter = 0;
			}
		}
		/* </ ForDebugging > */
	}

}
