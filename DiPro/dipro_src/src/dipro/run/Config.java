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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.MathContext;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import parser.Values;
import parser.ast.ModulesFile;
import parser.ast.PropertiesFile;
import prism.PrismLog;
import dipro.util.DiProException;

public class Config {

	public static final String programName = "DiPro - Directed Probabilistic Counterexample Generation - University of Konstanz";
	public static final String programShortName = "DiPro";
	public static final String version = "1.0";
	public static final String authors = "Husain Aljazzar, Florian Leitner-Fischer and Dimitar Simeonov";
	// public static String DIPRO_DIR;
	// public static String DIPRO_DIR = "/home/aljazzar/workspace/DiPro";
	// public static String DIPRO_DIR =
	// "C:/Dokumente und Einstellungen/Aljazzar/workspace/DiPro";
	// public static String CONFIG_FILE_NAME = DIPRO_DIR+"/etc/dipro.cfg";
	// public static String CONFIG_FILE_NAME;

	public static final String DEFAULT_REPORT_FILE_NAME = "dipro_report.txt";
	public static final String DEFAULT_SOLUTION_FILE_NAME = "dipro_sol.txt";
	public static final String DEFAULT_SEARCH_LOG_NAME = "dipro_search.log";
	
	public static final int BF = 1;
	public static final int BF_STAR = 2;
	public static final int XBF = 11;
	public static final int K_STAR = 21;
	public static final int EPPSTEIN = 25;

	public final static int DIRECTED_GRAPH = 0;
	public final static int PRISM_MODEL = 1;
	public final static int MRMC_MODEL = 2;
	// public final static int PRISM_MRMC_MODEL = 3;
	public final static int PRISM_EXPLICIT_MODEL = 4;

	public static final int ALG_LOG_DISABLED = 0;
	public static final int ALG_LOG_BASIC = 1;
	public static final int ALG_LOG_NORMAL = 2;
	public static final int ALG_LOG_DETAILED = 3;
	public static final int ALG_LOG_VERBOSE = 4;
	public static final int ALG_LOG_DEBUG = 5;

	private DiPro dipro;

	/* Algorithm settings */
	public int algType;
	public boolean greedy;
	public boolean usePi;
	public String algLogName;
	public String cxFileName;
	
	public int k;
	public int maxIter;
	public float maxTime;
	public double pruneBound;
	public int logLevel;
	public boolean report;
	public boolean isInStepByStepModus;
	public boolean complete;
	public String heuristicName;
	public boolean lengthHeuristic;
	public boolean isProbPatternH;
	public boolean mc;
	public boolean kxsol;
	public int mcsol;
	public boolean mrmcsol;
	public String reportName;
	public double cxIncrementRatio;

	public String modelName;
	
	public String propName;




	/* Model settings */
	public double uniformRate;
	public int modelType;
	public ModulesFile modelFile;
	public PropertiesFile propFile;
	public int propId;
	private Values constantValues;
	public int solutionTrace;

	public boolean onlineVisualization;
	public String colorScaleFileName;
	public MathContext mathContext;

	public List<String> parameters;
//	public String modelFileName;
//	public String propFileName;
//	public int propIndex;
	public boolean isVisualizationEnabled;
	private PrismLog prismMainLog;
	private PrismLog prismTechLog;
	public static boolean mdp2dtmc = false;

	public Config(DiPro dipro) {
		this.dipro = dipro;
		if (dipro.isPlugin())
			loadDefaultConfiguration();
		else
			loadConfiguration();
		parameters = new LinkedList<String>();
	}

	public void reset() {
		parameters.clear();
		loadDefaultConfiguration();
	}

	private void loadConfiguration() {
		try {
			loadConfigurationFromFile();
		} catch (IOException e) {
			loadDefaultConfiguration();
			Registry.getMain().handleWarning("Failed to load configurations from file!", e);
			Registry.getMain().handleWarning("Internal default configurations are loaded!");
		}
	}

	// private String configFileName() {
	// return
	// DIPRO_DIR+System.getProperty("file.separator")+"etc"+System.getProperty("file.separator")+"dipro.cfg";
	// }

	private void loadConfigurationFromFile() throws FileNotFoundException,
			IOException {
		Properties configFile = new Properties();
		String srcName = "etc/dipro.cfg";
		URL url = ClassLoader.getSystemResource(srcName);
		if (url == null) {
//			Registry.getMain().handleWarning(
//					"Configuration file not available: \"" + srcName + "\"");
			throw new FileNotFoundException(
					"Configuration file not available: \"" + srcName + "\"");
		}
		File file = null;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			throw new IOException("Invalid URL "+url);
		}
		configFile.load(new FileInputStream(file));
		algLogName = configFile.getProperty("algLogName");
		algType = Integer.parseInt(configFile.getProperty("algType"));
		greedy = Boolean.parseBoolean(configFile.getProperty("greedy"));
		usePi = Boolean.parseBoolean(configFile.getProperty("usePi"));
		k = Integer.parseInt(configFile.getProperty("k"));
		maxIter = Integer.parseInt(configFile.getProperty("maxIter"));
		maxTime = Float.parseFloat(configFile.getProperty("maxTime"));
		pruneBound = Double.parseDouble(configFile.getProperty("pruneBound"));
		logLevel = Integer.parseInt(configFile.getProperty("logLevel"));
		report = Boolean.parseBoolean(configFile.getProperty("report"));
		isInStepByStepModus = Boolean.parseBoolean(configFile
				.getProperty("isInStepByStepModus"));
		complete = Boolean.parseBoolean(configFile.getProperty("complete"));
		mc = Boolean.parseBoolean(configFile.getProperty("mc"));
		kxsol = Boolean.parseBoolean(configFile.getProperty("kxsol"));
		mcsol = Integer.parseInt(configFile.getProperty("mcsol"));
		mrmcsol = Boolean.parseBoolean(configFile.getProperty("mrmcsol"));
		reportName = configFile.getProperty("reportName");
		onlineVisualization = Boolean.parseBoolean(configFile
				.getProperty("onlineVisualization"));
		cxIncrementRatio = Double.parseDouble(configFile.getProperty("cxIncrementRatio"));
		solutionTrace = Integer.parseInt(configFile.getProperty("solutionTrace"));
		isVisualizationEnabled = Boolean.parseBoolean(configFile.getProperty("isVisualizationEnabled"));
		lengthHeuristic =  Boolean.parseBoolean(configFile.getProperty("lenH"));
		isProbPatternH  =  Boolean.parseBoolean(configFile.getProperty("probH"));
		srcName = configFile.getProperty("colorScaleFileName");
		url = ClassLoader.getSystemResource(srcName);
		if (url == null) {
			Registry.getMain().handleFatalError("Failed to load color scale file: "+ srcName, 
					new DiProException("Failed to load color scale file \""
							+ srcName + "\""));
		}
		colorScaleFileName = url.getFile();
		int mc = Integer.parseInt(configFile.getProperty("accuracy"));
		switch (mc) {
		case 32:
			mathContext = MathContext.DECIMAL32;
			break;
		case 64:
			mathContext = MathContext.DECIMAL64;
			break;
		case 128:
			mathContext = MathContext.DECIMAL128;
			break;
		default: {
			Registry.getMain().handleWarning(
					"Invalid accuracy configuration parameter: " + mc
							+ ". \n Accuracy 32 is used.");
			mathContext = MathContext.DECIMAL32;
			break;
		}
		}
		modelType = PRISM_MODEL;
		cxFileName = null;
		uniformRate = -1.0d;
		// ForDebugging
		// System.out.println(System.getProperty("java.library.path"));
	}

	private void loadDefaultConfiguration() {
		algLogName = "stdout";
		algType = BF;
		greedy = false;
		usePi = false;
		k = 0;
		maxIter = Integer.MAX_VALUE - 1;
		maxTime = 0;
		pruneBound = -1.0d;
		logLevel = 2;
		report = false;
		isInStepByStepModus = false;
		complete = false;
		mc = false;
		kxsol = false;
		mcsol = 0;
		mrmcsol = false;
		reportName = "stdout";
		onlineVisualization = true;
		cxIncrementRatio = 1.2;
		solutionTrace = -1;
		String srcName = "etc"+File.separator+"colorscale_blue_256.txt";
		URL url = ClassLoader.getSystemResource(srcName);
		if (url == null) {
			Registry.getMain().handleFatalError("Failed to load color scale file: "+ srcName, 
					new DiProException("Failed to load color scale file \""
							+ srcName + "\""));
		}
		try {
			File f = new File(url.toURI());
			colorScaleFileName = f.getAbsolutePath();
		} catch (URISyntaxException e) {
			Registry.getMain().handleFatalError("Failed to load color scale file: "+ srcName, e);
		}
		mathContext = MathContext.DECIMAL32;
		modelType = PRISM_MODEL;
		cxFileName = null;
		uniformRate = -1.0d;
		isVisualizationEnabled = true;
		lengthHeuristic = false;
		isProbPatternH = false;
	}



	public void commit() {
		if (pruneBound < 0.0d) {
			switch (modelType) {
			case DIRECTED_GRAPH:
				pruneBound = Double.MAX_VALUE - 1;
				break;
			case PRISM_MODEL:
			case PRISM_EXPLICIT_MODEL:
			case MRMC_MODEL:
				// case PRISM_MRMC_MODEL:
				pruneBound = 0;
				break;
			}
		}
		switch (algType) {
		case BF:
		case BF_STAR:
			if (k == 0)
				k = 1;
			break;
		case XBF:
		case K_STAR:
		case EPPSTEIN:
			greedy = false;
			if (k == 0)
				k = Integer.MAX_VALUE - 1;
			break;
		}

		switch (modelType) {
		case DIRECTED_GRAPH:
			assert algType == BF || algType == BF_STAR || algType == EPPSTEIN
					|| algType == K_STAR;
			assert pruneBound >= 0.0d;
			break;
		case PRISM_MODEL:
		case PRISM_EXPLICIT_MODEL:
		case MRMC_MODEL:
			// case PRISM_MRMC_MODEL:
			assert algType == XBF || algType == K_STAR || algType == EPPSTEIN;
			assert pruneBound >= 0.0d || pruneBound <= 1.0d;
			break;
		}
		if (algType == EPPSTEIN) {
			assert heuristicName == null;
		}
	}

	public DiPro getDiPro() {
		return dipro;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("Configuration:\n");
		// sb.append("Main Log: "+mainLogName+"\n");
		sb.append("Alg. Log:        " + algLogName + "\n");
		sb.append("Model type:      " + modelType + "\n");
		sb.append("Alg. type:       " + algTypeStr() + "\n");
		sb.append("CX File:        " + cxFileName + "\n");
		if (algType == XBF) {
			sb.append("Use Pi vectors:   " + usePi + "\n");
			sb.append("Model check solution every  " + mcsol + " iterations\n");
		}
		sb.append("Greedy:          " + greedy + "\n");
		sb.append("Max. runtime: " + maxTime + "\n");
		sb.append("Max. iterations: " + maxIter + "\n");
		sb.append("Max. sol. traces:  " + k + "\n");
		sb.append("Prune bound:     " + pruneBound + "\n");
		sb.append("Log level:       " + logLevel + "\n");
		sb.append("Stepwise:        " + isInStepByStepModus + "\n");
		sb.append("Complete search: " + complete + "\n");
		sb.append("Heuristic:       " + heuristicName + "\n");
		sb.append("Model Checking:  " + mc + "\n");
		sb.append("Uniformization:  " + uniformRate + "\n");
		sb.append("Other param.:    " + parameters + "\n");
		return sb.toString();
	}

	private String algTypeStr() {
		switch (algType) {
		case BF:
			return "BF";
		case BF_STAR:
			return "BF*";
		case XBF:
			return "XBF";
		case K_STAR:
			return "K*";
		case EPPSTEIN:
			return "Eppstein";
		}
		return "Unkown";
	}

	public void setModel(ModulesFile modelFile) {
		this.modelFile = modelFile;
	}

	public ModulesFile getModel() {
		return this.modelFile;
	}

	public void setProp(PropertiesFile propFile) {
		this.propFile = propFile;
	}

	public PropertiesFile getProp() {
		return this.propFile;
	}

	public void setAlgorithm(int algType) {
		this.algType = algType;
	}

	public void setModelType(int modelType) {
		this.modelType = modelType;
	}

	
	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public int getModelType() {
		return this.modelType;
	}

	public boolean isGreedy() {
		return greedy;
	}

	public void setGreedy(boolean greedy) {
		this.greedy = greedy;
	}

	public boolean isUsePi() {
		return usePi;
	}

	public void setUsePi(boolean usePi) {
		this.usePi = usePi;
	}

	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}
	
	public String getAlgLogName() {
		return algLogName;
	}

	public void setAlgLogName(String algLogName) {
		this.algLogName = algLogName;
	}

	public String getCxFileName() {
		return cxFileName;
	}

	public void setCxFileName(String cxFileName) {
		this.cxFileName = cxFileName;
	}

	public int getMaxIter() {
		return maxIter;
	}

	public void setMaxIter(int maxIter) {
		this.maxIter = maxIter;
	}

	public float getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(float maxTime) {
		this.maxTime = maxTime;
	}

	public double getPruneBound() {
		return pruneBound;
	}

	public void setPruneBound(double pruneBound) {
		this.pruneBound = pruneBound;
	}

	public int getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	public boolean isReport() {
		return report;
	}

	public void setReport(boolean report) {
		this.report = report;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public String getHeuristicName() {
		return heuristicName;
	}

	public void setHeuristicName(String heuristicName) {
		this.heuristicName = heuristicName;
	}

	public boolean isMc() {
		return mc;
	}

	public void setMc(boolean mc) {
		this.mc = mc;
	}

	public boolean isKxsol() {
		return kxsol;
	}

	public void setKxsol(boolean kxsol) {
		this.kxsol = kxsol;
	}

	public int getMcsol() {
		return mcsol;
	}

	public void setMcsol(int mcsol) {
		this.mcsol = mcsol;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public double getUniformRate() {
		return uniformRate;
	}

	public void setUniformRate(double uniformRate) {
		this.uniformRate = uniformRate;
	}

	public void setPrismMainLog(PrismLog prismMainLog) {
		this.prismMainLog = prismMainLog;
	}

	public void setPrismTechLog(PrismLog prismTechLog) {
		this.prismTechLog = prismTechLog;
	}

	public PrismLog getPrismMainLog() {
		return prismMainLog;
	}

	public PrismLog getPrismTechLog() {
		return prismTechLog;
	}
	
	public int getPropId() {
		return propId;
	}
	
	public void setPropId(int propId) {
		this.propId = propId;
	}

	public void setConstantValues(Values constantValues) {
		this.constantValues = constantValues;
	}
	
	public Values getConstantValues(){
		return constantValues;
	}
	
	public double getCxIncrementRatio() {
		return cxIncrementRatio;
	}

	public void setCxIncrementRatio(double cxIncrementRatio) {
		this.cxIncrementRatio = cxIncrementRatio;
	}

	public void setLengthHeuristic(boolean lengthHeuristic) {
		this.lengthHeuristic = lengthHeuristic;
	}

	public boolean getLengthHeuristic() {
		return lengthHeuristic;
	}



	public boolean getIsProbPatternH() {
		return isProbPatternH;
	}

	public void setProbPatternH(boolean isProbPatternH) {
		this.isProbPatternH = isProbPatternH;
	}
}
