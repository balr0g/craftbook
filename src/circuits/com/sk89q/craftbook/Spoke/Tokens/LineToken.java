package com.sk89q.craftbook.Spoke.Tokens;

import java.util.ArrayList;

import com.sk89q.craftbook.Spoke.IToken;

public class LineToken {
	public ArrayList<IToken> Tokens;

	public LineToken(ArrayList<IToken> toks) {
		Tokens = toks;
	}

	public LineToken() {
		Tokens = new ArrayList<IToken>();
	}
}
