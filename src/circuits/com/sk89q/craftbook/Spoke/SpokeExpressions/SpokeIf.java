package com.sk89q.craftbook.Spoke.SpokeExpressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.sk89q.craftbook.Spoke.IToken;
import com.sk89q.craftbook.Spoke.Tuple2;

public class SpokeIf extends SpokeBasic implements SpokeLines, SpokeLine {
	public SpokeItem Condition;
	public SpokeLine[] IfLines;
	public SpokeLine[] ElseLines;
	public SpokeIf ElseIf;

	public SpokeIf(SpokeItem condition) {
		Condition = condition;
	}

	public SpokeLine[] getLines() {
		SpokeLine[] lm = new SpokeLine[IfLines.length
				+ (ElseLines == null ? 0 : ElseLines.length)];
		int o = 0;

		for (int i = 0; i < IfLines.length; i++) {
			lm[o++] = IfLines[i];
		}
		if (ElseLines != null)
			for (int i = 0; i < ElseLines.length; i++) {
				lm[o++] = ElseLines[i];
			}
		return lm;
	}

	public void setLines(SpokeLine[] lines) {

	}

	@Override
	public String toString() {
		return "if";
	}

	@Override
	public ISpokeLine getLType() {
		// TODO Auto-generated method stub
		return ISpokeLine.If;
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
