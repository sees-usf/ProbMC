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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

public class RGBColorScale implements ColorScale {

	protected String scaleFileName;
	protected double maxColorLevel = -1.0;
	protected Vector<Color> colors;

	public RGBColorScale(String scaleFileName) throws IOException {
		this.scaleFileName = scaleFileName;
		load();
	}

	protected void load() throws IOException {
		colors = new Vector<Color>(256);
		File scaleFile = new File(scaleFileName);
		assert scaleFile.exists();
		BufferedReader in = new BufferedReader(new FileReader(scaleFile));
		int i = 0;
		String s = in.readLine();
		while (s != null) {
			StringTokenizer st = new StringTokenizer(s, ",");
			assert st.countTokens() == 3;
			float r = Float.parseFloat(st.nextToken()) / 255.0f;
			float g = Float.parseFloat(st.nextToken()) / 255.0f;
			float b = Float.parseFloat(st.nextToken()) / 255.0f;
			colors.add(i, new Color(r, g, b));
			s = in.readLine();
			i++;
		}
	}

	public Color color(int c) {
		return colors.elementAt(c);
	}

	public int numColor() {
		return colors.size();
	}

}
