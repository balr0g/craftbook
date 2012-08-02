package com.sk89q.craftbook.Spoke;

import com.sk89q.craftbook.Spoke.SpokeInstructions.SpokeObject;

public class SpokeStack {
	SpokeObject[] Variables;
	String MethodName;
	String SelectedTokens;
	private String[] VariableNames;

	public SpokeStack(String[] vnames,SpokeObject[] v, String m, String debugToks) {
		Variables = v;
		MethodName = m;
		VariableNames=vnames;
		SelectedTokens=debugToks;
	}
}