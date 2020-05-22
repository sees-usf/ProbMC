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

import dipro.alg.BF;
import dipro.run.Registry;
import dipro.vis.Visualizer;

public class VisualizationControl {

	protected Visualizer visualizer;

	public VisualizationControl(Visualizer vis) {
		this.visualizer = vis;
	}

	public int getAlgStatus() {
		return visualizer.getAlgStatus();
	}

	public void closeVisualization() throws Exception {
		visualizer.close();
//		Thread thr = new Thread() {
//			@Override
//			public void run() {
//				try {
//					visualizer.close();
//				} catch (Exception e) {
//					Registry.getMain().handleError(e);
//				}
//			}
//		};
//		thr.start();
	}

	public void modelCheckSolution() {
//		visualizer.modelCheckSolution();
//		Thread thr = new Thread() {
//			@Override
//			public void run() {
////				try {
////					visualizer.getAlgorithm().modelCheckSolution();
////				} catch (Exception e) {
////					Registry.getMain().handleError(e);
////				}
//			}
//		};
//		thr.start();
	}

	public void performModelChecking() {
		// visualizer.performModelChecking();
		Thread thr = new Thread() {
			@Override
			public void run() {
				try {
					visualizer.performModelChecking();
				} catch (Exception e) {
					Registry.getMain().handleError("Failed to perform the model checking step!", e);
				}
			}
		};
		thr.start();
	}

	public void start() {
		// visualizer.start();
		Thread thr = new Thread() {
			@Override
			public void run() {
				try {
					visualizer.start();
				} catch (Exception e) {
					Registry.getMain().handleError("Starting the visualisation failed!", e);
				}
			}
		};
		thr.start();
	}

	public void requestPause() {
		// visualizer.requestPause();
		Thread thr = new Thread() {
			@Override
			public void run() {
				visualizer.requestPause();
			}
		};
		thr.start();
	}

	public void requestResume() {
		Thread thr = new Thread() {
			@Override
			public void run() {
				visualizer.requestResume();
			}
		};
		thr.start();
	}

	public void requestTermination() {
		Thread thr = new Thread() {
			@Override
			public void run() {
				visualizer.requestTermination();
			}
		};
		thr.start();
	}

	public void visualizeFromScratch() {
		Thread thr = new Thread() {
			@Override
			public void run() {
				int s = visualizer.getAlgStatus();
				switch (s) {
				case BF.NOT_INITIALIZED:
				case BF.READY:
					break;
				case BF.RUNNING:
					visualizer.requestPause();
					visualizer.visualizeFromScratch();
					visualizer.requestResume();
					break;
				case BF.PAUSED:
				case BF.TO_TERMINATE:
				case BF.TERMINATED:
					visualizer.visualizeFromScratch();
					break;
				case BF.CLEANED_UP:
					break;
				}
			}
		};
		thr.start();
	}

	public void exportImage() {
		Thread thr = new Thread() {
			@Override
			public void run() {
				try {
					visualizer.exportImage();
				} catch (Exception e) {
					Registry.getMain().handleError("The visualisation could not exported correctly into an image!", e);
				}
			}
		};
		thr.start();
	}
}
