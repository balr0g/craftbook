package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.IToken;

public class SpokeLessThanOrEqual extends SpokeBasic implements SpokeItem {
	public SpokeItem LeftSide;
	public SpokeItem RightSide;

	public SpokeLessThanOrEqual(SpokeItem currentItem, SpokeItem eval) {LeftSide=currentItem;
	RightSide=eval;
	}

	public ISpokeItem getIType() {
		return ISpokeItem.LessEqual;
	}

	@Override
	public String toString() {
		return LeftSide + "<=" + RightSide;
	}	IToken[] tokens;
	@Override	public SpokeBasic setTokens(IToken... toks) {		tokens=toks;		return this;	}
	@Override	public IToken[] getTokens() {		return tokens;	}


}