package com.sk89q.craftbook.Spoke.Tokens;

import com.sk89q.craftbook.Spoke.IToken;
import com.sk89q.craftbook.Spoke.SpokeTokenType;
import com.sk89q.craftbook.Spoke.Token;

public class TokenString implements IToken {
	public final String _value;

	public Token getType() {
		return Token.String;
	}

	public TokenString(String value,int cInd) {
		charIndex=cInd;
		_value = value;
	}

	@Override
	public String toString() {
		return "'" + _value + "'";
	}	int charIndex = -1;
	@Override public int getCharacterIndex() {return charIndex;}
	@Override public void setCharacterIndex(int index) {charIndex = index;}
	
	@Override public String htmlToString() {return "\""+_value+"\"";}
	@Override public String getTokenType() {return SpokeTokenType.String.Name;}

}