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

package dipro.run;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import dipro.alg.BF;
import dipro.vis.AbstractVisualizer;
import dipro.vis.VisualizationEvent;

public class DummyVisualizer extends AbstractVisualizer {

	private static JLabel dummyLabel = new JLabel("No visualization view is loaded!");
	private JSplitPane dummyPane; 
	
	public DummyVisualizer(VisContext context, BF alg) {
		super(context, alg);

		dummyPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		Dimension d = ((VisMain) Registry.getMain()).getGUI().getSize();
		dummyPane.setPreferredSize(new Dimension(d.width, (int) (d.height * 0.7)));
		dummyPane.setSize(dummyPane.getPreferredSize());
		dummyPane.setResizeWeight(1.0f);
		d = dummyPane.getPreferredSize();
//		view.setPreferredSize(new Dimension(d.width - 350, d.height));
//		view.setSize(view.getPreferredSize());
		JPanel jpanel = new JPanel();
		jpanel.add(dummyLabel);
		dummyPane.setLeftComponent(jpanel);
//		infoPanel = new JPanel();
//		d = rootPane.getPreferredSize();
//		infoPanel.setPreferredSize(new Dimension(310, d.height));
//		JScrollPane sp = new JScrollPane(infoPanel);
//		sp.setPreferredSize(new Dimension(350, d.height));
//		sp.setSize(sp.getPreferredSize());
//		sp.setMaximumSize(sp.getPreferredSize());
//		rootPane.setRightComponent(sp);
//		rootPane.setDividerLocation(view.getPreferredSize().width);
//		toolFrame = new ToolFrame();
//		(((VisMain) Registry.getMain()).getGUI()).setToolFrame(toolFrame);
//		dummyPanel = new JSplitPane();
//		dummyPanel.
//		dummyPanel.add(dummyLabel);
	}

	@Override
	protected void handleCloseStateEvent(VisualizationEvent event)
			throws Exception {
	}

	@Override
	protected void handleInitialStateEvent(VisualizationEvent event)
			throws Exception {
	}

	@Override
	protected void handleNewStateEvent(VisualizationEvent event)
			throws Exception {
	}

	@Override
	protected void handleRelaxOpenStateEvent(VisualizationEvent event)
			throws Exception {
	}

	@Override
	protected void handleReopenStateEvent(VisualizationEvent event)
			throws Exception {
	}

	@Override
	public void exportImage() throws Exception {
	}

	@Override
	public Component getVisualizationComponent() {
		return dummyPane;
	}

	@Override
	public double normalize(double f) {
		return f;
	}

	@Override
	public void update() {
	}

	@Override
	public void visualizeFromScratch() {
	}

}
