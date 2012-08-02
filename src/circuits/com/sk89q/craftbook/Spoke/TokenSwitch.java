package com.sk89q.craftbook.Spoke;

public class TokenSwitch implements IToken {

	public Token getType() {
		return Token.Switch;
	}

	public TokenSwitch(int cInd) {
		charIndex=cInd;
	}

	@Override
	public String toString() {
		return "switch";
	}
	int charIndex = -1;
	@Override public int getCharacterIndex() {return charIndex;}
	@Override public void setCharacterIndex(int index) {charIndex = index;}
	
	@Override public String htmlToString() {return "switch";}
	@Override public String getTokenType() {return SpokeTokenType.Keyword.Name;}

}
