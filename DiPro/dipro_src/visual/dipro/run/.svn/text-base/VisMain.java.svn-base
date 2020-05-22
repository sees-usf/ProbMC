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

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

import javax.swing.JTextArea;

import dipro.alg.BF;
import dipro.run.wizard.ExperimentSetup;
import dipro.util.DiProException;
import dipro.vis.Visualizer;
import dipro.vis.gui.GUI;

public class VisMain extends Main {

	// protected VisualizationThread currentVisThread;
	protected GUI gui;

	private static Splash splash;

	public VisMain() {
		super();
		try {
			gui = GUI.createSingleGUI();
			gui.load();
		} catch (Exception e) {
			handleFatalError(e);
		}
		JTextArea console = gui.getConsole();
		out = new TextAreaPrintStream(console);
	}

	public GUI getGUI() {
		return gui;
	}

	protected DiPro createDiPro() throws Exception {
		return createDiPro(false);
	}

	protected DiPro createDiPro(boolean plugIn) throws Exception {
		return new VisDiPro(plugIn);
	}

	public void start(String[] args) {
		if (args.length == 1
				&& (args[0].equals("--help") || args[0].equals("-help"))) {
			try {
				File wDir = new File(System.getProperty("user.dir"));
				expBasePath = wDir.getCanonicalPath();
			} catch (IOException e) {
				expBasePath = System.getProperty("user.dir");
				handleError(e);
			}
			try {
				printManual();
			} catch (IOException e) {
				handleFatalError(e);
			}
			System.exit(0);
		}
		if (args.length == 0) {
			try {
				printManual();
			} catch (IOException e) {
				handleFatalError(e);
			}
			System.exit(0);
		}
		if (args.length == 2 && args[0].equals("-batch")) {
			out.println("Run in batch mode...");
			out.println("Batch file: " + args[1]);
			out.println();
			File expFile = new File(args[1]);
			try {
				expFile = expFile.getCanonicalFile();
				if (!expFile.exists()) {
					DiProException e = new DiProException(
							"Batch file does not exist: "
									+ expFile.getCanonicalPath());
					handleFatalError(e);
				}
				expBasePath = expFile.getParentFile().getCanonicalPath();
			} catch (IOException e) {
				expBasePath = (expFile.getParentFile()).getAbsolutePath();
				handleWarning("Failed to get the canonical path from "
						+ expBasePath, e);
			}
			loadExperiments(args[1]);
			runNextExperiment();
		} else {
			run(args);
		}
	}

	public void batchRun(String expFileName) {
		throw new UnsupportedOperationException();
	}

	public void run(String[] params) {
		experimentCounter++;
		DiPro dipro = null;
		Config config = null;
		VisContext context = null;
		BF alg = null;
		try {
			dipro = createDiPro();
			config = dipro.loadConfig(params);
			out.println("Run Experiment");
			out.println(formatParams(params));
			context = (VisContext) dipro.loadContext(experimentCounter, config);
			context.init();
			out.println(context);
			alg = context.loadAlgorithm();
			alg.init();
			out.println("Algorithm " + alg + " loaded");
			Visualizer visualizer = context.createVisualizer(alg);
			visualizer.init();
			if (config.onlineVisualization) {
				gui.openVisualization(visualizer);
			} else {
				out.println("Search algorithm started...");
				alg.execute();
				out.println(alg.getSummaryReport());
				out.println("Visualizer loaded.");
				gui.openVisualization(visualizer);
				out.println("Visualizing...");
				visualizer.visualizeFromScratch();
				out.println("Visualization created.");
			}
		} catch (Exception e) {
			handleError(e);
		}
	}

	public void run() {
		run(false);
	}

	public void run(boolean oldConfig) {
		experimentCounter++;
		DiPro dipro = null;
		Config config = null;
		VisContext context = null;
		BF alg = null;
		try {
			dipro = createDiPro(true);
			config = ExperimentSetup.createInstance(dipro, oldConfig);
			if (config == null)
				handleFatalError("Not a valid configuration!");
			Registry.setMain(this);

			// config = dipro.loadConfig();
			out.println("Run Experiment");
			context = (VisContext) dipro.loadContext(experimentCounter, config);
			context.init();
			out.println(context);
			alg = context.loadAlgorithm();
			alg.init();
			out.println("Algorithm " + alg + " loaded");
			Visualizer visualizer = context.createVisualizer(alg);
			visualizer.init();
			if (config.onlineVisualization) {
				gui.openVisualization(visualizer);
			} else {
				out.println("Search algorithm started...");
				alg.execute();
				out.println(alg.getSummaryReport());
				out.println("Visualizer loaded.");
				gui.openVisualization(visualizer);
				out.println("Visualizing...");
				visualizer.visualizeFromScratch();
				out.println("Visualization created.");
			}

		} catch (Exception e) {
			handleError(e);
		}
	}

	public void closeVisualization() {
		gui.releaseVisualizationView();
	}

	public void handleFatalError(Exception e) {
		gui.showErrorDialog(e.toString());
		super.handleFatalError(e.toString());
	}

	public void handleError(Exception e) {
		gui.showErrorDialog(e.toString());
		super.handleError(e.toString());
	}

	public void handleWarning(Object msg) {
		gui.showWarningDialog(msg.toString());
		super.handleWarning(msg.toString());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		splash = new Splash("etc/splash.png");
		splash.display();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		EventQueue.invokeLater(new VisMain.SplashScreenCloser());
		VisMain main = new VisMain();
		// main.start(args);
		main.run();
	}

	private class TextAreaPrintStream extends PrintStream {

		private JTextArea console;

		public TextAreaPrintStream(JTextArea console) {
			super(System.out);
			this.console = console;
		}

		@Override
		public PrintStream append(char c) {
			console.append(Character.toString(c));
			return this;
		}

		@Override
		public PrintStream append(CharSequence csq, int start, int end) {
			console.append(csq.subSequence(start, end).toString());
			return this;
		}

		@Override
		public PrintStream append(CharSequence csq) {
			return append(csq, 0, csq.length());
		}

		@Override
		public boolean checkError() {
			return false;
		}

		@Override
		protected void clearError() {
		}

		@Override
		public void close() {
		}

		@Override
		public void flush() {
		}

		@Override
		public PrintStream format(Locale l, String format, Object... args) {
			throw new UnsupportedOperationException();
		}

		@Override
		public PrintStream format(String format, Object... args) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void print(boolean b) {
			console.append(Boolean.toString(b));
		}

		@Override
		public void print(char c) {
			console.append(Character.toString(c));
		}

		@Override
		public void print(char[] s) {
			console.append(new String(s));
		}

		@Override
		public void print(double d) {
			console.append(Double.toString(d));
		}

		@Override
		public void print(float f) {
			console.append(Float.toString(f));
		}

		@Override
		public void print(int i) {
			console.append(Integer.toString(i));
		}

		@Override
		public void print(long l) {
			console.append(Long.toString(l));
		}

		@Override
		public void print(Object obj) {
			console.append(obj.toString());
		}

		@Override
		public void print(String s) {
			console.append(s);
		}

		@Override
		public PrintStream printf(Locale l, String format, Object... args) {
			throw new UnsupportedOperationException();
		}

		@Override
		public PrintStream printf(String format, Object... args) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void println() {
			console.append("\n");
		}

		@Override
		public void println(boolean x) {
			console.append(x + "\n");
		}

		@Override
		public void println(char x) {
			console.append(x + "\n");
		}

		@Override
		public void println(char[] x) {
			console.append(new String(x) + "\n");
		}

		@Override
		public void println(double x) {
			console.append(x + "\n");
		}

		@Override
		public void println(float x) {
			console.append(x + "\n");
		}

		@Override
		public void println(int x) {
			console.append(x + "\n");
		}

		@Override
		public void println(long x) {
			console.append(x + "\n");
		}

		@Override
		public void println(Object x) {
			console.append(x + "\n");
		}

		@Override
		public void println(String x) {
			console.append(x + "\n");
		}

		@Override
		protected void setError() {
		}

		@Override
		public void write(byte[] buf, int off, int len) {
			console.append(new String(buf, off, len));
		}

		@Override
		public void write(int b) {
			console.append(Integer.toString(b));
		}

		@Override
		public void write(byte[] buf) throws IOException {
			console.append(new String(buf));
		}

	}

	/**
	 * Thread to close the splash screen
	 */
	private static final class SplashScreenCloser implements Runnable {
		public void run() {
			splash.dispose();
		}
	}
}
