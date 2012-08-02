package com.sk89q.craftbook.Spoke;

public enum Token {
	OpenSquare('['), CloseSquare(']'), OpenCurly('{'), CloseCurly('}'), OpenParen(
			'('), CloseParen(')'), String('\"'), Comma(')'), Period('.'), Plus(
			'+'), Minus('-'), Divide('/'), Mulitply('*'), Greater('>'), Less(
			'<'), Equal('('), Tab(0), Def(1), Class(2), Create(3), Return(4), Word(
			5), Int(6), Float(7), If(8), Else(9), NewLine(10), DoubleEqual(12), AnonMethodStart(
			11), Bar('|'), Yield(17), EndOfCodez(13), DoubleAnd(15), DoubleOr(
			16), Not('!'), NotEqual(14), True(18), False(19), Ampersand('&'), SemiColon(
			';'), Colon(':'), QuestionMark('?'), Carot('^'), Enum(17), Percent('%'), Macro('M'), Static('S'), Switch('$');

	public int Type;

	Token(int c) {
		Type = c;
	}
}
