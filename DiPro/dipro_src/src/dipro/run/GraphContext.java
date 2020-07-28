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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import dipro.alg.BF;
import dipro.alg.BFSearchCreator;
import dipro.alg.BFStarSearchCreator;
import dipro.alg.EppsteinCreator;
import dipro.alg.KSPAlgorithm;
import dipro.alg.KStarCreator;
import dipro.graph.DefaultVertex;
import dipro.graph.ExternDirectedGraph;
import dipro.graph.SetPredicate;
import dipro.util.DefaultSolutionCollector;
import dipro.util.DiProException;
import dipro.util.KDefaultSolutionCollector;
import dipro.util.Proposition;
import dipro.util.SolutionCollector;

public class GraphContext extends AbstractContext {

	protected String dirName = null;

	//Constructor
	protected GraphContext(int id, Config config) throws Exception {
		super(id, config);
	}

	protected void loadModel() throws IOException, DiProException {
		ExternDirectedGraph g = new ExternDirectedGraph(dirName);
		dirName = g.getDirName();
		DefaultVertex v = new DefaultVertex(0);
		this.start = v;
		this.graph = g;
		this.property = loadProposition();
	}

	protected Proposition loadProposition() throws IOException {
		String fn = dirName + "/targets.txt";
		BufferedReader in = new BufferedReader(new FileReader(fn));
		SetPredicate prop = new SetPredicate();
		String line = null;
		line = in.readLine();
		while (line != null) {
			prop.add(Integer.parseInt(line));
			line = in.readLine();
		}
		in.close();
		return prop;
	}

	public BF loadAlgorithm() throws Exception {
		BF alg;
		switch (config.algType) {
		case Config.BF: 
			alg = new BFSearchCreator().createSearch(this,config.getLengthHeuristic());
			break;
		case Config.BF_STAR: 
			alg = new BFStarSearchCreator().createSearch(this);
			break;
		case Config.K_STAR:
			alg = new KStarCreator().createSearch(this);
			break;
		case Config.EPPSTEIN:
			alg = new EppsteinCreator().createSearch(this);
			break;

		default:
			throw new IllegalStateException("Invalid algorithm type : "
					+ config.algType);
		}
		if (config.report)
			attachReporter(alg);
		return alg;
	}


	public SolutionCollector createSolutionCollector(BF alg) throws Exception {
		SolutionCollector solutionCollector;
		switch (config.algType) {
		case Config.BF:
		case Config.BF_STAR:
			solutionCollector = new DefaultSolutionCollector(alg);
			break;
		case Config.K_STAR:
		case Config.EPPSTEIN:
			solutionCollector = new KDefaultSolutionCollector((KSPAlgorithm) alg);
			break;
		default:
			throw new IllegalStateException("Invalid algorithm type : "
					+ config.algType);
		}
		return solutionCollector;
	}

	protected void readParameters() throws DiProException {
		for (String param : config.parameters) {
			if (dirName == null) {
				dirName = config.getDiPro().makeAbsoluteFileName(param);
				break;
			}
		}
	}

	@Override
	public void performModelChecking() throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getModelCheckingResult() throws Exception {
		throw new UnsupportedOperationException();
	}

	public String getSolutionFileName() throws Exception {
		return dirName+"/solution"+tStamp+".txt";
	}

}
