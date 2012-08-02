package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.IToken;

public class SpokeTernary extends SpokeBasic implements SpokeItem {
	public SpokeItem LeftSide;
	public SpokeItem RightSide;
	public SpokeItem Condition;

	public SpokeTernary(SpokeItem cond, SpokeItem currentItem, SpokeItem eval) {
		Condition = cond;
		LeftSide = currentItem;
		RightSide = eval;
	}

	public ISpokeItem getIType() {
		return ISpokeItem.Ternary;
	}

	@Override
	public String toString() {
		return "?:";
	}

	IToken[] tokens;

	@Override
	public SpokeBasic setTokens(IToken... toks) {
		tokens = toks;
		return this;
	}

	@Override
	public IToken[] getTokens() {
		return tokens;
	}

}