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

package dipro.stoch.mrmc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MRMCInvoker {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String logic = "csl";
		String tra = "examples/mrmc_export_test/model.tra";
		String lab = "examples/mrmc_export_test/model.lab";
		String prop = "P{<=0.001} [ tt U[0, 0.1] minimum]";

		List<String> command = new ArrayList<String>();
		command.add(0, "mrmc");
		command.add(1, logic);
		command.add(2, tra);
		command.add(3, lab);

		// ProcessBuilder mrmcBuilder = new ProcessBuilder(command);
		ProcessBuilder mrmcBuilder = new ProcessBuilder("top");
		Process mrmc;
		try {
			mrmc = mrmcBuilder.start();
			System.out.println("MRMC started");
			// InputStream in = mrmc.getInputStream();
			BufferedReader mrmcIN = new BufferedReader(new InputStreamReader(
					mrmc.getInputStream()));
			PrintWriter mrmcOut = new PrintWriter(mrmc.getOutputStream());
			mrmcOut.println(prop);
			mrmcOut.flush();
			String line;
			try {
				while ((line = mrmcIN.readLine()) != null)
					System.out.println(line);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// InputStream mrmcErr = mrmc.getErrorStream();
			// int n = mrmcIN.available();
			// byte[] buff = new byte[];

			// Thread err = new Thread(new InOut(mrmcErr, System.err));
			// Thread in = new Thread(new InOut(mrmcIN, System.out));
			// // //Thread out = new Thread(new InOut(System.in, mrmcOut));
			// err.start();
			// in.start();
			// out.start();

			System.out.println("Property entered");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Process mrmc = System.

	}

	static void read(InputStream in, OutputStream out) {
		// synchronized(this) {
		System.out.println("READ");
		int n;
		try {
			n = in.available();
		} catch (IOException e) {
			e.printStackTrace();
			n = 0;
		}
		if (n > 0) {
			byte[] buff = new byte[n];
			try {
				in.read(buff, 0, n);
			} catch (IOException e) {
				e.printStackTrace();
				n = 0;
			}
			try {
				if (n > 0)
					out.write(buff, 0, n);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// try {
		// wait(500);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
	}

	static class BInOut implements Runnable {

		protected BufferedReader in;
		protected PrintStream out;

		BInOut(BufferedReader in, PrintStream out) {
			this.in = in;
			this.out = out;
		}

		public void run() {
			while (true) {
				String line;
				try {
					while ((line = in.readLine()) != null)
						out.println(line);
				} catch (IOException e) {
					e.printStackTrace(out);
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace(out);
				}
			}
		}
	}

	static class InOut implements Runnable {

		protected InputStream in;
		protected OutputStream out;

		InOut(InputStream in, OutputStream out) {
			this.in = in;
			this.out = out;
		}

		public void run() {
			while (true) {
				// synchronized(this) {
				System.out.println("READ");
				int n;
				try {
					n = in.available();
				} catch (IOException e) {
					e.printStackTrace();
					n = 0;
				}
				if (n > 0) {
					byte[] buff = new byte[n];
					try {
						in.read(buff, 0, n);
					} catch (IOException e) {
						e.printStackTrace();
						n = 0;
					}
					try {
						if (n > 0)
							out.write(buff, 0, n);
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						out.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				// try {
				// wait(500);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
				// }
			}
		}

	}

}
