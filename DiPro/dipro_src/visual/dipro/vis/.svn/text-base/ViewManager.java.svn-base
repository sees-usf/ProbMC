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
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTabbedPane;

public final class ViewManager extends Observable implements Observer {

	public static final String TYPE_INTERACTIVE = new String("TYPE_INTERACTIVE");
	public static final String TYPE_STATIC = new String("TYPE_STATIC");

	private LinkedList<View> views;

	private JTabbedPane pane;

	private static ViewManager instance = null;

	private ViewManager() {
		views = new LinkedList<View>();
		pane = new JTabbedPane();
	}

	public void add(View v) {
		add(v.getName(), v);
	}

	public void add(String title, View v) {
		if (!views.contains(v)) {
			views.add(v);
			pane.addTab(title, v.getIcon(), v.getViewer());
			pane.getRootPane().updateUI();
		}

	}

	public void reset() {
		views.clear();
		pane.removeAll();
	}

	public static ViewManager getInstance() {
		if (instance == null) {
			instance = new ViewManager();
		}
		return instance;
	}

	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

	public JTabbedPane getPane() {
		return pane;
	}

}
