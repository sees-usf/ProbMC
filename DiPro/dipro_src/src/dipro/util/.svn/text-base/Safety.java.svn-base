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

import java.util.Collection;

public interface Safety extends Proposition {

	/**
	 * This String is used to identify the flag which is added to identify
	 * target state in the solution delivered by the search algorthim.
	 */
	public static final String TARGET_FLAG_NAME = "isTarget";

	/**
	 * Delivers the names of labels, e.g. variables, which are relevant for this
	 * safety property.
	 * 
	 * @return a collection containing the names of all relavent labels.
	 * @throws Exception
	 */
	public Collection<String> relevantLabels() throws Exception;

}
