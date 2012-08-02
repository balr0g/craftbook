package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.IToken;

public abstract class SpokeBasic {
	public static int Index;
	private String guid = Integer.toString(Index++);

	public String getGuid() {
		return guid;
	}

	public void resetGUID() {
		guid = Integer.toString(Index++);
	}
	public abstract SpokeBasic setTokens(IToken ... toks);
	public abstract IToken[] getTokens();
}
