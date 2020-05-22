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

public class VisDiPro extends DiPro {

	public VisDiPro() throws Exception {
		this(false);
	}
	public VisDiPro(boolean isPlugin) throws Exception {
		super(isPlugin);
	}


	public AbstractContext loadContext(int id, Config config) throws Exception {
		AbstractContext abstractContext = null;
		switch (config.modelType) {
		case Config.DIRECTED_GRAPH:
			abstractContext = new VisGraphContext(id, config);
			break;
		case Config.PRISM_MODEL:
			abstractContext = new VisPrismContext(id, config);
			break;
//		case Config.PRISM_MRMC_MODEL:
//			PrismContext tempSett = new VisPrismContext(id, config);
//			tempSett.init();
//			abstractContext = tempSett.convertToMRMC();
//			break;
		case Config.MRMC_MODEL:
			abstractContext = new VisMRMCContext(id, config);
			break;
		default:
			throw new IllegalArgumentException("Unsupported model type: "
					+ config.modelType);
		}
		return abstractContext;
	}
	
}
