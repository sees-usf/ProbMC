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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import dipro.alg.BF;
import dipro.run.Registry;

public class DiagnosticPath {

	private double probability;
	private double probabilitySum;
	private ArrayList<String> trace;
	private ArrayList<String> states;
	private ArrayList<String> transitionLabels;
	private boolean isinFT;
	private boolean isXoutOfN;
	private boolean isPriorityAnd;
	private int id;

	//private String ignore;

	public DiagnosticPath() {

	}

	public DiagnosticPath(int i) {
		this.id = i;
	}

	public void buildTraceFromString(String strTrace,
			ArrayList<String> ignoreList) {
		trace = new ArrayList<String>();
		states = new ArrayList<String>();
		transitionLabels = new ArrayList<String>();
		setPriorityAnd(false);

		StringTokenizer st;
		st = new StringTokenizer(strTrace, "\n");

		while (st.hasMoreTokens()) {
			String curr = st.nextToken();
			// System.out.println(curr);
			switch (curr.charAt(0)) {
			case '[':
				trace.add(curr.substring(1, curr.lastIndexOf(']')));
				states.add(curr.substring(1, curr.lastIndexOf(']')));
				break;

			case '{':
				trace.add(curr.substring(1, curr.lastIndexOf('}')));
				if ((curr.indexOf('[') + 1 != curr.lastIndexOf(']'))
						&& !ignoreList.contains(curr.substring(
								curr.indexOf('[') + 1, curr.lastIndexOf(']')))) {
					transitionLabels.add(curr.substring(curr.indexOf('[') + 1,
							curr.lastIndexOf(']')));
				} else {
					// transitionLabels.add("");
				}
				break;
			}
		}
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public double getProbability() {
		return probability;
	}

	public ArrayList<String> getTransitionLabels() {
		return this.transitionLabels;
	}

	public ArrayList<String> getStates() {
		return this.states;
	}

	public void setTransitionLabels(ArrayList<String> labels) {
		this.transitionLabels = labels;
	}

	public void setStates(ArrayList<String> states) {
		this.states = states;
	}

	public int getLength() {
		return this.transitionLabels.size();
	}

	public void setIsinFT(boolean isinFT) {
		this.isinFT = isinFT;
	}

	public boolean isIsinFT() {
		return isinFT;
	}

	public void setPriorityAnd(boolean isPriorityAnd) {
		this.isPriorityAnd = isPriorityAnd;
	}

	public boolean isPriorityAnd() {
		return isPriorityAnd;
	}

	public void setProbabilitySum(double probabilitySum) {
		this.probabilitySum = probabilitySum;
	}

	public double getProbabilitySum() {
		return probabilitySum;
	}

	public int getID() {
		return this.id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public String toString() {

		return "Diagnostic Path[" + getID() + "](p=" + getProbabilitySum()
				+ ")=" + trace.toString();

	}

	public void setXoutOfN(boolean isXoutOfN) {
		this.isXoutOfN = isXoutOfN;
	}

	public boolean isXoutOfN() {
		return isXoutOfN;
	}

	public static void readCXXML(String filename, ArrayList<String> ignoreList,
			BF alg) {

		/*
		 * Create the CX Converter object
		 */

		double probability = 0.0;
		int pathCount = 0;
		InputStream in;
		File file = null;
		try {

			URL url = ClassLoader.getSystemResource(filename);
			if (url == null) {
				Registry.getMain().handleWarning(
						"File not available: \"" + filename + "\"");
				return;
			}
			file = new File(url.toURI());
			in = new FileInputStream(file);

			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLEventReader parser = factory.createXMLEventReader(in);

			DiagnosticPath path = null;
			String lastElem = "";
			String trace = "";

			// Parse XML File
			while (parser.hasNext()) {
				XMLEvent event = parser.nextEvent();
				switch (event.getEventType()) {
				case XMLStreamConstants.START_DOCUMENT:
					break;
				case XMLStreamConstants.END_DOCUMENT:
					parser.close();
					break;
				case XMLStreamConstants.START_ELEMENT:
					StartElement element = event.asStartElement();
					if (element.getName().toString().equals("DiagnosticPath")) {
						path = new DiagnosticPath(pathCount);
						pathCount++;
						lastElem = "";
						trace = "";
					}
					if (element.getName().toString().equals("Probability")) {
						lastElem = "Probability";
					}
					if (element.getName().toString().equals("Trace")) {
						lastElem = "Trace";
					}
					for (Iterator<?> attributes = element.getAttributes(); attributes
							.hasNext();) {
						attributes.next();
					}
					break;
				case XMLStreamConstants.CHARACTERS:
					Characters characters = event.asCharacters();
					if (!characters.isWhiteSpace())
						if (lastElem.equals("Probability")) {
							lastElem = "";
							path.setProbability(Double.parseDouble(characters
									.getData()));
							probability += Double.parseDouble(characters
									.getData());
						}
					if (lastElem.equals("Trace")) {
						trace += characters.getData();

					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					if (event.asEndElement().getName().toString()
							.equals("DiagnosticPath")) {

						path.buildTraceFromString(trace, ignoreList);
						alg.getDiagnosticPath().add(path);
						path = null;
					}
					break;
				case XMLStreamConstants.ATTRIBUTE:
					break;
				default:
					break;
				}
			}
		} catch (FileNotFoundException e) {
			Registry.getMain().handleError(
					" Counterexample file note found! " + filename);
		} catch (XMLStreamException e) {
			Registry.getMain().handleError(
					"The counterexample file is not a valid XML file!", e);
		} catch (URISyntaxException e) {
			Registry.getMain().handleError("The file url is not found!", e);
		}

	}

}
