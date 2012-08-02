package com.sk89q.craftbook.Spoke.ClassInfo;

import java.util.ArrayList;

import com.sk89q.craftbook.Spoke.ALH;
import com.sk89q.craftbook.Spoke.IToken;
import com.sk89q.craftbook.Spoke.SpokeMethodParameter;
import com.sk89q.craftbook.Spoke.ALH.Aggregator;
import com.sk89q.craftbook.Spoke.Tokens.LineToken;

public class ClassWithLines {

	public ArrayList<LineToken> Variables = new ArrayList<LineToken>();// only
																		// needed
																		// in
																		// the
																		// creation
																		// process
	public ArrayList<String> VariableNames = new ArrayList<String>();
	public ArrayList<MethodWithLines> Methods = new ArrayList<MethodWithLines>();
	public String Name;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Class " + Name);

		sb.append("  \tVariables:"
				+ ALH.Aggregate(VariableNames, "\r\n",
						new Aggregator<String, String>() {
							@Override
							public String Accumulate(String n, String t) {
								return n + ("  \t  \t" + t + "\r\n");
							}
						}));

		ALH.Aggregate(Methods, "\r\n",
				new Aggregator<MethodWithLines, String>() {
					@Override
					public String Accumulate(String n, MethodWithLines t) {
						return n
								+ ("  \t  \t"
										+ "Method "
										+ t.Name
										+ " ("
										+ ALH.Aggregate(
												t.paramNames,
												"",
												new Aggregator<SpokeMethodParameter, String>() {
													@Override
													public String Accumulate(
															String a,
															SpokeMethodParameter b) {
														return a + b + ",";
													}
												}) + ")\r\n" + ALH.Aggregate(
										t.Lines, "",
										new Aggregator<LineToken, String>() {
											@Override
											public String Accumulate(String a,
													LineToken b) {
												return a
														+ "  \t"
														+ ALH.Aggregate(
																b.Tokens,
																"",
																new Aggregator<IToken, String>() {
																	@Override
																	public String Accumulate(
																			String m,
																			IToken l) {
																		return m
																				+ l.toString();
																	}
																}) + "\r\n";
											}
										}));
					}
				});

		return sb.toString();
	}
}
