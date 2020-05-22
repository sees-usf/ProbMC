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

package dipro.stoch.prism;

public class PrismTransitionData {
	String moduleName;
	int moduleIndex;
	String label;
	int actionIndex;
	float probOrRate;
	String assignment;

	PrismTransitionData() {
	}

	PrismTransitionData(String moduleName, int moduleIndex, String label,
			int actionIndex, float probOrRate, String assignment) {
		super();
		this.moduleName = moduleName;
		this.moduleIndex = moduleIndex;
		this.label = label;
		this.actionIndex = actionIndex;
		this.probOrRate = probOrRate;
		this.assignment = assignment;
	}

	
	public int getActionIndex() {
		return actionIndex;
	}

	public String getAssignment() {
		return assignment;
	}

	public String getLabel() {
		return label;
	}

	public int getModuleIndex() {
		return moduleIndex;
	}

	public String getModuleName() {
		return moduleName;
	}

	public float getProbOrRate() {
		return probOrRate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + actionIndex;
		result = prime * result
				+ ((assignment == null) ? 0 : assignment.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + moduleIndex;
		result = prime * result
				+ ((moduleName == null) ? 0 : moduleName.hashCode());
		result = prime * result + Float.floatToIntBits(probOrRate);
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
		PrismTransitionData other = (PrismTransitionData) obj;
		if (actionIndex != other.actionIndex)
			return false;
		if (assignment == null) {
			if (other.assignment != null)
				return false;
		} else if (!assignment.equals(other.assignment))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (moduleIndex != other.moduleIndex)
			return false;
		if (moduleName == null) {
			if (other.moduleName != null)
				return false;
		} else if (!moduleName.equals(other.moduleName))
			return false;
		if (Float.floatToIntBits(probOrRate) != Float
				.floatToIntBits(other.probOrRate))
			return false;
		return true;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(">");
		sb.append(moduleName);
		sb.append(">");
		sb.append(label);
		sb.append(">");
		sb.append(actionIndex);
		sb.append("> ");
		sb.append(probOrRate);
		sb.append(">");
		sb.append(assignment);
		sb.append(">");
		return sb.toString();
	}
}
