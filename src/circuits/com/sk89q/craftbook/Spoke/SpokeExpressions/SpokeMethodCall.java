package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.IToken;

public class SpokeMethodCall extends SpokeBasic implements SpokeParent,
		SpokeLine, SpokeItem {
	SpokeParent parent;

	public SpokeMethodCall(SpokeItem currentItem) {
		parent=(SpokeParent) currentItem;
	}
	public SpokeMethodCall(SpokeItem currentItem,SpokeItem[] para) {
		Parameters=para;
		parent=(SpokeParent) currentItem;
	}

	public SpokeMethodCall() {
		// TODO Auto-generated constructor stub
	}
	public SpokeItem getParent() {
		return parent;
	}

	public void setParent(SpokeItem im) {
		parent = (SpokeParent) im;
	}

	public SpokeItem[] Parameters;

	public ISpokeLine getLType() {
		return ISpokeLine.MethodCall;
	}

	public ISpokeItem getIType() {
		return ISpokeItem.MethodCall;
	}

	@Override
	public String toString() {
		return getParent().toString();
	}	IToken[] tokens;
	@Override	public SpokeBasic setTokens(IToken... toks) {		tokens=toks;		return this;	}
	@Override	public IToken[] getTokens() {		return tokens;	}

}