// $Id$
/*
 * Copyright (C) 2010, 2011 sk89q <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.craftbook.Spoke;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.print.attribute.standard.MediaSize.Other;

import com.sk89q.craftbook.Spoke.ALH.Aggregator;

import com.sk89q.craftbook.Spoke.ClassInfo.ClassWithLines;
import com.sk89q.craftbook.Spoke.ClassInfo.EnumWithLines;
import com.sk89q.craftbook.Spoke.ClassInfo.GlobalsWithLines;
import com.sk89q.craftbook.Spoke.ClassInfo.MethodWithLines;
import com.sk89q.craftbook.Spoke.SpokeExpressions.ObjectType;
import com.sk89q.craftbook.Spoke.SpokeExpressions.SpokeType;
import com.sk89q.craftbook.Spoke.SpokeExpressions.TokenEnumerator;
import com.sk89q.craftbook.Spoke.SpokeInstructions.ParamEter;
import com.sk89q.craftbook.Spoke.Tokens.*;

/**
 * Plugin for the Spoke Language.
 * 
 * @author Dested
 */
public class SpokeParser {
	static Integer firstHalfOfFloat = Integer.MIN_VALUE;

	public static ArrayList<IToken> getWords(Maintainer<String> bx) {
		ArrayList<IToken> returnTokens = new ArrayList<IToken>();
		boolean wasSpace = false;
		String addToWord = "";

		String b = bx.Actual;
		b = b.replace("    ", "  \t");
		b = b.replace("  ", " ").replace("  ", " ").replace("  ", " ")
				.replace("  ", " ");
		b = b.replace("  \t ", " ");
		b = b.replace("\r\n", "\r");
		b = b.replace("\n", "\r");
		Integer tabCount = 0;
		String[] toks = new String[] { ";", "?", "[", "{", "%", "}", "]", "^",
				"(", "|", ")", "\"", ",", ".", ";", ":", "&", "!", "+", "-",
				"/", "*", "<", ">", "=", "  \t", "\r" };
		boolean breakTilNewLine = false;
		boolean stringStart = false;
		bx.Actual = b;
		for (int i = 0; i < b.length(); i++) {

			if (breakTilNewLine) {
				if (b.charAt(i) == '\r') {
					breakTilNewLine = false;
				}
				continue;

			}
			if (b.charAt(i) != '\t' && tabCount > 0) {
				if (returnTokens.size() > 0
						&& returnTokens.get(returnTokens.size() - 1) instanceof TokenTab)
					((TokenTab) returnTokens.get(returnTokens.size() - 1)).TabIndex += tabCount;
				else
					returnTokens.add(new TokenTab(tabCount, i));
				tabCount = 0;
			}

			if (stringStart && b.charAt(i) != '\"') {

				addToWord += b.charAt(i);
				continue;
			}
			switch (b.charAt(i)) {
			case '$':
				breakTilNewLine = true;
				break;
			case ' ':

				if (wasSpace || addToWord.length() == 0) {
					continue;
				}
				returnTokens.add(addWord(addToWord, i));
				addToWord = "";
				break;
			case '\t':
				tabCount++;
				break;
			default:
				if (!stringStart
						&& ALH.Contains(Arrays.asList(toks),
								Character.toString(b.charAt(i)))
						&& addToWord.length() > 0) {
					Integer fb;
					Integer df;
					if (b.charAt(i) == '.'
							&& (fb = SpokeCommon.IntegerTryParse(addToWord)) > Integer.MIN_VALUE
							&& (df = SpokeCommon.IntegerTryParse(Character
									.toString(b.charAt(i + 1)))) > Integer.MIN_VALUE) {
						i++;
						firstHalfOfFloat = fb;
					} else
						returnTokens.add(addWord(addToWord, i));
					addToWord = "";
				}
				switch (b.charAt(i)) {
				case '[':
					returnTokens.add(new TokenOpenSquare(i));
					break;
				case '{':
					returnTokens.add(new TokenOpenCurly(i));
					break;
				case '}':

					returnTokens.add(new TokenCloseCurly(i));
					break;
				case '|':

					if (b.charAt(i + 1) == '|') {
						i++;
						returnTokens.add(new TokenDoubleOr(i));
					} else
						returnTokens.add(new TokenBar(i));
					break;
				case ']':
					returnTokens.add(new TokenCloseSquare(i));

					break;
				case '!':
					if (b.charAt(i + 1) == '=') {
						i++;
						returnTokens.add(new TokenNotEqual(i));
					} else
						returnTokens.add(new TokenNot(i));

					break;
				case '(':
					returnTokens.add(new TokenOpenParen(i));

					break;
				case ')':
					returnTokens.add(new TokenCloseParen(i));

					break;
				case '%':
					returnTokens.add(new TokenPercent(i));

					break;
				case '\"':

					if (stringStart) {
						returnTokens.add(new TokenString(addToWord, i));
					}
					addToWord = "";
					stringStart = !stringStart;

					break;
				case ',':
					returnTokens.add(new TokenComma(i));

					break;
				case '.':
					returnTokens.add(new TokenPeriod(i));

					break;
				case '+':
					returnTokens.add(new TokenPlus(i));
					break;
				case '-':
					returnTokens.add(new TokenMinus(i));

					break;
				case '/':
					returnTokens.add(new TokenDivide(i));

					break;
				case '*':
					returnTokens.add(new TokenMulitply(i));

					break;
				case '<':
					returnTokens.add(new TokenLess(i));

					break;
				case '>':
					returnTokens.add(new TokenGreater(i));

					break;
				case ';':
					returnTokens.add(new TokenSemiColon(i));

					break;
				case '?':
					returnTokens.add(new TokenQuestionMark(i));

					break;
				case ':':
					returnTokens.add(new TokenColon(i));

					break;
				case '^':
					returnTokens.add(new TokenCarot(i));

					break;
				case '=':

					if (b.charAt(i + 1) == '>') {
						i++;
						returnTokens.add(new TokenAnonMethodStart(i));
					} else if (b.charAt(i + 1) == '=') {
						i++;
						returnTokens.add(new TokenDoubleEqual(i));
					} else
						returnTokens.add(new TokenEqual(i));

					break;

				case '&':

					if (b.charAt(i + 1) == '&') {
						i++;
						returnTokens.add(new TokenDoubleAnd(i));
					} else
						returnTokens.add(new TokenAmpersand(i));

					break;

				case '\r':

					for (int j = returnTokens.size() - 1; j >= 0; j--) {
						if (returnTokens.get(j).getType() == Token.Tab)
							returnTokens.remove(j);
						else
							break;
					}

					returnTokens.add(new TokenNewLine(i));

					break;
				default:

					addToWord += b.charAt(i);

					break;
				}

				break;
			}
		}

		if (addToWord.length() > 0) {

			returnTokens.add(addWord(addToWord, b.length()));

		}

		return returnTokens;
	}

	private static IToken addWord(String g, int i) {
		Integer Integerf;
		if ((Integerf = SpokeCommon.IntegerTryParse(g)) > Integer.MIN_VALUE) {
			if (firstHalfOfFloat > Integer.MIN_VALUE) {
				Integer d = firstHalfOfFloat;
				firstHalfOfFloat = Integer.MIN_VALUE;
				return new TokenFloat(Float.valueOf(d + "." + Integerf), i);
			}
			firstHalfOfFloat = Integer.MIN_VALUE;
			return new TokenInt(Integerf, i);
		}

		if (g.toLowerCase().equals("def")) {
			return new TokenDef(i);
		} else if (g.toLowerCase().equals("create")) {
			return new TokenCreate(i);
		} else if (g.toLowerCase().equals("switch")) {
			return new TokenSwitch(i);
		} else if (g.toLowerCase().equals("enum")) {
			return new TokenEnum(i);
		} else if (g.toLowerCase().equals("macro")) {
			return new TokenMacro(i);
		} else if (g.toLowerCase().equals("return")) {
			return new TokenReturn(i);
		} else if (g.toLowerCase().equals("yield")) {
			return new TokenYield(i);
		} else if (g.toLowerCase().equals("class")) {
			return new TokenClass(i);
		} else if (g.toLowerCase().equals("static")) {
			return new TokenStatic(i);
		} else if (g.toLowerCase().equals("if")) {
			return new TokenIf(i);
		} else if (g.toLowerCase().equals("else")) {
			return new TokenElse(i);
		} else if (g.toLowerCase().equals("false")) {
			return new TokenFalse(i);
		} else if (g.toLowerCase().equals("true")) {
			return new TokenTrue(i);
		} else {
			return new TokenWord(g, i);
		}

	}

	public Tuple5<ArrayList<ClassWithLines>, ArrayList<EnumWithLines>, ArrayList<GlobalsWithLines>, ArrayList<TokenMacroPiece>, IToken[]> Parse(
			Maintainer<String> total) throws Exception {

		ArrayList<IToken> words = getWords(total);
		ArrayList<LineToken> lines = new ArrayList<LineToken>();
		lines.add(new LineToken());

		for (Iterator<IToken> i = words.iterator(); i.hasNext();) {
			IToken n = (IToken) i.next();
			LineToken last = ALH.Last(lines);

			last.Tokens.add(n);

			if (n.getType() == Token.NewLine) {
				if (last.Tokens.size() == 0) {
					continue;
				}

				if (((last.Tokens.get(0).getType() == Token.Tab || last.Tokens
						.get(0).getType() == Token.NewLine) && last.Tokens
						.size() == 1)) {
					last.Tokens.clear();
					continue;
				}
				lines.add(new LineToken());
				continue;
			}
			continue;

		}

		for (Iterator<LineToken> i = lines.iterator(); i.hasNext();) {
			LineToken a = (LineToken) i.next();
			if (!(a.Tokens.size() == 0 || (a.Tokens.get(0).getType() == Token.Class
					|| a.Tokens.get(0).getType() == Token.Enum || a.Tokens.get(
					0).getType() == Token.Tab))) {

				// TODO: BAD

			}
		}

		for (int index = lines.size() - 1; index >= 0; index--) {
			LineToken lineToken = lines.get(index);

			if (lineToken.Tokens.size() == 0) {
				lines.remove(index);
				continue;
			}
			if (lineToken.Tokens.get(lineToken.Tokens.size() - 1) instanceof TokenTab) {
				lineToken.Tokens.remove(lineToken.Tokens.size() - 1);
			}

			if (lineToken.Tokens.get(0).getType() == Token.Tab) {
				if (lineToken.Tokens.size() == 1) {
					lines.remove(index);
					continue;
				}
				if (lineToken.Tokens.get(1).getType() == Token.NewLine) {
					lines.remove(index);
					continue;
				}
				while (true) {
					if (lineToken.Tokens.get(1).getType() == Token.Tab) {
						((TokenTab) lineToken.Tokens.get(0)).TabIndex += ((TokenTab) lineToken.Tokens
								.get(1)).TabIndex;
						lineToken.Tokens.remove(1);
						continue;
					}
					break;
				}

			}
		}

		ArrayList<ArrayList<LineToken>> classes = new ArrayList<ArrayList<LineToken>>();
		ArrayList<ArrayList<LineToken>> enums = new ArrayList<ArrayList<LineToken>>();
		ArrayList<ArrayList<LineToken>> globals = new ArrayList<ArrayList<LineToken>>();

		ArrayList<ArrayList<LineToken>> macros = new ArrayList<ArrayList<LineToken>>();
		int inPiece = 0;

		for (Iterator<LineToken> i = lines.iterator(); i.hasNext();) {
			LineToken n = (LineToken) i.next();
			ArrayList<ArrayList<LineToken>> itms = new ArrayList<ArrayList<LineToken>>();

			if (n.Tokens.size() == 0) {
				continue;
			}
			if (n.Tokens.get(0).getType().equals(Token.Class)) {
				inPiece = 0;
				classes.add(new ArrayList<LineToken>());
			}
			if (n.Tokens.get(0).getType().equals(Token.Enum)) {
				inPiece = 1;
				enums.add(new ArrayList<LineToken>());
			}
			if (n.Tokens.get(0).getType().equals(Token.Macro)) {
				inPiece = 3;
				macros.add(new ArrayList<LineToken>());
			}
			if (n.Tokens.get(0).getType().equals(Token.Word)
					&& ((TokenWord) n.Tokens.get(0)).Word
							.equalsIgnoreCase("global")) {
				inPiece = 2;
				globals.add(new ArrayList<LineToken>());
			}
			switch (inPiece) {
			case 0:
				ALH.Last(classes).add(n);
				break;
			case 1:
				ALH.Last(enums).add(n);
				break;
			case 2:
				ALH.Last(globals).add(n);
				break;
			case 3:
				ALH.Last(macros).add(n);
				break;
			}
			continue;
		}

		ArrayList<EnumWithLines> someEnums = new ArrayList<EnumWithLines>();
		for (Iterator<ArrayList<LineToken>> i = enums.iterator(); i.hasNext();) {
			ArrayList<LineToken> spclass = (ArrayList<LineToken>) i.next();

			EnumWithLines c = new EnumWithLines();
			someEnums.add(c);
			c.Name = ((TokenWord) spclass.get(0).Tokens.get(1)).Word;
			for (int index = 1; index < spclass.size(); index++) {
				LineToken v = spclass.get(index);
				if (v.Tokens.get(0).getType() != Token.Tab)
					throw new Exception("bad");
				c.Variables.add(v);
			}
		}

		ArrayList<GlobalsWithLines> someGlobals = new ArrayList<GlobalsWithLines>();
		for (Iterator<ArrayList<LineToken>> i = globals.iterator(); i.hasNext();) {
			ArrayList<LineToken> spclass = (ArrayList<LineToken>) i.next();

			GlobalsWithLines c = new GlobalsWithLines();
			someGlobals.add(c);
			for (int index = 1; index < spclass.size(); index++) {
				LineToken v = spclass.get(index);
				if (v.Tokens.get(0).getType() != Token.Tab)
					throw new Exception("bad");
				c.Variables.add(v);
			}
		}

		final MacroPart mp = new MacroPart();
		ArrayList<TokenMacroPiece> allMacros = new ArrayList<TokenMacroPiece>();

		for (ArrayList<LineToken> macro : macros) {
			TokenMacroPiece mp1 = new TokenMacroPiece();

			allMacros.add(mp1);

			mp1.Lines = new ArrayList<LineToken>();

			LineToken lm = macro.get(0);
			TokenEnumerator en = new TokenEnumerator(new LineToken[] { lm });
			SpokeCommon.Assert(en.getCurrent().getType() == Token.Macro,
					"notMacro?", en);
			en.MoveNext();

			SpokeCommon.Assert(en.getCurrent().getType() == Token.SemiColon,
					"", en);
			en.MoveNext();
			ArrayList<IToken> mps = new ArrayList<IToken>();

			while (en.getCurrent().getType() != Token.SemiColon) {
				mps.add(en.getCurrent());

				en.MoveNext();
			}
			mp1.Macro = mps.toArray(new IToken[mps.size()]);
			SpokeCommon.Assert(en.getCurrent().getType() == Token.SemiColon,
					"", en);
			en.MoveNext();

			SpokeCommon.Assert(
					en.getCurrent().getType() == Token.AnonMethodStart, "", en);
			en.MoveNext();

			SpokeCommon.Assert(en.getCurrent().getType() == Token.Bar, "", en);
			en.MoveNext();
			SpokeCommon.Assert(en.getCurrent().getType() == Token.OpenParen,
					"", en);
			en.MoveNext();

			ArrayList<ParamEter> parameters_ = new ArrayList<ParamEter>();
			if (en.getCurrent().getType() != Token.CloseParen) {
				while (true) {

					boolean byRef = false;

					if (((TokenWord) en.getCurrent()).Word.toLowerCase() == "ref") {
						byRef = true;
						en.MoveNext();
					}
					parameters_.add(new ParamEter(byRef, ((TokenWord) en
							.getCurrent()).Word));
					en.MoveNext();
					switch (en.getCurrent().getType()) {
					case CloseParen:
						en.MoveNext();
						break;
					case Comma:
						en.MoveNext();
						continue;
					default:
						throw new Exception();
					}
					break;
				}
			}

			mp1.Parameters = parameters_.toArray(new ParamEter[parameters_
					.size()]);

			for (int i = 1; i < macro.size(); i++) {
				mp1.Lines.add(macro.get(i));
			}

		}

		ArrayList<ClassWithLines> someClasses = new ArrayList<ClassWithLines>();
		for (Iterator<ArrayList<LineToken>> i = classes.iterator(); i.hasNext();) {
			ArrayList<LineToken> spclass = (ArrayList<LineToken>) i.next();

			ClassWithLines c = new ClassWithLines();
			someClasses.add(c);
			c.Name = ((TokenWord) spclass.get(0).Tokens.get(1)).Word;

			((TokenWord) spclass.get(0).Tokens.get(1))
					.SetTokenType(SpokeTokenType.Object);

			for (int index = 1; index < spclass.size(); index++) {
				LineToken v = spclass.get(index);
				if (v.Tokens.get(0).getType() != Token.Tab)
					throw new Exception("bad");

				if (v.Tokens.get(1).getType() != Token.Def) {
					c.Variables.add(v);
				} else {
					MethodWithLines m = new MethodWithLines();
					int increase = 0;

					if (v.Tokens.get(2) instanceof TokenStatic) {
						m.Static = true;
						increase = 1;
					}

					if (v.Tokens.get(2 + increase) instanceof TokenOpenParen)
						m.Name = ".ctor";
					else {
						m.Name = ((TokenWord) v.Tokens.get(2 + increase)).Word;

						((TokenWord) v.Tokens.get(2 + increase))
								.SetTokenType(SpokeTokenType.Method);
					}

					if (v.Tokens.size() != 5
							+ increase
							+ (v.Tokens.get(2) instanceof TokenOpenParen ? 0
									: 1))// if it has parameters
					{ // tab def name openP closeP newline

						for (int i1 = 3
								+ increase
								+ (v.Tokens.get(2) instanceof TokenOpenParen ? 0
										: 1); i1 < v.Tokens.size() - 2; i1++) {
							if (m.Static) {

								((TokenWord) v.Tokens.get(i1))
										.SetTokenType(SpokeTokenType.Keyword);

								m.paramNames
										.add(new SpokeMethodParameter(
												NameToType(((TokenWord) v.Tokens
														.get(i1)).Word),
												((TokenWord) v.Tokens
														.get(i1 + 1)).Word));
								i1 += 2;
							} else {
								m.paramNames.add(new SpokeMethodParameter(
										NameToType("unset"),
										((TokenWord) v.Tokens.get(i1)).Word));
								i1 += 1;
							}
						}
					}
					int tabl = ((TokenTab) v.Tokens.get(0)).TabIndex + 1;
					index++;
					for (; index < spclass.size(); index++) {
						if (((TokenTab) spclass.get(index).Tokens.get(0)).TabIndex < tabl) {
							index--;
							break;
						}
						m.Lines.add(spclass.get(index));
					}
					// index++;
					c.Methods.add(m);
				}
			}
		}

		for (Iterator<ClassWithLines> i = someClasses.iterator(); i.hasNext();) {
			ClassWithLines someClass = (ClassWithLines) i.next();
			MethodWithLines ctor = ALH.FirstOrDefault(someClass.Methods,
					new ALH.Finder<MethodWithLines>() {
						@Override
						public boolean Find(MethodWithLines item) {
							return item.Name.equals(".ctor");
						};
					});

			if (ctor == null) {
				someClass.Methods.add(ctor = new MethodWithLines());
				ctor.Name = ".ctor";
			}
			for (int index = 0; index < someClass.Variables.size(); index++) {
				LineToken lineToken = someClass.Variables.get(index);
				((TokenTab) lineToken.Tokens.get(0)).TabIndex++;
				someClass.VariableNames.add(((TokenWord) lineToken.Tokens
						.get(1)).Word);
				ctor.Lines.add(index, lineToken);
			}
			someClass.Variables.clear();

		}

		return new Tuple5<ArrayList<ClassWithLines>, ArrayList<EnumWithLines>, ArrayList<GlobalsWithLines>, ArrayList<TokenMacroPiece>, IToken[]>(
				someClasses, someEnums, someGlobals, allMacros,
				words.toArray(new IToken[words.size()]));

	}

	private SpokeType NameToType(String word) {
		word = word.toLowerCase();
		if (word.equals("unset")) {
			return new SpokeType(ObjectType.Unset);
		} else if (word.equals("null")) {
			return new SpokeType(ObjectType.Null);
		} else if (word.equals("int")) {
			return new SpokeType(ObjectType.Int);
		} else if (word.equals("float")) {
			return new SpokeType(ObjectType.Float);
		} else if (word.equals("string")) {
			return new SpokeType(ObjectType.String);
		} else if (word.equals("bool")) {
			return new SpokeType(ObjectType.Bool);
		} else if (word.equals("array")) {
			return new SpokeType(ObjectType.Array);
		} else if (word.equals("object")) {
			return new SpokeType(ObjectType.Object);
		} else if (word.equals("method")) {
			return new SpokeType(ObjectType.Method);
		} else if (word.equals("void")) {
			return new SpokeType(ObjectType.Void);
		}
		return null;// todo: throw

	}
}