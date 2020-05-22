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

import java.awt.Color;
import java.awt.Component;
import java.util.Observer;

import dipro.run.VisContext;

/**
 * This interface describes objects capable of visualizing how any algorithm
 * explores a state space.
 * 
 */
public interface VisualizerOld extends Observer {

	// public final static String IDENTIFIER = new String("VISUALIZER");
	public final static String COLOR_SCALE_FILE_NAME = new String(
			"etc/colorscale_blue_256.txt");

	public void init() throws Exception;

	// /**
	// * Get the file filter describing the file format
	// * of this visualizer.
	// *
	// * @return the file filter
	// */
	// public FileFilter getFileFilter();

	public String getName();

	public void update();

	/**
	 * This method should be called when the algorithm has terminated or should
	 * not expand further. In file-oriented visualization outputs this method is
	 * used to determine when to build and save the output files.
	 * 
	 * @throws Exception
	 * 
	 */
	public void finished() throws Exception;

	/**
	 * Save the actual state of the state space. The file format depends on the
	 * implementation of the visSettings
	 * 
	 * @param fileName -
	 *            the file to store the state space in
	 */
	public void save(String fileName) throws Exception;

	public Color color(float f);

	public String toString();

	public Component getVisComponent();

	// public BF getAlgorithm();
	public VisContext getContext();

	public VisSearchTree getVisExploredGraph();
}
