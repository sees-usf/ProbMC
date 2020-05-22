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

package dipro.vis.jung;

import java.awt.Paint;
import java.awt.Shape;

import javax.swing.Icon;

import org.apache.commons.collections15.functors.ConstantTransformer;

import dipro.graph.Vertex;
import dipro.vis.gui.IconLoader;
import edu.uci.ics.jung.visualization.util.VertexShapeFactory;

public class JungVertexRealizer {

	static final double maxExtraWidth = 100;
	static final double maxExtraHeight = 100;
	static final double normalSize = 10;
	static final String FIRST_OPEN_ICON = "expansion_arrow_32.gif";
	static final String SECOND_OPEN_ICON = "expansion_arrow_24.gif";
	static final String THIRD_OPEN_ICON = "expansion_arrow_16.gif";

	protected JungVisualizer visualizer;
	protected Vertex vertex;
	protected VertexShapeFactory<Vertex> shapeFactory;
	protected String icon;

	@SuppressWarnings("unchecked")
	protected JungVertexRealizer(JungVisualizer vis, Vertex v) {
		this.visualizer = vis;
		this.vertex = v;
		shapeFactory = new VertexShapeFactory<Vertex>(new ConstantTransformer(
				normalSize), new ConstantTransformer(1.0f));
	}

	public Vertex getDiProVertex() {
		return vertex;
	}

	public Shape getShape() {
		Shape shape = shapeFactory.getRoundRectangle(vertex);
		double c = visualizer.getSolutionCardinality(vertex);
		if (c > 0) {
			int s = (int) (normalSize + visualizer.getGradient() * c
					/ visualizer.getSolutionValue());
			shapeFactory = new VertexShapeFactory<Vertex>(
					new ConstantTransformer(s), new ConstantTransformer(1.0f));
		}
		icon = "";
		if (visualizer.getSearchTree().isOpen(vertex) != null) {
			int pos = visualizer.getPositionInQueue(vertex);
			if (pos < 3) {
				switch (pos) {
				case 0:
					icon = FIRST_OPEN_ICON;
					break;
				case 1:
					icon = SECOND_OPEN_ICON;
					break;
				case 2:
					icon = THIRD_OPEN_ICON;
					break;
				}
			}
		} else {
			shape = shapeFactory.getEllipse(vertex);
		}
		if (visualizer.getContext().getStart().equals(vertex)) {
			shape = shapeFactory.getRegularPolygon(vertex, 6);
		}
		if (visualizer.isTargetVertex(vertex)) {
			shape = shapeFactory.getRegularPolygon(vertex, 4);
		}

		return shape;
	}

	public Paint getColor() {
		boolean b = visualizer.belongsToSolution(vertex);
		if (b)
			return JungVisualizer.solColor;
		return visualizer.color(visualizer.getSearchTree().f(vertex));
	}

	public String getIcon() {
		if (!icon.isEmpty()) {
			return "<html><img src=" + "etc"
					+ System.getProperty("file.separator") + icon
					+ ">";
		}
		return "";
	}

}
