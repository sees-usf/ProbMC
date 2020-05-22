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

package dipro.vis;

import dipro.util.SearchTree;

/**
 * The instances of this class represent changes which occur in the
 * {@link SearchTree ExploredSpace}. These objects are used as messages
 * which are sent to the {@link  dipro.vis.Visualizer Visualizer} observing the
 * <tt>ExploredSpace</tt>. The type <tt>Change</tt> is an <tt>Integer</tt>
 * identifying the type of the change, e.g. new <tt>Vertex</tt>, close
 * <tt>Vertex</tt>, etc... This class contains constants identifying all
 * possible update operation of <tt>ExploredSpace</tt>. The data of the
 * <tt>Change</tt> object contains all the information needed by the
 * Visualizer to react correctely on this <tt>Change</tt>.
 * 
 * @author aljazzar
 * 
 */
// public class VisualizationEvent implements Comparable<VisualizationEvent> {
public class VisualizationEvent {

	// private static final int CLUSTER_EVENT_CATEGORY= 10;
	// private static final int STATE_EVENT_CATEGORY= 20;
	// private static final int TRANSITION_EVENT_CATEGORY= 30;
	// private static final int INTER_CLUSTER_CONNECTION_EVENT_CATEGORY= 40;
	// private static final int SOLUTION_EVENT_CATEGORY= 50;

	// // First: Cluster Events.
	// public static final int NEW_CLUSTER_EVENT = CLUSTER_EVENT_CATEGORY+1;
	// public static final int MODIFIED_CLUSTER_EVENT =
	// CLUSTER_EVENT_CATEGORY+2;
	// public static final int DISCARDED_CLUSTER_EVENT =
	// CLUSTER_EVENT_CATEGORY+3;

	// Secound: State Events
	public static final int CLOSE_STATE_EVENT = 1;
	public static final int NEW_STATE_EVENT = 2;
	public static final int RELAX_OPEN_STATE_EVENT = 3;
	public static final int REOPEN_STATE_EVENT = 4;
	public static final int INITIAL_STATE_EVENT = 5;

	// Third: Transition Events
	public static final int NEW_TRANSITION_EVENT = 11;
	public static final int DISCARDED_TRANSITION_EVENT = 12;

	// //Fourth: Inter Cluster Connection Events
	// public static final int NEW_INTER_CLUSTER_CONNECTION_EVENT =
	// INTER_CLUSTER_CONNECTION_EVENT_CATEGORY+1;
	// public static final int MODIFIED_INTER_CLUSTER_CONNECTION_EVENT =
	// INTER_CLUSTER_CONNECTION_EVENT_CATEGORY+2;
	// public static final int DISCARDED_INTER_CLUSTER_CONNECTION_EVENT =
	// INTER_CLUSTER_CONNECTION_EVENT_CATEGORY+3;

	// //Fifth: Solution Marking Events
	// public static final int MARK_SOLUTION_REGULAR_STATE_EVENT =
	// SOLUTION_EVENT_CATEGORY+1;
	// public static final int MARK_SOLUTION_TARGET_STATE_EVENT =
	// SOLUTION_EVENT_CATEGORY+2;
	// public static final int MARK_SOLUTION_TRANSITION_EVENT =
	// SOLUTION_EVENT_CATEGORY+3;
	//	

	protected Integer type;
	protected VisInfo info;

	public VisualizationEvent(Integer type, VisInfo info) {
		this.type = type;
		this.info = info;
	}

	public Integer getEventType() {
		return type;
	}

	// public Object data() {
	// return data;
	// }

	public VisInfo getVisInfo() {
		return info;
	}

	public String toString() {
		return type.toString() + ": " + info.toString();
	}

	// public int compareTo(VisualizationEvent event) {
	// int r = type - event.type;
	// if(r!=0) r = r/ Math.abs(r);
	// return r;
	// }

	public boolean equals(Object o) {
		if (!(o instanceof VisualizationEvent))
			return false;
		VisualizationEvent event = (VisualizationEvent) o;
		return type == event.type && info.equals(event.info);
	}

	// public boolean isStateEvent() {
	// return type > STATE_EVENT_CATEGORY && type < TRANSITION_EVENT_CATEGORY;
	// }
	//	
	// public boolean isClusteringEvent() {
	// return type > CLUSTER_EVENT_CATEGORY && type < STATE_EVENT_CATEGORY &&
	// type > INTER_CLUSTER_CONNECTION_EVENT_CATEGORY && type <
	// SOLUTION_EVENT_CATEGORY;
	// }
}
