package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.IToken;


public class SpokeNot extends SpokeBasic implements SpokeItem {

	public SpokeItem NotValue;
	@Override
	public ISpokeItem getIType() {
		// TODO Auto-generated method stub
		return ISpokeItem.Not;
	}
	public SpokeNot(SpokeItem notvalue){
		NotValue=notvalue; 
	}
	IToken[] tokens;
	@Override	public SpokeBasic setTokens(IToken... toks) {		tokens=toks;		return this;	}
	@Override	public IToken[] getTokens() {		return tokens;	}

}
