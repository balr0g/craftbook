package com.sk89q.craftbook.Spoke;

public enum SpokeTokenType {
	Token("T"), Variable("V"), Method("M"), Keyword("K"),Boolean("B"),String("S"),Number("B"), Object("O"), Param("P"), Enum("E");

	public String Name;

	SpokeTokenType(String name) {
		Name = name;
	}
}