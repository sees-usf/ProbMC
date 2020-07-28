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

package dipro.graph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Random;

import dipro.util.DiProException;

public class GraphRandomGenerator {

	protected int numVertices;
	protected String name;
	protected File dir;

	public void generate(int numVertices, String name) throws DiProException,
			IOException {
		this.numVertices = numVertices;
		this.name = name;
		setup();
		generateEdgeMatrix();
		generateTargets();
		clean();
	}

	protected void setup() throws DiProException, IOException {
		dir = new File(name);
		if (dir.exists()) {
			File[] fa = dir.listFiles();
			for (int i = 0; i < fa.length; i++)
				fa[i].delete();
			dir.delete();
		}
		boolean b = dir.mkdirs();
		if (!b) {
			throw new DiProException("Unable to create folder: " + name);
		}
	}

	protected void clean() {
		name = null;
		dir = null;
		numVertices = 0;
		System.gc();
	}

	protected int generateEdgeMatrix() throws IOException, DiProException {
		Random r = new Random();
		int maxOutDegree = Math.min(numVertices / 2, 20);
		int n = 0;
		for (int v = 0; v < numVertices; v++) {
			String outFileName = dir.getCanonicalPath() + "/out_" + v + ".txt";
			PrintStream stream = new PrintStream(new FileOutputStream(outFileName));
			int k = r.nextInt(maxOutDegree) + 1;
			System.out.println("Handle " + v + ", " + k + " outgoing edges");
			n = n + k;
			HashMap<Integer, Integer> targets = new HashMap<Integer, Integer>();
			for (int i = 0; i < k; i++) {
				int t = r.nextInt(numVertices);
				if (targets.get(t) != null) {
					i--;
				} else {
					// integer weight from [1, 10]
					int w = r.nextInt(10) + 1;
					// int w = 1;
					targets.put(t, w);
					stream.println(v + " " + t + " " + w);
					String str = dir.getCanonicalPath() + "/in_" + t + ".txt";
					File f = new File(str);
					if (!f.exists()) {
						boolean b = f.createNewFile();
						if (!b)
							throw new DiProException("Unable to create File: " + f.getCanonicalPath());
					}
					FileWriter fw = new FileWriter(f, true);
					fw.write(v + " " + t + " " + w + "\n");
					fw.close();
				}
				stream.close();
			}
		}
		String mainFileName = dir.getCanonicalPath() + "/main.txt";
		PrintStream out = new PrintStream(new FileOutputStream(mainFileName));
		out.println("VERTICES " + numVertices);
		out.println("EDGES " + n);
		out.close();
		return n;
	}

	protected int generateTargets() throws IOException, DiProException {
		Random r = new Random();
		int maxTargets = Math.max(1, (int) (0.1 * numVertices));
		String targetsFileName = dir.getCanonicalPath() + "/targets.txt";
		PrintStream out = new PrintStream(new FileOutputStream(targetsFileName));
		int n = r.nextInt(maxTargets) + 1;
		HashMap<Integer, Integer> targets = new HashMap<Integer, Integer>();
		for (int i = 0; i < n; i++) {
			int t = r.nextInt(numVertices);
			if (targets.get(t) != null) {
				i--;
			} else {
				targets.put(t, t);
				out.println(t);
			}
		}
		out.close();
		String str = dir.getCanonicalPath() + "/main.txt";
		File mainFile = new File(str);
		if (!mainFile.exists()) {
			throw new DiProException("Main file is missing, graph = "
					+ dir.getCanonicalPath());
		}
		FileWriter fw = new FileWriter(mainFile, true);
		fw.write("TARGETS " + n);
		fw.close();
		mainFile = null;
		return n;
	}
}
