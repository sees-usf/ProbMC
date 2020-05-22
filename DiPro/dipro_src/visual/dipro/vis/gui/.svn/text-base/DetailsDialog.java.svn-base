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

package dipro.vis.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import dipro.graph.DirectedEdge;
import dipro.graph.Vertex;
import dipro.run.Registry;
import dipro.run.VisMain;

public abstract class DetailsDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4198680151344382842L;
	protected Vertex v1;
	protected Vertex v2;
	protected DirectedEdge e;

	public DetailsDialog() {
		super(((VisMain) Registry.getMain()).getGUI());
	}

	public void selectVertex(Vertex v) {
		v1 = v;
		v2 = null;
		e = null;
		update();
	}

	public void compareVertices(Vertex v1, Vertex v2) {
		this.v1 = v1;
		this.v2 = v2;
		e = null;
		update();
	}

	public void selectEdge(DirectedEdge e) {
		this.v1 = e.source();
		this.v2 = e.target();
		this.e = e;
		update();
	}

	public void actionPerformed(ActionEvent e) {
	}

	protected abstract void update();
}
