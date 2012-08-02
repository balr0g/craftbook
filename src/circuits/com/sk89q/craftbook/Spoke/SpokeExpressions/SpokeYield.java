package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.IToken;

public class SpokeYield extends SpokeBasic implements SpokeLine {
	public SpokeItem Yield;

	public SpokeYield(SpokeItem eval) {
		Yield=eval;
	}

	public ISpokeLine getLType() {
		return ISpokeLine.Yield;
	}	IToken[] tokens;
	@Override	public SpokeBasic setTokens(IToken... toks) {		tokens=toks;		return this;	}
	@Override	public IToken[] getTokens() {		return tokens;	}

}