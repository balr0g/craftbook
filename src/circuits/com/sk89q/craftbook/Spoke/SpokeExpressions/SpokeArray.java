package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.ALH;
import com.sk89q.craftbook.Spoke.IToken;

public class SpokeArray extends SpokeBasic implements SpokeItem {
	public SpokeItem[] Parameters;

	public SpokeArray(SpokeItem[] p) {
		Parameters = p;
	}

	public ISpokeItem getIType() {
		return ISpokeItem.Array;
	}

	@Override
	public String toString() {
		return "["
				+ ALH.Aggregate(ALH.ArrayToList(Parameters), "",
						new ALH.Aggregator<SpokeItem, String>() {

							@Override
							public String Accumulate(String accumulate,
									SpokeItem source) {
								// TODO Auto-generated method stub
								return accumulate + source;
							}

						}) + "]";
	}

	public SpokeType Type;

	public SpokeArray(SpokeType t) {
		Type = t;
	}
	IToken[] tokens;
	@Override	public SpokeBasic setTokens(IToken... toks) {		tokens=toks;		return this;	}
	@Override	public IToken[] getTokens() {		return tokens;	}

}