package com.sk89q.craftbook.Spoke.Tokens;

import com.sk89q.craftbook.Spoke.IToken;
import com.sk89q.craftbook.Spoke.SpokeTokenType;
import com.sk89q.craftbook.Spoke.Token;

public class TokenEndOfCodez implements IToken {
	public Token getType() {
		return Token.EndOfCodez;
	}

	public TokenEndOfCodez(int cInd) {
		charIndex=cInd;
	}

	@Override
	public String toString() {
		return "\r\nEOF";
	}
	int charIndex = -1;
	@Override public int getCharacterIndex() {return charIndex;}
	@Override public void setCharacterIndex(int index) {charIndex = index;}
	
	@Override public String htmlToString() {return "</br>";}
	@Override public String getTokenType() {return SpokeTokenType.Token.Name;}
}