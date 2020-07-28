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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import parser.Values;
import parser.ast.Expression;
import parser.ast.ModulesFile;
import parser.ast.PropertiesFile;
import prism.Prism;
import prism.PrismException;
import simulator.SimulatorEngine;
import dipro.graph.Edge;
import dipro.graph.OfflineStateSpace;
import dipro.graph.State;
import dipro.graph.Transition;
import dipro.graph.Vertex;
import dipro.run.PrismExplicitContext;
import dipro.run.Registry;
import dipro.stoch.CTMC;
import dipro.stoch.DTMC;
import dipro.stoch.MDP;
import dipro.stoch.MarkovModel;
import dipro.stoch.StochasticTransition;

public class PrismExplicitModel extends OfflineStateSpace implements PrismModel {
		
	protected PrismExplicitContext context;
//	protected HashMap<Integer, PrismState> idMap;
	protected int numEdges;
	protected PrismState startState;
	protected Prism prism;
	protected ModulesFile modulesFile;
	protected PropertiesFile propertiesFile;
	protected Properties externalConstantValues;
	protected Values constantValues;
	protected int stateSize; 
	protected int memory;
	protected Connection conn;
	
	public PrismExplicitModel(PrismExplicitContext context) throws IOException, PrismException {
		this.context = context;
//		idMap = new HashMap<Integer, PrismState>();
//		base = new Hashtable<PrismState, BaseEntry>();
		numEdges = 0;
		startState = null;
		stateSize = -1;
//		memory = -1;
//		isLoading = false;
		load();
		propertiesFile = prism.parsePropertiesFile(modulesFile, new File(
				context.getPropFileName()));
		// load external constant values
		externalConstantValues = new Properties();
		if (context.getConstFileName() != null) {
			InputStream in = new FileInputStream(context.getConstFileName());
			externalConstantValues.load(in);
			in.close();
		}
		constantValues = PrismRawModel.defineConstants(modulesFile, propertiesFile, externalConstantValues);
	}
	
	protected void load() throws IOException, PrismException {
		prism = new Prism(context.getPrismMainLog(), context.getPrismTechLog());
		modulesFile = prism.parseExplicitModel(new File(context.getStaFileName()), new File(context.getTraFileName()), new File(context.getLabFileName()), context.getModelType());
		HashMap<String, Integer> varTypeMap = new HashMap<String, Integer>();
		Vector<String> varNames = modulesFile.getVarNames();
		stateSize = 0;
		for(int i=0; i<varNames.size(); i++) {
			String varName = varNames.elementAt(i);
			Integer varType = modulesFile.getVarTypes().elementAt(i);
			switch (varType) {
			case Expression.INT:
				stateSize += 4;
				break;
			case Expression.BOOLEAN:
				stateSize += 4;
				break;
			case Expression.DOUBLE:
				stateSize += 8;
				break;
			default:
				throw new IllegalStateException("Invalid Prism data type: "
						+ varType);
			}
			varTypeMap.put(varName, varType);
		}
		System.out.println("State Size = "+stateSize);
		Values initValues = modulesFile.getInitialValues();
		PrismState s1 = new ExplicitPrismState(initValues,0);
		String url = "jdbc:mysql://localhost/"+context.getDatabaseName();
		String user = "root";
		String password = "dipro";
		String driver = "com.mysql.jdbc.Driver";
		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url, user, password);
			startState = getState(0);
			assert startState.equals(s1);
		} catch (Exception e) {
			if(conn!=null) {
				try {
					conn.close();
				} catch (SQLException e1) {
					Registry.getMain().handleError("Failure while emergency closing the database connection!", e1);
				}
			}
			Registry.getMain().handleFatalError("Failed to load the database driver!", e);
		} 
	}
	
	private String getQueryGetOutgoingTransitions(ExplicitPrismState state) {
		int id = state.getId();
		StringBuilder query = new StringBuilder("SELECT * FROM TRANSITIONS where SOURCE=");
		query.append(id);
		query.append(";");
		return query.toString();
	}
	
	private String getQueryGetState(int id) {
		StringBuilder query = new StringBuilder("SELECT * FROM STATES where ID=");
		query.append(id);
		query.append(";");
		return query.toString();
	}
	
	private ExplicitPrismState getState(int id) {
		String query = getQueryGetState(id);
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData ms = null;
		ExplicitPrismState state = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			ms = rs.getMetaData();
			int cn = ms.getColumnCount();
			assert cn == modulesFile.getVarNames().size() + 1;
			if(rs.next()) {
				int x = rs.getInt(1);
				assert id == x;
				Values values = new Values();
				for(int i=2; i<= cn; i++) {
					String varName = ms.getColumnName(i);
					assert modulesFile.getVarNames().contains(varName); 
					Object varValue = rs.getObject(i);
					values.addValue(varName, varValue);
				}
				state = new ExplicitPrismState(values, id);
			}
			
		} catch (SQLException e) {
			Registry.getMain().handleError("Failure while accessing the database", e);
		} finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					Registry.getMain().handleError("Failure while emergency closing the SQL result set", e);
				}
			}
			if(stmt!=null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					Registry.getMain().handleError("Failure while emergency closing the SQL statement", e);
				}
			}
		}
		return state;
	}

//	protected void load() throws IOException, PrismException {
//		Registry.getMain().out().println("Explicit model is being loaded...");
////		isLoading = true;
//		long time1 = System.currentTimeMillis();
//		prism = new Prism(context.getPrismMainLog(), context.getPrismTechLog());
//		modulesFile = prism.parseExplicitModel(new File(context.getStaFileName()), new File(context.getTraFileName()), new File(context.getLabFileName()), context.getModelType());
//		HashMap<String, Integer> varTypeMap = new HashMap<String, Integer>();
//		Vector<String> varNames = modulesFile.getVarNames();
//		for(int i=0; i<varNames.size(); i++) {
//			String varName = varNames.elementAt(i);
//			Integer varType = modulesFile.getVarTypes().elementAt(i);
//			varTypeMap.put(varName, varType);
//		}
//		Values initValues = modulesFile.getInitialValues();
//		startState = new PrismState(initValues);
//		time1 = System.currentTimeMillis() - time1;
//		Registry.getMain().out().println("Module file parsed; time = "+time1);
//		Registry.getMain().out().println("External state und transition files are being loaded...");
//		startLoadingWatcher();
//		long time2 = System.currentTimeMillis();
//		BufferedReader sta = new BufferedReader(new FileReader(context.getStaFileName()));
//		String line = sta.readLine();
//		line = line.substring(1, line.length()-1);
//		String[] names = line.split(",");
//		line = sta.readLine();
//		while (line != null) {
////			System.out.println(line);
//			Values values = new Values();
////			String[] list=line.split(":");
////			int id = Integer.parseInt(list[0]);
////			line=(String) list[1].subSequence(1, list[1].length()-1);
////			String[] tokens = line.split(",");
//			String[] tokens = line.split("[:(,)]");
//			int id = Integer.parseInt(tokens[0]);
//			int firstVarIndex = 2; 
//			for(int i = 0; i<names.length;i++){	
////				String varName = names[i];
////				String varValue = tokens[i];
//				String varName = names[i];
//				String varValue = tokens[firstVarIndex+i];
////				int type = context.getVarType(varName);
//				int type = varTypeMap.get(varName);
//				Object o; 
//				switch(type) {
//				case Expression.BOOLEAN:
//					o = Boolean.parseBoolean(varValue);
//					break;
//				case Expression.INT:
//					o = Integer.parseInt(varValue);
//					break;
//				case Expression.DOUBLE: 
//					o = Double.parseDouble(varValue);
//					break;
//				default: 
//					throw new IllegalArgumentException();
//				}
//				values.addValue(varName, o);
//			}
//			PrismState state = new PrismState(values);
//			idMap.put(id, state);
//		    BaseEntry entry = new BaseEntry(state);
//		    entry.state = state;
//		    base.put(state, entry);
//			line = sta.readLine();
//		}
//		sta.close();
//		Registry.getMain().out().println("States was imported");
//		Registry.getMain().out().println("Transition are being imported...");
//		BufferedReader tra = new BufferedReader(new FileReader(context.getTraFileName()));
//		line = tra.readLine();
//		line = tra.readLine();
//		while (line != null) {
//			String[] tokens = line.split(" ");
//			int id1 = Integer.parseInt(tokens[0]);
//			int id2 = Integer.parseInt(tokens[1]);
//			float prob = Float.parseFloat(tokens[2]);
//			PrismState source = idMap.get(id1);
//			PrismState target = idMap.get(id2);
//			PrismTransition t = new PrismTransition(source, target);
//			t.setProbOrRate(prob);
//			BaseEntry entry = base.get(source);
//			entry.outgoingEdges.add(t);
//			numEdges++;
//			line = tra.readLine();
//		}
//		tra.close();
//		isLoading = false;
//		time2 = System.currentTimeMillis() - time2;
//		Registry.getMain().out().println("Transitions were imported");
//		Registry.getMain().out().println("Time to load external files = "+time2);
//		
//		computeStateSize();
//		computeUsedMemory();
//		System.out.println("Explicit model imported ("+numVertices()+" states and "+numEdges+" transitions)");
//		System.out.println("Total time for importing the model was "+(time1+time2)+" milliseconds.");
//		System.out.println("Memory used to store the model is "+memory+" Bytes.");
//	}
	

	
//	private void computeUsedMemory() {
//		assert memory == -1;
//		memory = 0;
//		memory = memory + numVertices() * vertexSize();
//		memory = memory + numEdges() * edgeSize();
//	}
//	
//	public int getUsedMemory() {
//		assert memory >= 0;
//		return memory;
//	}
	
//	public int numVertices() {
//		return base.size();
//	}
//
//	public int numEdges() {
////		return numEdges;
//	}

	public Iterator<? extends State> vertices() {
		throw new UnsupportedOperationException();
	}

	public Iterator<? extends Transition> edges() {
		throw new UnsupportedOperationException();
	}

	public int degree(Vertex v) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Iterator<? extends StochasticTransition> outgoingEdges(Vertex v) {
		ExplicitPrismState state = (ExplicitPrismState) v;
		LinkedList<PrismTransition> trans = new LinkedList<PrismTransition>();
		String query = getQueryGetOutgoingTransitions(state);
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				int s1 = rs.getInt(1);
				int s2 = rs.getInt(2);
				float w = rs.getFloat(3);
				assert s1 == state.getId();
				ExplicitPrismState source = getState(s1);
				ExplicitPrismState target = getState(s2);
				PrismTransition t = new PrismTransition(source, target);
				t.setProbOrRate(w);
				trans.add(t);
			}
			rs.close();
		} catch (SQLException e) {
			Registry.getMain().handleError("Failure while accessing the database", e);
		} finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					Registry.getMain().handleError("Failure while emergency closing the SQL result set", e);
				}
			}
			if(stmt!=null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					Registry.getMain().handleError("Failure while emergency closing the SQL statement", e);
				}
			}
		}
		return trans.iterator();
	}	

	public Iterator<? extends StochasticTransition> incomingEdges(Vertex v) {
//		LinkedList<PrismTransition> list = new LinkedList<PrismTransition>();
//		for (Vertex u : base.keySet()) {
//			BaseEntry uEntry = base.get(u);
//			assert u!= null;
//			Iterator<PrismTransition> iter = uEntry.outgoingEdges.iterator();
//			while (iter.hasNext()) {
//				PrismTransition e = iter.next();
//				if (e.target().equals(v))
//					list.add(e);
//			}
//		}
//		return list.iterator();
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<? extends StochasticTransition> adjacentEdges(final Vertex v) {
//		return new Iterator<StochasticTransition>() {
//			Iterator<? extends StochasticTransition> iter1 = outgoingEdges(v);
//			Iterator<? extends StochasticTransition> iter2 = incomingEdges(v);
//			public boolean hasNext() {
//				return iter1.hasNext() || iter2.hasNext();
//			}
//			public StochasticTransition next() {
//				if (iter1.hasNext())
//					return iter1.next();
//				return iter2.next();
//			}
//			public void remove() {
//				throw new UnsupportedOperationException();
//			}
//		};
		throw new UnsupportedOperationException();
	}

	@Override
	public Transition createTransition(State s1, State s2) {
		return new PrismTransition((PrismState)s1,(PrismState)s2);
	}

	@Override
	public State getInitialState() {
		return startState;
	}

	@Override
	public boolean isInitialState(State s) {
		return s.equals(startState);
	}

	@Override
	public void clear() throws Exception {
//		idMap.clear();
//		base.clear();
		conn.close();
		modulesFile = null;
		propertiesFile = null;
		startState = null;
		startState = null;
		prism.closeDown();
		numEdges = 0;
		stateSize = -1;
		memory = -1;
	}

	@Override
	public int edgeSize()  {
		// 2 References (integer: 4) + one real (float: 4)
		return 2 * 4 + 4;
	}

	@Override
	public Class getVertexLabelType(String label) throws Exception {
		int type =startState.values.getType(startState.values.getIndexOf(label));
		switch (type) {
		case SimulatorEngine.BOOLEAN:
			return Boolean.class;
		case SimulatorEngine.INTEGER:
			return Integer.class;
		case SimulatorEngine.DOUBLE:
			return Double.class;
		}
		throw new IllegalArgumentException(label);
	}

	@Override
	public List<String> getVertexLabels() throws Exception {
		return modulesFile.getVarNames();
//		Set<String> set = context.getVarNames();
//		ArrayList<String> list = new ArrayList<String>(set.size());
//		list.addAll(set);
//		return list;
	}

	@Override
	public int vertexSize() {
		return stateSize;
	}

//	private void computeStateSize() {
//		assert stateSize == -1;
//		int size = 0;
//		Vector<String> variableNames = modulesFile.getVarNames();
//		Vector<Integer> variableTypes = modulesFile.getVarTypes();
////		Set<String> varSet = context.getVarNames();
////		for(String var: varSet) {
//		for(int i=0; i< variableNames.size(); i++) {
//			int type = variableTypes.get(i);
////			int type = context.getVarType(var);
//			switch (type) {
//			case Expression.INT:
//				size += 4;
//				break;
//			case Expression.BOOLEAN:
//				size += 4;
//				break;
//			case Expression.DOUBLE:
//				size += 8;
//				break;
//			default:
//				throw new IllegalStateException("Invalid Prism data type: "
//						+ type);
//			}
//		}
//		stateSize = size;
//	}
	
	@Override
	public float weight(Edge e) throws Exception {
		return ((PrismTransition) e).getProbOrRate();
	}

	@Override
	public Values constantValues() {
		return constantValues;
	}

	@Override
	public MarkovModel createMarkovModel() {
		switch (type()) {
		case ModulesFile.PROBABILISTIC:
			return new DTMC(this);
		case ModulesFile.STOCHASTIC:
			return new CTMC(this);
		case ModulesFile.NONDETERMINISTIC:
			return new MDP(this);
		default:
			throw new IllegalStateException("Unsupported model type: "
					+ type());
		}
	}

	@Override
	public Properties externalConstantValues() {
		return externalConstantValues;
	}

	@Override
	public Prism getPrism() {
		return prism;
	}
	@Override
	public PropertiesFile propertiesFile() {
		System.out.println("Properteies File");
		return propertiesFile;
	}

	@Override
	public int type() {
		return context.getModelType();
	}
	
	public String toString() {
//		return "Explicit Prism Model - "+context.getStaFileName();
		StringBuilder sb = new StringBuilder();
		sb.append("Explicit Model - db: "+context.getDatabaseName());
		Enumeration<?> constants = externalConstantValues.propertyNames();
		if(constants.hasMoreElements()) sb.append("\nConstants:");
		while(constants.hasMoreElements()) {
			String constant = (String)constants.nextElement();
			String value = externalConstantValues.getProperty(constant);
			sb.append("\n");
			sb.append(constant);
			sb.append(" = ");
			sb.append(value);
		}
		return sb.toString();
	}
	
	@Override
	public ModulesFile modulesFile() {
//		return modulesFile;
		throw new UnsupportedOperationException();
	}

	@Override
	public int numEdges() throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public int numVertices() throws Exception {
		throw new UnsupportedOperationException();
	}
}
