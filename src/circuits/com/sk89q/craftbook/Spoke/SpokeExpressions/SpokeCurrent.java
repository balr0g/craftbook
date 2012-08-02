package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.IToken;

public class SpokeCurrent extends SpokeBasic implements SpokeItem {
	public ISpokeItem getIType() {
		return ISpokeItem.Current;
	}

	@Override
	public String toString() {
		return "this";
	}
	IToken[] tokens;
	@Override	public SpokeBasic setTokens(IToken... toks) {		tokens=toks;		return this;	}
	@Override	public IToken[] getTokens() {		return tokens;	}

}