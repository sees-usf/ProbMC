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

import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import parser.Values;
import prism.PrismException;
import dipro.graph.DirectedEdge;
import dipro.graph.Vertex;
import dipro.run.Registry;
import dipro.stoch.prism.PrismState;
import dipro.stoch.prism.PrismTransition;
import dipro.stoch.prism.PrismTransitionData;
import dipro.vis.AbstractVisualizer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.picking.PickedInfo;
import edu.uci.ics.jung.visualization.util.VertexShapeFactory;

//public class CXRenderContex extends JungRenderContext {
public class RenderContextFactory {

	private VertexShapeFactory<Vertex> shapeFactory;
	private JungVisualizer visualizer;
	private static final double normalSize = 20;
	protected static final int normalFontSize = 6;

	private static final String FIRST_OPEN_ICON = "expansion_arrow_32.gif";
	private static final String SECOND_OPEN_ICON = "expansion_arrow_24.gif";
	private static final String THIRD_OPEN_ICON = "expansion_arrow_16.gif";

	protected String icon;

	public RenderContextFactory(JungVisualizer visualizer) {
		this.visualizer = visualizer;
		shapeFactory = new VertexShapeFactory<Vertex>(new ConstantTransformer(
				10), new ConstantTransformer(1.0f));
	}

	public Transformer<DirectedEdge, String> getEdgeToolTipTransformer() {

		return new Transformer<DirectedEdge, String>() {

			@Override
			public String transform(DirectedEdge e) {
				StringBuilder text = new StringBuilder("<html>");
				PrismTransition t = (PrismTransition) e;
				PrismTransitionData tData = t.getTransitionData();
				text.append("<table>");
				text.append("<tr>");
				text.append("<th colspan=\"2\" align=\"center\">");
				text.append(t.toString());
				text.append("</th>");
				text.append("</tr>");

				text.append("<tr>");
				text.append("<td align=\"right\"> Weight: </td>");
				text.append("<td align=\"left\">");
				text.append(t.getProbOrRate());
				text.append("</td>");
				text.append("</tr>");

				text.append("<tr>");
				text.append("<td align=\"right\"> Action: </td>");
				text.append("<td align=\"left\">");
				text.append(tData.getLabel());
				text.append("</td>");
				text.append("</tr>");

				text.append("<tr>");
				text.append("<td align=\"right\"> Module: </td>");
				text.append("<td align=\"left\">");
				text.append(tData.getModuleName());
				text.append("</td>");
				text.append("</tr>");

				text.append("<tr>");
				text.append("<td align=\"right\"> Assignment: </td>");
				text.append("<td align=\"left\">");
				text.append(tData.getAssignment());
				text.append("</td>");
				text.append("</tr>");

				text.append("</table>");
				text.append("</html>");
				return text.toString();
			}

		};
	}

	public Transformer<Vertex, String> getVertexToolTipTransformer() {

		return new Transformer<Vertex, String>() {

			public String transform(Vertex v) {
				StringBuilder text = new StringBuilder("<html>");

				double f = visualizer.getSearchTree().f(v);
				double g = visualizer.getSearchTree().g(v);
				double h = visualizer.getSearchTree().h(v);
				double c = visualizer.getSolutionCardinality(v);
				text.append("<table>");
				text.append("<tr>");
				text.append("<td align=\"right\"><em> f Value: </em></td>");
				text.append("<td align=\"left\"><b>");
				text.append(AbstractVisualizer.getFormat().format(f));
				text.append("</b></td>");
				text.append("</tr>");
				text.append("<tr>");
				text.append("<td align=\"right\"><em> g Value: </em></td>");
				text.append("<td align=\"left\"><b>");
				text.append(AbstractVisualizer.getFormat().format(g));
				text.append("</b></td>");
				text.append("</tr>");
				text.append("<tr>");
				text.append("<td align=\"right\"><em> h Value: </em></td>");
				text.append("<td align=\"left\"><b>");
				text.append(AbstractVisualizer.getFormat().format(h));
				text.append("</b></td>");
				text.append("</tr>");
				text.append("<tr>");
				text.append("<td align=\"right\"><em> Sol Card: </em></td>");
				text.append("<td align=\"left\"><b>");
				text.append(AbstractVisualizer.getFormat().format(c));
				text.append("</b></td>");
				text.append("</tr>");
				Values values = ((PrismState) v).values();
				Collection<String> relevantVars;
				try {
					relevantVars = visualizer.getContext().getProperty()
							.relevantLabels();
				} catch (Exception e) {
					relevantVars = new LinkedList<String>();
					Registry.getMain().handleError(
							"Failed to get the property relevant labels!", e);
				}
				for (String name : relevantVars) {
					text.append("<tr>");
					text.append("<td align=\"right\"><b>");
					text.append(name);
					text.append("= ");
					text.append("</b></td>");
					text.append("<td align=\"left\"><b>");
					try {
						text.append(values.getValueOf(name));
					} catch (PrismException e) {
						text.append("Unknown");
					}
					text.append("</b></td>");
					text.append("</tr>");
				}

				for (int i = 0; i < values.getNumValues(); i++) {
					String name = values.getName(i);
					if (!relevantVars.contains(name)) {
						text.append("<tr>");
						text.append("<td align=\"right\">");
						text.append(name);
						text.append("= ");
						text.append("</td>");
						text.append("<td align=\"left\">");
						text.append(values.getValue(i));
						text.append("</td>");
						text.append("</tr>");
					}
				}
				text.append("</table>");
				text.append("</html>");
				return text.toString();
			}

		};
	}

	public Transformer<DirectedEdge, String> getEdgeLabelTransformer() {

		return new Transformer<DirectedEdge, String>() {

			@Override
			public String transform(DirectedEdge edge) {
				PrismTransitionData tData = ((PrismTransition) edge)
						.getTransitionData();
				String lbl = tData.getLabel();
				if (lbl == null) {
					System.out.println("label==null\n " + tData);
				}
				assert lbl.length() >= 2 && lbl.charAt(0) == '['
						&& lbl.charAt(lbl.length() - 1) == ']';
				lbl = lbl.substring(1, lbl.length() - 1);
				if (!lbl.equals(""))
					return lbl;
				return "";
			}
		};
	}

	// public Transformer<Vertex, String> getVertexLabelTransformer() {
	//
	// return new Transformer<Vertex, String>() {
	//
	// public String transform(Vertex arg0) {
	// return "";
	// }
	//
	// };
	// }

	public Transformer<DirectedEdge, Shape> getEdgeShapeTransformer() {
		return new Transformer<DirectedEdge, Shape>() {
			@Override
			public Shape transform(DirectedEdge e) {
				// Vertex v = e.getVertices().value();
				// double f = visualizer.getSearchTree().f(v);
				// int l = (int) Math.max(20, visualizer.normalize(f) * 100
				// *visualizer.getScale());
				// Line<Vertex, DirectedEdge> line = new EdgeShape.Line<Vertex,
				// DirectedEdge>();
				// line.setControlOffsetIncrement(l);
				// return (Shape) line;
				return (Shape) new EdgeShape.Line<Vertex, DirectedEdge>();
			}
		};
	}

	public Transformer<DirectedEdge, Paint> getEdgeFillPaintTransformer() {
		return getEdgeDrawPaintTransformer();
	}

	public Transformer<DirectedEdge, Paint> getEdgeDrawPaintTransformer() {

		return new Transformer<DirectedEdge, Paint>() {

			public Paint transform(DirectedEdge edge) {

				PickedInfo<DirectedEdge> info = visualizer.view
						.getPickedEdgeState();
				if (info.isPicked(edge))
					visualizer.showEdgeInfo(edge);
				visualizer.getView().getPickedEdgeState().pick(edge, false);
				double f = visualizer.getSearchTree().f(edge.target());
				boolean b = visualizer.belongsToSolution(edge);
				if (b)
					return JungVisualizer.solColor;
				return visualizer.color(f);
			}
		};
	}

	public Transformer<DirectedEdge, Font> getEdgeFontTransformer() {
		// TODO Auto-generated method stub
		return new Transformer<DirectedEdge, Font>() {

			@Override
			public Font transform(DirectedEdge edge) {
				double c = visualizer.getSolutionCardinality(edge);
				if (c > 0) {
					int size = normalFontSize
							+ (int) Math.round(visualizer.getGradient() * c
									/ visualizer.getSolutionValue());
					return new Font("Helvetica", Font.PLAIN, size);
				}
				return new Font("Helvetica", Font.PLAIN, normalFontSize);
			}
		};
	}

	public Transformer<Vertex, Paint> getVertexFillPaintTransformer() {

		return new Transformer<Vertex, Paint>() {

			@Override
			public Paint transform(Vertex vertex) {
				return visualizer.color(visualizer.getSearchTree().f(vertex));
			}
		};
	}

	public Transformer<Vertex, Paint> getVertexDrawPaintTransformer() {

		return new Transformer<Vertex, Paint>() {

			@Override
			public Paint transform(Vertex vertex) {

				PickedInfo<Vertex> info = visualizer.view
						.getPickedVertexState();
				if (info.isPicked(vertex))
					visualizer.showNodeInfo(vertex);
				visualizer.getView().getPickedVertexState().pick(vertex, false);
				boolean b = visualizer.belongsToSolution(vertex);
				if (b)
					return JungVisualizer.solColor;
				return visualizer.color(visualizer.getSearchTree().f(vertex));
			}
		};
	}

	public Transformer<Vertex, Shape> getVertexShapeTransformer() {
		return new Transformer<Vertex, Shape>() {

			@Override
			public Shape transform(Vertex vertex) {
				Shape shape = shapeFactory.getRoundRectangle(vertex);
				double c = visualizer.getSolutionCardinality(vertex);
				if (c > 0) {
					int s = (int) (normalSize + visualizer.getScale() * c
							/ visualizer.getSolutionValue());
					shapeFactory = new VertexShapeFactory<Vertex>(
							new ConstantTransformer(s),
							new ConstantTransformer(1.0f));
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
		};

	}

	// @Override
	// public Transformer<Vertex, String> getVertexLabelTransformer() {
	// // TODO Auto-generated method stub
	// return new Transformer<Vertex, String>() {
	//
	// @Override
	// public String transform(Vertex arg0) {
	// if (!icon.isEmpty()) {
	// return "<html><img src=" + "etc"
	// + System.getProperty("file.separator") + icon
	// + ">";
	// }
	// return "";
	// }
	// };
	// }
}
