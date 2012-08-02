package com.sk89q.craftbook.Spoke.SpokeExpressions;

import java.util.ArrayList;
import java.util.Arrays;

import com.sk89q.craftbook.Spoke.IToken;
import com.sk89q.craftbook.Spoke.Tuple2;

public class SpokeSwitch extends SpokeBasic implements SpokeLines, SpokeLine {
	public SpokeItem Condition;
	public Case[] Cases;

	public class Case {
		public SpokeItem Item;
		public SpokeLine[] Lines;
		public boolean NeedsTop;

		public Case(SpokeItem i, SpokeLine[] l) {
			Item = i;
			Lines = l;
		}
	}

	public SpokeSwitch(SpokeItem condition) {
		Condition = condition;
	}

	public SpokeLine[] getLines() {

		ArrayList<SpokeLine> fm = new ArrayList<SpokeLine>();
		for (int i = 0; i < Cases.length; i++) {
			Case j = Cases[i];
			fm.addAll(Arrays.asList(j.Lines));
		}
		return fm.toArray(new SpokeLine[fm.size()]);
	}

	public void setLines(SpokeLine[] lines) {

	}

	@Override
	public String toString() {
		return "switch";
	}

	@Override
	public ISpokeLine getLType() {
		// TODO Auto-generated method stub
		return ISpokeLine.Switch;
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