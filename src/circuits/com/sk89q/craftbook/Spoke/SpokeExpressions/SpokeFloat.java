package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.IToken;

public class SpokeFloat extends SpokeBasic implements SpokeItem {
	public float Value;
	public SpokeFloat(float v) {
		Value = v;
	}
	public ISpokeItem getIType() {
		return ISpokeItem.Float;
	}

	@Override
	public String toString() {
		return Float.toString(Value);
	}	IToken[] tokens;
	@Override	public SpokeBasic setTokens(IToken... toks) {		tokens=toks;		return this;	}
	@Override	public IToken[] getTokens() {		return tokens;	}


}