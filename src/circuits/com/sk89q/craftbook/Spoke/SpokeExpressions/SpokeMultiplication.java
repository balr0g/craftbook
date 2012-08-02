package com.sk89q.craftbook.Spoke.SpokeExpressions;

import java.util.ArrayList;

import com.sk89q.craftbook.Spoke.IToken;

public class SpokeMultiplication extends SpokeBasic implements SpokeItem , SpokeMathItem {
	public ArrayList<SpokeItem> Items=new ArrayList<SpokeItem>();
	@Override	public SpokeItem[] getItems() {		return Items.toArray(new SpokeItem[Items.size()]);	}
	
	@Override
	public void PushItem(SpokeItem it) {Items.add(it);}
	@Override
	public int getWeight() {return 2;}

	public SpokeMultiplication() { 
	}
	
	public ISpokeItem getIType() {
		return ISpokeItem.Multiplication;
	}

	@Override
	public String toString() {
		return "*";
	}	IToken[] tokens;
	@Override	public SpokeBasic setTokens(IToken... toks) {		tokens=toks;		return this;	}
	@Override	public IToken[] getTokens() {		return tokens;	}


}