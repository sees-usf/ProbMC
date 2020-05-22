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


package dipro.installer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class BashFactory {
	
	public static void createLinux(String diproPath, String prismPath){
		 FileWriter fstream;
			try {
				fstream = new FileWriter(diproPath+"/dipro.sh");
				BufferedWriter out = new BufferedWriter(fstream);
				out.write("#!/bin/sh");
				out.newLine();
				out.write("clear");
				out.newLine();
				out.write("DIPRO_DIR="+diproPath);
				out.newLine();
				out.write("PRISM_DIR="+prismPath);
				out.newLine();
				out.write("if [ \"$LD_LIBRARY_PATH\" = \"\" ]; then");
				out.newLine();
				out.write("	LD_LIBRARY_PATH=\"$PRISM_DIR\"/lib");
				out.newLine();
				out.write("else");
				out.newLine();
				out.write("LD_LIBRARY_PATH=\"$PRISM_DIR\"/lib:$LD_LIBRARY_PATH");
				out.newLine();
				out.write("fi");
				out.newLine();
				out.write("DIPRO_CLASSPATH=\\");
				out.newLine();
				out.write("\"$DIPRO_DIR\"/lib/dipro.jar\\");
				out.newLine();
				out.write(":\"$DIPRO_DIR\"\\");
				out.newLine();
				out.write(":\"$PRISM_DIR\"/lib/prism.jar\\");
				out.newLine();
				out.write(":\"$PRISM_DIR\"/lib/pepa.zip\\");
				out.newLine();
				out.write(":\"$DIPRO_DIR\"/lib/forms.jar\\");
				out.newLine();
				out.write(":\"$DIPRO_DIR\"/lib/jcommon.jar\\");
				out.newLine();
				out.write(":\"$DIPRO_DIR\"/lib/looks.jar\\");
				out.newLine();
				out.write(":\"$DIPRO_DIR\"/lib/jung-algorithms-2.0.1.jar\\");
				out.newLine();
				out.write(":\"$DIPRO_DIR\"/lib/jung-api-2.0.1.jar\\");
				out.newLine();
				out.write(":\"$DIPRO_DIR\"/lib/jung-graph-impl-2.0.1.jar\\");
				out.newLine();
				out.write(":\"$DIPRO_DIR\"/lib/jung-visualization-2.0.1.jar\\");
				out.newLine();
				out.write(":\"$DIPRO_DIR\"/lib/jfreechart-1.0.9.jar");
				out.newLine();
				out.write("export DIPRO_DIR PRISM_DIR LD_LIBRARY_PATH");
				out.newLine();
				out.write("java -Djava.library.path=$PRISM_DIR/lib -classpath \"$DIPRO_CLASSPATH\" dipro.run.VisMain \"$@\"");
				out.close();
			} catch (IOException e) {
			}
		
	}
	public static void createMacOS(String diproPath, String prismPath){
		 FileWriter fstream;
			try {
				fstream = new FileWriter(diproPath+"/dipro.sh");
				BufferedWriter out = new BufferedWriter(fstream);
				out.write("#!/bin/sh");
				out.newLine();
				out.write("clear");
				out.newLine();
				out.write("DIPRO_DIR="+diproPath);
				out.newLine();
				out.write("PRISM_DIR="+prismPath);
				out.newLine();
				out.write("if [ \"$DYLD_LIBRARY_PATH\" = \"\" ]; then");
				out.newLine();
				out.write("	DYLD_LIBRARY_PATH=\"$PRISM_DIR\"/lib");
				out.newLine();
				out.write("else");
				out.newLine();
				out.write("DYLD_LIBRARY_PATH=\"$PRISM_DIR\"/lib:$DYLD_LIBRARY_PATH");
				out.newLine();
				out.write("fi");
				out.newLine();
				out.write("DIPRO_CLASSPATH=\\");
				out.newLine();
				out.write("\"$DIPRO_DIR\"/lib/dipro_obf.jar\\");
				out.newLine();
				out.write(":\"$DIPRO_DIR\"\\");
				out.newLine();
				out.write(":\"$PRISM_DIR\"/lib/prism.jar\\");
				out.newLine();
				out.write(":\"$PRISM_DIR\"/lib/pepa.zip\\");
				out.newLine();
				out.write(":\"$DIPRO_DIR\"/lib/forms.jar\\");
				out.newLine();
				out.write(":\"$DIPRO_DIR\"/lib/jcommon.jar\\");
				out.newLine();
				out.write(":\"$DIPRO_DIR\"/lib/looks.jar\\");
				out.newLine();
				out.write(":\"$DIPRO_DIR\"/lib/y_obf.jar\\");
				out.newLine();
				out.write(":\"$DIPRO_DIR\"/lib/jfreechart-1.0.9.jar");
				out.newLine();
				out.write("export DIPRO_DIR PRISM_DIR DYLD_LIBRARY_PATH");
				out.newLine();
				out.write("java -Djava.library.path=$PRISM_DIR/lib -classpath \"$DIPRO_CLASSPATH\" dipro.run.VisMain \"$@\"");
				out.close();
			} catch (IOException e) {
			}
		
	}
	public static void createWindows(String diproPath, String prismPath){
		 FileWriter fstream;
		try {
			fstream = new FileWriter(diproPath+"/dipro.bat");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("@echo off");
			out.newLine();
			out.write("set DIPRO_DIR="+diproPath);
			out.newLine();
			out.write("set PRISM_DIR="+prismPath);
			out.newLine();
			out.write("path=%PRISM_DIR%\\lib\\;%DIPRO_DIR%\\lib\\;%path%");
			out.newLine();
			out.write("set CP=%DIPRO_DIR%\\lib\\dipro_obf.jar;%DIPRO_DIR%;%DIPRO_DIR%\\lib\\jcommon.jar\\;%DIPRO_DIR%\\lib\\looks.jar\\;%DIPRO_DIR%\\lib\\forms.jar\\;%DIPRO_DIR%\\lib\\y_obf.jar\\;%DIPRO_DIR%\\lib\\jfreechart-1.0.9.jar\\;%PRISM_DIR%\\lib\\prism.jar;%PRISM_DIR%\\lib\\pepa.zip");
			out.newLine();
			out.write("java -Djava.library.path=\"%PRISM_DIR%\\lib\" -classpath \"%CP%\" dipro.run.VisMain %*"); 
			out.close();
		} catch (IOException e) {
		}
	}
}
