package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.IToken;

public class SpokeBool extends SpokeBasic implements SpokeItem {
	public boolean Value;
	public SpokeBool(boolean v) {
		Value = v;
	}
	public ISpokeItem getIType() {
		return ISpokeItem.Bool;
	}

	@Override
	public String toString() {
		return Boolean.toString(Value);
	}	IToken[] tokens;
	@Override	public SpokeBasic setTokens(IToken... toks) {		tokens=toks;		return this;	}
	@Override	public IToken[] getTokens() {		return tokens;	}


}