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

package dipro.run.wizard;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class ExchangeablePanel extends JPanel {
	private ArrayList<PartPanel> panelSet;
	private int position = 0;

	public ExchangeablePanel(ArrayList<PartPanel> set) {
		panelSet = set;
		add(set.get(position));
	}

	void addPanel(PartPanel panel) {
		panelSet.add(panel);
	}

	synchronized void nextPanel() {
		if (position < panelSet.size() - 1) {
			removeAll();
			add(panelSet.get(++position));
			validate();

		}
	}

	synchronized void backPanel() {
		if (position > 0) {
			removeAll();
			add(panelSet.get(--position));
			validate();
		}
	}

	boolean isLast() {
		if (position == (panelSet.size() - 1))
			return true;
		else
			return false;
	}

	boolean isFirst() {
		if (position == 0)
			return true;
		else
			return false;

	}

	PartPanel current() {
		return panelSet.get(position);

	}
}
