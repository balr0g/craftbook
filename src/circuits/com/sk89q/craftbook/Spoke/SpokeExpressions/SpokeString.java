package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.IToken;

public class SpokeString extends SpokeBasic implements SpokeItem {
	public String Value;

	public SpokeString(String v) {
		Value = v;
	}

	public ISpokeItem getIType() {
		return ISpokeItem.String;
	}

	@Override
	public String toString() {
		return Value.toString();
	}	IToken[] tokens;
	@Override	public SpokeBasic setTokens(IToken... toks) {		tokens=toks;		return this;	}
	@Override	public IToken[] getTokens() {		return tokens;	}


}