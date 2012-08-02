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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import org.bukkit.Server;
import com.google.gson.Gson;
import com.sk89q.craftbook.Spoke.ALH.ManySelector;
import com.sk89q.craftbook.Spoke.DebugManager.InterperateRequest;
import com.sk89q.craftbook.Spoke.ClassInfo.ClassWithLines;
import com.sk89q.craftbook.Spoke.ClassInfo.EnumWithLines;
import com.sk89q.craftbook.Spoke.ClassInfo.GlobalsWithLines;
import com.sk89q.craftbook.Spoke.SpokeExpressions.*;
import com.sk89q.craftbook.Spoke.SpokeInstructions.SpokeInstruction;
import com.sk89q.craftbook.Spoke.SpokeInstructions.SpokeInstructionType;
import com.sk89q.craftbook.Spoke.SpokeInstructions.SpokeObject;
import com.sk89q.craftbook.Spoke.Tokens.LineToken;
import com.sk89q.craftbook.Spoke.Tokens.TokenWord;
import com.sk89q.craftbook.bukkit.CircuitsPlugin;

/**
 * Plugin for the Spoke Language.
 * 
 * @author Dested
 */
public class SpokeLoader {

	private CircuitsPlugin plugin;

	public void load(String total, final Server server,
			final CircuitsPlugin plugin, String worldName) throws Exception {
		this.plugin = plugin;
		DebuggerSetup();

		if (internalMethods == null) {
			internalMethods = new SpokeInternalMethod[intMethods.size()];
			intMethods.toArray(internalMethods);
		}
		ArrayList<SpokeMethod> mets;
		Tuple5<ArrayList<ClassWithLines>, ArrayList<EnumWithLines>, ArrayList<GlobalsWithLines>, ArrayList<TokenMacroPiece>, IToken[]> classes = null;
		Tuple3<ArrayList<SpokeClass>, ArrayList<SpokeEnum>, SpokeGlobal> sc;

		SpokeParser sp = new SpokeParser();
		try {
			Maintainer<String> g;
			classes = sp.Parse(g = new Maintainer<String>(total));
			total = g.Actual;
			BuildExpressions builder = new BuildExpressions();
			sc = builder.build(server, plugin, worldName, classes);

			HashMap<String, Tuple2<SpokeType, Integer>> internalMethodTypes = new HashMap<String, Tuple2<SpokeType, Integer>>();

			for (int i = 0; i < internalMethods.length; i++) {
				SpokeInternalMethod sim = internalMethods[i];
				internalMethodTypes.put(
						sim.getMethodName(),
						new Tuple2<SpokeType, Integer>(sim.getMethodType(), sim
								.getMethodIndex()));
			}

			mets = ALH.SelectMany(sc.Item1,
					new ManySelector<SpokeClass, SpokeMethod>() {

						@Override
						public Collection<SpokeMethod> Select(SpokeClass item) {
							return item.Methods;
						}
					});

			SpokePreparse spp = new SpokePreparse(sc, internalMethodTypes, mets);
			spp.Run();
		} catch (SpokeException ec) {
			if (classes == null) {
				throw new Exception(ec.Print(total));
			}
			throw new Exception(ec.Print(total));

		}
		SpokeMethod[] muts = new SpokeMethod[mets.size()];
		mets.toArray(muts);

		SpokeApplication sa = new SpokeApplication(muts, sc.Item3, total,
				classes.Item5);
		sa.PasserInfo = sc;
		allApps.add(sa);

		// SpokeRunner sr = new SpokeRunner(internalMethods, muts);
		// sr.evaluateStaticMethod(0, new SpokeObject[0]);
	}

	private ArrayList<SpokeInternalMethod> intMethods = new ArrayList<SpokeInternalMethod>();

	public void addInternalMethod(SpokeInternalMethod sim) {
		intMethods.add(sim);
	}

	SpokeInternalMethod[] internalMethods;
	public ArrayList<SpokeApplication> allApps = new ArrayList<SpokeApplication>();

	public interface InstructionsFinished {
		public void Do(SpokeObject d);

		public void Bad(SpokeException e, IToken[] file);
	}

	public void Run(String className, String methodName,
			final SpokeObject[] params, final String globalBank,
			final InstructionsFinished finished) throws Exception {

		for (int a = 0; a < allApps.size(); a++) {
			final SpokeApplication sa = allApps.get(a);

			for (int i = 0; i < sa.allMethods.length; i++) {
				SpokeMethod sm = sa.allMethods[i];

				if (sm.Class.Name.equals(className)
						&& sm.MethodName.equals(methodName)) {

					final SpokeRunner sr = new SpokeRunner(sa, internalMethods,
							sa.allMethods, DMH);

					final int gm = i;
					Runnable runn = new Runnable() {

						@Override
						public void run() {
							try {
								sr.SetGlobal(sa.getGlobal(globalBank,
										new GetGlobalObject() {

											@Override
											public SpokeObject evalInstructions(
													SpokeInstruction[] ins) {
												try {
													return sr
															.evaluateInstructions(
																	ins,
																	0,
																	new SpokeObject[0]);
												} catch (SpokeException e) {
													// TODO Auto-generated catch
													// block
													System.out.println((e
															.Print(sa.Tokens)));
												}
												return null;

											}
										}));

								if (CDS.WaitingForDebug)
									finished.Do(sr.evaluateStaticMethodDebug(
											gm, params, CDS));
								else
									finished.Do(sr.evaluateStaticMethod(gm,
											params));
							} catch (SpokeException e) {
								finished.Bad(e, sa.Tokens);
							}
						}
					};
					runn.run();
					// Thread t = new Thread(runn);
					// t.start();

					return;

				}

			}
		}

	}

	public DebugHolder DMH;

	CurrentDebugSetup CDS = new CurrentDebugSetup();

	public class CurrentDebugSetup {
		public String LastDebugger = null;
		public boolean WaitingForDebug = false;
		public DebugState State = DebugState.Run;
		public boolean AppBroken;
		protected SpokeMethod CurrentMethod;
		protected SpokeStackTrace CurrentStackTrace;

	}

	public enum DebugState {
		Run, StepInto, StepOver, StepOut
	}

	private void DebuggerSetup() throws Exception {
		DMH = new DebugHolder() {

			@Override
			public void writeDebugInfo(SpokeStackTrace msg,
					SpokeMethod offendingMethod) {
				if (!CDS.WaitingForDebug)
					return;

				while (CDS.LastDebugger != null) {

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				CDS.CurrentStackTrace = msg;
				Gson g = new Gson();
				CDS.LastDebugger = g.toJson(msg);
				CDS.CurrentMethod = offendingMethod;
			}
		};
		DebugManager.CloseServer();
		DebugManager.Start(1997, new InterperateRequest() {
			@Override
			public String requestRecieved(String request) {
				if (request.toLowerCase().startsWith("getapp/")) {
					String g = request.toLowerCase().replace("getapp/", "");

					for (int a = 0; a < allApps.size(); a++) {
						SpokeApplication sa = allApps.get(a);
						return makeFile(sa.Tokens);
					}
				}

				if (request.toLowerCase().startsWith("run/")) {
					String g = request.toLowerCase().replace("run/", "");
					CDS.State = DebugState.Run;
					for (int a = 0; a < allApps.size(); a++) {
						SpokeApplication sa = allApps.get(a);
						CDS.WaitingForDebug = true;
						CDS.LastDebugger = null;
						while (true) {
							if (CDS.LastDebugger != null) {
								String m = CDS.LastDebugger;

								return m;
							} else {
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}

					}
				}

				if (request.toLowerCase().startsWith("execute/")) {
					String g = request.replace("execute/", "").replace("%22",
							"\"");
					for (int a = 0; a < allApps.size(); a++) {
						try {
							SpokeApplication sa = allApps.get(a);
							ArrayList<IToken> toks = SpokeParser
									.getWords(new Maintainer<String>(g));

							BuildExpressions be = new BuildExpressions();
							Spoke toms = be.eval(new TokenEnumerator(
									new LineToken[] { new LineToken(toks) }),
									0, new evalInformation().CheckMacs(1000));

							HashMap<String, Tuple2<SpokeType, Integer>> internalMethodTypes = new HashMap<String, Tuple2<SpokeType, Integer>>();

							for (int i = 0; i < internalMethods.length; i++) {
								SpokeInternalMethod sim = internalMethods[i];
								internalMethodTypes.put(
										sim.getMethodName(),
										new Tuple2<SpokeType, Integer>(sim
												.getMethodType(), sim
												.getMethodIndex()));
							}
							SpokePreparse sp = new SpokePreparse(sa.PasserInfo,
									internalMethodTypes,
									new ArrayList<SpokeMethod>(Arrays
											.asList(sa.allMethods)));

							SpokeInstruction.Beginner();
							sp.evaluateItem((SpokeItem) toms,
									CDS.CurrentMethod.OnlyMethodParse, false,
									CDS.CurrentMethod.MethodVariableInfo, true);
							new SpokeInstruction(SpokeInstructionType.Return);
							SpokeInstruction[] h = SpokeInstruction.Ender();

							SpokeRunner r = new SpokeRunner(sa,
									internalMethods, sa.allMethods, null);
							SpokeObject gc = r
									.evaluateInstructions(
											h,
											CDS.CurrentStackTrace.Stack[CDS.CurrentStackTrace.Stack.length - 1].Variables);
							return gc.toString();

						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							return "bad " + request;
						}

					}
				}

				if (request.toLowerCase().startsWith("stepinto/")) {
					String g = request.toLowerCase().replace("stepinto/", "");
					CDS.State = DebugState.StepInto;
					CDS.AppBroken = true;

					CDS.LastDebugger = null;

					for (int a = 0; a < allApps.size(); a++) {
						SpokeApplication sa = allApps.get(a);
						CDS.WaitingForDebug = true;
						while (true) {
							if (CDS.LastDebugger != null) {
								String m = CDS.LastDebugger;
								return m;
							} else {
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}

					}
				}

				if (request.toLowerCase().startsWith("stepover/")) {
					String g = request.toLowerCase().replace("stepover/", "");
					CDS.State = DebugState.StepOver;

					for (int a = 0; a < allApps.size(); a++) {
						SpokeApplication sa = allApps.get(a);
						CDS.WaitingForDebug = true;
						CDS.LastDebugger = null;
						while (true) {
							if (CDS.LastDebugger != null) {
								String m = CDS.LastDebugger;
								return m;
							} else {
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
				}

				if (request.toLowerCase().startsWith("stepout/")) {
					String g = request.toLowerCase().replace("stepout/", "");
					CDS.State = DebugState.StepOut;

					for (int a = 0; a < allApps.size(); a++) {
						SpokeApplication sa = allApps.get(a);
						CDS.WaitingForDebug = true;
						CDS.LastDebugger = null;

						while (true) {
							if (CDS.LastDebugger != null) {
								String m = CDS.LastDebugger;

								return m;
							} else {
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}

					}
				}

				return request;
			}
		}, OpenFile("World.html"));
	}

	public String makeFile(IToken[] total) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < total.length; i++) {
			IToken iToken = total[i];

			String g = "";
			if (iToken instanceof TokenWord
					&& ((TokenWord) iToken).VariableIndex > -1) {
				g = "v" + ((TokenWord) iToken).VariableIndex;
			}
			sb.append("<span class='" + g + " " + iToken.getTokenType() + " t"
					+ i + "'>" + iToken.htmlToString() + "</span>");

			if (iToken.getTokenType().equals(SpokeTokenType.Keyword.Name)) {
				sb.append("&nbsp;");
			}
		}
		return sb.toString();

	}

	private String OpenFile(String fileName) {
		String total = "";
		try {

			// Open the file that is the first
			// command line parameter
			File actual = new File(plugin.getDataFolder(), fileName);

			FileInputStream fstream = new FileInputStream(actual);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				total += strLine + "\r\n";
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return total;
	}

	public class Player {
		public String Name;
		public String FullName;
		public boolean IsReady = false;

		public Player(String fullname) {
			FullName = fullname;
			Name = FullName.split("/")[FullName.split("/").length - 1];
		}
	}

	public SpokeMethod getMethod(String className, String methodName)
			throws Exception {

		for (int a = 0; a < allApps.size(); a++) {
			SpokeApplication sa = allApps.get(a);

			for (int i = 0; i < sa.allMethods.length; i++) {
				SpokeMethod sm = sa.allMethods[i];

				if (sm.Class.Name.equals(className)
						&& sm.MethodName.equals(methodName)) {
					return sm;
				}

			}
		}

		return null;
	}

	public void loadGlobalsFile(String total) {
		// allApps.get(0).loadGlobalsFile(total);

	}
}