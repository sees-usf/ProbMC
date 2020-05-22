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

import java.util.HashMap;
import java.util.Iterator;

/**
 * Base class for all statespace objects, allowing them to set attributes
 * dynamically and to notify registered Observers of changes.
 * 
 * This object is following the decorable and observer design patterns.
 * 
 */
public class Decoration {

	protected HashMap<Attribute, Object> attributes;

	public Decoration() {
		attributes = new HashMap<Attribute, Object>();
	}

	/**
	 * Add or update an attribute <code>attr</code> whose value will be set to
	 * <code>value</code>. All registered observers are notified of that
	 * change.
	 * 
	 */
	public void set(Attribute attr, Object value) {
		attributes.put(attr, value);
	}

	/**
	 * Get the value of attribute <code>attr</code>
	 */
	public Object get(Attribute attr) {
		return attributes.get(attr);
	}

	/**
	 * Remove the attribute <code>attr</code>. All registered observers will
	 * be notified of this change.
	 * 
	 * @return
	 * @see Map.remove();
	 */
	public Object destroy(Attribute attr) {
		return attributes.remove(attr);
	}

	public void destroyAttributes() {
		attributes.clear();
	}

	/**
	 * Check if this object has the attribute
	 * 
	 * @return true if this object has the attribute <code>attr</code>
	 */
	public boolean has(Attribute attr) {
		return attributes.containsKey(attr);
	}

	/**
	 * Get all attributes
	 * 
	 * @return Iterator over the attributes
	 */
	public Iterator<Attribute> attributes() {
		return attributes.keySet().iterator();
	}

	public void takeOnAttributes(Decoration deco) {
		Iterator<Attribute> iter = deco.attributes();
		while (iter.hasNext()) {
			Attribute attr = iter.next();
			if (has(attr))
				resolveAttributeConflict(attr, deco);
			else
				set(attr, deco.get(attr));
		}
	}

	public void overwriteAttributes(Decoration deco) {
		destroyAttributes();
		takeOnAttributes(deco);
	}

	protected void resolveAttributeConflict(Attribute attr, Decoration deco) {
	}
}
