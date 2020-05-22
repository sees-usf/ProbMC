package dipro.stoch.prism;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import parser.PrismParser;
import parser.Values;
import parser.ast.Expression;
import parser.ast.ModulesFile;
import parser.ast.PropertiesFile;
import prism.Prism;
import prism.PrismException;
import simulator.SimulatorEngine;
import dipro.alg.BF;
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
import dipro.stoch.prism.PrismModel;
import dipro.stoch.prism.PrismRawModel;
import dipro.stoch.prism.PrismState;
import dipro.stoch.prism.PrismTransition;

public class CopyOfPrismExplicitModel2 extends OfflineStateSpace implements PrismModel {
	
	protected class BaseEntry {
		PrismState state; 
		HashSet<PrismTransition> outgoingEdges; 
		
		BaseEntry(PrismState state) {
			this.state = state;
			outgoingEdges = new HashSet<PrismTransition>();
		}
	}
	
	protected PrismExplicitContext context;
	protected HashMap<Integer, PrismState> idMap;
	protected Hashtable<PrismState, BaseEntry> base;
	protected int numEdges;
	protected PrismState startState;
	protected Prism prism;
	protected ModulesFile modulesFile;
	protected PropertiesFile propertiesFile;
	protected Properties externalConstantValues;
	protected Values constantValues;
	protected int stateSize; 
	protected int memory;
	private boolean isLoading;
	
	public CopyOfPrismExplicitModel2(PrismExplicitContext context) throws IOException, PrismException {
		this.context = context;
		idMap = new HashMap<Integer, PrismState>();
		base = new Hashtable<PrismState, BaseEntry>();
		numEdges = 0;
		startState = null;
		stateSize = -1;
		memory = -1;
		isLoading = false;
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
		Registry.getMain().out().println("Explicit model is being loaded...");
		isLoading = true;
		long time1 = System.currentTimeMillis();
		prism = new Prism(context.getPrismMainLog(), context.getPrismTechLog());
		modulesFile = prism.parseExplicitModel(new File(context.getStaFileName()), new File(context.getTraFileName()), new File(context.getLabFileName()), context.getModelType());
		HashMap<String, Integer> varTypeMap = new HashMap<String, Integer>();
		Vector<String> varNames = modulesFile.getVarNames();
		for(int i=0; i<varNames.size(); i++) {
			String varName = varNames.elementAt(i);
			Integer varType = modulesFile.getVarTypes().elementAt(i);
			varTypeMap.put(varName, varType);
		}
		Values initValues = modulesFile.getInitialValues();
		startState = new PrismState(initValues);
		time1 = System.currentTimeMillis() - time1;
		Registry.getMain().out().println("Module file parsed; time = "+time1);
		Registry.getMain().out().println("External state und transition files are being loaded...");
		startLoadingWatcher();
		long time2 = System.currentTimeMillis();
		BufferedReader sta = new BufferedReader(new FileReader(context.getStaFileName()));
		String line = sta.readLine();
		line = line.substring(1, line.length()-1);
		String[] names = line.split(",");
		line = sta.readLine();
		while (line != null) {
//			System.out.println(line);
			Values values = new Values();
//			String[] list=line.split(":");
//			int id = Integer.parseInt(list[0]);
//			line=(String) list[1].subSequence(1, list[1].length()-1);
//			String[] tokens = line.split(",");
			String[] tokens = line.split("[:(,)]");
			int id = Integer.parseInt(tokens[0]);
			int firstVarIndex = 2; 
			for(int i = 0; i<names.length;i++){	
//				String varName = names[i];
//				String varValue = tokens[i];
				String varName = names[i];
				String varValue = tokens[firstVarIndex+i];
//				int type = context.getVarType(varName);
				int type = varTypeMap.get(varName);
				Object o; 
				switch(type) {
				case Expression.BOOLEAN:
					o = Boolean.parseBoolean(varValue);
					break;
				case Expression.INT:
					o = Integer.parseInt(varValue);
					break;
				case Expression.DOUBLE: 
					o = Double.parseDouble(varValue);
					break;
				default: 
					throw new IllegalArgumentException();
				}
				values.addValue(varName, o);
			}
			PrismState state = new PrismState(values);
			idMap.put(id, state);
		    BaseEntry entry = new BaseEntry(state);
		    entry.state = state;
		    base.put(state, entry);
			line = sta.readLine();
		}
		sta.close();
		Registry.getMain().out().println("States was imported");
		Registry.getMain().out().println("Transition are being imported...");
		BufferedReader tra = new BufferedReader(new FileReader(context.getTraFileName()));
		line = tra.readLine();
		line = tra.readLine();
		while (line != null) {
			String[] tokens = line.split(" ");
			int id1 = Integer.parseInt(tokens[0]);
			int id2 = Integer.parseInt(tokens[1]);
			float prob = Float.parseFloat(tokens[2]);
			PrismState source = idMap.get(id1);
			PrismState target = idMap.get(id2);
			PrismTransition t = new PrismTransition(source, target);
			t.setProbOrRate(prob);
			BaseEntry entry = base.get(source);
			entry.outgoingEdges.add(t);
			numEdges++;
			line = tra.readLine();
		}
		tra.close();
		isLoading = false;
		time2 = System.currentTimeMillis() - time2;
		Registry.getMain().out().println("Transitions were imported");
		Registry.getMain().out().println("Time to load external files = "+time2);
		
		computeStateSize();
		computeUsedMemory();
		System.out.println("Explicit model imported ("+numVertices()+" states and "+numEdges+" transitions)");
		System.out.println("Total time for importing the model was "+(time1+time2)+" milliseconds.");
		System.out.println("Memory used to store the model is "+memory+" Bytes.");
	}
	

	
	private void computeUsedMemory() {
		assert memory == -1;
		memory = 0;
		memory = memory + numVertices() * vertexSize();
		memory = memory + numEdges() * edgeSize();
	}
	
	public int getUsedMemory() {
		assert memory >= 0;
		return memory;
	}
	
	public int numVertices() {
		return base.size();
	}

	public int numEdges() {
		return numEdges;
	}

	public Iterator<? extends State> vertices() {
		return base.keySet().iterator();
	}

	public Iterator<? extends Transition> edges() {
		throw new UnsupportedOperationException();
	}

	public int degree(Vertex v) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Iterator<? extends StochasticTransition> outgoingEdges(Vertex v) {
		BaseEntry entry = base.get(v);
		if (entry == null)	
			return Collections.EMPTY_LIST.iterator();
		else 
			return entry.outgoingEdges.iterator();
	}	

	public Iterator<? extends StochasticTransition> incomingEdges(Vertex v) {
		LinkedList<PrismTransition> list = new LinkedList<PrismTransition>();
		for (Vertex u : base.keySet()) {
			BaseEntry uEntry = base.get(u);
			assert u!= null;
			Iterator<PrismTransition> iter = uEntry.outgoingEdges.iterator();
			while (iter.hasNext()) {
				PrismTransition e = iter.next();
				if (e.target().equals(v))
					list.add(e);
			}
		}
		return list.iterator();
	}

	@Override
	public Iterator<? extends StochasticTransition> adjacentEdges(final Vertex v) {
		return new Iterator<StochasticTransition>() {
			Iterator<? extends StochasticTransition> iter1 = outgoingEdges(v);
			Iterator<? extends StochasticTransition> iter2 = incomingEdges(v);
			public boolean hasNext() {
				return iter1.hasNext() || iter2.hasNext();
			}
			public StochasticTransition next() {
				if (iter1.hasNext())
					return iter1.next();
				return iter2.next();
			}
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
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
		idMap.clear();
		base.clear();
		startState = null;
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

	private void computeStateSize() {
		assert stateSize == -1;
		int size = 0;
		Vector<String> variableNames = modulesFile.getVarNames();
		Vector<Integer> variableTypes = modulesFile.getVarTypes();
//		Set<String> varSet = context.getVarNames();
//		for(String var: varSet) {
		for(int i=0; i< variableNames.size(); i++) {
			int type = variableTypes.get(i);
//			int type = context.getVarType(var);
			switch (type) {
			case Expression.INT:
				size += 4;
				break;
			case Expression.BOOLEAN:
				size += 4;
				break;
			case Expression.DOUBLE:
				size += 8;
				break;
			default:
				throw new IllegalStateException("Invalid Prism data type: "
						+ type);
			}
		}
		stateSize = size;
	}
	
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
	public ModulesFile modulesFile() {
//		return modulesFile;
		throw new UnsupportedOperationException();
	}

	@Override
	public PropertiesFile propertiesFile() {
		return propertiesFile;
	}

	@Override
	public int type() {
		return context.getModelType();
	}
}
