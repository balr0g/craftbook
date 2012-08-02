package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.IToken;

public class SpokeYieldReturn extends SpokeBasic implements SpokeLine {
	public SpokeItem YieldReturn; 
	public SpokeYieldReturn(SpokeItem v) {
		YieldReturn = v;
	}
	public ISpokeLine getLType() {
		return ISpokeLine.YieldReturn;
	}
	IToken[] tokens;
	@Override	public SpokeBasic setTokens(IToken... toks) {		tokens=toks;		return this;	}
	@Override	public IToken[] getTokens() {		return tokens;	}

}