package com.sk89q.craftbook.Spoke.SpokeExpressions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.sk89q.craftbook.Spoke.ALH;
import com.sk89q.craftbook.Spoke.ALH.Finder;
import com.sk89q.craftbook.Spoke.IToken;
import com.sk89q.craftbook.Spoke.Token;
import com.sk89q.craftbook.Spoke.Tokens.*;

public class TokenEnumerator {
	private LineToken[] lines_;

	public TokenEnumerator(LineToken[] lines) {
		lines_ = lines;
	}

	public void Dispose() {
		lines_ = null;
	}

	public TokenEnumerator Clone() {
		LineToken[] ts = new LineToken[lines_.length];
		for (int index = 0; index < lines_.length; index++) {
			LineToken lineToken = lines_[index];
			ts[index] = new LineToken(lineToken.Tokens);
		}

		TokenEnumerator tn = new TokenEnumerator(ts);
		tn.lineIndex = lineIndex;
		tn.tokenIndex = tokenIndex;
		return tn;
	}

	public LineToken getCurrentLine() {

		return lines_[lineIndex];
	}

	public List<LineToken> getCurrentLines() {
		int start = lineIndex + 1;
		ArrayList<LineToken> lmn = new ArrayList<LineToken>(3);
		lmn.add(lines_[lineIndex]);
		int ind = 0;
		boolean done = true;
		while (done) {

			ArrayList<IToken> d = lmn.get(ind).Tokens;
			if (d.get(d.size() - 1).getType() == Token.AnonMethodStart
					|| d.get(d.size() - 2).getType() == Token.AnonMethodStart) {
				int curta = ((TokenTab) lines_[lineIndex].Tokens.get(0)).TabIndex;

				while (true) {
					if (start < lines_.length)
						if (((TokenTab) lines_[start++].Tokens.get(0)).TabIndex == curta) {
							lmn.add(lines_[start - 1]);
							ind++;
							break;
						} else
							continue;

					break;
				}
			}
			break;
		}

		return lmn;

	}

	public IToken[] Next5() {
		ArrayList<IToken> its = new ArrayList<IToken>();
		for (int i = 0; i < 5; i++) {
			its.add(PeakNext(i));
		}
		return its.toArray(new IToken[its.size()]);
	}

	public IToken PeakNext() {
		return PeakNext(1);
	}

	public IToken PeakNext(int g) {
		int peakLineIndex = lineIndex;
		int peakTokenIndex = tokenIndex;
		IToken cur;
		while (true) {
			if (peakLineIndex >= lines_.length) {
				return null;
			}
			if (lines_[peakLineIndex].Tokens.size() - 1 == peakTokenIndex) {
				peakLineIndex++;
				peakTokenIndex = 0;
			} else
				peakTokenIndex++;

			if (peakLineIndex >= lines_.length) {
				return null;
			}
			cur = lines_[peakLineIndex].Tokens.get(peakTokenIndex);
			g--;
			if (g == 0) {
				return cur;
			}
		}

	}	
	
	
	public IToken GetLast(int g) {
		int peakLineIndex = lineIndex;
		int peakTokenIndex = tokenIndex;
		IToken cur;
		while (true) {

			if (0 == peakTokenIndex) {
				peakLineIndex--;
				peakTokenIndex = lines_[peakLineIndex].Tokens.size()-1;
			} else
				peakTokenIndex--;

			if (peakLineIndex >= lines_.length) {
				return null;
			}
			cur = lines_[peakLineIndex].Tokens.get(peakTokenIndex);
			g--;
			if (g == 0) {
				return cur;
			}
		}

	}

	private int lineIndex = 0;
	public int tokenIndex = 0;

	public ArrayList<IToken> getOutstandingLine() {
		ArrayList<IToken> lmn = new ArrayList<IToken>();
		for (int i = tokenIndex; i < lines_[lineIndex].Tokens.size(); i++) {
			lmn.add(lines_[lineIndex].Tokens.get(i));
		}
		int start = lineIndex + 1;

		while (true) {
			boolean bad = true;
			int i;

			if (ALH.LastOrDefault(lmn, new Finder<IToken>() {
				@Override
				public boolean Find(IToken item) {
					return item.getType() != Token.NewLine;
				}
			}) == null) {
				return lmn;
			}

			boolean gotofb = false;

			if (lmn.get(lmn.size() - 1).getType() == Token.AnonMethodStart) {
				int curta = ((TokenTab) lines_[lineIndex].Tokens.get(0)).TabIndex;
				while (true) {
					gotofb = false;
					if (start < lines_.length)
						if (((TokenTab) lines_[start++].Tokens.get(0)).TabIndex == curta) {

							lmn.addAll(lines_[start - 1].Tokens);
							gotofb = true;
							break;
						} else
							continue;
					break;

				}
			}
			if (gotofb) {
				continue;
			}
			break;
		}

		return lmn;

	}

	public boolean MoveNext() {
		if (lines_.length == lineIndex) {
			return true;
		}

		if (lines_[lineIndex].Tokens.size() - 1 == tokenIndex) {
			++lineIndex;
			tokenIndex = 0;
		} else
			tokenIndex++;

		if(curToks.size()>0)
			curToks.get(curToks.size()-1).add(this.getCurrent());

		
		return lineIndex < lines_.length;
	}

	public void Reset() {
		// lineIndex = 0;
		// tokenIndex = 0;
	}

	public IToken getCurrent() {

		if (lines_.length == lineIndex) {

			return new TokenEndOfCodez(lineIndex);
		}

		return lines_[lineIndex].Tokens.get(tokenIndex);

	}

	public void PutBack()

	{
		PutBack(1);
	}

	public void PutBack(int g) {
		while (true) {

			if (tokenIndex == 0) {
				lineIndex--;
				tokenIndex = lines_[lineIndex].Tokens.size() - 1;
			} else {
				tokenIndex--;
			}

			g--;
			if (g == 0) {
				return;
			}
		}

	}

	public IToken getFirstInLine() {
		return lines_[lineIndex].Tokens.get(0);
	}

	public void Set(TokenEnumerator tm) {
		this.tokenIndex = tm.tokenIndex;
		this.lineIndex = tm.lineIndex;
	}

	ArrayList<ArrayList<IToken>> curToks = new ArrayList<ArrayList<IToken>>();

	public TokenEnumerator IncreaseDebugLevel() {
		curToks.add(new ArrayList<IToken>());
		curToks.get(curToks.size() - 1).add(this.getCurrent());
		return this;
	}

	public IToken[] DecreaseDebugLevel() {
		ArrayList<IToken> hm=curToks.get(curToks.size()-1);
			curToks.remove(hm);
		return hm.toArray(new IToken[hm.size()]);
	}

}
