package com.sk89q.craftbook.Spoke.Tokens;

import com.sk89q.craftbook.Spoke.IToken;
import com.sk89q.craftbook.Spoke.SpokeTokenType;
import com.sk89q.craftbook.Spoke.Token;

public class TokenWord implements IToken {
	public String Word;

	public Token getType() {
		return Token.Word;
	}

	public TokenWord(String word, int cInd) {
		charIndex = cInd;
		Word = word;
	}

	@Override
	public String toString() {
		return Word;
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
		return Word;
	}

	public void SetTokenType(SpokeTokenType v) {
		vType = v;
	}

	@Override
	public String getTokenType() {
		return vType.Name;
	}

	SpokeTokenType vType=SpokeTokenType.Variable;
	public int VariableIndex=-1;

}