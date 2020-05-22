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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dipro.run.Registry;
import dipro.util.Trace;
import dipro.vis.Visualizer;

public class TracesPanel extends JPanel {

	private static final long serialVersionUID = 4866873892533645900L;
	protected Visualizer visualizer;
	protected LinkedList<Trace> traces;
	protected JPanel tracesListPanel;

	public TracesPanel(Visualizer visualizer) {
		this.visualizer = visualizer;
		traces = new LinkedList<Trace>();
		setBorder(BorderFactory.createTitledBorder("Selected Traces"));
		tracesListPanel = new JPanel();
		JScrollPane pane = new JScrollPane(tracesListPanel,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane.setPreferredSize(new Dimension(250, 100));
		pane.setSize(pane.getPreferredSize());
		// FormLayout layout = new FormLayout( "3dlu, 22dlu, 10dlu, 120dlu",
		// "p, 3dlu, p, 2dlu, p, 3dlu, p");
		JButton viewButton = new JButton();
		viewButton.setAction(new ViewBarChart());
		viewButton.setText("View Traces Barchart");
		FormLayout layout = new FormLayout("100dlu, 5dlu, 45dlu", "p, 3dlu, p");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(pane, cc.xyw(1, 1, 3));
		builder.add(viewButton, cc.xy(3, 3));
		add(builder.getPanel());
		update();
	}

	public void setTraces(LinkedList<Trace> t) {
		traces = t;
		update();
	}

	public void addTrace(Trace trace) {
		if (!traces.contains(trace)) {
			traces.add(trace);
			update();
		}
	}

	protected void update() {
		FormLayout layout = new FormLayout("133dlu, 5dlu, 12dlu");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);

		/* Add traces */
		if (traces.size() == 0) {
			builder.append(new JLabel("No traces selected yet"),
					new JLabel(" "));
		} else {
			for (Trace t : traces) {
				JButton delete = new JButton(new DeleteTrace(t));
				delete.setIcon(IconLoader.get("delete_edit.gif"));
				builder.append(new JLabel(t.toBoundedString(100)), delete);
			}

		}
		tracesListPanel.removeAll();
		tracesListPanel.add(builder.getPanel());
		// add(pane);
		// /* Set border */
		// pane.setBorder(BorderFactory.createTitledBorder("Selected Traces"));

		validate();
	}

	class ViewBarChart extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1158075277027239316L;

		public void actionPerformed(ActionEvent e) {
			try {
				DiagramFrame diag = new DiagramFrame(visualizer);
				diag.visualizeTraces(traces);
			} catch (Exception e1) {
				Registry.getMain().handleError("Failure while visualising a trace!", e1);
			}
		}

	}

	class DeleteTrace extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1158075277027239316L;

		protected Trace t;

		public DeleteTrace(Trace t) {
			super();
			this.t = t;
		}

		public void actionPerformed(ActionEvent e) {
			traces.remove(t);
			update();
			getRootPane().updateUI();
		}

	}

}
