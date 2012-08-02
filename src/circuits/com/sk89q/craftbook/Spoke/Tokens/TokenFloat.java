package com.sk89q.craftbook.Spoke.Tokens;

import com.sk89q.craftbook.Spoke.IToken;
import com.sk89q.craftbook.Spoke.SpokeTokenType;
import com.sk89q.craftbook.Spoke.Token;

public class TokenFloat implements IToken {
	public final float _value;

	public Token getType() {
		return Token.Float;
	}

	public TokenFloat(float value,int cInd) {
		charIndex=cInd;
		_value = value;
	}

	@Override
	public String toString() {
		return Float.toString(_value);
	}
	int charIndex = -1;
	@Override public int getCharacterIndex() {return charIndex;}
	@Override public void setCharacterIndex(int index) {charIndex = index;}
	
	@Override public String htmlToString() {return _value+"";}
	@Override public String getTokenType() {return SpokeTokenType.Number.Name;}
}