package com.sk89q.craftbook.Spoke.Tokens;

import com.sk89q.craftbook.Spoke.IToken;
import com.sk89q.craftbook.Spoke.SpokeTokenType;
import com.sk89q.craftbook.Spoke.Token;

public class TokenTab implements IToken {
	public int TabIndex;

	public Token getType() {
		return Token.Tab;
	}

	public TokenTab(int tabIndex, int cInd) {
		charIndex = cInd;
		TabIndex = tabIndex;
	}

	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < TabIndex; i++) {
			s += "  \t";
		}
		return s;
	}

	int charIndex = -1;

	@Override
	public int getCharacterIndex() {
		return charIndex;
	}

	@Override
	public void setCharacterIndex(int index) {
		charIndex = index;
	}

	@Override
	public String htmlToString() {
		String j = "";

		for (int i = 0; i < TabIndex; i++) {
			j += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		}
		return j;

	}

	@Override
	public String getTokenType() {
		return SpokeTokenType.Token.Name;
	}

}