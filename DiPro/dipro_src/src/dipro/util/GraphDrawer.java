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

package dipro.util;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class GraphDrawer {

	public void draw(String traFile, String init) {
		try {
			BufferedReader tra = new BufferedReader(new FileReader(traFile));
			assert traFile.substring(traFile.length()-4).equals(".tra");
			String fname = traFile.substring(0, traFile.length()-4);
			System.out.println("Write Dot file "+fname+".dot");
			PrintStream dot = new PrintStream(new FileOutputStream(fname+".dot"));
//			digraph G {
//			  a -> b [label="hello", style=dashed];
//			  a -> c [label="world"];
//			  c -> d; b -> c; d -> a;
//			  b [shape=Mdiamond, label="this is b"];
//			  c [shape=polygon, sides=5, peripheries=3];
//			  d [style=bold];
//			}
			dot.println("digraph G {");
			System.out.println(tra.readLine());
			System.out.println(tra.readLine());
			String line = tra.readLine();
			while(line!=null) {
				String[] tokens = line.split("[ ]");
				assert tokens.length == 3;
//				dot.println("\t"+tokens[0]+" -> "+tokens[1]+" [label=\""+tokens[2]+"\"];");
				dot.println("\t"+tokens[0]+" -> "+tokens[1]+";");
				line = tra.readLine();
			}
			dot.println("\t"+init+" [shape=diamond];");
			dot.println("}");
			tra.close();
			dot.close();
			System.out.println("dot -Tps "+fname+".dot"+" -o "+fname+".ps");
//			Process proc = Runtime.getRuntime().exec("dot -Tps "+fname+".dot"+" -o "+fname+".ps");
//			proc.waitFor();
			System.out.println("Done");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
//		catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public static void main(String[] args) {
		GraphDrawer drawer = new GraphDrawer();
		drawer.draw(args[0], args[1]);
	}
}
