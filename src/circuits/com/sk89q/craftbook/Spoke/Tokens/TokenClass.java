package com.sk89q.craftbook.Spoke.Tokens;

import com.sk89q.craftbook.Spoke.IToken;
import com.sk89q.craftbook.Spoke.SpokeTokenType;
import com.sk89q.craftbook.Spoke.Token;

public class TokenClass implements IToken {
	public Token getType() {
		return Token.Class;
	}

	public TokenClass(int cInd) {
		charIndex=cInd;
	}

	@Override
	public String toString() {
		return getType().toString() + " ";
	}	int charIndex = -1;
	@Override public int getCharacterIndex() {return charIndex;}
	@Override public void setCharacterIndex(int index) {charIndex = index;}
	
	@Override public String htmlToString() {return "class";}
	@Override public String getTokenType() {return SpokeTokenType.Keyword.Name;}

}