package dipro.run;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;

import parser.ast.Expression;
import parser.ast.ModulesFile;
import prism.Prism;
import prism.PrismCL;
import prism.PrismException;
import prism.PrismFileLog;

public class MDP2DTMC {
	
	private int numStates;
	private int numTransitions;
//	private String dbName;
	private boolean isLoading;
	
	public MDP2DTMC() {
		reset();
	}
	
	private void reset() {
		numStates = 0;
		numTransitions = 0;
//		dbName = null;
		isLoading = false;
	}
	
	public PrismExplicitContext convertToDTMC(PrismDefaultContext context) throws Exception {
		reset();
		
		String mf=context.getModelFileName();
		String pf = context.getPropFileName();
		String cf = context.getConstFileName();
		int prop =context.getProbIndex()+1;
		convertToDTMC(mf, pf, prop, cf);
		Config config = context.getConfig();
		config.modelType = Config.PRISM_EXPLICIT_MODEL;
		config.parameters.clear();
		String dbName = getDatabaseName(mf);
		String traFileName = "adv.tra";
		String staFileName = "adv.sta";
		String labFileName = "adv.lab";
		config.parameters.add(dbName);
		config.parameters.add(staFileName);
		config.parameters.add(traFileName);
		config.parameters.add(labFileName);
		config.parameters.add(pf);
		if(cf!=null) config.parameters.add(cf);
		config.parameters.add("-prop");
		config.parameters.add(Integer.toString(context.getProbIndex()));
		config.parameters.add("-type");
		config.parameters.add(Integer.toString(ModulesFile.PROBABILISTIC));
		PrismExplicitContext newContext = new PrismExplicitContext(context.getId(), config);
		return newContext;
	}
	
	public void convertToDTMC(String mf, String pf, int prop, String cf) throws Exception {
		Registry.getMain().out().println("Convert the model "+mf+" to a DTMC ");
		String consts = cf==null? "":readConsts(cf);
		StringBuilder sb = new StringBuilder();
		sb.append(mf);
		sb.append(" ");
		sb.append(pf);
		sb.append(" -prop ");
		sb.append(prop);
		if(consts.length()>0) {
			sb.append(" -const ");
			sb.append(consts);
		}
		sb.append(" -fixdl -maxiters 100000000 -s -nopre -exportstates adv.sta -exportlabels adv.lab -mainlog adv.prism.main.log -techlog adv.prism.tech.log");
//		System.out.println(sb);
		String[] command = sb.toString().split(" ");
		PrismCL prismCL = new PrismCL();
		long time = System.currentTimeMillis();
		prismCL.run(command);
		time = System.currentTimeMillis() - time;
		Registry.getMain().out().println("Converting the model to a DTMC took "+time+" milliseconds.");
	}

	
	public void loadExplicitModelIntoSQLDatabase(PrismExplicitContext context) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, PrismException {
		String staFile = context.getStaFileName();
		String traFile = context.getTraFileName();
		String labFile = context.getLabFileName();
		String dbName = context.getDatabaseName();
		int modelType = context.getModelType();
		loadIntoSQLDatabase(staFile, traFile, labFile, modelType, dbName);
//		context.setDatabaseName(dbName);
	}
	
	private String getDatabaseName(String modelFilePath) {
		int i = modelFilePath.lastIndexOf('/');
		assert i>0 && i < modelFilePath.length()-1;
		String[] tokens = modelFilePath.substring(i+1).split("[.]");
		assert tokens.length > 0;
		return tokens[0];
	}
	
	public void loadIntoSQLDatabase(String staFile, String traFile, String labFile, int modelType, String dbName) throws IOException, PrismException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		String driver = "com.mysql.jdbc.Driver";
		Class.forName(driver).newInstance();
		boolean b = createDatabase(dbName);
		if(!b) {
			Registry.getMain().out().println("Database "+dbName+" could not correctly created. ");
			return;
		}
		Registry.getMain().out().println("Loading explicit prism model into SQL database "+dbName);
		Registry.getMain().out().println("State file: "+staFile);
		Registry.getMain().out().println("Transition file: "+traFile);
		Registry.getMain().out().println("Label file: "+labFile);
		PrismFileLog mainLog = new PrismFileLog(staFile+".prism.main.log");
		PrismFileLog techLog = new PrismFileLog(staFile+".prism.tech.log");
		long time1 = System.currentTimeMillis();
		Prism prism = new Prism(mainLog, techLog);
		ModulesFile modulesFile = prism.parseExplicitModel(new File(staFile), new File(traFile), new File(labFile), modelType);
		time1 = System.currentTimeMillis() - time1;
		Registry.getMain().out().println("Module file parsed; time = "+time1);
		Registry.getMain().out().println("External state und transition files are being loaded...");
		String url = "jdbc:mysql://localhost/"+dbName;
		String user = "root";
		String password = "dipro";
		Connection conn = null;
		Statement stmt = null;
		numStates = 0;
		numTransitions = 0;
		try {
			conn = DriverManager.getConnection(url, user, password); 
			stmt = conn.createStatement();
			isLoading = true;
			startLoadingWatcher();
			long time2 = System.currentTimeMillis();
			HashMap<String, Integer> varTypeMap = new HashMap<String, Integer>();
			Vector<String> varNames = modulesFile.getVarNames();
			for(int i=0; i<varNames.size(); i++) {
				String varName = varNames.elementAt(i);
				Integer varType = modulesFile.getVarTypes().elementAt(i);
				varTypeMap.put(varName, varType);
			}
			BufferedReader sta = new BufferedReader(new FileReader(staFile));
			String line = sta.readLine();
			line = line.substring(1, line.length()-1);
			String[] names = line.split("[,]");
			StringBuilder sqlSB = new StringBuilder("CREATE TABLE STATES ( ID INTEGER NOT NULL, ");
			for(int i=0; i<names.length; i++) {
				sqlSB.append(names[i]);
				int varType = varTypeMap.get(names[i]);
				switch(varType) {
				case Expression.INT: 
					sqlSB.append(" INTEGER NOT NULL,");
					break;
				case Expression.BOOLEAN: 
					sqlSB.append(" BOOL NOT NULL,");
					break;
				case Expression.DOUBLE:
					sqlSB.append(" REAL NOT NULL,");
					break;
				}
			}
			sqlSB.append("PRIMARY KEY( ID ));");
//			System.out.println(sqlSB);
			stmt.executeUpdate(sqlSB.toString());
			stmt.executeUpdate("CREATE TABLE TRANSITIONS ( " +
					"SOURCE INTEGER NOT NULL, " +
					"TARGET INTEGER NOT NULL, "+
					"PROB REAL NOT NULL );");
			line = sta.readLine();
			while (line != null) {
				String[] tokens = line.split("[:(,)]");
				StringBuilder valSB = new StringBuilder("( ");
				valSB.append(tokens[0]);
				valSB.append(", ");
				int firstVarIndex = 2; 
				for(int i = 0; i<names.length;i++){	
					String varValue = tokens[firstVarIndex+i];
					valSB.append(varValue);
					if(i < names.length-1) valSB.append(", ");
				}
				valSB.append(" )");
				String sql = "INSERT INTO STATES VALUES "+valSB.toString()+";";
//				System.out.println(sql);
				try {
					int x = stmt.executeUpdate(sql);
					assert x == 1;
					numStates++;
					line = sta.readLine();
				} catch(SQLException e) {
					Registry.getMain().handleError(e.toString());
					conn = DriverManager.getConnection(url, user, password); 
					stmt = conn.createStatement();
				}
			}
			stmt.close();
			sta.close();
			Registry.getMain().out().println("States were imported");
			Registry.getMain().out().println("Transition are being imported...");
			BufferedReader tra = new BufferedReader(new FileReader(traFile));
			line = tra.readLine();
			line = tra.readLine();
			while (line != null) {
				StringBuilder valSB = new StringBuilder("( ");
				String[] tokens = line.split(" ");
				valSB.append(tokens[0]);
				valSB.append(", ");
				valSB.append(tokens[1]);
				valSB.append(", ");
				valSB.append(tokens[2]);
				valSB.append(" )");
				try {
					int x = stmt.executeUpdate("INSERT INTO TRANSITIONS VALUES "+valSB.toString()+";");
					assert x == 1;
					numTransitions++;
					line = tra.readLine();
				} catch(SQLException e) {
					Registry.getMain().handleError(e.toString());
					conn = DriverManager.getConnection(url, user, password); 
					stmt = conn.createStatement();
				}
			}
			tra.close();
			time2 = System.currentTimeMillis() - time2;
			isLoading = false;
			Registry.getMain().out().println("Transitions were imported");
			Registry.getMain().out().println("Time to load external files = "+time2+" milliseconds");
			Registry.getMain().out().println("Explicit model imported ("+numStates+" states and "+numTransitions+" transitions)");
			Registry.getMain().out().println("Total time for importing the model was "+(time1+time2)+" milliseconds.");
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			if(stmt!=null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					Registry.getMain().handleError(e.toString());
				}
			}
			if(conn!=null) {
				try {
					conn.close();
				} catch (SQLException e) {
					Registry.getMain().handleError(e.toString());
				}
			}
		}	
//		System.out.println("Memory used to store the model is "+memory+" Bytes.");
	}
	

	
	private void startLoadingWatcher() {
		Thread thr = new Thread() {
			public void run() {
				Object lock = new Object();
				while(isLoading) {
					synchronized (lock) {
						try {
							lock.wait(100000);
						} catch (InterruptedException e) {
							Registry.getMain().handleError(e.toString());
						}
					}
					if(!isLoading) return;
					synchronized(MDP2DTMC.this) {
						try {
							Registry.getMain().tech().println("Loaded so far: "+numStates+" states and "+numTransitions+" transitions");
						} catch (Exception e) {
							Registry.getMain().handleError(e.toString());
						}
					}
				}
			}
		};
		thr.start();
	}
	
	private boolean removeDatabase(String dbName) {
		String url = "jdbc:mysql://localhost/mysql";
		String user = "root";
		String password = "dipro";
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DriverManager.getConnection(url,user,password);
			stmt = conn.createStatement();
			String sql = "DROP DATABASE "+dbName+";";
			stmt.executeUpdate(sql);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(stmt!=null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					//Registry.getMain().handleError(e);
				}
			}
			if(conn!=null) {
				try {
					conn.close();
				} catch (SQLException e) {
					//Registry.getMain().handleError(e);
				}
			}
		}
		return false;
	}
	
	private boolean createDatabase(String dbName) {
		String url = "jdbc:mysql://localhost/mysql";
		String user = "root";
		String password = "dipro";
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DriverManager.getConnection(url,user,password);
			stmt = conn.createStatement();
			String sql = "CREATE DATABASE "+dbName+";";
			stmt.executeUpdate(sql);
			return true;
		} catch (SQLException e) {
			Registry.getMain().handleError(e.toString());
		} finally {
			if(stmt!=null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn!=null) {
				try {
					conn.close();
				} catch (SQLException e) {
					Registry.getMain().handleError(e.toString());
				}
			}
		}
		return false;
	}
	
	private static String readConsts(String fName){
		File f = new File(fName);
		boolean firsttime = true;
	    String con = new String();
	    String tmp = new String();
		try {
			BufferedReader in = new BufferedReader(new FileReader(f));
			String line = in.readLine();
			while (line != null) {
				tmp = line;
				firsttime=false;
				line = in.readLine();	
				if(firsttime ==false){
					if(line != null)
						con+=tmp+",";
					else con+=tmp;
				}
				else {
					firsttime = false;
					con+="K=0,";
					con+=tmp;
				}
			}
			in.close();
			
		} catch (IOException e) {
			Registry.getMain().handleError(e.toString());
		}

		return con;
	}
	
	public static void main(String[] args) {
		new Main();
		if(args.length<4) {
			System.out.println("At least the following are required: ");
			System.out.println("1: Database name");
			System.out.println("2: Model file name");
			System.out.println("3: Properties file name");
			System.out.println("4: Property index");
			System.out.println("If there are undefined constants, then the following additional parameter is needed: ");
			System.out.println("5: Constants file name");
			System.exit(0);
		}
		String dbName = args[0];
		String mf = args[1];
		String pf = args[2];
		int prop = Integer.parseInt(args[3]);
		String cf = null;
		if(args.length > 4) cf = args[4];
		File dir = new File(mf).getParentFile();
		String staFileName = dir.getAbsolutePath()+"/"+dbName+".sta";
		String traFileName = dir.getAbsolutePath()+"/"+dbName+".tra";
		String labFileName = dir.getAbsolutePath()+"/"+dbName+".lab";
		MDP2DTMC converter = new MDP2DTMC();
		try {
			converter.convertToDTMC(mf, pf, prop, cf);
			String cmd = "mv adv.sta "+staFileName;
			System.out.println(cmd);
			Runtime.getRuntime().exec(cmd);
			cmd = "mv adv.tra "+traFileName;
			System.out.println(cmd);
			Runtime.getRuntime().exec(cmd);
			cmd = "mv adv.lab "+labFileName;
			System.out.println(cmd);
			Runtime.getRuntime().exec(cmd);
			converter.loadIntoSQLDatabase(staFileName, traFileName, labFileName, ModulesFile.PROBABILISTIC, dbName);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
