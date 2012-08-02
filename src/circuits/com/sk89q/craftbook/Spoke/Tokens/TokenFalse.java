package com.sk89q.craftbook.Spoke.Tokens;

import com.sk89q.craftbook.Spoke.IToken;
import com.sk89q.craftbook.Spoke.SpokeTokenType;
import com.sk89q.craftbook.Spoke.Token;

public class TokenFalse implements IToken {
	public Token getType() {
		return Token.False;
	}

	public TokenFalse(int cInd) {
		charIndex=cInd;
	}

	@Override
	public String toString() {
		return getType().toString() + " ";
	}
	int charIndex = -1;
	@Override public int getCharacterIndex() {return charIndex;}
	@Override public void setCharacterIndex(int index) {charIndex = index;}
	
	@Override public String htmlToString() {return "false";}
	@Override public String getTokenType() {return SpokeTokenType.Boolean.Name;}
}