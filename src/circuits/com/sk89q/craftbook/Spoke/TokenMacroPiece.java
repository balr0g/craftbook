package com.sk89q.craftbook.Spoke;

import java.util.ArrayList;

import com.sk89q.craftbook.Spoke.SpokeInstructions.ParamEter;
import com.sk89q.craftbook.Spoke.Tokens.LineToken;


public class TokenMacroPiece {
	public IToken[] Macro;
	public ParamEter[] Parameters;
	public ArrayList<LineToken> Lines;

	@Override
	public String toString() {/*
							 * StringBuilder sb = new StringBuilder();
							 * sb.Append("Macro " + this.Macro.Aggregate("",
							 * (m, l) => m + l.ToString()) + "");
							 * 
							 * 
							 * sb.AppendLine(("(" + Parameters.Aggregate("",
							 * (a, b) => a + (b.ByRef ? "ref " : "") +
							 * b.Name + ",") + ")\r\n" + Lines.Aggregate("",
							 * (a, b) => a + "  \t" + b.Tokens.Aggregate("",
							 * (m, l) => m + l.ToString()) + "\r\n")));
							 * 
							 * return sb.ToString();
							 */
		return "";
	}

}
