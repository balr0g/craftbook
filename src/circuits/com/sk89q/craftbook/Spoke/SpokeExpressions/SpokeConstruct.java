package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.IToken;

public class SpokeConstruct extends SpokeBasic implements SpokeItem, SpokeLine {
	public String ClassName;
	public SpokeItem[] Parameters = new SpokeItem[0];
	public SVarItems[] SetVars = new SVarItems[0];
	public int NumOfVars;

	public int MethodIndex = -1;

	public ISpokeItem getIType() {
		return ISpokeItem.Construct;
	}

	public ISpokeLine getLType() {
		return ISpokeLine.Construct;
	}

	@Override
	public String toString() {
		return "Construct " + ClassName;
	}	IToken[] tokens;
	@Override	public SpokeBasic setTokens(IToken... toks) {		tokens=toks;		return this;	}
	@Override	public IToken[] getTokens() {		return tokens;	}

}