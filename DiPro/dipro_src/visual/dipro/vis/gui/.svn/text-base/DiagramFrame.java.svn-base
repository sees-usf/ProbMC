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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import dipro.graph.Vertex;
import dipro.run.Registry;
import dipro.util.Trace;
import dipro.vis.AbstractVisualizer;
import dipro.vis.Visualizer;

public class DiagramFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3666691282325604155L;

	protected Visualizer visualizer;
	protected LinkedList<Trace> traces;
	protected JComboBox variableSelector;
	protected ChartPanel chartPanel;

	public DiagramFrame(Visualizer visualizer) throws Exception {
		this.visualizer = visualizer;
		this.setTitle("Dipro Barchart Window");
		this.traces = new LinkedList<Trace>();
		Vector<String> variables = new Vector<String>();
		variables.add("Heuristic Value");
		variables.addAll(visualizer.getContext().getGraph().getVertexLabels());
		variableSelector = new JComboBox(variables);
		variableSelector.setSelectedIndex(0);
		variableSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					visualize();
				} catch (Exception e1) {
					Registry.getMain().handleError("Visualisation Failure!", e1);
				}
			}
		});
		JPanel panel = new JPanel();
		panel.add(new JLabel("Select variable to draw"), BorderLayout.EAST);
		panel.add(variableSelector, BorderLayout.SOUTH);
		add(panel, BorderLayout.SOUTH);
		setSize(new Dimension(600, 300));
		setVisible(false);
	}

	public void visualizeTrace(Trace t) throws Exception {
		traces.clear();
		traces.add(t);
		visualize();
		setVisible(true);
	}

	public void visualizeTraces(Collection<Trace> t) throws Exception {
		traces.clear();
		traces.addAll(t);
		visualize();
		setVisible(true);
	}

	public void visualize() throws Exception {
		if (traces.isEmpty())
			return;
		int selectedVariable = variableSelector.getSelectedIndex();
		String varName = (String) variableSelector.getSelectedItem();
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		// StandardCategoryToolTipGenerator ttGenerator = new
		// StandardCategoryToolTipGenerator();
		int t = 0;
		for (Trace trace : traces) {
			Integer c = new Integer(t);
			Iterator<Vertex> iter = trace.getVertices();
			int j = 0;
			while (iter.hasNext()) {
				Vertex vertex = iter.next();
				if (selectedVariable == 0) {
					double value = visualizer.getSearchTree().f(vertex);
					dataSet.addValue(value, c, new Integer(j));
				} else {
					Number value = getNumberValue(vertex, varName);
					dataSet.addValue(value, c, new Integer(j));
				}
				// series, category
				// ttGenerator.generateToolTip(dataSet, )
				j++;
			}
			t++;
		}
		JFreeChart chart = createChart(selectedVariable, dataSet);
		if (chartPanel == null) {
			chartPanel = new ChartPanel(chart, true);
			add(chartPanel, BorderLayout.NORTH);
			// chartPanel.setPreferredSize(new Dimension(500, 270));
			chartPanel.setVisible(true);
		} else
			chartPanel.setChart(chart);
	}

	private Number getNumberValue(Vertex v, String varName) throws Exception {
		Object value = v.getLabelValue(varName);
		if (value instanceof Boolean) {
			return ((Boolean) value) ? new Integer(1) : new Integer(0);
		}
		return (Number) value;
	}

	private JFreeChart createChart(int i, CategoryDataset dataset) {
		// create the chart...
		StringBuilder title = new StringBuilder();
		if (i != 0)
			title.append("Values of the Variable ");
		title.append(variableSelector.getItemAt(i).toString());
		JFreeChart chart = ChartFactory.createBarChart(title.toString(), // chart
																			// title
				"Vertex Index", // x-axis label
				"Value", // y-axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				true, // tooltips?
				false // URLs?
				);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

		// set the background color for the chart...
		chart.setBackgroundPaint(Color.white);

		// get a reference to the plot for further customisation...
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.white);

		CategoryItemRenderer renderer = ((CategoryItemRenderer) plot
				.getRenderer());
		renderer.setBaseToolTipGenerator(new TooltipGenerator());
		// renderer.setItemLabelGenerator(new LabelGenerator());
		renderer.setBaseItemLabelGenerator(new LabelGenerator());

		// ******************************************************************
		// More than 150 demo applications are included with the JFreeChart
		// Developer Guide...for more information, see:
		//
		// > http://www.object-refinery.com/jfreechart/guide.html
		//
		// ******************************************************************

		// set the range axis to display integers only...
		// final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		// rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// disable bar outlines...
		// BarRenderer renderer = (BarRenderer) plot.getRenderer();
		// renderer.setDrawBarOutline(false);

		// set up gradient paints for series...
		// GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue,
		// 0.0f, 0.0f, new Color(0, 0, 64));
		// GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green,
		// 0.0f, 0.0f, new Color(0, 64, 0));
		// GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red,
		// 0.0f, 0.0f, new Color(64, 0, 0));
		// renderer.setSeriesPaint(0, gp0);
		// renderer.setSeriesPaint(1, gp1);
		// renderer.setSeriesPaint(2, gp2);

		// CategoryAxis domainAxis = plot.getDomainAxis();
		// domainAxis.setCategoryLabelPositions(
		// (Math.PI / 6.0));
		// OPTIONAL CUSTOMISATION COMPLETED.

		return chart;
	}

	public String getStateTooltip(int traceIndex, int index) {
		// Vertex v = getVertex(string, depth);
		assert traceIndex >= 0 && traceIndex < traces.size();
		Trace trace = traces.get(traceIndex);
		assert index <= trace.length();
		Vertex vertex = trace.getVertex(index);
		StringBuilder text = new StringBuilder("<html>");
		// State s = getState(string, depth);
		text.append("<table>");
		text.append("<tr>");
		text.append("<td align=\"right\"><em> f Value: </em></td>");
		double f = visualizer.getSearchTree().f(vertex);
		text.append("<td align=\"left\"><b>");
		text.append(AbstractVisualizer.getFormat().format(f));
		text.append("</b></td>");
		text.append("</tr>");
		// Values values = ((PrismState)s).values();
		Collection<String> relevantVars;
		try {
			relevantVars = visualizer.getContext().getProperty()
					.relevantLabels();
		} catch (Exception e) {
			relevantVars = new LinkedList<String>();
			Registry.getMain().handleWarning("Unable to fetch relevant labels");
		}
		for (String name : relevantVars) {
			text.append("<tr>");
			text.append("<td align=\"right\"><b>");
			text.append(name);
			text.append("</b></td>");
			text.append("<td align=\"left\"><b>");
			try {
				text.append(vertex.getLabelValue(name));
			} catch (Exception e) {
				text.append("Unknown");
			}
			text.append("</b></td>");
			text.append("</tr>");
		}
		List<String> labelNames;
		try {
			labelNames = visualizer.getContext().getGraph().getVertexLabels();
		} catch (Exception e1) {
			Registry.getMain().handleError("Failed to get the vertex labels!", e1);
			labelNames = Collections.EMPTY_LIST;
		}
		for (String name : labelNames) {
			// String name = values.getName(i);
			if (!relevantVars.contains(name)) {
				text.append("<tr>");
				text.append("<td align=\"right\">");
				text.append(name);
				text.append("</td>");
				String value;
				try {
					value = vertex.getLabelValue(name).toString();
				} catch (Exception e) {
					Registry.getMain().handleWarning("Unkown label " + name);
					value = "UNKOWN";
				}
				text.append("<td align=\"right\">");
				text.append(value);
				text.append("</td>");
				text.append("</tr>");
			}
		}
		text.append("</table>");
		text.append("</html>");
		return text.toString();
	}

	private class TooltipGenerator implements CategoryToolTipGenerator {
		public String generateToolTip(CategoryDataset set, int row, int col) {
			return getStateTooltip(row, col);
		}
	}

	private class LabelGenerator extends StandardCategoryItemLabelGenerator {

		public String generateRowLabel(CategoryDataset dataset, int row) {
			Trace trace = traces.get(row);
			return trace.toBoundedString(50);
		}

		public String generateColumnLabel(CategoryDataset dataset, int col) {
			Trace trace = traces.get(col);
			return trace.toBoundedString(50);
		}
	}
}
