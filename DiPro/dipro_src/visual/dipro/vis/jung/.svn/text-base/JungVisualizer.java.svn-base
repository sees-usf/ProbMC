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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import dipro.alg.BF;
import dipro.graph.DirectedEdge;
import dipro.graph.Edge;
import dipro.graph.Vertex;
import dipro.run.Registry;
import dipro.run.VisContext;
import dipro.run.VisMain;
import dipro.util.Trace;
import dipro.vis.AbstractVisualizer;
import dipro.vis.ViewManager;
import dipro.vis.VisInfo;
import dipro.vis.VisualizationEvent;
import dipro.vis.gui.DetailsDialog;
import dipro.vis.gui.IconLoader;
import dipro.vis.gui.TracesPanel;
import edu.uci.ics.jung.algorithms.layout.BalloonLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalLensGraphMouse;
import edu.uci.ics.jung.visualization.control.SatelliteVisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.shape.HyperbolicShapeTransformer;
import edu.uci.ics.jung.visualization.transform.shape.ViewLensSupport;
import edu.uci.ics.jung.visualization.util.Animator;

public class JungVisualizer extends AbstractVisualizer {

	public static final int BALLOON_LAYOUTER_INDEX = 0;
	public static final int TREE_LAYOUTER_INDEX = 1;
	public static final int RADIAL_LAYOUTER_INDEX = 2;
	public static final int DEFAULT_LAYOUTER_INDEX = 1;

	public static final String DECORATION_DATA_PROVIDER_KEY = "DECORATION";
	public static final String EXPLORED_SPACE_KEY = "EXPLORED_SPACE";

	public static final Color solColor = new Color(1.0f, 0.4f, 0.0f);

	private static final String[] supportedLayouters = { "Balloon", // 0
			"Tree", // 1
			"RadialTree"/*
						 * ,// 2 , "ARTree"
						 */}; // 3

	protected JSplitPane rootPane;
	protected JPanel infoPanel, propPanel;
	protected JungVisualizerControlPanel controlPanel;
	protected DetailsDialog detailsPanel;

	protected Forest<Vertex, DirectedEdge> graph;
	// protected Layout<Vertex, DirectedEdge> layout;
	protected VisualizationModel<Vertex, DirectedEdge> model;
	protected VisualizationViewer<Vertex, DirectedEdge> view;
	protected SatelliteVisualizationViewer<Vertex, DirectedEdge> overview;
	protected CrossoverScalingControl scaler, overviewScaler;
	protected int currentLayoutIndex;
	protected DefaultModalGraphMouse graphMouse;
	protected GraphZoomScrollPane zoom;
	protected ViewLensSupport<Vertex, DirectedEdge> hyperbolicView;
	protected LayoutTransition<Vertex, DirectedEdge> layoutTransition;
	//

	protected Hashtable<Vertex, Vertex> vertexMap;
	protected Hashtable<DirectedEdge, Edge> edgeMap;
	// protected Layouter layouter;
	// protected DataProvider innerSphereOfActionDP;
	private final HashSet<DirectedEdge> movable;
	private double maxVisFValue;

	/* Animation objects and settings */
	protected int morphDuration = 0;
	// protected Animator animationPlayer;
	// protected ViewAnimationFactory animationFactory;
	protected TracesPanel tracesPanel;
	private Timer timer;

	public JungVisualizer(VisContext context, BF alg) {
		super(context, alg);
		gradient = 100;
		vertexMap = new Hashtable<Vertex, Vertex>();
		edgeMap = new Hashtable<DirectedEdge, Edge>();
		movable = new HashSet<DirectedEdge>();
		maxVisFValue = 1.0f;

		rootPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		Dimension d = ((VisMain) Registry.getMain()).getGUI().getSize();
		rootPane.setPreferredSize(new Dimension(d.width, (int) (d.height * 0.7)));
		rootPane.setSize(rootPane.getPreferredSize());
		rootPane.setResizeWeight(1.0f);
		d = rootPane.getPreferredSize();

		graph = new DelegateForest<Vertex, DirectedEdge>();
		if (DEFAULT_LAYOUTER_INDEX != TREE_LAYOUTER_INDEX)
			model = new DefaultVisualizationModel<Vertex, DirectedEdge>(
					createLayout(DEFAULT_LAYOUTER_INDEX), new Dimension(
							(int) (d.width * 1.5), (int) (d.height * 1.5)));
		else
			model = new DefaultVisualizationModel<Vertex, DirectedEdge>(
					createLayout(DEFAULT_LAYOUTER_INDEX));

		graphMouse = new DefaultModalGraphMouse();
		graphMouse.setMode(ModalGraphMouse.Mode.PICKING);

		scaler = new CrossoverScalingControl();

		view = new VisualizationViewer<Vertex, DirectedEdge>(model,
				new Dimension(d.width - 350, d.height));
		view.setPreferredSize(new Dimension(d.width - 350, d.width));
		view.setSize(view.getPreferredSize());
		view.setMaximumSize(view.getPreferredSize());
		view.scaleToLayout(scaler);
		view.setBackground(Color.white);
		view.setBorder(BorderFactory.createTitledBorder(""));
		view.setGraphMouse(graphMouse);
		view.addKeyListener(graphMouse.getModeKeyListener());
		hyperbolicView = new ViewLensSupport<Vertex, DirectedEdge>(
				view,
				new HyperbolicShapeTransformer(view, view.getRenderContext()
						.getMultiLayerTransformer().getTransformer(Layer.VIEW)),
				new ModalLensGraphMouse());

		graphMouse.addItemListener(hyperbolicView.getGraphMouse()
				.getModeListener());

		infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.setAlignmentY(JComponent.TOP_ALIGNMENT);

		JScrollPane scrollPaneInfo = new JScrollPane(infoPanel);
		scrollPaneInfo.setPreferredSize(new Dimension(350, d.height));
		scrollPaneInfo.setSize(scrollPaneInfo.getPreferredSize());
		scrollPaneInfo.setMaximumSize(scrollPaneInfo.getPreferredSize());

		d = infoPanel.getPreferredSize();
		overview = new SatelliteVisualizationViewer<Vertex, DirectedEdge>(view,
				new Dimension(350, d.height));
		overview.setBackground(Color.white);
		overview.setBorder(BorderFactory.createTitledBorder(""));
		overviewScaler = new CrossoverScalingControl();
		// System.out.println("Over:" + overview.getSize());
		// overviewScaler.setCrossover(overview.getWidth() /
		// model.getGraphLayout().getSize().width);

		overview.scaleToLayout(overviewScaler);
		// overview.setPreferredSize(new Dimension(350, d.width));
		// overview.setSize(overview.getPreferredSize());

		overview.setBorder(BorderFactory.createTitledBorder("Graph Overview"));

		infoPanel.add(overview);
		zoom = new GraphZoomScrollPane(view);
		rootPane.setLeftComponent(zoom);
		rootPane.setRightComponent(scrollPaneInfo);
		rootPane.setDividerLocation(view.getPreferredSize().width);

	}

	public void init() throws Exception {
		super.init();
		view.getRenderer().getVertexLabelRenderer()
				.setPosition(Renderer.VertexLabel.Position.CNTR);
		RenderContextFactory context = new RenderContextFactory(this);
		view.getRenderContext().setEdgeShapeTransformer(
				new EdgeShape.Line<Vertex, DirectedEdge>());
		view.getRenderContext().setVertexShapeTransformer(
				context.getVertexShapeTransformer());
		view.getRenderContext().setVertexFillPaintTransformer(
				context.getVertexFillPaintTransformer());
		view.getRenderContext().setVertexDrawPaintTransformer(
				context.getVertexDrawPaintTransformer());
		view.getRenderContext().setEdgeLabelTransformer(
				context.getEdgeLabelTransformer());
		view.getRenderContext().setEdgeFillPaintTransformer(
				context.getEdgeFillPaintTransformer());
		view.getRenderContext().setEdgeDrawPaintTransformer(
				context.getEdgeDrawPaintTransformer());
		view.setVertexToolTipTransformer(context.getVertexToolTipTransformer());
		view.setEdgeToolTipTransformer(context.getEdgeToolTipTransformer());

		overview.getRenderContext().setEdgeShapeTransformer(
				new EdgeShape.Line<Vertex, DirectedEdge>());
		overview.getRenderContext().setVertexShapeTransformer(
				context.getVertexShapeTransformer());
		overview.getRenderContext().setVertexFillPaintTransformer(
				context.getVertexFillPaintTransformer());
		overview.getRenderContext().setVertexDrawPaintTransformer(
				context.getVertexDrawPaintTransformer());
		overview.getRenderContext().setEdgeFillPaintTransformer(
				context.getEdgeFillPaintTransformer());
		overview.getRenderContext().setEdgeDrawPaintTransformer(
				context.getEdgeDrawPaintTransformer());

		controlPanel = createControlPanel();
		addObserver(controlPanel);
		detailsPanel = createDetailsDialog();
		tracesPanel = new TracesPanel(this);
		infoPanel.add(controlPanel);
		tracesPanel.setPreferredSize(new Dimension(infoPanel.getWidth(),
				tracesPanel.getPreferredSize().height));
		// timer = new Timer();
		// timer.schedule(new IncrementLayoutTask(), 2500, 1500);
		registerViewModes();
		registerViewActions();
		registerViewListeners();

	}

	public VisualizationViewer<Vertex, DirectedEdge> getView() {
		return view;
	}

	protected void reset() {
		// view.getGraph2D().clear();
		// vertexNodeMap.clear();
		// edgeEdgeMap.clear();
		// if (isOnline()) {
		// ((VisSearchTree) getSearchTree()).deliverVisEvents();
		// }
		// update();
	}

	//
	// protected Layouter createLayouter(int selectedLayouter) {
	// switch (selectedLayouter) {
	// case BALLOON_LAYOUTER_INDEX:
	// return createBalloonLayouter();
	// case INCREMENTAL_HIERARCHICAL_LAYOUTER_INDEX:
	// return createIncrementalHierarchicLayouter();
	// // case ORGANIC_LAYOUTER_INDEX:
	// // return createOrganicLayouter();
	// case AR_TREE_LAYOUTER_INDEX:
	// return createARTreeLayouter();
	// }
	// throw new IllegalArgumentException("Unsupported Layouter");
	// }

	// protected OrganicLayouter createOrganicLayouter() {
	// OrganicLayouter ol = new OrganicLayouter();
	// ol.setGravityFactor(0.0d);
	// ol.setRepulsion(2);
	// ol.setAttraction(2);
	// ol.setObeyNodeSize(true);
	// ol.setActivateDeterministicMode(true);
	// DataProviderAdapter edgeLengthData = new DataProviderAdapter() {
	// public int getInt(Object o) {
	// JungEdgeRealizer er = (JungEdgeRealizer) view.getGraph2D()
	// .getRealizer((Edge) o);
	// Vertex v = er.getDiProEdge().getVertices().value();
	// double f = getSearchTree().f(v);
	// int l = (int) Math.max(20, normalize(f) * 200);
	// return l;
	// }
	// };
	// ol.setPreferredEdgeLength(30);
	// // view.getGraph2D().addDataProvider(
	// // OrganicLayouter.PREFERRED_EDGE_LENGTH_DATA, edgeLengthData);
	// // ol.setInitialPlacement(OrganicLayouter.AS_IS);
	// // view.getGraph2D().addDataProvider(
	// // OrganicLayouter.SPHERE_OF_ACTION_NODES, innerSphereOfActionDP);
	// ol.setSphereOfAction(OrganicLayouter.MAINLY_SELECTION);
	// return ol;
	// }
	//
	// protected IncrementalHierarchicLayouter
	// createIncrementalHierarchicLayouter() {
	// IncrementalHierarchicLayouter ihl = new IncrementalHierarchicLayouter();
	// ihl.setLayoutMode(IncrementalHierarchicLayouter.LAYOUT_MODE_INCREMENTAL);
	// ihl.setFixedElementsLayerer(new OldLayererWrapper(
	// new TopologicalLayerer()));
	// ihl.setMinimumLayerDistance(50.0d);
	// ihl.getEdgeLayoutDescriptor().setSourcePortOptimizationEnabled(true);
	// ihl.getEdgeLayoutDescriptor().setTargetPortOptimizationEnabled(true);
	// ihl.getEdgeLayoutDescriptor().setOrthogonallyRouted(true);
	// ihl.setComponentLayouterEnabled(false);
	// ihl.setGroupNodeHidingEnabled(false);
	// return ihl;
	// }
	//
	// protected ARTreeLayouter createARTreeLayouter() {
	// ARTreeLayouter artl = new ARTreeLayouter();
	// return artl;
	// }
	//
	// protected BalloonLayouter createBalloonLayouter() {
	// BalloonLayouter bl = new BalloonLayouter();
	// return bl;
	// }
	//
	// protected DetailsDialog createDetailsDialog() throws Exception {
	// return null;
	// }
	//
	public Component getVisualizationComponent() {
		// System.out.println("view Pref. Size: " + view.getPreferredSize());
		// System.out.println("view Size: " + view.getSize());
		// System.out.println("infoPanel Pref. Size: "
		// + infoPanel.getPreferredSize());
		// System.out.println("infoPanel Size: " + infoPanel.getSize());
		return rootPane;
	}

	public Component getControlComponent() {
		return controlPanel;
	}

	protected void registerViewActions() {
		// // register keyboard actions
		// // Graph2DViewActions actions = new Graph2DViewActions(view);
		// ActionMap amap = actions.createActionMap();
		// InputMap imap = actions.createDefaultInputMap(amap);
		// amap.remove(Graph2DViewActions.EDIT_LABEL);
		// amap.remove(Graph2DViewActions.EDIT_NODE);
		// amap.remove(Graph2DViewActions.DELETE_SELECTION);
		// view.getCanvasComponent().setActionMap(amap);
		// view.getCanvasComponent().setInputMap(JComponent.WHEN_FOCUSED, imap);
	}

	/**
	 * Instantiates and registers the listeners for the view. (e.g.
	 * {@link y.view.Graph2DViewMouseWheelZoomListener}
	 */
	protected void registerViewListeners() {
		// view.getCanvasComponent().addMouseWheelListener(
		// new Graph2DViewMouseWheelZoomListener());
	}

	protected void registerViewModes() {
		// view.addViewMode(new VisualDebuggingMode(this));
	}

	public String getType() {
		return ViewManager.TYPE_INTERACTIVE;
	}

	public String getName() {
		return toString();
	}

	public ImageIcon getIcon() {
		return IconLoader.get("yfiles.gif");
	}

	public void setMorphDuration(int value) {
		morphDuration = value;
	}

	public int getMorphDuration() {
		return morphDuration;
	}

	// layouter = createLayouter(layouterIndex);
	// if (layouter instanceof OrganicLayouter) {
	// ((OrganicLayouter) layouter).setSphereOfAction(OrganicLayouter.ALL);
	// }
	// update();
	// if (layouter instanceof OrganicLayouter) {
	// ((OrganicLayouter) layouter)
	// .setSphereOfAction(OrganicLayouter.MAINLY_SELECTION);
	// }/

	public void update() {
		justifyGraph();
		Dimension layoutDim = model.getGraphLayout().getSize();
		// overviewScaler.setCrossover(overview.getWidth()/layoutDim.width);
		// System.out.println("Scaler:"+overviewScaler.getCrossover());
		view.repaint();
	}

	@Override
	protected void handleCloseStateEvent(VisualizationEvent event) {
		VisInfo info = event.getVisInfo();
		// Node n = vertexNodeMap.get(info.getMark().vertex());
		// assert n != null;
		// ((YVertexRealizer)view.getGraph2D().getRealizer(n)).setStateType(StateRealizer.CLOSED_STATE);
		// AnimationObject anim = AnimationFactory.createRepetition(
		// animationFactory.fadeIn(view.getGraph2D().getRealizer(n), 200),
		// 2, false);
		// animationPlayer.animate(anim);
	}

	protected void fadeIn(Trace trace) {
	}

	// @Override
	// protected void handleDiscardTransitionEvent(VisualizationEvent event) {
	// Transition t = (Transition) event.data();
	// Edge e = (Edge)t.get(YFILES_ITEM_KEY);
	// if(e!=null) {
	// view.getGraph2D().removeEdge(e);
	// }
	// t.destroy(YFILES_ITEM_KEY);
	// }

	// @Override
	// protected void handleMarkSolutionRegularStateEvent(VisualizationEvent
	// event) {}
	//
	// @Override
	// protected void handleMarkSolutionTargetStateEvent(VisualizationEvent
	// event) throws Exception {
	// // State s = (State) event.data();
	// // List<Trace> traces = navigator.selectAllTraces(s);
	// // for(Trace trace:traces) {
	// // Iterator<State> states = trace.getStates();
	// // while(states.hasNext()) {
	// // Node n = (Node) states.next().get(YFILES_ITEM_KEY);
	// // StateRealizer sr = (StateRealizer)view.getGraph2D().getRealizer(n);
	// // sr.setDirectTraceState(true);
	// // }
	// // }
	// }

	// @Override
	// protected void handleMarkSolutionTransitionEvent(VisualizationEvent
	// event) {}

	@Override
	protected synchronized void handleNewStateEvent(VisualizationEvent event) {
		VisInfo info = event.getVisInfo();
		if (maxVisFValue == 1.0f) {
			DirectedEdge e = getSearchTree().getTreeEdge(info.getMark());
			if (e != null) {
				if (e.source().equals(context.getStart()))
					maxVisFValue = info.getMark().f();
			}
		}
		Vertex v = info.getMark().vertex();
		DirectedEdge uv = getSearchTree().getTreeEdge(v);
		if (uv != null) {
			movable.add(uv);
			if (layoutTransition != null) {
				if (layoutTransition.done()) {
					HashSet<DirectedEdge> edges = (HashSet<DirectedEdge>) movable
							.clone();
					movable.clear();
					Layout<Vertex, DirectedEdge> oldLayout = model
							.getGraphLayout();
					Iterator<? extends DirectedEdge> eIter = edges.iterator();
					while (eIter.hasNext()) {
						DirectedEdge e = eIter.next();
						addEdge(e);
					}
					layoutTransition = new LayoutTransition<Vertex, DirectedEdge>(
							view, oldLayout, createLayout(currentLayoutIndex));
					Animator animator = new Animator(layoutTransition);
					animator.setSleepTime(5 * edges.size());
					animator.start();
					view.getRenderContext().getMultiLayerTransformer()
							.getTransformer(Layer.LAYOUT).setToIdentity();
				}

			} else {
				addVertex(alg.getContext().getStart());
				changeLayouter(currentLayoutIndex);
			}

			// movable.add(uv);
			// layoutTransition = new LayoutTransition<Vertex,
			// DirectedEdge>(view, model.getGraphLayout(),
			// createLayout(currentLayoutIndex));
			// Animator animator = new Animator(layoutTransition);
			// animator.setSleepTime(10);
			// animator.start();
			// view.getRenderContext().getMultiLayerTransformer()
			// .getTransformer(Layer.LAYOUT).setToIdentity();
			// update();
			// addEdge(uv);
			// Vertex sourceVertex = uv.source();
			// Vertex targetVertex = uv.target();
			// graph.addEdge(uv, sourceVertex, targetVertex);
			// refleshLayout();
			// System.out.println("Source-Vertex:"
			// + model.getGraphLayout().transform(sourceVertex)
			// + "Target-Vertex:"
			// + model.getGraphLayout().transform(targetVertex));
			// update();
		}
	}

	@Override
	protected synchronized void handleRelaxOpenStateEvent(
			VisualizationEvent event) {
		VisInfo info = event.getVisInfo();
		DirectedEdge oldEdge = getSearchTree().getTreeEdge(info.getOldMark());
		DirectedEdge newEdge = getSearchTree().getTreeEdge(info.getNewMark());
		removeEdge(oldEdge);
		addEdge(newEdge);
		Vertex v = info.getNewMark().vertex();
		// movable.add(v);
	}

	@Override
	protected synchronized void handleReopenStateEvent(VisualizationEvent event) {
		VisInfo info = event.getVisInfo();
		DirectedEdge oldEdge = getSearchTree().getTreeEdge(info.getOldMark());
		DirectedEdge newEdge = getSearchTree().getTreeEdge(info.getNewMark());
		removeEdge(oldEdge);
		addEdge(newEdge);
		Vertex v = info.getNewMark().vertex();
		// movable.add(v);

		// Node n = vertexNodeMap.get(v);
		// ((StateRealizer)view.getGraph2D().getRealizer(n)).setStateType(StateRealizer.OPEN_STATE);
	}

	protected void addVertex(Vertex v) {
		graph.addVertex(v);
	}

	protected Point2D.Double getCenter() {
		Dimension d = zoom.getSize();
		return new Point2D.Double(d.width / 2, d.height / 2);
	}

	protected void addEdge(DirectedEdge uv) {
		// if (layoutTransition != null) {
		// if (layoutTransition.done())
		// changeLayouter(currentLayoutIndex);
		// } else
		// changeLayouter(currentLayoutIndex);
		Vertex n1 = uv.source();
		Vertex n2 = uv.target();
		assert n1 != null && n2 != null;
		graph.addEdge(uv, n1, n2);

	}

	protected JungEdgeRealizer createEdgeRealizer(DirectedEdge uv) {
		return new JungEdgeRealizer(this, uv);
	}

	protected JungVertexRealizer createVertexRealizer(Vertex v) {
		return new JungVertexRealizer(this, v);
	}

	public void removeEdge(DirectedEdge uv) {
		graph.removeEdge(uv);
	}

	synchronized public void exportImage() throws Exception {
		justifyGraph();
		int width = view.getSize().width;
		int height = view.getSize().height;
		// paint.setDoubleBuffered(false);
		BufferedImage bi = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = bi.createGraphics();
		graphics.setColor(view.getBackground());
		graphics.fillRect(0, 0, width, height);
		view.paintComponents(graphics);
		// System.out.println("Image-size:"+view.getSize());
		JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showSaveDialog(((VisMain) Registry.getMain()).getGUI()) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			ImageIO.write(bi, "jpeg", file);

		} else {
		}
		//

		// GraphFile<Vertex, DirectedEdge> file = new MatrixFile<Vertex,
		// DirectedEdge>(null);
		// file.save(graph,
		// alg.getConfig().modelName+"_"+alg.getConfig().modelType);
		// System.currentTimeMillis()+ ".svg";
		// SVGIOHandler ioh = new SVGIOHandler();
		// Graph2D graph = view.getGraph2D();
		// assert graph.getCurrentView() == view;
		// Graph2DView exportView = ioh.createDefaultGraph2DView(graph);
		// exportView.setGraph2DRenderer(view.getGraph2DRenderer());
		// graph.setCurrentView(exportView);
		// exportView.setPaintDetailThreshold(0.0d);
		// ViewPortConfigurator vpc = new ViewPortConfigurator();
		// vpc.setGraph2D(graph);
		// vpc.setClipType(ViewPortConfigurator.CLIP_GRAPH);
		// vpc.setSizeType(ViewPortConfigurator.SIZE_USE_ORIGINAL);
		// vpc.configure(exportView);
		// String fn = Config.DIPRO_DIR+"/export/visual" +
		// System.currentTimeMillis()+ ".svg";
		// try {
		// ioh.write(graph, fn);
		// Registry.getMain().out().println("Image exported to "+fn);
		// } catch(IOException e) {
		// Registry.getMain().handleError(e);
		// }
		// finally {
		// assert graph.getCurrentView() == exportView;
		// graph.removeView(exportView);
		// graph.setCurrentView(view);
		// }
	}

	public double getScale() {
		double scale = scaler.getCrossover();
		return 1 > scale ? (1 + scale) : scale;
	}

	@Override
	public void visualizeFromScratch() {
		reset();
		// graph = new DelegateTree<Vertex, DirectedEdge>();
		Vertex startVertex = alg.getContext().getStart();
		// layout.setLocation(startVertex, view.getCenter());
		maxVisFValue = getSearchTree().isExplored(startVertex).f();
		Iterator<? extends Vertex> vIter = getExploredGraph().vertices();
		while (vIter.hasNext()) {
			Vertex v = vIter.next();
			addVertex(v);
		}
		Iterator<? extends DirectedEdge> eIter = getSearchTree().edges();
		while (eIter.hasNext()) {
			DirectedEdge e = eIter.next();
			addEdge(e);
		}
		refleshLayout();
		update();
		setChanged();
		notifyObservers();
		/*
		 * < ForDebugging > try { exportImage(); } catch (IOException e) {
		 * Registry.getMain().handleError(e); } /* </ ForDebugging >
		 */
	}

	protected DetailsDialog createDetailsDialog() throws Exception {
		return null;
	}

	@Override
	protected void handleInitialStateEvent(VisualizationEvent event) {
	}

	public void showNodeInfo(Vertex v) {
		detailsPanel.selectVertex(v);

	}

	public void showEdgeInfo(DirectedEdge e) {
		detailsPanel.selectEdge(e);
	}

	public TracesPanel getTracesPanel() {
		return tracesPanel;
	}

	public Vertex getVertex(Vertex v) {
		return vertexMap.get(v);
	}

	public Edge getEdge(DirectedEdge uv) {
		Edge e = null;
		try {
			e = edgeMap.get(uv);
		} catch (Exception ex) {
			requestTermination();
			try {
				context.getConfig().uniformRate = -1.0d;
				context.init();
				alg = context.loadAlgorithm();
				alg.init();
				start();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}
		return e;
	}

	// private Point2D.Double getInitialPlace() {
	// Dimension viewSize = view.getViewSize();
	// if (layouter instanceof IncrementalHierarchicLayouter)
	// return new Point2D.Double(viewSize.getWidth() / 2d, 10d);
	// if (layouter instanceof OrganicLayouter)
	// return new Point2D.Double(viewSize.getWidth() / 2d, viewSize
	// .getHeight() / 2d);
	// if (layouter instanceof BalloonLayouter)
	// return new Point2D.Double(viewSize.getWidth() / 2d, viewSize
	// .getHeight() / 2d);
	// if (layouter instanceof ARTreeLayouter)
	// return new Point2D.Double(10, 10);
	// return new Point2D.Double(viewSize.getWidth() / 2d, 0.3 * viewSize
	// .getHeight());
	// }

	public double normalize(double f) {
		double nf = Math.min(1.0d, f / maxVisFValue);
		return nf;
	}

	protected String[] getSupportedLayouters() {
		return supportedLayouters;
	}

	protected JungVisualizerControlPanel createControlPanel() {
		return new JungVisualizerControlPanel(this);
	}

	public void start() throws Exception {
		controlPanel.setEnabled(true);
		super.start();
	}

	public void requestPause() {
		super.requestPause();
		controlPanel.setEnabled(true);
	}

	public void requestResume() {
		super.requestResume();
		controlPanel.setEnabled(true);
	}

	public void requestTermination() {
		super.requestTermination();
		controlPanel.setEnabled(true);
	}

	// ////////////////////////////////////////////////////////// //
	// Unimplemented Methods
	// ////////////////////////////////////////////////////////// //
	// @Override
	// protected void loadSettings() throws Exception {
	// // TODO Auto-generated method stub
	//
	// }
	//
	//
	public void finished() throws Exception {
		// TODO Auto-generated method stub

	}

	//
	//
	// public FileFilter getFileFilter() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	//
	public void save(String fileName) throws Exception {
		// TODO Auto-generated method stub

	}

	private Layout<Vertex, DirectedEdge> createLayout(int layoutIndex) {
		currentLayoutIndex = layoutIndex;
		switch (currentLayoutIndex) {
		case BALLOON_LAYOUTER_INDEX:
			return new BalloonLayout<Vertex, DirectedEdge>(graph);
		case RADIAL_LAYOUTER_INDEX:
			return new RadialTreeLayout<Vertex, DirectedEdge>(graph);
		case TREE_LAYOUTER_INDEX:
			return new TreeLayout<Vertex, DirectedEdge>(graph);
		default:
			return new TreeLayout<Vertex, DirectedEdge>(graph);

		}
	}

	private void refleshLayout() {
		model.setGraphLayout(createLayout(currentLayoutIndex));
	}

	public void changeLayouter(int selectedIndex) {
		justifyGraph();
		Layout<Vertex, DirectedEdge> newLayout = createLayout(selectedIndex);
		layoutTransition = new LayoutTransition<Vertex, DirectedEdge>(view,
				model.getGraphLayout(), newLayout);
		Animator animator = new Animator(layoutTransition);
		animator.setSleepTime(100);
		animator.start();
		view.getRenderContext().getMultiLayerTransformer()
				.getTransformer(Layer.LAYOUT).setToIdentity();
		update();
	}

	protected void justifyGraph() {
		Vertex startVertex = alg.getContext().getStart();
		Point2D q = model.getGraphLayout().transform(startVertex);
		Point2D lvc = view.getRenderContext().getMultiLayerTransformer()
				.inverseTransform(view.getCenter());
		final double dx = (lvc.getX() - q.getX()); // / 10;
		final double dy = (lvc.getY() - q.getY()); // / 10;
		view.getRenderContext().getMultiLayerTransformer()
				.getTransformer(Layer.LAYOUT).translate(dx, dy);
	}

	//
	class IncrementLayoutTask extends TimerTask {

		@Override
		public void run() {
			if (layoutTransition != null)
				if (!layoutTransition.done())
					return;
			HashSet<DirectedEdge> newEdges = (HashSet<DirectedEdge>) movable
					.clone();
			movable.clear();
			Iterator<? extends DirectedEdge> edgeIter = newEdges.iterator();
			while (edgeIter.hasNext()) {
				DirectedEdge e = edgeIter.next();
				Vertex n1 = e.source();
				Vertex n2 = e.target();
				assert n1 != null && n2 != null;
				graph.addEdge(e, n1, n2);
			}

			layoutTransition = new LayoutTransition<Vertex, DirectedEdge>(view,
					model.getGraphLayout(), createLayout(currentLayoutIndex));
			Animator animator = new Animator(layoutTransition);
			animator.setSleepTime(10);
			animator.start();
			view.getRenderContext().getMultiLayerTransformer()
					.getTransformer(Layer.LAYOUT).setToIdentity();
			update();
			justifyGraph();

		}
	}

	// ////////////////////////////////////////////////////7
	// Inner Classes
	/**
	 * Action that prints the contents of the view
	 */
	protected class PrintAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4319352391844557066L;
		PageFormat pageFormat;

		// OptionHandler printOptions;

		PrintAction() {
			super("Print");

			// // setup option handler
			// printOptions = new OptionHandler("Print Options");
			// printOptions.addInt("Poster Rows", 1);
			// printOptions.addInt("Poster Columns", 1);
			// printOptions.addBool("Add Poster Coords", false);
			// final String[] area = { "View", "Graph" };
			// printOptions.addEnum("Clip Area", area, 1);
		}

		public void actionPerformed(ActionEvent e) {
			// Graph2DPrinter gprinter = new Graph2DPrinter(view);
			//
			// // show custom print dialog and adopt values
			// if (!printOptions.showEditor())
			// return;
			// gprinter.setPosterRows(printOptions.getInt("Poster Rows"));
			// gprinter.setPosterColumns(printOptions.getInt("Poster Columns"));
			// gprinter.setPrintPosterCoords(printOptions
			// .getBool("Add Poster Coords"));
			// if (printOptions.get("Clip Area").equals("Graph")) {
			// gprinter.setClipType(Graph2DPrinter.CLIP_GRAPH);
			// } else {
			// gprinter.setClipType(Graph2DPrinter.CLIP_VIEW);
			// }
			//
			// // show default print dialogs
			// PrinterJob printJob = PrinterJob.getPrinterJob();
			// if (pageFormat == null)
			// pageFormat = printJob.defaultPage();
			// PageFormat pf = printJob.pageDialog(pageFormat);
			// if (pf == pageFormat) {
			// return;
			// } else {
			// pageFormat = pf;
			// }
			//
			// // setup printjob.
			// // Graph2DPrinter is of type Printable
			// printJob.setPrintable(gprinter, pageFormat);
			//
			// if (printJob.printDialog()) {
			// try {
			// printJob.print();
			// } catch (Exception ex) {
			// ex.printStackTrace();
			// }
			// }
		}
	}

	// class ViewGrid implements Paintable {
	//
	// VisualizationViewer master;
	// VisualizationViewer vv;
	//
	// public ViewGrid(VisualizationViewer vv, VisualizationViewer master) {
	// this.vv = vv;
	// this.master = master;
	// }
	// public void paint(Graphics g) {
	// ShapeTransformer masterViewTransformer =
	// master.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);
	// ShapeTransformer masterLayoutTransformer =
	// master.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
	// ShapeTransformer vvLayoutTransformer =
	// vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
	//
	// Rectangle rect = master.getBounds();
	// GeneralPath path = new GeneralPath();
	// path.moveTo(rect.x, rect.y);
	// path.lineTo(rect.width,rect.y);
	// path.lineTo(rect.width, rect.height);
	// path.lineTo(rect.x, rect.height);
	// path.lineTo(rect.x, rect.y);
	//
	// for(int i=0; i<=rect.width; i+=rect.width/10) {
	// path.moveTo(rect.x+i, rect.y);
	// path.lineTo(rect.x+i, rect.height);
	// }
	// for(int i=0; i<=rect.height; i+=rect.height/10) {
	// path.moveTo(rect.x, rect.y+i);
	// path.lineTo(rect.width, rect.y+i);
	// }
	// Shape lens = path;
	// lens = masterViewTransformer.inverseTransform(lens);
	// lens = masterLayoutTransformer.inverseTransform(lens);
	// lens = vvLayoutTransformer.transform(lens);
	// Graphics2D g2d = (Graphics2D)g;
	// Color old = g.getColor();
	// g.setColor(Color.cyan);
	// g2d.draw(lens);
	//
	// path = new GeneralPath();
	// path.moveTo((float)rect.getMinX(), (float)rect.getCenterY());
	// path.lineTo((float)rect.getMaxX(), (float)rect.getCenterY());
	// path.moveTo((float)rect.getCenterX(), (float)rect.getMinY());
	// path.lineTo((float)rect.getCenterX(), (float)rect.getMaxY());
	// Shape crosshairShape = path;
	// crosshairShape = masterViewTransformer.inverseTransform(crosshairShape);
	// crosshairShape =
	// masterLayoutTransformer.inverseTransform(crosshairShape);
	// crosshairShape = vvLayoutTransformer.transform(crosshairShape);
	// g.setColor(Color.black);
	// g2d.setStroke(new BasicStroke(3));
	// g2d.draw(crosshairShape);
	//
	// g.setColor(old);
	// }
	//
	// }
	//
	//

}
