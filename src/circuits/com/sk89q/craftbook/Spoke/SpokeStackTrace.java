package com.sk89q.craftbook.Spoke;

import java.util.ArrayList;

public class SpokeStackTrace {
	public SpokeStack[] Stack;
	public String CurrentTokens;
	public transient SpokeApplication SpokeApp; 

	public SpokeStackTrace(ArrayList<SpokeStack> s, String c, SpokeApplication sa) {
		Stack = s.toArray(new SpokeStack[s.size()]);
		CurrentTokens  = c;
		SpokeApp=sa;
	}
	public SpokeStackTrace() {
	}

}