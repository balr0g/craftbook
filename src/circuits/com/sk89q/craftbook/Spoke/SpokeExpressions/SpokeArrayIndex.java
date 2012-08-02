package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.IToken;

public class SpokeArrayIndex extends SpokeBasic implements SpokeItem,
		SpokeParent {
	public boolean ForSet;
	SpokeParent parent;

	public SpokeItem getParent() {
		return parent;
	}

	public void setParent(SpokeItem im) {
		parent = (SpokeParent) im;
	}

	public SpokeItem Index;

	public ISpokeItem getIType() {
		return ISpokeItem.ArrayIndex;
	}

	@Override
	public String toString() {
		return getParent() + "[" + Index + "]";
	}
	IToken[] tokens;
	@Override	public SpokeBasic setTokens(IToken... toks) {		tokens=toks;		return this;	}
	@Override	public IToken[] getTokens() {		return tokens;	}

}