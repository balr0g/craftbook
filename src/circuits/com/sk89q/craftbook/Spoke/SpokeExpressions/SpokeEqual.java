package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.IToken;

public class SpokeEqual extends SpokeBasic implements SpokeLine {
	public SpokeItem LeftSide;
	public SpokeItem RightSide;

	public SpokeEqual(SpokeItem currentItem) {
LeftSide=currentItem;
	}

	public SpokeEqual(SpokeItem currentItem,SpokeItem currentItem2) {
LeftSide=currentItem;
RightSide=currentItem2;
	}

	public ISpokeItem getIType() {
		return ISpokeItem.Equal;
	}

	public ISpokeLine getLType() {
		return ISpokeLine.Set;
	}

	@Override
	public String toString() {
		return LeftSide + "==" + RightSide;
	}
	IToken[] tokens;
	@Override	public SpokeBasic setTokens(IToken... toks) {		tokens=toks;		return this;	}
	@Override	public IToken[] getTokens() {		return tokens;	}

}