package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.IToken;

public class SpokeOr extends SpokeBasic implements SpokeItem {
	public SpokeItem LeftSide;
	public SpokeItem RightSide;

	public SpokeOr(SpokeItem currentItem, SpokeItem eval) {
LeftSide=currentItem;
RightSide=eval;
	}

	public ISpokeItem getIType() {
		return ISpokeItem.Or;
	}

	@Override
	public String toString() {
		return "||";
	}	IToken[] tokens;
	@Override	public SpokeBasic setTokens(IToken... toks) {		tokens=toks;		return this;	}
	@Override	public IToken[] getTokens() {		return tokens;	}


}