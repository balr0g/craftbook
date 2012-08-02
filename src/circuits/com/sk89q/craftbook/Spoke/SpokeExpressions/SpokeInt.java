package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.IToken;

public class SpokeInt extends SpokeBasic implements SpokeItem {
	public int Value;
	public SpokeInt(int v) {
		Value = v;
	}

	public ISpokeItem getIType() {
		return ISpokeItem.Int;
	}

	@Override
	public String toString() {
		return Integer.toString(Value);
	}
	IToken[] tokens;
	@Override	public SpokeBasic setTokens(IToken... toks) {		tokens=toks;		return this;	}
	@Override	public IToken[] getTokens() {		return tokens;	}

}