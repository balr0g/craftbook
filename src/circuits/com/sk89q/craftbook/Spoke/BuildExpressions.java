package com.sk89q.craftbook.Spoke;

import org.bukkit.material.Button;
import org.bukkit.craftbukkit.block.CraftSign;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.print.attribute.standard.MediaSize.Other;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.*;

import com.sk89q.craftbook.Spoke.ALH.Aggregator;
import com.sk89q.craftbook.Spoke.ALH.Finder;
import com.sk89q.craftbook.Spoke.ClassInfo.ClassWithLines;
import com.sk89q.craftbook.Spoke.ClassInfo.EnumWithLines;
import com.sk89q.craftbook.Spoke.ClassInfo.GlobalsWithLines;
import com.sk89q.craftbook.Spoke.ClassInfo.MethodWithLines;
import com.sk89q.craftbook.Spoke.SpokeExpressions.*;
import com.sk89q.craftbook.Spoke.SpokeInstructions.ParamEter;
import com.sk89q.craftbook.Spoke.SpokeInstructions.SpokeObject;
import com.sk89q.craftbook.Spoke.Tokens.*;
import com.sk89q.craftbook.bukkit.AsyncBlockChange;
import com.sk89q.craftbook.bukkit.CircuitsPlugin;
import com.sun.net.ssl.internal.ssl.Debug;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

public class BuildExpressions {

	private String CurMethodName;

	public Tuple3<ArrayList<SpokeClass>, ArrayList<SpokeEnum>, SpokeGlobal> build(
			Server server,
			final CircuitsPlugin plugin,
			String worldName,
			Tuple5<ArrayList<ClassWithLines>, ArrayList<EnumWithLines>, ArrayList<GlobalsWithLines>, ArrayList<TokenMacroPiece>, IToken[]> classes2)
			throws SpokeException {

		allMacros_ = classes2.Item4;
		if (allMacros_ == null) {
			allMacros_ = new ArrayList<TokenMacroPiece>();
		}
		final World world = server.getWorld(worldName);
		ArrayList<SpokeClass> classes = new ArrayList<SpokeClass>();
		ArrayList<SpokeGlobal> globs = new ArrayList<SpokeGlobal>();

		SpokeClass cl = new SpokeClass();
		classes.add(cl);

		cl.Name = "Array";
		cl.Methods = new ArrayList<SpokeMethod>(
				Arrays.asList(new SpokeMethod[] {
						new SpokeMethod(
								".ctor",
								cl,
								new SpokeType(ObjectType.Object),
								new SpokeMethodParameter[] { new SpokeMethodParameter(
										"this",
										new SpokeType(ObjectType.Object)) },
								new SpokeInternalMethod() {
									@Override
									public SpokeObject Evaluate(
											SpokeObject[] parameters) {
										return parameters[0];
									}

									@Override
									public String getMethodName() {
										return ".ctor";
									}

									@Override
									public SpokeType getMethodType() {
										return new SpokeType(ObjectType.Object);
									}

									@Override
									public Integer getMethodIndex() {
										return 0;
									}
								}),

						new SpokeMethod("add", cl, new SpokeType(
								ObjectType.Void), new SpokeMethodParameter[] {
								new SpokeMethodParameter("this", new SpokeType(
										ObjectType.Array)),
								new SpokeMethodParameter("v", new SpokeType(
										ObjectType.Object)) },
								new SpokeInternalMethod() {
									@Override
									public SpokeObject Evaluate(
											SpokeObject[] parameters) {
										parameters[0].ArrayItems
												.add(parameters[1]);
										return null;
									}

									@Override
									public String getMethodName() {
										return "add";
									}

									@Override
									public SpokeType getMethodType() {
										return new SpokeType(ObjectType.Void);
									}

									@Override
									public Integer getMethodIndex() {
										return 1;
									}
								})

						,
						new SpokeMethod(
								"clear",
								cl,
								new SpokeType(ObjectType.Void),
								new SpokeMethodParameter[] { new SpokeMethodParameter(
										"this", new SpokeType(ObjectType.Array)) },
								new SpokeInternalMethod() {
									@Override
									public SpokeObject Evaluate(
											SpokeObject[] parameters) {
										parameters[0].ArrayItems.clear();
										return null;
									}

									@Override
									public String getMethodName() {
										return "clear";
									}

									@Override
									public SpokeType getMethodType() {
										return new SpokeType(ObjectType.Void);
									}

									@Override
									public Integer getMethodIndex() {
										return 2;
									}
								})

						,
						new SpokeMethod(
								"length",
								cl,
								new SpokeType(ObjectType.Int),
								new SpokeMethodParameter[] { new SpokeMethodParameter(
										"this", new SpokeType(ObjectType.Array)) },
								new SpokeInternalMethod() {
									@Override
									public SpokeObject Evaluate(
											SpokeObject[] parameters) {
										return new SpokeObject(
												parameters[0].ArrayItems.size());
									}

									@Override
									public String getMethodName() {
										return "length";
									}

									@Override
									public SpokeType getMethodType() {
										return new SpokeType(ObjectType.Int);
									}

									@Override
									public Integer getMethodIndex() {
										return 3;
									}
								})

						,
						new SpokeMethod("remove", cl, new SpokeType(
								ObjectType.Void), new SpokeMethodParameter[] {
								new SpokeMethodParameter("this", new SpokeType(
										ObjectType.Array)),
								new SpokeMethodParameter("v", new SpokeType(
										ObjectType.Object)) },
								new SpokeInternalMethod() {
									@Override
									public SpokeObject Evaluate(
											SpokeObject[] parameters) {
										// TODO: a[0].ArrayItems.RemoveAll(b =>
										// SpokeObject.Compare(b,a[1]));
										;
										return null;
									}

									@Override
									public String getMethodName() {
										return "remove";
									}

									@Override
									public SpokeType getMethodType() {
										return new SpokeType(ObjectType.Void);
									}

									@Override
									public Integer getMethodIndex() {
										return 4;
									}
								})

						,
						new SpokeMethod(
								"last",
								cl,
								new SpokeType(ObjectType.Object),
								new SpokeMethodParameter[] { new SpokeMethodParameter(
										"this", new SpokeType(ObjectType.Array)) },
								new SpokeInternalMethod() {
									@Override
									public SpokeObject Evaluate(
											SpokeObject[] parameters) {
										return parameters[0].ArrayItems
												.get(parameters[0].ArrayItems
														.size() - 1);
									}

									@Override
									public String getMethodName() {
										return "last";
									}

									@Override
									public SpokeType getMethodType() {
										return new SpokeType(ObjectType.Object);
									}

									@Override
									public Integer getMethodIndex() {
										return 5;
									}
								})

						,
						new SpokeMethod(
								"first",
								cl,
								new SpokeType(ObjectType.Object),
								new SpokeMethodParameter[] { new SpokeMethodParameter(
										"this", new SpokeType(ObjectType.Array)) },
								new SpokeInternalMethod() {
									@Override
									public SpokeObject Evaluate(
											SpokeObject[] parameters) {
										return (parameters[0].ArrayItems.get(0));
									}

									@Override
									public String getMethodName() {
										return "first";
									}

									@Override
									public SpokeType getMethodType() {
										return new SpokeType(ObjectType.Object);
									}

									@Override
									public Integer getMethodIndex() {
										return 6;
									}
								})

						,
						new SpokeMethod("insert", cl, new SpokeType(
								ObjectType.Void), new SpokeMethodParameter[] {
								new SpokeMethodParameter("this", new SpokeType(
										ObjectType.Array)),
								new SpokeMethodParameter("v", new SpokeType(
										ObjectType.Int)),
								new SpokeMethodParameter("v2", new SpokeType(
										ObjectType.Object)) },
								new SpokeInternalMethod() {
									@Override
									public SpokeObject Evaluate(
											SpokeObject[] parameters) {
										parameters[0].ArrayItems.add(
												parameters[1].IntVal,
												parameters[2]);
										return null;
									}

									@Override
									public String getMethodName() {
										return "insert";
									}

									@Override
									public SpokeType getMethodType() {
										return new SpokeType(ObjectType.Void);
									}

									@Override
									public Integer getMethodIndex() {
										return 7;
									}
								}),
						new SpokeMethod("contains", cl, new SpokeType(
								ObjectType.Bool), new SpokeMethodParameter[] {
								new SpokeMethodParameter("this", new SpokeType(
										ObjectType.Array)),
								new SpokeMethodParameter("v", new SpokeType(
										ObjectType.Object)) },
								new SpokeInternalMethod() {
									@Override
									public SpokeObject Evaluate(
											SpokeObject[] parameters) {

										for (SpokeObject s : parameters[0].ArrayItems) {
											if (s.Compare(parameters[1])) {
												return new SpokeObject(true);
											}
										}
										return new SpokeObject(false);
									}

									@Override
									public String getMethodName() {
										return "contains";
									}

									@Override
									public SpokeType getMethodType() {
										return new SpokeType(ObjectType.Bool);
									}

									@Override
									public Integer getMethodIndex() {
										return 8;
									}
								}),
						new SpokeMethod("indexOf", cl, new SpokeType(
								ObjectType.Int), new SpokeMethodParameter[] {
								new SpokeMethodParameter("this", new SpokeType(
										ObjectType.Array)),
								new SpokeMethodParameter("v", new SpokeType(
										ObjectType.Object)) },
								new SpokeInternalMethod() {
									@Override
									public SpokeObject Evaluate(
											SpokeObject[] parameters) {
										int index = 0;
										for (SpokeObject s : parameters[0].ArrayItems) {
											if (s.Compare(parameters[1])) { 
												return new SpokeObject(index);
											}
											index++;
										} 
										return new SpokeObject(-1);
									}

									@Override
									public String getMethodName() {
										return "indexOf";
									}

									@Override
									public SpokeType getMethodType() {
										return new SpokeType(ObjectType.Int);
									}

									@Override
									public Integer getMethodIndex() {
										return 9;
									}
								})

				}));

		cl = new SpokeClass();
		classes.add(cl);
		cl.Name = "Sign";
		cl.Variables = new String[1];
		cl.Variables[0] = "signData";

		cl.Methods = new ArrayList<SpokeMethod>(
				Arrays.asList(new SpokeMethod[] {
						new SpokeMethod(
								".ctor",
								cl,
								new SpokeType(ObjectType.Object),
								new SpokeMethodParameter[] { new SpokeMethodParameter(
										"this",
										new SpokeType(ObjectType.Object)) },
								new SpokeInternalMethod() {
									@Override
									public SpokeObject Evaluate(
											SpokeObject[] parameters) {
										return parameters[0];
									}

									@Override
									public String getMethodName() {
										return ".ctor";
									}

									@Override
									public SpokeType getMethodType() {
										return new SpokeType(ObjectType.Object);
									}

									@Override
									public Integer getMethodIndex() {
										return 0;
									}
								}),
						new SpokeMethod(
								"turnIntoIC",
								cl,
								new SpokeType(ObjectType.Object),
								new SpokeMethodParameter[] { new SpokeMethodParameter(
										"this",
										new SpokeType(ObjectType.Object)) },
								new SpokeInternalMethod() {
									@Override
									public SpokeObject Evaluate(
											SpokeObject[] parameters) {
										return parameters[0];
									}

									@Override
									public String getMethodName() {
										return "turnIntoIC";
									}

									@Override
									public SpokeType getMethodType() {
										return new SpokeType(ObjectType.Object);
									}

									@Override
									public Integer getMethodIndex() {
										return 1;
									}
								}), }));

		cl = new SpokeClass();
		classes.add(cl);
		cl.Name = "Block";
		cl.Variables = new String[1];
		cl.Variables[0] = "blockData";

		cl.Methods = new ArrayList<SpokeMethod>(
				Arrays.asList(new SpokeMethod[] {
						new SpokeMethod(
								".ctor",
								cl,
								new SpokeType(ObjectType.Object),
								new SpokeMethodParameter[] { new SpokeMethodParameter(
										"this",
										new SpokeType(ObjectType.Object)) },
								new SpokeInternalMethod() {
									@Override
									public SpokeObject Evaluate(
											SpokeObject[] parameters) {
										return parameters[0];
									}

									@Override
									public String getMethodName() {
										return ".ctor";
									}

									@Override
									public SpokeType getMethodType() {
										return new SpokeType(ObjectType.Object);
									}

									@Override
									public Integer getMethodIndex() {
										return 0;
									}
								}),
						new SpokeMethod(
								"getType",
								cl,
								new SpokeType(ObjectType.Int),
								new SpokeMethodParameter[] { new SpokeMethodParameter(
										"this",
										new SpokeType(ObjectType.Object)) },
								new SpokeInternalMethod() {
									@Override
									public SpokeObject Evaluate(
											SpokeObject[] parameters) {
										return new SpokeObject(
												((Block) parameters[0].Variables[0].javaObject)
														.getType().getId());

									}

									@Override
									public String getMethodName() {
										return "getType";
									}

									@Override
									public SpokeType getMethodType() {
										return new SpokeType(ObjectType.Int);
									}

									@Override
									public Integer getMethodIndex() {
										return 1;
									}
								}),
						new SpokeMethod("setType", cl, new SpokeType(
								ObjectType.Void), new SpokeMethodParameter[] {
								new SpokeMethodParameter("this", new SpokeType(
										ObjectType.Object)),
								new SpokeMethodParameter("v", new SpokeType(
										ObjectType.Int)) },
								new SpokeInternalMethod() {
									@Override
									public SpokeObject Evaluate(
											SpokeObject[] parameters) {
										plugin.getScheduler()
												.push(((Block) parameters[0].Variables[0].javaObject)
														.getLocation(),
														parameters[1].IntVal);
										return null;
									}

									@Override
									public String getMethodName() {
										return "setType";
									}

									@Override
									public SpokeType getMethodType() {
										return new SpokeType(ObjectType.Void);
									}

									@Override
									public Integer getMethodIndex() {
										return 2;
									}
								}),
						new SpokeMethod("makeSign", cl, new SpokeType(
								ObjectType.Void), new SpokeMethodParameter[] {
								new SpokeMethodParameter("this", new SpokeType(
										ObjectType.Object)),
								new SpokeMethodParameter("v", new SpokeType(
										ObjectType.Array, new SpokeType(
												ObjectType.String))) },
								new SpokeInternalMethod() {
									@Override
									public SpokeObject Evaluate(
											SpokeObject[] parameters) {
										String[] lines = new String[4];

										for (int i = 0; i < parameters[1].ArrayItems
												.size(); i++) {
											SpokeObject line = parameters[1].ArrayItems
													.get(i);
											lines[i] = line.StringVal;
										}

										plugin.getScheduler()
												.pushAsyncChange(
														new AsyncBlockChange(
																((Block) parameters[0].Variables[0].javaObject)
																		.getLocation(),
																68, (byte) 0x3,
																(byte) 1, lines));

										SpokeObject so = new SpokeObject(
												ObjectType.Object,
												((Block) parameters[0].Variables[0].javaObject));
										so.ClassName = "Sign";
										return so;
									}

									@Override
									public String getMethodName() {
										return "makeSign";
									}

									@Override
									public SpokeType getMethodType() {
										return new SpokeType(ObjectType.Object,
												"Sign");
									}

									@Override
									public Integer getMethodIndex() {
										return 3;
									}
								}),
						new SpokeMethod("makeStoneButton", cl, new SpokeType(
								ObjectType.Void), new SpokeMethodParameter[] {
								new SpokeMethodParameter("this", new SpokeType(
										ObjectType.Object)),
								new SpokeMethodParameter("v", new SpokeType(
										ObjectType.Int)) },
								new SpokeInternalMethod() {
									@Override
									public SpokeObject Evaluate(
											SpokeObject[] parameters) {

										BlockFace f = null;
										switch (parameters[1].IntVal) {
										case 0:
											f = BlockFace.NORTH;
											break;
										case 1:
											f = BlockFace.WEST;
											break;
										case 2:
											f = BlockFace.SOUTH;
											break;
										case 3:
											f = BlockFace.EAST;
											break;
										}

										plugin.getScheduler()
												.pushAsyncChange(
														new AsyncBlockChange(
																((Block) parameters[0].Variables[0].javaObject)
																		.getLocation(),
																org.bukkit.Material.STONE_BUTTON,
																f));

										return null;
									}

									@Override
									public String getMethodName() {
										return "makeSign";
									}

									@Override
									public SpokeType getMethodType() {
										return new SpokeType(ObjectType.Object,
												"Sign");
									}

									@Override
									public Integer getMethodIndex() {
										return 3;
									}
								}),

						new SpokeMethod("setTypeAndData", cl, new SpokeType(
								ObjectType.Void), new SpokeMethodParameter[] {
								new SpokeMethodParameter("this", new SpokeType(
										ObjectType.Object)),
								new SpokeMethodParameter("v", new SpokeType(
										ObjectType.Int)),
								new SpokeMethodParameter("v2", new SpokeType(
										ObjectType.Int)) },
								new SpokeInternalMethod() {
									@Override
									public SpokeObject Evaluate(
											SpokeObject[] parameters) {

										plugin.getScheduler()
												.push(((Block) parameters[0].Variables[0].javaObject)
														.getLocation(),
														parameters[1].IntVal,
														(byte) parameters[2].IntVal);

										return null;
									}

									@Override
									public String getMethodName() {
										return "setTypeAndData";
									}

									@Override
									public SpokeType getMethodType() {
										return new SpokeType(ObjectType.Void);
									}

									@Override
									public Integer getMethodIndex() {
										return 3;
									}
								})

				}));

		cl = new SpokeClass();
		classes.add(cl);
		cl.Name = "World";

		cl.Methods = new ArrayList<SpokeMethod>(
				Arrays.asList(new SpokeMethod[] {
						new SpokeMethod(
								".ctor",
								cl,
								new SpokeType(ObjectType.Object),
								new SpokeMethodParameter[] { new SpokeMethodParameter(
										"this",
										new SpokeType(ObjectType.Object)) },
								new SpokeInternalMethod() {
									@Override
									public SpokeObject Evaluate(
											SpokeObject[] parameters) {
										return parameters[0];
									}

									@Override
									public String getMethodName() {
										return ".ctor";
									}

									@Override
									public SpokeType getMethodType() {
										return new SpokeType(ObjectType.Object);
									}

									@Override
									public Integer getMethodIndex() {
										return 0;
									}
								}),

						new SpokeMethod(
								"getBlockAt",
								cl,
								new SpokeType(ObjectType.Object, "Block"),
								new SpokeMethodParameter[] {
										new SpokeMethodParameter("this",
												new SpokeType(ObjectType.Array)),
										new SpokeMethodParameter("v1",
												new SpokeType(ObjectType.Int)),
										new SpokeMethodParameter("v2",
												new SpokeType(ObjectType.Int)),
										new SpokeMethodParameter("v3",
												new SpokeType(ObjectType.Int)) },
								new SpokeInternalMethod() {
									@Override
									public SpokeObject Evaluate(
											SpokeObject[] parameters) {
										SpokeObject jc = new SpokeObject(
												new SpokeObject[] { new SpokeObject(
														ObjectType.Object,
														world.getBlockAt(
																parameters[1].IntVal,
																parameters[2].IntVal,
																parameters[3].IntVal)) });

										jc.ClassName = "Block";
										return jc;
									}

									@Override
									public String getMethodName() {
										return "getBlockAt";
									}

									@Override
									public SpokeType getMethodType() {
										return new SpokeType(ObjectType.Object,
												"Block");
									}

									@Override
									public Integer getMethodIndex() {
										return 1;
									}
								})

				}));

		ArrayList<SpokeEnum> enums = new ArrayList<SpokeEnum>();

		for (Iterator<EnumWithLines> i = classes2.Item2.iterator(); i.hasNext();) {
			EnumWithLines enumWithLines = (EnumWithLines) i.next();

			ObjectType ot = null;

			switch (enumWithLines.Variables.get(0).Tokens.get(3).getType()) {
			case Int:
				ot = ObjectType.Int;
				break;
			case String:
				ot = ObjectType.String;
				break;
			case Float:
				ot = ObjectType.Float;
				break;
			}

			SpokeEnum se = new SpokeEnum(enumWithLines.Name, ot);
			enums.add(se);
			for (LineToken variable : enumWithLines.Variables) {

				switch (variable.Tokens.get(3).getType()) {
				case Int:
					se.addVariable(((TokenWord) variable.Tokens.get(1)).Word,
							((TokenInt) variable.Tokens.get(3))._value);
					break;
				case String:
					se.addVariable(((TokenWord) variable.Tokens.get(1)).Word,
							((TokenString) variable.Tokens.get(3))._value);
					break;
				case Float:
					se.addVariable(((TokenWord) variable.Tokens.get(1)).Word,
							((TokenFloat) variable.Tokens.get(3))._value);
					break;
				}

			}
		}

		for (Iterator<ClassWithLines> i = classes2.Item1.iterator(); i
				.hasNext();) {
			ClassWithLines classWithLines = (ClassWithLines) i.next();

			classes.add(cl = new SpokeClass());
			cl.Variables = classWithLines.VariableNames
					.toArray(new String[classWithLines.VariableNames.size()]);
			cl.Name = classWithLines.Name;

			for (Iterator<MethodWithLines> ic = classWithLines.Methods
					.iterator(); ic.hasNext();) {
				MethodWithLines method = (MethodWithLines) ic.next();

				SpokeMethod me;
				cl.Methods.add(me = new SpokeMethod(method.Name));

				me.Static = method.Static;
				CurMethodName = method.Name;
				if (!method.Static) {
					me.Parameters = new SpokeMethodParameter[method.paramNames
							.size() + 1];
					me.Parameters[0] = new SpokeMethodParameter(new SpokeType(
							ObjectType.Object, cl.Name), "this");
					for (int index = 0; index < method.paramNames.size(); index++) {
						me.Parameters[index + 1] = new SpokeMethodParameter(
								method.paramNames.get(index).Type,
								method.paramNames.get(index).Name);
					}
				} else {
					me.Parameters = new SpokeMethodParameter[method.paramNames
							.size()];
					for (int index = 0; index < method.paramNames.size(); index++) {
						me.Parameters[index] = new SpokeMethodParameter(
								method.paramNames.get(index).Type,
								method.paramNames.get(index).Name);
					}
				}
				me.Class = cl;

				TokenEnumerator enumerator = new TokenEnumerator(
						method.Lines.toArray(new LineToken[method.Lines.size()]));
				ArrayList<SpokeLine> gc = null;
				try {
					gc = getLines(enumerator, 2, new evalInformation());
				} catch (SpokeException ec) {

					ec.printStackTrace();
					throw ec;
				}

				me.Lines = gc.toArray(new SpokeLine[gc.size()]);

				me.HasYieldReturn = linesHave(me.Lines, ISpokeLine.YieldReturn);
				me.HasYield = linesHave(me.Lines, ISpokeLine.Yield);
				me.HasReturn = linesHave(me.Lines, ISpokeLine.Return);

			}
		}

		int ind = 0;
		for (Iterator<GlobalsWithLines> i = classes2.Item3.iterator(); i
				.hasNext();) {
			GlobalsWithLines globalWithLines = (GlobalsWithLines) i.next();
			SpokeGlobal gl;
			globs.add(gl = new SpokeGlobal());

			for (Iterator<LineToken> ic = globalWithLines.Variables.iterator(); ic
					.hasNext();) {
				LineToken method = (LineToken) ic.next();
				String varName;
				Spoke val;
				method.Tokens.add(new TokenNewLine(-1));
				TokenEnumerator enumerator = new TokenEnumerator(
						new LineToken[] { method });

				SpokeCommon.Assert(
						enumerator.getCurrent().getType() == Token.Tab,
						"bad global", enumerator);
				enumerator.MoveNext();
				varName = ((TokenWord) enumerator.getCurrent()).Word;
				enumerator.MoveNext();
				SpokeCommon.Assert(
						enumerator.getCurrent().getType() == Token.Equal,
						"bad global equal: " + varName, enumerator);
				enumerator.MoveNext();

				try {
					val = eval(enumerator, 2, new evalInformation());
				} catch (Exception ec) {
					ec.printStackTrace();
					continue;
				}

				SpokeGlobalVariable gv;
				gl.Variables.add(gv = new SpokeGlobalVariable());
				gv.Name = varName;
				gv.SpokeItem = val;
				gv.Index = ind++;

			}
		}
		return new Tuple3<ArrayList<SpokeClass>, ArrayList<SpokeEnum>, SpokeGlobal>(
				classes, enums, ALH.Aggregate(globs, new SpokeGlobal(),
						new Aggregator<SpokeGlobal, SpokeGlobal>() {
							@Override
							public SpokeGlobal Accumulate(
									SpokeGlobal accumulate, SpokeGlobal source) {
								accumulate.Variables.addAll(source.Variables);
								return accumulate;
							}
						}));

	}

	private boolean linesHave(SpokeLine[] lines, ISpokeLine r) {
		for (int index = 0; index < lines.length; index++) {
			SpokeLine e = lines[index];
			if (e.getLType() == r) {
				return true;
			}
			if (e instanceof SpokeLines
					&& (!(e instanceof SpokeAnonMethod) || (r == ISpokeLine.Return))) {

				if (e instanceof SpokeAnonMethod) {
					if (linesHave(((SpokeLines) e).getLines(),
							ISpokeLine.Return)
							|| linesHave(((SpokeLines) e).getLines(),
									ISpokeLine.Yield)
							|| linesHave(((SpokeLines) e).getLines(),
									ISpokeLine.YieldReturn)) {
						return true;
					}
				} else if (linesHave(((SpokeLines) e).getLines(), r)) {
					return true;
				}
			}

		}
		return false;
	}

	private SpokeItem CurrentItem;

	public ArrayList<SpokeLine> getLines(TokenEnumerator enumerator,
			int tabIndex, evalInformation inf) throws SpokeException {
		int lineIndex = 0;
		ArrayList<SpokeLine> lines = new ArrayList<SpokeLine>();

		while (true) {

			IToken token = enumerator.getCurrent();

			if (token == null || token.getType() == Token.EndOfCodez) {
				return lines;
			}

			if (token.getType() == Token.NewLine) {
				enumerator.MoveNext();
				continue;
			}

			if (token.getType() == Token.Tab
					&& enumerator.PeakNext().getType() == Token.NewLine) {
				enumerator.MoveNext();
				enumerator.MoveNext();
				continue;
			}

			if (!(token instanceof TokenTab)) {
				throw new SpokeException(token);
			}

			if (((TokenTab) token).TabIndex < tabIndex) {
				enumerator.PutBack();
				return lines;
			}

			SpokeCommon.Assert(((TokenTab) token).TabIndex == tabIndex,
					"Bad Tab", enumerator);

			enumerator.MoveNext();

			if (enumerator.getCurrent().getType() == Token.NewLine) {
				enumerator.MoveNext();
				continue;
			}

			CurrentItem = null;

			Spoke s;
			try {
				s = eval(
						enumerator,
						tabIndex,
						new evalInformation(inf).EatTab(false).ResetCurrentVal(
								true));
			} catch (SpokeException e) {
				throw e;
			} catch (Exception e) {
				throw new SpokeException(e, enumerator.getCurrent());
			}
			if (s instanceof SpokeLine) {
				lines.add((SpokeLine) s);
			} else {
				throw new SpokeException(enumerator.getCurrent());/*
																 * SpokeException(
																 * "Build",
																 * "problem on line "
																 * +
																 * lines.size()
																 * +
																 * "   line is"
																 * +
																 * s.getClass()
																 * .getName() +
																 * " Method name="
																 * +
																 * CurMethodName
																 * +
																 * "  tab index="
																 * + tabIndex);
																 */
			}
		}
	}

	public Spoke eval(TokenEnumerator enumerator, int tabIndex,
			evalInformation inf) throws Exception {
		if (inf.ResetCurrentVal) {
			CurrentItem = null;
		}

		if (inf.CheckMacs < 2) {
			SpokeItem df = CurrentItem;
			CurrentItem = null;
			SpokeItem rm = checkRunMacro(enumerator, tabIndex, inf);
			if (rm != null) {
				CurrentItem = rm;
			} else
				CurrentItem = df;
		}

		if (enumerator.getCurrent().getType() == Token.Not) {
			enumerator.MoveNext();
			Spoke gc = eval(enumerator.IncreaseDebugLevel(), tabIndex,
					new evalInformation(inf).BreakBeforeEvaler(true));
			CurrentItem = (SpokeItem) new SpokeEquality((SpokeItem) gc,
					new SpokeBool(false));

			((SpokeEquality) CurrentItem).setTokens(enumerator
					.DecreaseDebugLevel());
		}

		if (!inf.SkipStart) {

			switch (enumerator.getCurrent().getType()) {
			case Word:

				if (((TokenWord) enumerator.IncreaseDebugLevel().getCurrent()).Word
						.toLowerCase() == "null") {
					CurrentItem = (SpokeItem) new SpokeNull()
							.setTokens(enumerator.DecreaseDebugLevel());

				} else {

					CurrentItem = (SpokeItem) new SpokeVariable(
							(((TokenWord) enumerator.getCurrent()).Word),
							CurrentItem).setTokens(enumerator
							.DecreaseDebugLevel());

				}
				enumerator.MoveNext();
				break;
			case Int:
				CurrentItem = (SpokeItem) new SpokeInt(((TokenInt) enumerator
						.IncreaseDebugLevel().getCurrent())._value)
						.setTokens(enumerator.DecreaseDebugLevel());
				enumerator.MoveNext();
				break;
			case Float:
				CurrentItem = (SpokeItem) new SpokeFloat(
						((TokenFloat) enumerator.IncreaseDebugLevel()
								.getCurrent())._value).setTokens(enumerator
						.DecreaseDebugLevel());
				enumerator.MoveNext();
				break;
			case String:
				CurrentItem = (SpokeItem) new SpokeString(
						((TokenString) enumerator.IncreaseDebugLevel()
								.getCurrent())._value).setTokens(enumerator
						.DecreaseDebugLevel());
				enumerator.MoveNext();
				break;
			case False:
				CurrentItem = (SpokeItem) new SpokeBool(false)
						.setTokens(enumerator.DecreaseDebugLevel());
				enumerator.MoveNext();
				break;
			case True:
				CurrentItem = (SpokeItem) new SpokeBool(true)
						.setTokens(enumerator.DecreaseDebugLevel());
				enumerator.MoveNext();
				break;
			case OpenParen:
				enumerator.MoveNext();

				CurrentItem = (SpokeItem) eval(enumerator, tabIndex,
						new evalInformation(inf));

				if (enumerator.getCurrent().getType() == Token.Tab) {
					enumerator.MoveNext();

				}

				if (enumerator.getCurrent().getType() != Token.CloseParen) {
					throw new Exception("Bad");

				}
				enumerator.MoveNext();

				break;
			case If:
				return (SpokeLine) evaluateIf(enumerator, tabIndex, inf);
			case Switch:
				return (SpokeLine) evaluateSwitch(enumerator, tabIndex, inf);
			case Bar:
				SpokeAnonMethod an = new SpokeAnonMethod(
						(SpokeParent) CurrentItem);
				enumerator.IncreaseDebugLevel().MoveNext();
				SpokeCommon.Assert(
						enumerator.getCurrent().getType() == Token.OpenParen,
						enumerator.getCurrent().getType() + " Isnt OpenParen",
						enumerator);
				ArrayList<ParamEter> parameters_ = new ArrayList<ParamEter>();
				enumerator.MoveNext();
				if (enumerator.getCurrent().getType() != Token.CloseParen) {
					while (true) {
						boolean byRe_f = false;

						if (((TokenWord) enumerator.getCurrent()).Word
								.toLowerCase().equals("ref")) {
							byRe_f = true;
							enumerator.MoveNext();

						}
						parameters_.add(new ParamEter(((TokenWord) enumerator
								.getCurrent()).Word, 0, byRe_f));
						enumerator.MoveNext();
						switch (enumerator.getCurrent().getType()) {
						case CloseParen:
							enumerator.MoveNext();
							break;
						case Comma:
							enumerator.MoveNext();

							continue;

						}
						break;

					}
				}

				an.Parameters = parameters_.toArray(new ParamEter[parameters_
						.size()]);

				SpokeCommon
						.Assert(enumerator.getCurrent().getType() == Token.AnonMethodStart,
								enumerator.getCurrent().getType()
										+ " Isnt anonmethodstart", enumerator);
				enumerator.MoveNext();

				SpokeCommon.Assert(
						enumerator.getCurrent().getType() == Token.NewLine,
						enumerator.getCurrent().getType() + " Isnt Newline",
						enumerator);
				enumerator.MoveNext();

				ArrayList<SpokeLine> dc = getLines(enumerator, tabIndex + 1,
						new evalInformation(inf));

				an.lines = dc.toArray(new SpokeLine[dc.size()]);
				SpokeCommon
						.Assert(enumerator.getCurrent().getType() == Token.NewLine
								|| enumerator.getCurrent().getType() == Token.EndOfCodez,
								enumerator.getCurrent().getType()
										+ " Isnt Newline", enumerator);
				enumerator.MoveNext();
				an.HasYield = linesHave(an.lines, ISpokeLine.Yield);
				an.HasReturn = linesHave(an.lines, ISpokeLine.Return);
				an.HasYieldReturn = linesHave(an.lines, ISpokeLine.YieldReturn);

				SpokeCommon
						.Assert(enumerator.getCurrent().getType() == Token.Tab
								&& ((TokenTab) enumerator.getCurrent()).TabIndex == tabIndex,
								"Bad tabindex", enumerator);
				if (enumerator.getCurrent().getType() == Token.Tab
						&& inf.EatTab)
					enumerator.MoveNext();
				if (enumerator.PeakNext().getType() != Token.CloseParen) {
					return an;
				} else {
					enumerator.MoveNext();
					CurrentItem = an;
				}
				((SpokeBasic) CurrentItem).setTokens(enumerator
						.DecreaseDebugLevel());

				break;

			case OpenSquare:
				CurrentItem = (SpokeItem) dyanmicArray(
						enumerator.IncreaseDebugLevel(), tabIndex, inf)
						.setTokens(enumerator.DecreaseDebugLevel());
				break;

			case OpenCurly:
				CurrentItem = new SpokeConstruct();
				CurrentItem = (SpokeItem) dynamicObject(
						enumerator.IncreaseDebugLevel(), tabIndex, inf)
						.setTokens(enumerator.DecreaseDebugLevel());
				break;
			case Create:

				return (SpokeItem) createObject(enumerator, tabIndex, inf);

			case Return:
				enumerator.IncreaseDebugLevel().MoveNext();
				SpokeReturn r = (SpokeReturn) new SpokeReturn((SpokeItem) eval(
						enumerator, tabIndex, new evalInformation(inf)))
						.setTokens(enumerator.DecreaseDebugLevel());
				enumerator.MoveNext();
				return r;
			case Yield:
				enumerator.IncreaseDebugLevel().MoveNext();
				if (enumerator.getCurrent().getType() == Token.Return) {
					enumerator.MoveNext();
					SpokeYieldReturn y = (SpokeYieldReturn) new SpokeYieldReturn(
							(SpokeItem) eval(enumerator, tabIndex,
									new evalInformation(inf)))
							.setTokens(enumerator.DecreaseDebugLevel());
					;
					enumerator.MoveNext();
					return y;
				} else {
					SpokeYield y = (SpokeYield) new SpokeYield(
							(SpokeItem) eval(enumerator, tabIndex,
									new evalInformation(inf)))
							.setTokens(enumerator.DecreaseDebugLevel());
					;
					enumerator.MoveNext();
					return y;
				}
			}
		}
		switch (enumerator.getCurrent().getType()) {
		case OpenSquare:
			SpokeArrayIndex ar = new SpokeArrayIndex();
			ar.setParent(CurrentItem);

			enumerator.IncreaseDebugLevel().MoveNext();

			ar.Index = (SpokeItem) eval(enumerator, tabIndex,
					new evalInformation(inf).BreakBeforeEqual(false)
							.ResetCurrentVal(true));

			SpokeCommon.Assert(
					enumerator.getCurrent().getType() == Token.CloseSquare,
					enumerator.getCurrent().getType() + " Isnt closesquare",
					enumerator);
			enumerator.MoveNext();

			if (enumerator.getCurrent().getType() == Token.OpenSquare) {
				CurrentItem = ar;
				return eval(enumerator, tabIndex, new evalInformation(inf)
						.ResetCurrentVal(false).SkipStart(true));

			}

			CurrentItem = ar;
			((SpokeBasic) CurrentItem).setTokens(enumerator
					.DecreaseDebugLevel());
			break;
		case OpenParen:
			if (enumerator.GetLast(1) instanceof TokenWord) {
				((TokenWord) enumerator.GetLast(1))
						.SetTokenType(SpokeTokenType.Method);
			}

			SpokeMethodCall meth = new SpokeMethodCall(CurrentItem);
			meth.setTokens(((TokenWord) enumerator.GetLast(1)));

			enumerator.MoveNext();
			ArrayList<SpokeItem> param_ = new ArrayList<SpokeItem>();
			param_.add(new SpokeCurrent());
			CurrentItem = null;
			if (enumerator.getCurrent().getType() != Token.CloseParen) {
				while (true) {
					param_.add((SpokeItem) eval(enumerator, tabIndex,
							new evalInformation(inf).ResetCurrentVal(true)));
					if (enumerator.getCurrent().getType() == Token.Comma) {
						enumerator.MoveNext();
						continue;
					}
					break;
				}
			}
			enumerator.MoveNext();// closeparen

			meth.Parameters = param_.toArray(new SpokeItem[param_.size()]);

			CurrentItem = meth;
			// loop params
			break;
		case Period:
			SpokeItem t = CurrentItem;
			enumerator.IncreaseDebugLevel().MoveNext();
			SpokeParent g;
			Spoke c;
			CurrentItem = g = (SpokeParent) (c = eval(enumerator, tabIndex,
					new evalInformation(inf).BreakBeforeEqual(true).BreakBeforeEvaler(true)));
			// g.Parent = t;

			// enumerator.MoveNext();
			((SpokeBasic) CurrentItem).setTokens(enumerator
					.DecreaseDebugLevel());
			break;

		}
		switch (enumerator.getCurrent().getType()) {
		case Period:
			enumerator.MoveNext();
			SpokeItem t = CurrentItem;
			SpokeParent g;
			Spoke c;
			CurrentItem = g = (SpokeParent) (c = eval(enumerator, tabIndex,
					new evalInformation(inf).BreakBeforeEqual(true).BreakBeforeEvaler(true)));
			// g.Parent = t;
			((SpokeBasic) CurrentItem).setTokens(((SpokeBasic) t).getTokens());
			// enumerator.MoveNext();
			break;

		}

		while (true) {
			switch (enumerator.getCurrent().getType()) {
			case OpenParen:
				SpokeMethodCall meth = new SpokeMethodCall(CurrentItem);
				enumerator.IncreaseDebugLevel().MoveNext();
				ArrayList<SpokeItem> param_ = new ArrayList<SpokeItem>();
				param_.add(new SpokeCurrent());
				while (true) {
					param_.add((SpokeItem) eval(enumerator, tabIndex,
							new evalInformation(inf).ResetCurrentVal(true)));
					if (enumerator.getCurrent().getType() == Token.Comma) {
						enumerator.MoveNext();
						continue;
					}
					break;
				}
				enumerator.MoveNext();// closeparen

				meth.Parameters = param_.toArray(new SpokeItem[param_.size()]);

				CurrentItem = meth;
				((SpokeBasic) CurrentItem).setTokens(enumerator
						.DecreaseDebugLevel());
				continue;
			}
			break;
		}
		if (inf.BreakBeforeEvaler) {
			return CurrentItem;
		}

		if (!inf.DontEvalEquals) {
			if (enumerator.IncreaseDebugLevel().getCurrent().getType() == Token.Equal) {

				SpokeEqual equ = new SpokeEqual(CurrentItem);
				enumerator.MoveNext();

				equ.RightSide = (SpokeItem) eval(
						enumerator,
						tabIndex,
						new evalInformation(inf).EatTab(false).ResetCurrentVal(
								true));

				if (enumerator.getCurrent().getType() == Token.NewLine) {
					// enumerator.MoveNext(); //newline
				}
				equ.setTokens(enumerator.DecreaseDebugLevel());
				return equ;

			}

		}

		switch (enumerator.getCurrent().getType()) {
		case AnonMethodStart:
			// checkparamsgetlines

			SpokeAnonMethod an = new SpokeAnonMethod(CurrentItem);
			enumerator.MoveNext();
			if (enumerator.getCurrent().getType() == Token.Bar) {
				enumerator.MoveNext();
				SpokeCommon.Assert(
						enumerator.getCurrent().getType() == Token.OpenParen,
						enumerator.getCurrent().getType() + " Isnt openparen",
						enumerator);

				ArrayList<ParamEter> parameters_ = new ArrayList<ParamEter>();
				enumerator.MoveNext();

				while (true) {
					boolean byRe_f = false;
					if (((TokenWord) enumerator.getCurrent()).Word
							.toLowerCase() == "ref") {
						byRe_f = true;
						enumerator.MoveNext();

					}
					parameters_.add(new ParamEter(((TokenWord) enumerator
							.getCurrent()).Word, 0, byRe_f));

					((SpokeBasic) CurrentItem).setTokens((TokenWord) enumerator
							.getCurrent());

					enumerator.MoveNext();
					switch (enumerator.getCurrent().getType()) {
					case CloseParen:
						enumerator.MoveNext();
						break;
					case Comma:
						enumerator.MoveNext();
						continue;
					}
					break;
				}

				an.Parameters = parameters_.toArray(new ParamEter[parameters_
						.size()]);

			} else {
				an.RunOnVar = (SpokeItem) eval(enumerator, tabIndex,
						new evalInformation(inf).ResetCurrentVal(true)
								.BreakBeforeEqual(false)
								.BreakBeforeEvaler(true));
			}

			SpokeCommon.Assert(
					enumerator.getCurrent().getType() == Token.NewLine,
					enumerator.getCurrent().getType() + " Isnt Newline",
					enumerator);
			enumerator.MoveNext();
			CurrentItem = null;

			ArrayList<SpokeLine> dc = getLines(enumerator, tabIndex + 1,
					new evalInformation(inf));

			an.lines = dc.toArray(new SpokeLine[dc.size()]);
			an.HasYield = linesHave(an.lines, ISpokeLine.Yield);
			an.HasReturn = linesHave(an.lines, ISpokeLine.Return);
			an.HasYieldReturn = linesHave(an.lines, ISpokeLine.YieldReturn);
			SpokeCommon
					.Assert(enumerator.getCurrent().getType() == Token.NewLine
							|| enumerator.getCurrent().getType() == Token.EndOfCodez,
							enumerator.getCurrent().getType() + " Isnt Newline",
							enumerator);
			enumerator.MoveNext();

			if (enumerator.getCurrent().getType() == Token.Tab && inf.EatTab)
				enumerator.MoveNext();

			CurrentItem = an;

			break;
		}
		
		if (inf.BreakBeforeMath) {
			return CurrentItem;
		}
		
		IToken dm;
		IToken cm;
		SpokeBasic ccm;
		// 5*6-7+8/9+10 does not evaluate properly
		switch (enumerator.getCurrent().getType()) {
		case Percent:
			dm = enumerator.getCurrent();
			enumerator.MoveNext();
			int me = 0;
			if (currentMathItem.size() == 0) {
				SpokeMod j;
				currentMathItem.add(j = new SpokeMod());
				j.PushItem(CurrentItem);

			} else if (currentMathItem.get(currentMathItem.size() - 1)
					.getWeight() > me) {
				SpokeMod j;
				SpokeMathItem c = currentMathItem
						.get(currentMathItem.size() - 1);
				c.PushItem(CurrentItem);
				currentMathItem.remove(currentMathItem.size() - 1);
				currentMathItem.add(j = new SpokeMod());
				j.PushItem((SpokeItem) c);

			} else if (currentMathItem.get(currentMathItem.size() - 1)
					.getWeight() < me) {

				SpokeMod j;
				currentMathItem.add(j = new SpokeMod());
				j.PushItem(CurrentItem);

			} else if (currentMathItem.get(currentMathItem.size() - 1)
					.getWeight() == me) {
				SpokeMod j = (SpokeMod) currentMathItem.get(currentMathItem
						.size() - 1);
				if (!CurrentItem.equals(j))
					j.PushItem(CurrentItem);

			}
			ccm = ((SpokeBasic) eval(enumerator, tabIndex, new evalInformation(
					inf).BreakBeforeEqual(true).ResetCurrentVal(true)
					.DoingMath(true)));

			if (currentMathItem.size() > 0) {
				CurrentItem = (SpokeItem) currentMathItem.get(currentMathItem
						.size() - 1);
				((SpokeMathItem) CurrentItem).PushItem((SpokeItem) ccm);
				((SpokeBasic) CurrentItem).setTokens(dm);
			} else
				CurrentItem = (SpokeItem) ccm.setTokens(dm);

			break;
		case Plus:
			dm = enumerator.getCurrent();
			enumerator.MoveNext();
			me = 0;
			if (currentMathItem.size() == 0) {
				SpokeAddition j;
				currentMathItem.add(j = new SpokeAddition());
				j.PushItem(CurrentItem);

			} else if (currentMathItem.get(currentMathItem.size() - 1)
					.getWeight() > me) {
				SpokeAddition j;
				currentMathItem.add(j = new SpokeAddition());
				j.PushItem(((SpokeItem) currentMathItem.get(currentMathItem
						.size() - 1)));
			} else if (currentMathItem.get(currentMathItem.size() - 1)
					.getWeight() < me) {
				SpokeAddition j;
				currentMathItem.add(j = new SpokeAddition());
				j.PushItem(CurrentItem);

			} else if (currentMathItem.get(currentMathItem.size() - 1)
					.getWeight() == me) {
				SpokeAddition j = (SpokeAddition) currentMathItem
						.get(currentMathItem.size() - 1);
				if (!CurrentItem.equals(j))
					j.PushItem(CurrentItem);

			}

			ccm = ((SpokeBasic) eval(enumerator, tabIndex, new evalInformation(
					inf).BreakBeforeEqual(true).ResetCurrentVal(true)
					.DoingMath(true)));
			if (currentMathItem.size() > 0) {
				CurrentItem = (SpokeItem) currentMathItem.get(currentMathItem
						.size() - 1);
				((SpokeMathItem) CurrentItem).PushItem((SpokeItem) ccm);
				((SpokeBasic) CurrentItem).setTokens(dm);
			} else
				CurrentItem = (SpokeItem) ccm.setTokens(dm);

			break;
		case Minus:
			dm = enumerator.getCurrent();
			enumerator.MoveNext();
			me = 1;
			if (currentMathItem.size() == 0) {
				SpokeSubtraction j;
				currentMathItem.add(j = new SpokeSubtraction());
				j.PushItem(CurrentItem);

			} else if (currentMathItem.get(currentMathItem.size() - 1)
					.getWeight() > me) {
				SpokeSubtraction j;
				SpokeMathItem c = currentMathItem
						.get(currentMathItem.size() - 1);
				c.PushItem(CurrentItem);
				currentMathItem.remove(currentMathItem.size() - 1);
				currentMathItem.add(j = new SpokeSubtraction());
				j.PushItem((SpokeItem) c);

			} else if (currentMathItem.get(currentMathItem.size() - 1)
					.getWeight() < me) {
				SpokeSubtraction j;
				currentMathItem.add(j = new SpokeSubtraction());
				j.PushItem(CurrentItem);

			} else if (currentMathItem.get(currentMathItem.size() - 1)
					.getWeight() == me) {
				SpokeSubtraction j = (SpokeSubtraction) currentMathItem
						.get(currentMathItem.size() - 1);
				if (!CurrentItem.equals(j))
					j.PushItem(CurrentItem);

			}

			ccm = ((SpokeBasic) eval(enumerator, tabIndex, new evalInformation(
					inf).BreakBeforeEqual(true).ResetCurrentVal(true)
					.DoingMath(true)));

			if (currentMathItem.size() > 0) {
				CurrentItem = (SpokeItem) currentMathItem.get(currentMathItem
						.size() - 1);
				((SpokeMathItem) CurrentItem).PushItem((SpokeItem) ccm);
				((SpokeBasic) CurrentItem).setTokens(dm);
			} else
				CurrentItem = (SpokeItem) ccm.setTokens(dm);

			break;
		case Divide:
			dm = enumerator.getCurrent();
			enumerator.MoveNext();
			me = 3;
			if (currentMathItem.size() == 0) {
				SpokeDivision j;
				currentMathItem.add(j = new SpokeDivision());
				j.PushItem(CurrentItem);

			} else if (currentMathItem.get(currentMathItem.size() - 1)
					.getWeight() > me) {
				SpokeDivision j;
				SpokeMathItem c = currentMathItem
						.get(currentMathItem.size() - 1);
				c.PushItem(CurrentItem);
				currentMathItem.remove(currentMathItem.size() - 1);
				currentMathItem.add(j = new SpokeDivision());
				j.PushItem((SpokeItem) c);

			} else if (currentMathItem.get(currentMathItem.size() - 1)
					.getWeight() < me) {
				SpokeDivision j;
				currentMathItem.add(j = new SpokeDivision());
				j.PushItem(CurrentItem);

			} else if (currentMathItem.get(currentMathItem.size() - 1)
					.getWeight() == me) {
				SpokeDivision j = (SpokeDivision) currentMathItem
						.get(currentMathItem.size() - 1);
				if (!CurrentItem.equals(j))
					j.PushItem(CurrentItem);

			}

			ccm = ((SpokeBasic) eval(enumerator, tabIndex, new evalInformation(
					inf).BreakBeforeEqual(true).ResetCurrentVal(true)
					.DoingMath(true)));

			if (currentMathItem.size() > 0) {
				CurrentItem = (SpokeItem) currentMathItem.get(currentMathItem
						.size() - 1);
				((SpokeMathItem) CurrentItem).PushItem((SpokeItem) ccm);
				((SpokeBasic) CurrentItem).setTokens(dm);
			} else
				CurrentItem = (SpokeItem) ccm.setTokens(dm);

			break;
		case Mulitply:
			dm = enumerator.getCurrent();
			enumerator.MoveNext();
			me = 2;
			if (currentMathItem.size() == 0) {
				SpokeMultiplication j;
				currentMathItem.add(j = new SpokeMultiplication());
				j.PushItem(CurrentItem);

			} else if (currentMathItem.get(currentMathItem.size() - 1)
					.getWeight() > me) {
				SpokeMultiplication j;
				SpokeMathItem c = currentMathItem
						.get(currentMathItem.size() - 1);
				c.PushItem(CurrentItem);
				currentMathItem.remove(currentMathItem.size() - 1);
				currentMathItem.add(j = new SpokeMultiplication());
				j.PushItem((SpokeItem) c);

			} else if (currentMathItem.get(currentMathItem.size() - 1)
					.getWeight() < me) {
				SpokeMultiplication j;
				currentMathItem.add(j = new SpokeMultiplication());
				j.PushItem(CurrentItem);

			} else if (currentMathItem.get(currentMathItem.size() - 1)
					.getWeight() == me) {
				SpokeMultiplication j = (SpokeMultiplication) currentMathItem
						.get(currentMathItem.size() - 1);
				if (!CurrentItem.equals(j))
					j.PushItem(CurrentItem);

			}

			ccm = ((SpokeBasic) eval(enumerator, tabIndex, new evalInformation(
					inf).BreakBeforeEqual(true).ResetCurrentVal(true)
					.DoingMath(true)));
			if (currentMathItem.size() > 0) {
				CurrentItem = (SpokeItem) currentMathItem.get(currentMathItem
						.size() - 1);
				((SpokeMathItem) CurrentItem).PushItem((SpokeItem) ccm);
				((SpokeBasic) CurrentItem).setTokens(dm);
			} else
				CurrentItem = (SpokeItem) ccm.setTokens(dm);
			break;
		}

		if (inf.BreakBeforeEqual) {

			if (currentMathItem.size() > 0 && inf.DoingMath) {

				SpokeMathItem a = (currentMathItem
						.get(currentMathItem.size() - 1));
				if (!CurrentItem.equals(a))
					a.PushItem(CurrentItem);
				currentMathItem.remove(a);
				return (Spoke) a;
			}

			return CurrentItem;
		}

		switch (enumerator.getCurrent().getType()) {
		case DoubleOr:

			dm = enumerator.getCurrent();
			enumerator.MoveNext();
			CurrentItem = (SpokeItem) new SpokeOr(CurrentItem,
					(SpokeItem) eval(enumerator, tabIndex, new evalInformation(
							inf).ResetCurrentVal(true))).setTokens(dm);
			break;
		case DoubleAnd:
			dm = enumerator.getCurrent();
			enumerator.MoveNext();
			CurrentItem = (SpokeItem) new SpokeAnd(CurrentItem,
					(SpokeItem) eval(enumerator, tabIndex, new evalInformation(
							inf).ResetCurrentVal(true))).setTokens(dm);
			;

			break;

		}

		SpokeItem e;
		switch (enumerator.getCurrent().getType()) {
		case DoubleEqual:
			dm = enumerator.getCurrent();
			enumerator.MoveNext();
			e = (SpokeItem) new SpokeEquality(CurrentItem, (SpokeItem) eval(
					enumerator, tabIndex, new evalInformation(inf)
							.BreakBeforeEqual(true).ResetCurrentVal(true)))
					.setTokens(dm);
			;

			CurrentItem = e;

			break;
		case NotEqual:
			dm = enumerator.getCurrent();
			enumerator.MoveNext();
			e = (SpokeItem) new SpokeNotEqual(CurrentItem, (SpokeItem) eval(
					enumerator, tabIndex, new evalInformation(inf)
							.BreakBeforeEqual(true).ResetCurrentVal(true)))
					.setTokens(dm);

			CurrentItem = e;

			break;
		case Less:
			dm = enumerator.getCurrent();
			enumerator.MoveNext();
			if (enumerator.getCurrent().getType() == Token.Equal) {
				cm = enumerator.getCurrent();
				enumerator.MoveNext();
				e = (SpokeItem) new SpokeLessThanOrEqual(CurrentItem,
						(SpokeItem) eval(enumerator, tabIndex,
								new evalInformation(inf).BreakBeforeEqual(true)
										.ResetCurrentVal(true))).setTokens(dm,
						cm);

				CurrentItem = e;
				break;
			}
			e = (SpokeItem) new SpokeLessThan(CurrentItem, (SpokeItem) eval(
					enumerator, tabIndex, new evalInformation(inf)
							.BreakBeforeEqual(true).ResetCurrentVal(true)))
					.setTokens(dm);

			CurrentItem = e;
			break;
		case Greater:
			dm = enumerator.getCurrent();
			enumerator.MoveNext();
			if (enumerator.getCurrent().getType() == Token.Equal) {
				cm = enumerator.getCurrent();
				enumerator.MoveNext();
				e = (SpokeItem) new SpokeGreaterThanOrEqual(CurrentItem,
						(SpokeItem) eval(enumerator, tabIndex,
								new evalInformation(inf).BreakBeforeEqual(true)
										.ResetCurrentVal(true))).setTokens(dm,
						cm);

				CurrentItem = e;
				break;
			}
			e = (SpokeItem) new SpokeGreaterThan(CurrentItem, (SpokeItem) eval(
					enumerator, tabIndex, new evalInformation(inf)
							.BreakBeforeEqual(true).ResetCurrentVal(true)))
					.setTokens(dm);

			CurrentItem = e;
			break;

		}
		switch (enumerator.getCurrent().getType()) {
		case DoubleOr:
			dm = enumerator.getCurrent();
			enumerator.MoveNext();
			CurrentItem = (SpokeItem) new SpokeOr(CurrentItem,
					(SpokeItem) eval(enumerator, tabIndex, new evalInformation(
							inf).ResetCurrentVal(true))).setTokens(dm);

			break;
		case DoubleAnd:
			dm = enumerator.getCurrent();
			enumerator.MoveNext();
			CurrentItem = (SpokeItem) new SpokeAnd(CurrentItem,
					(SpokeItem) eval(enumerator, tabIndex, new evalInformation(
							inf).ResetCurrentVal(true))).setTokens(dm);

			break;

		}

		switch (enumerator.getCurrent().getType()) {
		case QuestionMark:
			dm = enumerator.getCurrent();
			enumerator.MoveNext();

			SpokeItem bas = CurrentItem;

			SpokeItem left = (SpokeItem) eval(enumerator, tabIndex,
					new evalInformation(inf).ResetCurrentVal(true));

			if (!(enumerator.getCurrent() instanceof TokenColon))
				throw new SpokeException("bad ternary", enumerator.getCurrent());
			enumerator.MoveNext();
			SpokeItem right = (SpokeItem) eval(enumerator, tabIndex,
					new evalInformation(inf).ResetCurrentVal(true));

			CurrentItem = (SpokeItem) new SpokeTernary(bas, left, right)
					.setTokens(dm);

			break;
		}

		return CurrentItem;

	}

	ArrayList<SpokeMathItem> currentMathItem = new ArrayList<SpokeMathItem>();

	private SpokeIf evaluateIf(TokenEnumerator enumerator, int tabIndex,
			evalInformation inf) throws Exception {
		enumerator.MoveNext();

		SpokeIf i_f = new SpokeIf((SpokeItem) eval(enumerator, tabIndex,
				new evalInformation(inf).ResetCurrentVal(true)));
		enumerator.MoveNext();
		ArrayList<SpokeLine> gc = getLines(enumerator, tabIndex + 1,
				new evalInformation(inf));
		i_f.IfLines = gc.toArray(new SpokeLine[gc.size()]);
		SpokeCommon
				.Assert(enumerator.getCurrent().getType() == Token.NewLine
						|| enumerator.getCurrent().getType() == Token.EndOfCodez,
						enumerator.getCurrent().getType() + " Isnt Newline",
						enumerator);
		enumerator.MoveNext();

		if (enumerator.getCurrent().getType() == Token.Tab
				&& enumerator.PeakNext().getType() == Token.Else) {
			if (((TokenTab) enumerator.getCurrent()).TabIndex < tabIndex) {
				return i_f;
			}
			enumerator.MoveNext();
			enumerator.MoveNext();

			if (enumerator.getCurrent().getType() == Token.If) {
				i_f.ElseIf = evaluateIf(enumerator, tabIndex,
						inf.ResetCurrentVal(true));
			} else {

				gc = getLines(enumerator, tabIndex + 1,
						new evalInformation(inf));
				SpokeLine[] fc = new SpokeLine[gc.size()];
				gc.toArray(fc);
				i_f.ElseLines = fc;
				SpokeCommon
						.Assert(enumerator.getCurrent().getType() == Token.NewLine
								|| enumerator.getCurrent().getType() == Token.EndOfCodez,
								enumerator.getCurrent().getType()
										+ " Isnt Newline", enumerator);
				enumerator.MoveNext();
			}
		}

		if (enumerator.getCurrent().getType() == Token.Tab && inf.EatTab)
			enumerator.MoveNext();
		return i_f;
	}

	private SpokeSwitch evaluateSwitch(TokenEnumerator enumerator,
			int tabIndex, evalInformation inf) throws Exception {
		enumerator.MoveNext();

		SpokeSwitch sw = new SpokeSwitch((SpokeItem) eval(enumerator, tabIndex,
				new evalInformation(inf).ResetCurrentVal(true)));

		ArrayList<SpokeSwitch.Case> als = new ArrayList<SpokeSwitch.Case>();

		while (true) {
			if (enumerator.PeakNext() instanceof TokenTab) {
				enumerator.MoveNext();
				if (((TokenTab) enumerator.getCurrent()).TabIndex == tabIndex + 1) {
					enumerator.MoveNext();
					SpokeItem jf = (SpokeItem) eval(enumerator, tabIndex,
							new evalInformation(inf).ResetCurrentVal(true));
					SpokeCommon.Assert(
							enumerator.getCurrent() instanceof TokenColon,
							"case bad", enumerator);
					enumerator.MoveNext();
					enumerator.MoveNext();

					ArrayList<SpokeLine> gc = getLines(enumerator,
							tabIndex + 2, new evalInformation(inf));
					als.add(sw.new Case(jf,
							gc.toArray(new SpokeLine[gc.size()])));
					SpokeCommon
							.Assert(enumerator.getCurrent().getType() == Token.NewLine
									|| enumerator.getCurrent().getType() == Token.EndOfCodez,
									enumerator.getCurrent().getType()
											+ " Isnt Newline", enumerator);
				}
				continue;
			}
			break;
		}
		sw.Cases = als.toArray(new SpokeSwitch.Case[als.size()]);

		if (enumerator.getCurrent().getType() == Token.Tab && inf.EatTab)
			enumerator.MoveNext();
		return sw;
	}

	private ArrayList<TokenMacroPiece> allMacros_;

	private SpokeMethodCall checkRunMacro(TokenEnumerator enumerator,
			int tabIndex, evalInformation inf) throws Exception { // tm.

		for (int index = 0;; index++) {
			TokenMacroPiece tokenMacroPiece = null;
			ArrayList<SpokeItem> parameters = null;
			TokenEnumerator tm = null;
			boolean bad = false;
			boolean continueBack = false;
			boolean breakFor = false;
			while (true) {
				bad = false;
				continueBack = false;
				if (index >= allMacros_.size()) {
					breakFor = true;
					break;
				}

				tokenMacroPiece = allMacros_.get(index);
				parameters = new ArrayList<SpokeItem>();
				tm = enumerator.Clone();
				ArrayList<IToken> outs = tm.getOutstandingLine();

				for (int i = 0; i < tokenMacroPiece.Macro.length; i++) {
					IToken mp = tokenMacroPiece.Macro[i];

					if (mp.getType() == Token.Ampersand) {

						for (int j = i + 1; j < tokenMacroPiece.Macro.length; j++) {
							final IToken r = tokenMacroPiece.Macro[j];

							if (!ALH.Any(outs, new Finder<IToken>() {

								@Override
								public boolean Find(IToken a) {
									return (r.getType() == Token.Ampersand || (a
											.getType() == r.getType() && (a
											.getType() == Token.Word
											&& r.getType() == Token.Word ? ((TokenWord) a).Word == ((TokenWord) r).Word
											: true)));
								}
							})) {
								bad = true;
								break;
							}
						}
						if (bad) {
							break;
						}

						IToken frf = tokenMacroPiece.Macro.length == i + 1 ? new TokenNewLine(
								-1) : tokenMacroPiece.Macro[i + 1];

						int selectedLine = 0;

						IToken tp = null;
						int selectedToken = tm.tokenIndex;

						List<LineToken> gm = tm.getCurrentLines();

						if (frf.getType() != Token.NewLine) {
							for (int j = 0; j < gm.size(); j++) {

								for (int ic = selectedToken; ic < gm.get(j).Tokens
										.size(); ic++) {
									IToken a = gm.get(j).Tokens.get(ic);

									if (gm.get(j).Tokens.get(ic).getType() == frf
											.getType()
											&& (a.getType() == frf.getType() && (a
													.getType() == Token.Word
													&& frf.getType() == Token.Word ? ((TokenWord) a).Word == ((TokenWord) frf).Word
													: true))) {
										tp = gm.get(j).Tokens.get(ic);
										break;
									}
								}

								if (tp != null) {
									selectedLine = j;
									break;
								} else {
									selectedToken = 0;
								}
							}

							TokenAmpersand bf = new TokenAmpersand(-1);
							gm.get(selectedLine).Tokens
									.add(gm.get(selectedLine).Tokens
											.indexOf(tp), bf);
							try {
								CurrentItem = null;
								Spoke d = eval(
										tm,
										tabIndex,
										new evalInformation(inf)
												.CheckMacs(inf.CheckMacs + 1)
												.DontEvalEquals(true)
												.BreakBeforeEqual(true));
								parameters.add((SpokeItem) d);
								if (d == null
										|| (!(tm.getCurrent().getType() == Token.Ampersand || tokenMacroPiece.Macro.length == i + 1))) {
									index++;

									continueBack = true;
									tm.getCurrentLine().Tokens.remove(bf);
									break;
								}
							} catch (Exception e) {
								index++;
								continueBack = true;
								tm.getCurrentLine().Tokens.remove(bf);
								break;
							}
							tm.getCurrentLine().Tokens.remove(bf);

						} else {
							try {
								CurrentItem = null;
								Spoke d = eval(
										tm,
										tabIndex,
										new evalInformation(inf)
												.CheckMacs(inf.CheckMacs + 1)
												.DontEvalEquals(true)
												.BreakBeforeEqual(true));
								parameters.add((SpokeItem) d);
								if (d == null) {
									index++;
									continueBack = true;
									break;
								}
							} catch (Exception e) {
								index++;
								continueBack = true;
								break;
							}

						}
					} else {
						if (mp.getType() == tm.getCurrent().getType()
								&& (mp.getType() == Token.Word
										&& tm.getCurrent().getType() == Token.Word ? ((TokenWord) mp).Word == ((TokenWord) tm
										.getCurrent()).Word : true)) {
							tm.MoveNext();
						} else {
							bad = true;
							break;
						}
					}
				}

				if (continueBack) {
					continue;
				}
				break;
			}
			if (breakFor) {
				break;
			}
			if (!bad) {
				SpokeMethodCall ambe = new SpokeMethodCall();
				SpokeAnonMethod me = new SpokeAnonMethod();
				me.SpecAnon = true;
				me.Parameters = tokenMacroPiece.Parameters;

				ArrayList<SpokeLine> gc = getLines(
						new TokenEnumerator(
								tokenMacroPiece.Lines
										.toArray(new LineToken[tokenMacroPiece.Lines
												.size()])), 1, inf);

				me.lines = gc.toArray(new SpokeLine[gc.size()]);

				me.HasYieldReturn = linesHave(me.lines, ISpokeLine.YieldReturn);
				me.HasYield = linesHave(me.lines, ISpokeLine.Yield);
				me.HasReturn = linesHave(me.lines, ISpokeLine.Return);

				ambe.setParent(me);
				parameters.add(0, new SpokeCurrent());

				ambe.Parameters = parameters.toArray(new SpokeItem[parameters
						.size()]);

				enumerator.Set(tm);
				return ambe;
			}
		}
		return null;
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
		return new SpokeType(ObjectType.Object, word);

	}

	private SpokeBasic createObject(TokenEnumerator enumerator, int tabIndex,
			evalInformation inf) throws Exception {
		enumerator.MoveNext();
		if (enumerator.getCurrent().getType() == Token.OpenCurly) {
			return dynamicObject(enumerator, tabIndex, inf);
		}
		SpokeConstruct sp = new SpokeConstruct();

		SpokeCommon.Assert(enumerator.getCurrent().getType() == Token.Word,
				enumerator.getCurrent().getType() + " Isnt word", enumerator);
		sp.setTokens(((TokenWord) enumerator.getCurrent()));

		sp.ClassName = ((TokenWord) enumerator.getCurrent()).Word;
		((TokenWord) enumerator.getCurrent())
				.SetTokenType(SpokeTokenType.Object);
		enumerator.MoveNext();

		if (enumerator.getCurrent().getType() == Token.OpenSquare) {
			enumerator.MoveNext();// closequare
			enumerator.MoveNext();
			return new SpokeArray(NameToType(sp.ClassName));
		}

		enumerator.MoveNext();// openeparam

		ArrayList<SpokeItem> param_ = new ArrayList<SpokeItem>();
		if (enumerator.getCurrent().getType() != Token.CloseParen) {
			CurrentItem = null;
			while (true) {
				param_.add((SpokeItem) eval(enumerator, tabIndex,
						new evalInformation(inf).ResetCurrentVal(true)));
				if (enumerator.getCurrent().getType() == Token.Comma) {
					enumerator.MoveNext();
					continue;
				}
				break;
			}

		}

		sp.Parameters = param_.toArray(new SpokeItem[param_.size()]);
		enumerator.MoveNext();// closeparam

		CurrentItem = sp;

		
		return (SpokeConstruct) CurrentItem;
	}

	private SpokeBasic dynamicObject(TokenEnumerator enumerator, int tabIndex,
			evalInformation inf) throws Exception {

		enumerator.MoveNext(); // openesquigly
		SpokeConstruct cons = (SpokeConstruct) CurrentItem;

		ArrayList<SpokeEqual> param_ = new ArrayList<SpokeEqual>();
		if (enumerator.getCurrent().getType() != Token.CloseCurly) {

			CurrentItem = null;
			while (true) {

				((TokenWord) enumerator.getCurrent())
						.SetTokenType(SpokeTokenType.Param);

				param_.add((SpokeEqual) eval(enumerator, tabIndex,
						new evalInformation(inf).ResetCurrentVal(true)));
				if (enumerator.getCurrent().getType() == Token.Comma) {
					enumerator.MoveNext();
					continue;
				}
				break;
			}

		}
		enumerator.MoveNext();// closesquigly

		cons.SetVars = ALH.Select(param_,
				new ALH.Selector<SpokeEqual, SVarItems>() {
					@Override
					public SVarItems Select(SpokeEqual item) {
						return new SVarItems(
								((SpokeVariable) item.LeftSide).VariableName,
								0, item.RightSide);
					}
				}).toArray(new SVarItems[param_.size()]);
		return cons;
	}

	private SpokeBasic dyanmicArray(TokenEnumerator enumerator, int tabIndex,
			evalInformation inf) throws Exception {
		SpokeArray ars = new SpokeArray(new SpokeItem[0]);

		ArrayList<SpokeItem> parameters = new ArrayList<SpokeItem>();

		enumerator.MoveNext();
		if (enumerator.getCurrent().getType() == Token.CloseSquare) {
			enumerator.MoveNext();
			ars.Parameters = parameters
					.toArray(new SpokeItem[parameters.size()]);
			return (ars);
		}

		while (true) {
			parameters.add((SpokeItem) eval(enumerator, tabIndex,
					new evalInformation(inf).ResetCurrentVal(true)));
			switch (enumerator.getCurrent().getType()) {
			case CloseSquare:
				enumerator.MoveNext();
				break;
			case Comma:
				enumerator.MoveNext();
				continue;

			default:
				throw new Exception("Bad");
			}
			break;
		}
		ars.Parameters = parameters.toArray(new SpokeItem[parameters.size()]);
		return (ars);
	}
}
