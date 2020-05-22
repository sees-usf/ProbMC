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

import java.util.LinkedList;
import java.util.List;

import dipro.alg.BF;
import dipro.alg.BF.SearchMark;
import dipro.util.SearchTree;

public class VisSearchTree extends SearchTree {

	protected LinkedList<VisualizationEvent> visEvents;

	public VisSearchTree(BF alg) {
		super(alg);
		visEvents = new LinkedList<VisualizationEvent>();
	}

	protected void addEvent(VisualizationEvent event) {
		visEvents.addLast(event);
	}

	public List<VisualizationEvent> deliverVisEvents() {
		LinkedList<VisualizationEvent> l = visEvents;
		visEvents = new LinkedList<VisualizationEvent>();
		return l;
	}

	@Override
	public void close(SearchMark mark) {
		super.close(mark);
		VisInfo info = new VisInfo(mark);
		addEvent(new VisualizationEvent(VisualizationEvent.CLOSE_STATE_EVENT,
				info));
	}

	@Override
	public void open(SearchMark mark) {
		super.insertOpen(mark);
		VisInfo info = new VisInfo(mark);
		addEvent(new VisualizationEvent(VisualizationEvent.NEW_STATE_EVENT,
				info));
	}

	public void reopen(SearchMark oldMark, SearchMark newMark) {
		super.reopen(oldMark, newMark);
		VisInfo info = new VisInfo(newMark, oldMark);
		addEvent(new VisualizationEvent(VisualizationEvent.REOPEN_STATE_EVENT,
				info));
	}
}
