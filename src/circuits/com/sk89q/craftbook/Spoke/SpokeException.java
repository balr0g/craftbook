package com.sk89q.craftbook.Spoke;

import java.io.PrintWriter;
import java.io.StringWriter;

public class SpokeException extends Exception {
	private IToken[] toks;
	public String Message;
	public Exception Exc;

	public SpokeException(IToken... tokens) {
		if (tokens == null || tokens.length == 0) {
			toks = new IToken[0];
		} else
			toks = tokens;
		Message = "";
	}

	public SpokeException(String string, IToken... tokens) {
		toks = tokens;
		Message = string;
	}

	public SpokeException(Exception ec, IToken... tokens) {
		Exc = ec;
		toks = tokens;
	}

	public void setTokens(IToken[] location) {
		toks = location;
	}

	public IToken[] getTokens() {
		return toks;
	}

	public String Print(IToken[] total) {
		if (toks==null ||toks.length == 0)
			return "";
		

		StringBuilder bud=new StringBuilder();
		for (IToken t : toks) {
			bud.append("t"+ java.util.Arrays.asList(total).indexOf(t)+",");
		}

		return bud.toString();
/*		
		int size = 400;
		return (sb
				.insert(last, "#")
				.insert(first, "#")
				.substring(first - size < 0 ? 0 : first - size,
						last + size > sb.length() ? sb.length() : last + size)
				.replaceAll("\r", "\r\n").toString())
				+ (Exc == null ? Message : exceptionStackTrace(Exc));
	*/}

	public String Print(String total) {
		if (toks==null ||toks.length == 0)
			return (Exc == null ? Message : exceptionStackTrace(Exc));
		
		
		StringBuffer sb = new StringBuffer(total);
		int first = -1, last = -1;
		for (IToken t : toks) {
			if (first == -1) {
				first = t.getCharacterIndex();
			}
			if (last == -1) {
				last = t.getCharacterIndex();
			}
		}

		int size = 400;
		return (sb
				.insert(last, "#")
				.insert(first, "#")
				.substring(first - size < 0 ? 0 : first - size,
						last + size > sb.length() ? sb.length() : last + size)
				.replaceAll("\r", "\r\n").toString())
				+ (Exc == null ? Message : exceptionStackTrace(Exc));
	}

	public String exceptionStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);

		return sw.toString();
	}
 
}
