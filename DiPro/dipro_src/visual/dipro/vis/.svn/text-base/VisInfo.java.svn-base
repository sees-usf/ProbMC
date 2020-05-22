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

import dipro.alg.BF.SearchMark;

public class VisInfo {

	protected SearchMark newMark;
	protected SearchMark oldMark;

	VisInfo(SearchMark mark) {
		this(mark, null);
	}

	VisInfo(SearchMark newMark, SearchMark oldMark) {
		super();
		this.newMark = newMark;
		this.oldMark = oldMark;
	}

	public SearchMark getNewMark() {
		if (oldMark == null)
			throw new IllegalStateException();
		return newMark;
	}

	public SearchMark getOldMark() {
		if (oldMark == null)
			throw new IllegalStateException();
		return oldMark;
	}

	public SearchMark getMark() {
		if (oldMark != null)
			throw new IllegalStateException();
		return newMark;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((newMark == null) ? 0 : newMark.hashCode());
		result = prime * result + ((oldMark == null) ? 0 : oldMark.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final VisInfo other = (VisInfo) obj;
		if (newMark == null) {
			if (other.newMark != null)
				return false;
		} else if (!newMark.equals(other.newMark))
			return false;
		if (oldMark == null) {
			if (other.oldMark != null)
				return false;
		} else if (!oldMark.equals(other.oldMark))
			return false;
		return true;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (oldMark == null)
			sb.append("Search mark:");
		else
			sb.append("New mark: ");
		sb.append(newMark);
		if (oldMark != null) {
			sb.append("Old mark:");
			sb.append(oldMark);
		}
		return sb.toString();
	}
}
