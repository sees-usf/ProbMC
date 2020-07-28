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

public class Prism2MRMC {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String mf; 
		String pf;
		String p;
		String cf;
		
		if(args.length<3) {
			System.out.println("Parameters: model_file properties_ file property_index [constants_file]");
			System.exit(0);
		}
		
		mf = args[0];
		pf = args[1];
		p =args[2];
		if(args.length>3) {
			cf = args[3];
		}
		else {
			cf = null;
		}
		String[] params; 
		if(cf==null) {
			params = new String[]{"-prism", mf, pf, "-prop", p}; 
		}
		else {
			params = new String[]{"-prism", mf, pf, cf, "-prop", p};
		}
		Config config = null;
		PrismDefaultContext context = null;
		try {
			new Main();
			DiPro dipro = new DiPro();
			config = dipro.loadConfig(params);
			context = (PrismDefaultContext)dipro.loadContext(1, config);
			context.init();
			context.convertToMRMC();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
