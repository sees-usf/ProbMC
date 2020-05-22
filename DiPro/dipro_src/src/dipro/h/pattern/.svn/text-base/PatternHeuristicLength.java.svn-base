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

package dipro.h.pattern;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import dipro.alg.BF;
import dipro.graph.Vertex;
import dipro.h.Heuristic;
import dipro.h.PrismHeuristic;
import dipro.run.Context;
import dipro.run.Registry;
import dipro.stoch.prism.PrismState;
import dipro.util.DiagnosticPath;

public class PatternHeuristicLength extends PrismHeuristic implements Serializable
{

	//ArrayList<DiagnosticPath> abstractDPs = new ArrayList<DiagnosticPath>();
	HashMap<ArrayList<String>,Double> patterns = new HashMap<ArrayList<String>, Double>();
	 ArrayList<ArrayList<ArrayList<String>>> patterns1;
	public PatternHeuristicLength() {
		super(null, null);
	}

	public PatternHeuristicLength(Context context, BF alg) {
		super(context, alg);
		Registry.getMain().out().println("Loading Pattern Heuristic ...");
		this.deserializePattern();
	}
	
	public void addAbstractDPs(ArrayList<DiagnosticPath> absDPs)
	{
		for(DiagnosticPath dp: absDPs)
		{
			ArrayList<String> states =  dp.getStates();
			for(String state :states)
			{
				state = state.replace("[", "");
				state = state.replace("]", "");
				String[] vars = state.split(",");
				ArrayList<String> varsL = new ArrayList<String>();
				for(String var : vars)
				{
					varsL.add(var);
				}
				
				if(patterns.get(varsL) != null)
				{
					if(patterns.get(varsL) < (states.size() - states.indexOf(state)))
					{
						patterns.put(varsL, (double) (states.size() - states.indexOf(state)));
					}
				}
				else
				{
					patterns.put(varsL, (double) (states.size() - states.indexOf(state)));
				}
			}
			
		}
	}
	
	public void serializePattern()
	{
		XMLEncoder enc;
		try {
			enc = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("pattern.xml")));
			this.patterns1 = new ArrayList<ArrayList<ArrayList<String>>>();
			
			for ( ArrayList<String> elem : this.patterns.keySet() )
			{
				ArrayList<ArrayList<String>> cur = new ArrayList<ArrayList<String>>();
				cur.add(elem);
				ArrayList<String> h = new ArrayList<String>();
				h.add(this.patterns.get(elem).toString());
				cur.add(h);
				this.patterns1.add(cur);
			}
			
			
			//Set<ArrayList<String>> keys = patterns.keySet();
			enc.writeObject(patterns1);
			enc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void deserializePattern()
	{
		XMLDecoder dec = null; 
		 
	    try 
	    { 
	      dec = new XMLDecoder( new FileInputStream("pattern.xml") ); 
	      this.patterns1 = (ArrayList<ArrayList<ArrayList<String>>>)dec.readObject(); 
	      dec.close();
	    } 
	    catch ( IOException e ) { 
	      e.printStackTrace(); 
	    } 
		
	}
	
	private double getHeuristicEstimate(Vertex v)
	{
		double h = 0.0d;
		String state = ((PrismState) v).values().toString();
		String[] vars = state.split(",");
		ArrayList<String> varsL = new ArrayList<String>();
		for(String var : vars)
		{
			varsL.add(var);
		}
		
		for ( ArrayList<ArrayList<String>> elem : this.patterns1 )
		{
			if(varsL.containsAll(elem.get(0)) && Double.parseDouble(((ArrayList<String>)elem.get(1)).get(0)) > h)
			{
				h = Double.parseDouble(((ArrayList<String>)elem.get(1)).get(0));
				Registry.getMain().out().println("Loookup " +state +" Found:  " +elem.get(0) + " "+h);
			}
				
		}
		return h;
	}
	
	@Override
	public double evaluate(Vertex v) throws Exception 
	{
		double h = 0.0d;
		h = getHeuristicEstimate(v);

		return h;
	}

}
