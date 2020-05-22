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

import java.awt.Component;

/**
 * This interface defines an object capable of drawing a 3D graph
 * 
 */
public interface InteractiveView extends View {

	/**
	 * Call this method to get the component where the 3D-Image is created in.
	 * 
	 * @return Component graphCanvas
	 */
	public Component getViewer();
	//	
	// /**
	// * Center the graph on the canvas
	// *
	// */
	// public abstract void centerGraph();
	//	
	// /**
	// * Hide all labels in the visualization
	// *
	// */
	// public void showLabels(boolean show);
	//	
	//	
	// /**
	// * Save a jpeg image of the actual graph. The resolution is not limited to
	// * screen resolution
	// *
	// * @param fileName the file to store the image in
	// * @param dim the dimension of the image
	// * @throws Exception I/O exceptions
	// */
	// public abstract void saveImage(String fileName, Dimension dim) throws
	// Exception;
	//	
	// /**
	// * Get the appropriate file filter for image output files.
	// * @return the file filter
	// */
	// public abstract FileFilter getImageFileFilter();
	//	
	// /**
	// * Update the layout and redraw the graph. This method must be called
	// manually
	// * if the graph has changed and should displayed in the new configuration.
	// * Automatic updating can be enabled by calling autoupdate(true)
	// *
	// */
	// public abstract void update();
	//	
	// // public void init(ExploredSpace space, Collection<String>
	// relevantStateLabels) throws Exception;
	//	
	// /**
	// * This method deletes all visual objects and restores
	// * the initial state of this object.
	// *
	// */
	// public abstract void reset();
	//	
	// public abstract String getName();
}
