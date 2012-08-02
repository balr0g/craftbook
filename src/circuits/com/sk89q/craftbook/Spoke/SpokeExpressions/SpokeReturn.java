package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.IToken;

public class SpokeReturn extends SpokeBasic implements SpokeLine {
	public SpokeItem Return;
public SpokeReturn(SpokeItem r){
	Return =r;
} 
	public ISpokeLine getLType() {
		return ISpokeLine.Return;
	}

	@Override
	public String toString() {
		return "Return ";
	}	IToken[] tokens;
	@Override	public SpokeBasic setTokens(IToken... toks) {		tokens=toks;		return this;	}
	@Override	public IToken[] getTokens() {		return tokens;	}

}