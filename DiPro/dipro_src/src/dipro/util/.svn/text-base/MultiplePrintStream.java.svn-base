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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Locale;

public class MultiplePrintStream extends PrintStream {

	private LinkedList<PrintStream> streams;

	public MultiplePrintStream() throws FileNotFoundException {
		super(System.out);
		streams = new LinkedList<PrintStream>();
	}

	public void add(PrintStream stream) {
		streams.add(stream);
	}

	public boolean remove(PrintStream stream) {
		return streams.remove(stream);
	}

	@Override
	public PrintStream append(char c) {
		for (PrintStream stream : streams)
			stream.append(c);
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq, int start, int end) {
		for (PrintStream stream : streams)
			stream.append(csq, start, end);
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq) {
		for (PrintStream stream : streams)
			stream.append(csq);
		return this;
	}

	@Override
	public boolean checkError() {
		boolean b = false;
		for (PrintStream stream : streams)
			b = b || stream.checkError();
		return b;
	}

	@Override
	public void close() {
		for (PrintStream stream : streams)
			stream.close();
	}

	@Override
	public void flush() {
		for (PrintStream stream : streams)
			stream.flush();
	}

	@Override
	public PrintStream format(Locale l, String format, Object... args) {
		for (PrintStream stream : streams)
			stream.format(l, format, args);
		return this;
	}

	@Override
	public PrintStream format(String format, Object... args) {
		for (PrintStream stream : streams)
			stream.format(format, args);
		return this;
	}

	@Override
	public void print(boolean b) {
		for (PrintStream stream : streams)
			stream.print(b);
	}

	@Override
	public void print(char c) {
		for (PrintStream stream : streams)
			stream.print(c);
	}

	@Override
	public void print(char[] s) {
		for (PrintStream stream : streams)
			stream.print(s);
	}

	@Override
	public void print(double d) {
		for (PrintStream stream : streams)
			stream.print(d);
	}

	@Override
	public void print(float f) {
		for (PrintStream stream : streams)
			stream.print(f);
	}

	@Override
	public void print(int i) {
		for (PrintStream stream : streams)
			stream.print(i);
	}

	@Override
	public void print(long l) {
		for (PrintStream stream : streams)
			stream.print(l);
	}

	@Override
	public void print(Object obj) {
		for (PrintStream stream : streams)
			stream.print(obj);
	}

	@Override
	public void print(String s) {
		for (PrintStream stream : streams)
			stream.print(s);
	}

	@Override
	public PrintStream printf(Locale l, String format, Object... args) {
		for (PrintStream stream : streams)
			stream.printf(l, format, args);
		return this;
	}

	@Override
	public PrintStream printf(String format, Object... args) {
		for (PrintStream stream : streams)
			stream.printf(format, args);
		return this;
	}

	@Override
	public void println() {
		for (PrintStream stream : streams)
			stream.println();
	}

	@Override
	public void println(boolean x) {
		for (PrintStream stream : streams)
			stream.println(x);
	}

	@Override
	public void println(char x) {
		for (PrintStream stream : streams)
			stream.println(x);
	}

	@Override
	public void println(char[] x) {
		for (PrintStream stream : streams)
			stream.println(x);
	}

	@Override
	public void println(double x) {
		for (PrintStream stream : streams)
			stream.println(x);
	}

	@Override
	public void println(float x) {
		for (PrintStream stream : streams)
			stream.println(x);
	}

	@Override
	public void println(int x) {
		for (PrintStream stream : streams)
			stream.println(x);
	}

	@Override
	public void println(long x) {
		for (PrintStream stream : streams)
			stream.println(x);
	}

	@Override
	public void println(Object x) {
		for (PrintStream stream : streams)
			stream.println(x);
	}

	@Override
	public void println(String x) {
		for (PrintStream stream : streams)
			stream.println(x);
	}

	@Override
	protected void setError() {
		throw new UnsupportedOperationException("setError");
	}

	@Override
	public void write(byte[] buf, int off, int len) {
		for (PrintStream stream : streams)
			stream.write(buf, off, len);
	}

	@Override
	public void write(int b) {
		for (PrintStream stream : streams)
			stream.write(b);
	}

	@Override
	public void write(byte[] arg0) throws IOException {
		for (PrintStream stream : streams)
			stream.write(arg0);
	}

}
