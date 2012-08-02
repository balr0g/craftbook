package com.sk89q.craftbook.Spoke;

public interface IToken { 
	
		 
        Token getType();
        int getCharacterIndex();
        void setCharacterIndex(int index);
		String htmlToString();
		String getTokenType();
}