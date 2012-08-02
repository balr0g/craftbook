package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.IToken;

public class SpokeVariable extends SpokeBasic implements SpokeParent, SpokeItem {
	SpokeParent parent;

 
	public SpokeItem getParent() {
		return parent;
	}

	public void setParent(SpokeItem im) {
		parent = (SpokeParent) im;
	}
	public SpokeVariable(String vname,SpokeItem im) {
		parent = (SpokeParent) im;
		VariableName=vname;
	}

	public SpokeVariable(String vname ) { 
		VariableName=vname;
	}

	public SpokeVariable(Integer ind,String vname,SpokeItem im) {
		parent = (SpokeParent) im;
		VariableName=vname;
		VariableIndex=ind;
	}

	public String VariableName;
	public int VariableIndex;
	public boolean This;
	public SpokeVType VType;
	public boolean ForSet;

	public ISpokeItem getIType() {
		return ISpokeItem.Variable;
	}

	@Override
	public String toString() {
		if (parent == null) {
			return VariableName;
		}
		return getParent() + "." + VariableName;
	}	IToken[] tokens;
	@Override	public SpokeBasic setTokens(IToken... toks) {		tokens=toks;		return this;	}
	@Override	public IToken[] getTokens() {		return tokens;	}


}