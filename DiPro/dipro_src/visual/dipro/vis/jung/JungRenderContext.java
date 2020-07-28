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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.JComponent;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.apache.commons.collections15.functors.TruePredicate;

import dipro.graph.DirectedEdge;
import dipro.graph.Vertex;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.DefaultParallelEdgeIndexFunction;
import edu.uci.ics.jung.graph.util.EdgeIndexFunction;
import edu.uci.ics.jung.graph.util.IncidentEdgeIndexFunction;
import edu.uci.ics.jung.visualization.BasicTransformer;
import edu.uci.ics.jung.visualization.MultiLayerTransformer;
//import edu.uci.ics.jung.visualization.PluggableRenderContext;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.decorators.ConstantDirectionalEdgeValueTransformer;
import edu.uci.ics.jung.visualization.decorators.DirectionalEdgeArrowTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.EdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.VertexLabelRenderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

public class JungRenderContext implements RenderContext<Vertex, DirectedEdge> {
	protected float arrowPlacementTolerance = 1;
	protected Predicate<Context<Graph<Vertex, DirectedEdge>, Vertex>> vertexIncludePredicate = TruePredicate
			.getInstance();
	protected Transformer<Vertex, Stroke> vertexStrokeTransformer = new ConstantTransformer(
			new BasicStroke(1.0f));

	protected Transformer<Vertex, Shape> vertexShapeTransformer = new ConstantTransformer(
			new Ellipse2D.Float(-10, -10, 20, 20));

	protected Transformer<Vertex, String> vertexLabelTransformer = new ConstantTransformer(
			null);
	protected Transformer<Vertex, Icon> vertexIconTransformer;
	protected Transformer<Vertex, Font> vertexFontTransformer = new ConstantTransformer(
			new Font("Helvetica", Font.PLAIN, 12));

	protected Transformer<Vertex, Paint> vertexDrawPaintTransformer = new ConstantTransformer(
			Color.BLACK);
	protected Transformer<Vertex, Paint> vertexFillPaintTransformer = new ConstantTransformer(
			Color.RED);

	protected Transformer<DirectedEdge, String> edgeLabelTransformer = new ConstantTransformer(
			null);
	protected Transformer<DirectedEdge, Stroke> edgeStrokeTransformer = new ConstantTransformer(
			new BasicStroke(1.0f));
	protected Transformer<DirectedEdge, Stroke> edgeArrowStrokeTransformer = new ConstantTransformer(
			new BasicStroke(1.0f));

	protected Transformer<Context<Graph<Vertex, DirectedEdge>, DirectedEdge>, Shape> edgeArrowTransformer = new DirectionalEdgeArrowTransformer<Vertex, DirectedEdge>(
			10, 8, 4);

	protected Predicate<Context<Graph<Vertex, DirectedEdge>, DirectedEdge>> edgeArrowPredicate = new DirectedEdgeArrowPredicate<Vertex, DirectedEdge>();
	protected Predicate<Context<Graph<Vertex, DirectedEdge>, DirectedEdge>> edgeIncludePredicate = TruePredicate
			.getInstance();
	protected Transformer<DirectedEdge, Font> edgeFontTransformer = new ConstantTransformer(
			new Font("Helvetica", Font.PLAIN, 12));
	protected Transformer<Context<Graph<Vertex, DirectedEdge>, DirectedEdge>, Number> edgeLabelClosenessTransformer = new ConstantDirectionalEdgeValueTransformer<Vertex, DirectedEdge>(
			0.5, 0.65);
	protected Transformer<Context<Graph<Vertex, DirectedEdge>, DirectedEdge>, Shape> edgeShapeTransformer = new EdgeShape.QuadCurve<Vertex, DirectedEdge>();
	protected Transformer<DirectedEdge, Paint> edgeFillPaintTransformer = new ConstantTransformer(
			null);
	protected Transformer<DirectedEdge, Paint> edgeDrawPaintTransformer =

	new ConstantTransformer(Color.black);
	protected Transformer<DirectedEdge, Paint> arrowFillPaintTransformer = new ConstantTransformer(
			Color.black);
	protected Transformer<DirectedEdge, Paint> arrowDrawPaintTransformer = new ConstantTransformer(
			Color.black);

	protected EdgeIndexFunction<Vertex, DirectedEdge> parallelEdgeIndexFunction = DefaultParallelEdgeIndexFunction
			.<Vertex, DirectedEdge> getInstance();

	protected EdgeIndexFunction<Vertex, DirectedEdge> incidentEdgeIndexFunction = IncidentEdgeIndexFunction
			.<Vertex, DirectedEdge> getInstance();

	protected MultiLayerTransformer multiLayerTransformer = new BasicTransformer();

	/**
	 * pluggable support for picking graph elements by finding them based on
	 * their coordinates.
	 */
	protected GraphElementAccessor<Vertex, DirectedEdge> pickSupport;

	protected int labelOffset = LABEL_OFFSET;

	/**
	 * the JComponent that this Renderer will display the graph on
	 */
	protected JComponent screenDevice;

	protected PickedState<Vertex> pickedVertexState;
	protected PickedState<DirectedEdge> pickedEdgeState;

	/**
	 * The CellRendererPane is used here just as it is in JTree and JTable, to
	 * allow a pluggable JLabel-based renderer for Vertex and DirectedEdge label
	 * strings and icons.
	 */
	protected CellRendererPane rendererPane = new CellRendererPane();

	/**
	 * A default GraphLabelRenderer - picked Vertex labels are blue, picked edge
	 * labels are cyan
	 */
	protected VertexLabelRenderer vertexLabelRenderer = new DefaultVertexLabelRenderer(
			Color.blue);

	protected EdgeLabelRenderer edgeLabelRenderer = new DefaultEdgeLabelRenderer(
			Color.cyan);

	protected GraphicsDecorator graphicsContext;

	
	protected JungRenderContext() {
		this.setEdgeShapeTransformer(new EdgeShape.Line<Vertex, DirectedEdge>());
	
//		getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

	}

	/**
	 * @return the vertexShapeTransformer
	 */
	public Transformer<Vertex, Shape> getVertexShapeTransformer() {

		return vertexShapeTransformer;
	}

	/**
	 * @param vertexShapeTransformer
	 *            the vertexShapeTransformer to set
	 */
	public void setVertexShapeTransformer(
			Transformer<Vertex, Shape> vertexShapeTransformer) {
		this.vertexShapeTransformer = vertexShapeTransformer;
	}

	/**
	 * @return the vertexStrokeTransformer
	 */
	public Transformer<Vertex, Stroke> getVertexStrokeTransformer() {
		return vertexStrokeTransformer;
	}

	/**
	 * @param vertexStrokeTransformer
	 *            the vertexStrokeTransformer to set
	 */
	public void setVertexStrokeTransformer(
			Transformer<Vertex, Stroke> vertexStrokeTransformer) {
		this.vertexStrokeTransformer = vertexStrokeTransformer;
	}

	public static float[] getDashing() {
		return dashing;
	}

	public static float[] getDotting() {
		return dotting;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getArrow_placement_tolerance()
	 */
	public float getArrowPlacementTolerance() {
		return arrowPlacementTolerance;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setArrow_placement_tolerance(float)
	 */
	public void setArrowPlacementTolerance(float arrow_placement_tolerance) {
		this.arrowPlacementTolerance = arrow_placement_tolerance;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeArrowTransformer()
	 */
	public Transformer<Context<Graph<Vertex, DirectedEdge>, DirectedEdge>, Shape> getEdgeArrowTransformer() {
		return edgeArrowTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeArrowTransformer(edu.uci.ics.jung.visualization.decorators.EdgeArrowTransformer)
	 */
	public void setEdgeArrowTransformer(
			Transformer<Context<Graph<Vertex, DirectedEdge>, DirectedEdge>, Shape> edgeArrowTransformer) {
		this.edgeArrowTransformer = edgeArrowTransformer;
	}

	/**
	 * @see RenderContext#getEdgeArrowPredicate()
	 */
	public Predicate<Context<Graph<Vertex, DirectedEdge>, DirectedEdge>> getEdgeArrowPredicate() {
		return edgeArrowPredicate;
	}

	/**
	 * @see RenderContext#setEdgeArrowPredicate(Predicate)
	 */
	public void setEdgeArrowPredicate(
			Predicate<Context<Graph<Vertex, DirectedEdge>, DirectedEdge>> edgeArrowPredicate) {
		this.edgeArrowPredicate = edgeArrowPredicate;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeFontTransformer()
	 */
	public Transformer<DirectedEdge, Font> getEdgeFontTransformer() {
		return edgeFontTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeFontTransformer(edu.uci.ics.jung.visualization.decorators.EdgeFontTransformer)
	 */
	public void setEdgeFontTransformer(
			Transformer<DirectedEdge, Font> edgeFontTransformer) {
		this.edgeFontTransformer = edgeFontTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeIncludePredicate()
	 */
	public Predicate<Context<Graph<Vertex, DirectedEdge>, DirectedEdge>> getEdgeIncludePredicate() {
		return edgeIncludePredicate;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeIncludePredicate(org.apache.commons.collections15.Predicate)
	 */
	public void setEdgeIncludePredicate(
			Predicate<Context<Graph<Vertex, DirectedEdge>, DirectedEdge>> edgeIncludePredicate) {
		this.edgeIncludePredicate = edgeIncludePredicate;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeLabelClosenessTransformer()
	 */
	public Transformer<Context<Graph<Vertex, DirectedEdge>, DirectedEdge>, Number> getEdgeLabelClosenessTransformer() {
		return edgeLabelClosenessTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeLabelClosenessTransformer(edu.uci.ics.jung.visualization.decorators.NumberDirectionalEdgeValue)
	 */
	public void setEdgeLabelClosenessTransformer(
			Transformer<Context<Graph<Vertex, DirectedEdge>, DirectedEdge>, Number> edgeLabelClosenessTransformer) {
		this.edgeLabelClosenessTransformer = edgeLabelClosenessTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeLabelRenderer()
	 */
	public EdgeLabelRenderer getEdgeLabelRenderer() {
		return edgeLabelRenderer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeLabelRenderer(edu.uci.ics.jung.visualization.EdgeLabelRenderer)
	 */
	public void setEdgeLabelRenderer(EdgeLabelRenderer edgeLabelRenderer) {
		this.edgeLabelRenderer = edgeLabelRenderer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getEdgePaintTransformer()
	 */
	public Transformer<DirectedEdge, Paint> getEdgeFillPaintTransformer() {
		return edgeFillPaintTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setEdgePaintTransformer(edu.uci.ics.jung.visualization.decorators.EdgePaintTransformer)
	 */
	public void setEdgeDrawPaintTransformer(
			Transformer<DirectedEdge, Paint> edgeDrawPaintTransformer) {
		this.edgeDrawPaintTransformer = edgeDrawPaintTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getEdgePaintTransformer()
	 */
	public Transformer<DirectedEdge, Paint> getEdgeDrawPaintTransformer() {
		return edgeDrawPaintTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setEdgePaintTransformer(edu.uci.ics.jung.visualization.decorators.EdgePaintTransformer)
	 */
	public void setEdgeFillPaintTransformer(
			Transformer<DirectedEdge, Paint> edgeFillPaintTransformer) {
		this.edgeFillPaintTransformer = edgeFillPaintTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeShapeTransformer()
	 */
	public Transformer<Context<Graph<Vertex, DirectedEdge>, DirectedEdge>, Shape> getEdgeShapeTransformer() {
		return edgeShapeTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeShapeTransformer(edu.uci.ics.jung.visualization.decorators.EdgeShapeTransformer)
	 */
	public void setEdgeShapeTransformer(
			Transformer<Context<Graph<Vertex, DirectedEdge>, DirectedEdge>, Shape> edgeShapeTransformer) {
		this.edgeShapeTransformer = edgeShapeTransformer;
		if (edgeShapeTransformer instanceof EdgeShape.Orthogonal) {
			((EdgeShape.IndexedRendering<Vertex, DirectedEdge>) edgeShapeTransformer)
					.setEdgeIndexFunction(this.incidentEdgeIndexFunction);
		} else if (edgeShapeTransformer instanceof EdgeShape.IndexedRendering) {
			((EdgeShape.IndexedRendering<Vertex, DirectedEdge>) edgeShapeTransformer)
					.setEdgeIndexFunction(this.parallelEdgeIndexFunction);
		}
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeLabelTransformer()
	 */
	public Transformer<DirectedEdge, String> getEdgeLabelTransformer() {
		return edgeLabelTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeLabelTransformer(edu.uci.ics.jung.visualization.decorators.EdgeLabelTransformer)
	 */
	public void setEdgeLabelTransformer(
			Transformer<DirectedEdge, String> edgeLabelTransformer) {
		this.edgeLabelTransformer = edgeLabelTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeStrokeTransformer()
	 */
	public Transformer<DirectedEdge, Stroke> getEdgeStrokeTransformer() {
		return edgeStrokeTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeStrokeTransformer(edu.uci.ics.jung.visualization.decorators.EdgeStrokeTransformer)
	 */
	public void setEdgeStrokeTransformer(
			Transformer<DirectedEdge, Stroke> edgeStrokeTransformer) {
		this.edgeStrokeTransformer = edgeStrokeTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getEdgeStrokeTransformer()
	 */
	public Transformer<DirectedEdge, Stroke> getEdgeArrowStrokeTransformer() {
		return edgeArrowStrokeTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setEdgeStrokeTransformer(edu.uci.ics.jung.visualization.decorators.EdgeStrokeTransformer)
	 */
	public void setEdgeArrowStrokeTransformer(
			Transformer<DirectedEdge, Stroke> edgeArrowStrokeTransformer) {
		this.edgeArrowStrokeTransformer = edgeArrowStrokeTransformer;
	}

	/**
	 * @see RenderContext#getGraphicsContext()
	 */
	public GraphicsDecorator getGraphicsContext() {
		return graphicsContext;
	}

	/**
	 * @see RenderContext#setGraphicsContext(GraphicsDecorator)
	 */
	public void setGraphicsContext(GraphicsDecorator graphicsContext) {
		this.graphicsContext = graphicsContext;
	}

	/**
	 * @see RenderContext#getLabelOffset()
	 */
	public int getLabelOffset() {
		return labelOffset;
	}

	/**
	 * @see RenderContext#setLabelOffset(int)
	 */
	public void setLabelOffset(int labelOffset) {
		this.labelOffset = labelOffset;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getParallelEdgeIndexTransformer()
	 */
	public EdgeIndexFunction<Vertex, DirectedEdge> getParallelEdgeIndexFunction() {
		return parallelEdgeIndexFunction;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setParallelEdgeIndexFunction(edu.uci.ics.graph.util.ParallelEdgeIndexFunction)
	 */
	public void setParallelEdgeIndexFunction(
			EdgeIndexFunction<Vertex, DirectedEdge> parallelEdgeIndexFunction) {
		this.parallelEdgeIndexFunction = parallelEdgeIndexFunction;
		// reset the edge shape transformer, as the parallel edge index function
		// is used by it
		this.setEdgeShapeTransformer(getEdgeShapeTransformer());
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getPickedEdgeState()
	 */
	public PickedState<DirectedEdge> getPickedEdgeState() {
		return pickedEdgeState;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setPickedEdgeState(edu.uci.ics.jung.visualization.picking.PickedState)
	 */
	public void setPickedEdgeState(PickedState<DirectedEdge> pickedEdgeState) {
		this.pickedEdgeState = pickedEdgeState;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getPickedVertexState()
	 */
	public PickedState<Vertex> getPickedVertexState() {
		return pickedVertexState;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setPickedVertexState(edu.uci.ics.jung.visualization.picking.PickedState)
	 */
	public void setPickedVertexState(PickedState<Vertex> pickedVertexState) {
		this.pickedVertexState = pickedVertexState;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getRendererPane()
	 */
	public CellRendererPane getRendererPane() {
		return rendererPane;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setRendererPane(javax.swing.CellRendererPane)
	 */
	public void setRendererPane(CellRendererPane rendererPane) {
		this.rendererPane = rendererPane;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getScreenDevice()
	 */
	public JComponent getScreenDevice() {
		return screenDevice;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setScreenDevice(edu.uci.ics.jung.visualization.VisualizationViewer)
	 */
	public void setScreenDevice(JComponent screenDevice) {
		this.screenDevice = screenDevice;
		screenDevice.add(rendererPane);
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getVertexFontTransformer()
	 */
	public Transformer<Vertex, Font> getVertexFontTransformer() {
		return vertexFontTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setVertexFontTransformer(edu.uci.ics.jung.visualization.decorators.VertexFontTransformer)
	 */
	public void setVertexFontTransformer(
			Transformer<Vertex, Font> vertexFontTransformer) {
		this.vertexFontTransformer = vertexFontTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getVertexIconTransformer()
	 */
	public Transformer<Vertex, Icon> getVertexIconTransformer() {
		return vertexIconTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setVertexIconTransformer(edu.uci.ics.jung.visualization.decorators.VertexIconTransformer)
	 */
	public void setVertexIconTransformer(
			Transformer<Vertex, Icon> vertexIconTransformer) {
		this.vertexIconTransformer = vertexIconTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getVertexIncludePredicate()
	 */
	public Predicate<Context<Graph<Vertex, DirectedEdge>, Vertex>> getVertexIncludePredicate() {
		return vertexIncludePredicate;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setVertexIncludePredicate(org.apache.commons.collections15.Predicate)
	 */
	public void setVertexIncludePredicate(
			Predicate<Context<Graph<Vertex, DirectedEdge>, Vertex>> vertexIncludePredicate) {
		this.vertexIncludePredicate = vertexIncludePredicate;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getVertexLabelRenderer()
	 */
	public VertexLabelRenderer getVertexLabelRenderer() {
		return vertexLabelRenderer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setVertexLabelRenderer(edu.uci.ics.jung.visualization.VertexLabelRenderer)
	 */
	public void setVertexLabelRenderer(VertexLabelRenderer vertexLabelRenderer) {
		this.vertexLabelRenderer = vertexLabelRenderer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getVertexPaintTransformer()
	 */
	public Transformer<Vertex, Paint> getVertexFillPaintTransformer() {
		return vertexFillPaintTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setVertexPaintTransformer(edu.uci.ics.jung.visualization.decorators.VertexPaintTransformer)
	 */
	public void setVertexFillPaintTransformer(
			Transformer<Vertex, Paint> vertexFillPaintTransformer) {
		this.vertexFillPaintTransformer = vertexFillPaintTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getVertexPaintTransformer()
	 */
	public Transformer<Vertex, Paint> getVertexDrawPaintTransformer() {
		return vertexDrawPaintTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setVertexPaintTransformer(edu.uci.ics.jung.visualization.decorators.VertexPaintTransformer)
	 */
	public void setVertexDrawPaintTransformer(
			Transformer<Vertex, Paint> vertexDrawPaintTransformer) {
		this.vertexDrawPaintTransformer = vertexDrawPaintTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#getVertexLabelTransformer()
	 */
	public Transformer<Vertex, String> getVertexLabelTransformer() {
		return vertexLabelTransformer;
	}

	/**
	 * @see edu.uci.ics.jung.visualization.RenderContext#setVertexLabelTransformer(edu.uci.ics.jung.visualization.decorators.VertexLabelTransformer)
	 */
	public void setVertexLabelTransformer(
			Transformer<Vertex, String> vertexLabelTransformer) {
		this.vertexLabelTransformer = vertexLabelTransformer;
	}

	/**
	 * @return the pickSupport
	 */
	public GraphElementAccessor<Vertex, DirectedEdge> getPickSupport() {
		return pickSupport;
	}

	/**
	 * @param pickSupport
	 *            the pickSupport to set
	 */
	public void setPickSupport(
			GraphElementAccessor<Vertex, DirectedEdge> pickSupport) {
		this.pickSupport = pickSupport;
	}

	/**
	 * @return the basicTransformer
	 */
	public MultiLayerTransformer getMultiLayerTransformer() {
		return multiLayerTransformer;
	}

	/**
	 * @param basicTransformer
	 *            the basicTransformer to set
	 */
	public void setMultiLayerTransformer(MultiLayerTransformer basicTransformer) {
		this.multiLayerTransformer = basicTransformer;
	}

	/**
	 * @see RenderContext#getArrowDrawPaintTransformer()
	 */
	public Transformer<DirectedEdge, Paint> getArrowDrawPaintTransformer() {
		return arrowDrawPaintTransformer;
	}

	/**
	 * @see RenderContext#getArrowFillPaintTransformer()
	 */
	public Transformer<DirectedEdge, Paint> getArrowFillPaintTransformer() {
		return arrowFillPaintTransformer;
	}

	/**
	 * @see RenderContext#setArrowDrawPaintTransformer(Transformer)
	 */
	public void setArrowDrawPaintTransformer(
			Transformer<DirectedEdge, Paint> arrowDrawPaintTransformer) {
		this.arrowDrawPaintTransformer = arrowDrawPaintTransformer;

	}

	/**
	 * @see RenderContext#setArrowFillPaintTransformer(Transformer)
	 */
	public void setArrowFillPaintTransformer(
			Transformer<DirectedEdge, Paint> arrowFillPaintTransformer) {
		this.arrowFillPaintTransformer = arrowFillPaintTransformer;

	}

}
