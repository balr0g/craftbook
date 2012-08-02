package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.IToken;
import com.sk89q.craftbook.Spoke.SpokeInstructions.ParamEter;

public class SpokeAnonMethod extends SpokeBasic implements SpokeParent, SpokeItem, SpokeLine, SpokeLines {
	SpokeItem parent;

	public SpokeAnonMethod(SpokeItem currentItem) {
		this.parent = currentItem;
	}

	public SpokeAnonMethod() {
		// TODO Auto-generated constructor stub
	}

	public SpokeItem getParent() {
		return parent;
	}

	public void setParent(SpokeItem im) {
		parent = (SpokeParent) im;
	}

	public SpokeLine[] getLines() {
		return lines;
	}

	public void setLines(SpokeLine[] im) {
		lines = im;
	}

	public SpokeItem RunOnVar;
	public SpokeLine[] lines;
	public ParamEter[] Parameters;
	public boolean SpecAnon;
	public SpokeVariable ReturnYield;

	public boolean HasYield;
	public boolean HasYieldReturn;
	public boolean HasReturn;

	public ISpokeItem getIType() {
		return ISpokeItem.AnonMethod;
	}

	public ISpokeLine getLType() {
		return ISpokeLine.AnonMethod;
	}
	IToken[] tokens;
	@Override	public SpokeBasic setTokens(IToken... toks) {		tokens=toks;		return this;	}
	@Override	public IToken[] getTokens() {		return tokens;	}

}