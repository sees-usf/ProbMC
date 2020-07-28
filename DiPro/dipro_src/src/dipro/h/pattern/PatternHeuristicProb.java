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
//import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Set;
//import java.util.Vector;
import org.apache.commons.lang.StringUtils;


import dipro.alg.BF;
import dipro.graph.Vertex;
//import dipro.h.Heuristic;
import dipro.h.PrismHeuristic;
import dipro.run.Context;
import dipro.run.Registry;
import dipro.stoch.prism.PrismState;
import dipro.util.DiagnosticPath;

public class PatternHeuristicProb extends PrismHeuristic implements Serializable
{


	HashMap<String,Double> patterns = new HashMap<String, Double>();
	 ArrayList<ArrayList<String>> patterns1;
	 double minP;
	public PatternHeuristicProb() {
		super(null, null);
	}

	public PatternHeuristicProb(Context context, BF alg) {
		super(context, alg);
		Registry.getMain().out().println("Loading Pattern Heuristic ...");
		this.deserializePattern();
	}
	
	public void addAbstractDPs(ArrayList<DiagnosticPath> absDPs)
	{
		this.minP = 1.0;
		for(DiagnosticPath dp: absDPs)
		{
			ArrayList<String> states =  dp.getStates();
			
			//Get Updates
			for(int i = 0; i < states.size()-1; i++)
			{
				String state1 = states.get(i).replace("[", "");
				state1 = state1.replace("]", "");
				String[] vars1 = state1.split(",");
				
				String state2 = states.get(i+1).replace("[", "");
				state2 = state2.replace("]", "");
				String[] vars2 = state2.split(",");
				
				for(int u = 0; u < vars1.length; u++)
				{
					//Is var1 updated to var2?
					if(!vars1[u].equals(vars2[u]))
					{
						if(patterns.get(vars1[u]+":"+vars2[u]) != null)
						{
							//if(patterns.get(varsL) < dp.getProbability())
							//{
								double p = patterns.get(vars1[u]+":"+vars2[u]) + dp.getProbability();
								patterns.put(vars1[u]+":"+vars2[u], p);
							//}
						}
						else
						{
							patterns.put(vars1[u]+":"+vars2[u], dp.getProbability());
						}
					}
				}
				
				if(dp.getProbability() > 0 && dp.getProbability() < this.minP)
				{
					this.minP = dp.getProbability();
				}
				
			}
			
			/*for(String state :states)
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
					//if(patterns.get(varsL) < dp.getProbability())
					//{
						double p = patterns.get(varsL) + dp.getProbability();
						patterns.put(varsL, p);
					//}
				}
				else
				{
					patterns.put(varsL, dp.getProbability());
				}
			}*/
			
		}
	}
	
	public void serializePattern()
	{
		XMLEncoder enc;
		try {
			enc = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("pattern.xml")));
			
			/*
			  this.patterns1 = new ArrayList<ArrayList<String>>();
			 
			
			for ( String elem : this.patterns.keySet() )
			{
				ArrayList<String> cur = new ArrayList<String>();
				cur.add(elem);
				cur.add(this.patterns.get(elem).toString());
				this.patterns1.add(cur);
			}
			
			*/
			//Set<ArrayList<String>> keys = patterns.keySet();
			enc.writeObject(patterns);
			enc.writeObject(this.minP);
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
	      this.patterns = (HashMap<String,Double>)dec.readObject(); 
	      this.minP = Double.parseDouble(dec.readObject().toString());
	      dec.close();
	    } 
	    catch ( IOException e ) { 
	      e.printStackTrace(); 
	    } 
		
	}
	
	private double computeHfromUpdates(String str1, String[] vars1, String str2, String[] vars2, int pos)
	{
		double h = 0.0;
		int diff = StringUtils.indexOfDifference(str1.substring(pos), str2.substring(pos));
		
		if(diff > 0)
		{
			int u = StringUtils.countMatches(str1.substring(pos, pos+diff), ",");
			//Registry.getMain().out().println(vars1[u]+":"+vars2[u]);
			if( patterns.get(vars1[u]+":"+vars2[u]) != null)
			{
				//Registry.getMain().out().println(vars1[u]+":"+vars2[u]+" h+= "+patterns.get(vars1[u]+":"+vars2[u]));
				//alg.log(vars1[u]+":"+vars2[u]+" h+= "+patterns.get(vars1[u]+":"+vars2[u]));
				 h +=  patterns.get(vars1[u]+":"+vars2[u]);
			}
			if(StringUtils.countMatches(str1.substring(pos + diff), ",") > 1)
			{
				h += computeHfromUpdates(str1, vars1, str2, vars2, str1.indexOf(",", pos + diff));
			}
		}
		
		return h;
	}
	
	private double getHeuristicEstimate(Vertex v, Vertex v2)
	{
		double h = 0.0d;
		String state1 = ((PrismState) v).values().toString();
		String[] vars1 = state1.split(",");
		String state2= ((PrismState) v2).values().toString();
		String[] vars2 = state2.split(",");
		
		h = computeHfromUpdates(state1, vars1, state2, vars2, 0);
		
		/*
		for(int u = 0; u < vars1.length; u++)
		{
			
			//Is var1 updated to var2?
			if(!vars1[u].equals(vars2[u]))
			{				 
				 if( patterns.get(vars1[u]+":"+vars2[u]) != null)
				 {
					 h +=  patterns.get(vars1[u]+":"+vars2[u]);
				 }
			}
		}*/
		
		/*
		for ( ArrayList<ArrayList<String>> elem : this.patterns1 )
		{
			//if(varsL.containsAll(elem.get(0)) && Double.parseDouble(((ArrayList<String>)elem.get(1)).get(0)) > h)
			if(varsL.containsAll(elem.get(0)))
			{
				h += Double.parseDouble(((ArrayList<String>)elem.get(1)).get(0));
			//	Registry.getMain().out().println("Loookup " +state +" Found:  " +elem.get(0) + " "+h);
			}
				
		}*/
		if(h > 1.0)
		{
			h = 1.0;
		}
		if(h <= 0.0)
		{ // no h found, since update does not exist in abstract model => h is prob of least probable cx path
			h = this.minP*0.99;
		}
		//h = 1.0;
		return h;
	}
	
	@Override
	public double evaluate(Vertex v, Vertex v2) throws Exception 
	{
		double h = 0.0d;
		h = getHeuristicEstimate(v, v2);

		return h;
	}

}
