package com.sk89q.craftbook.Spoke;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.sk89q.craftbook.Spoke.ALH.Finder;
import com.sk89q.craftbook.Spoke.ALH.HashFinder;
import com.sk89q.craftbook.Spoke.ALH.Selector;
import com.sk89q.craftbook.Spoke.SpokeExpressions.*;
import com.sk89q.craftbook.Spoke.SpokeExpressions.SpokeSwitch.Case;
import com.sk89q.craftbook.Spoke.SpokeInstructions.SpokeInstruction;
import com.sk89q.craftbook.Spoke.SpokeInstructions.SpokeInstructionType;
import com.sk89q.craftbook.Spoke.Tokens.TokenWord;

public class SpokePreparse {

	private ArrayList<SpokeClass> _cla;
	private ArrayList<SpokeEnum> _enu;
	private SpokeGlobal _glo;
	private ArrayList<SpokeMethod> Methods = new ArrayList<SpokeMethod>();
	private HashMap<String, Tuple2<SpokeType, Integer>> InternalMethodsTypes;

	public SpokePreparse(
			Tuple3<ArrayList<SpokeClass>, ArrayList<SpokeEnum>, SpokeGlobal> sc,
			HashMap<String, Tuple2<SpokeType, Integer>> internalMethods,
			ArrayList<SpokeMethod> mets) {
		Methods = mets;
		_cla = sc.Item1;
		_enu = sc.Item2;
		_glo = sc.Item3;
		InternalMethodsTypes = internalMethods;
	}

	public void Run() throws SpokeException {
		for (SpokeGlobalVariable sgv : _glo.Variables) {
			ArrayList<SpokeInstruction> toCotninue = SpokeInstruction.ins;

			SpokeInstruction.Beginner();
			sgv.Type = evaluateItem((SpokeItem) sgv.SpokeItem,
					new SpokeMethodParse(new SpokeType(ObjectType.Null)),
					false, new SpokeVariableInfo());
			new SpokeInstruction(SpokeInstructionType.Return);
			sgv.Instructions = SpokeInstruction.Ender();
			SpokeInstruction.ins = toCotninue;

		}

		for (Iterator<SpokeClass> iterator = _cla.iterator(); iterator
				.hasNext();) {
			SpokeClass sclass = (SpokeClass) iterator.next();

			for (Iterator<SpokeMethod> iterator2 = sclass.Methods.iterator(); iterator2
					.hasNext();) {
				SpokeMethod smethod = (SpokeMethod) iterator2.next();
				if (smethod.MethodFunc != null)
					continue;
				if (!smethod.Static)
					continue;

				SpokeType[] paras;

				SpokeType st = new SpokeType(ObjectType.Object);

				st.ClassName = sclass.Name;

				st.Variables = new SpokeVariableInfo();

				for (int i = 0; i < sclass.Variables.length; i++) {
					String v = sclass.Variables[i];
					st.Variables.Add(v, new SpokeType(ObjectType.Unset), null,
							false);
				}

				;

				SpokeType[] pams = new SpokeType[smethod.Parameters.length];
				ALH.Select(Arrays.asList(smethod.Parameters),
						new Selector<SpokeMethodParameter, SpokeType>() {

							@Override
							public SpokeType Select(SpokeMethodParameter item) {
								return item.Type;
							}
						}).toArray(pams);
				evaluateMethod(smethod, st, pams);
			}
		}

		SpokeMethod[] sms = new SpokeMethod[Methods.size()];
		Methods.toArray(sms);

		preparseInstructions(sms);

		for (SpokeMethod spokeMethod : sms) {
			if (spokeMethod.ParentVariableRefs != null)
				spokeMethod.ParentVariableRefs.Done();
			if (spokeMethod.MethodVariableInfo != null)
				spokeMethod.MethodVariableInfo.Done();
		}
	}

	private void preparseInstructions(SpokeMethod[] mets) throws SpokeException {

		for (int i = 0; i < mets.length; i++) {
			SpokeMethod spokeMethod = mets[i];
			SpokeInstruction[] smi = spokeMethod.Instructions;

			if (smi != null) {
				preparseIns(smi);
				for (int index = 0; index < smi.length; index++) {
					SpokeInstruction spokeInstruction = smi[index];

					if (spokeInstruction.Type == SpokeInstructionType.CallAnonMethod) {
						preparseIns(spokeInstruction.AnonMethod.Instructions);
					}
				}
			}

		}

		StringBuilder sbw = new StringBuilder();

		for (int i = 0; i < mets.length; i++) {
			SpokeMethod spokeMethod = mets[i];
			SpokeInstruction[] smi = spokeMethod.Instructions;
			sbw.append("Method: " + spokeMethod.Class.Name + ":"
					+ spokeMethod.MethodName + "\r\n");
			if (smi == null) {
				continue;
			}

			for (int index = 0; index < smi.length; index++) {
				SpokeInstruction spokeInstruction = smi[index];

				sbw.append(index + "\t\t\t" + spokeInstruction.toString()
						+ "\r\n");
			}
		}

		try {
			for (int i = 0; i < mets.length; i++) {
				SpokeMethod spokeMethod = mets[i];
				if (spokeMethod.Instructions != null) {
					doit(0, 0, spokeMethod.Instructions);
				}

			}
		} catch (Exception ec) {
			throw new SpokeException(ec);
		}
	}

	private void preparseIns(SpokeInstruction[] smi) throws SpokeException {
		HashMap<String, Integer> labels = new HashMap<String, Integer>();

		for (int index = 0; index < smi.length; index++) {
			SpokeInstruction spokeInstruction = smi[index];

			if (spokeInstruction.Type == SpokeInstructionType.CallAnonMethod) {
				preparseIns(spokeInstruction.AnonMethod.Instructions);
			}

			if (spokeInstruction.Type == SpokeInstructionType.Label) {
				if (spokeInstruction.labelGuy == null)
					throw new SpokeException(spokeInstruction.DebugTokens);
				labels.put(spokeInstruction.labelGuy, index);
			}

		}
		for (int index = 0; index < smi.length; index++) {
			SpokeInstruction spokeInstruction = smi[index];

			if (spokeInstruction.Type == SpokeInstructionType.Goto) {

				if (spokeInstruction.gotoGuy == null)
					throw new SpokeException(spokeInstruction.DebugTokens);

				spokeInstruction.Index = labels.get(spokeInstruction.gotoGuy);
			}
			if (spokeInstruction.Type == SpokeInstructionType.IfTrueContinueElseGoto) {
				spokeInstruction.Index = labels.get(spokeInstruction.elseGuy);
			}
			if (spokeInstruction.Type == SpokeInstructionType.IfEqualsContinueAndPopElseGoto) {
				spokeInstruction.Index = labels.get(spokeInstruction.elseGuy);
			}
		}
	}

	private void doit(int curStack, int index, SpokeInstruction[] ins)
			throws Exception {

		for (; index < ins.length; index++) {

			SpokeInstruction spokeInstruction = ins[index];

			if (spokeInstruction.Type == SpokeInstructionType.CallAnonMethod) {
				doit(0, 0, spokeInstruction.AnonMethod.Instructions);
			}

			int stackBefore = spokeInstruction.StackBefore();
			curStack += stackBefore;

			if (curStack < 0) {
				// throw new AbandonedMutexException("Gay");
			}
			if (spokeInstruction.Type == SpokeInstructionType.Return) {
				spokeInstruction.StackBefore_ = curStack;
				spokeInstruction.StackAfter_ = curStack
						+ spokeInstruction.StackAfter();

				return;
			}

			if (spokeInstruction.Type == SpokeInstructionType.Goto) {
				if (spokeInstruction.StackBefore_ > -1) {
					if (spokeInstruction.StackBefore_ != curStack) {
						throw new Exception("Theory gay");
					}
					return;
				} else {
					spokeInstruction.StackBefore_ = curStack;
					if (spokeInstruction.Index > -1) {
						index = spokeInstruction.Index;
					}
				}
			}
			if (spokeInstruction.Type == SpokeInstructionType.IfTrueContinueElseGoto) {
				if (spokeInstruction.StackBefore_ > -1) {
					if (spokeInstruction.StackBefore_ != curStack) {
						throw new Exception("Theory gay");
					}
				} else {
					spokeInstruction.StackBefore_ = curStack;

					doit(curStack, index + 1, ins);
					doit(curStack, spokeInstruction.Index + 1, ins);
				}
				return;
			}

			if (spokeInstruction.Type == SpokeInstructionType.IfEqualsContinueAndPopElseGoto) {
				if (spokeInstruction.StackBefore_ > -1) {
					if (spokeInstruction.StackBefore_ != curStack) {
						throw new Exception("Theory gay");
					}
				} else {
					spokeInstruction.StackBefore_ = curStack;

					doit(curStack - 2, index + 1, ins);
					doit(curStack - 1, spokeInstruction.Index + 1, ins);
				}
				return;
			}

			if (spokeInstruction.StackBefore_ == -1) {
				spokeInstruction.StackBefore_ = curStack;
				spokeInstruction.StackAfter_ = curStack
						+ spokeInstruction.StackAfter();
			} else {
				if (spokeInstruction.StackBefore_ != curStack) {
					throw new Exception("Theory gay");
				}

			}
			curStack += spokeInstruction.StackAfter();
		}
	}

	private ArrayList<HashMap<String, Boolean>> cd = new ArrayList<HashMap<String, Boolean>>();

	public class SpokeMethodParse {
		public SpokeType RunningClass;
		public SpokeType ForYieldArray;
		private SpokeType vr;

		public SpokeMethodParse(SpokeType runningClass2) {
			RunningClass = runningClass2;

		}

		public SpokeType getReturnType() {
			return vr;
		}

		public void setReturnType(SpokeType value) {

			vr = value;
			if (Method != null) {
				Method.returnType = vr;

			}
		}

		public SpokeMethod Method;
	}

	private HashMap<String, Boolean> ame;

	private HashMap<String, Boolean> getanonMethodsEntered() {
		if (ame != null) {
			if (cd.size() > 0) {
				if (ALH.Last(cd).size() != ame.size()) {
					cd.add(new HashMap<String, Boolean>(ame));
				}
			} else
				cd.add(new HashMap<String, Boolean>(ame));
		}

		return ame;
	}

	private void setanonMethodsEntered(HashMap<String, Boolean> value) {
		ame = value;
	}

	ArrayList<String> stackTrace = new ArrayList<String>();

	private SpokeType evaluateMethod(SpokeMethod fm, SpokeType parent,
			SpokeType[] paras) throws SpokeException {
		String me = fm.Class.Name + "   " + fm.MethodName;
		int counted = 0;
		for (String st : stackTrace) {
			if (st.equals(me)) {
				counted++;
			}
		}
		if (counted > 10) {
			return fm.returnType;
		}

		stackTrace.add(me);
		if (fm.Evaluated) {
			parent.Variables = fm.ParentVariableRefs;
			boolean dont = true;
			for (SpokeType st : paras) {
				if (st.AnonMethod != null) {
					dont = false;
					break;
				}
			}
			if (!dont) {
				for (int i = 0; i < fm.Parameters.length; i++) {
					if (!fm.MethodVariableInfo.Set(i, paras[i])) {
						System.out.print("Fail");
					}
				}

				ArrayList<SpokeInstruction> toCotninue = SpokeInstruction.ins;

				SpokeInstruction.Beginner();
				evaluateLines(fm.Lines, fm.OnlyMethodParse,
						fm.MethodVariableInfo, true);
				SpokeInstruction.Ender();
				SpokeInstruction.ins = toCotninue;

			}

			stackTrace.remove(stackTrace.size() - 1);
			return fm.returnType;
		}
		HashMap<String, Boolean> db = getanonMethodsEntered();
		setanonMethodsEntered(new HashMap<String, Boolean>());
		fm.Evaluated = true;
		SpokeVariableInfo variables = new SpokeVariableInfo();
		for (int i = 0; i < fm.Parameters.length; i++) {
			variables.Add(fm.Parameters[i].Name, paras[i], null, false);
		}

		boolean lastStatic = currentlyStaticMethod;

		currentlyStaticMethod = fm.Static;

		SpokeMethodParse sm = new SpokeMethodParse(parent);

		sm.Method = fm;
		if (fm.HasYield) {
			sm.ForYieldArray = new SpokeType(ObjectType.Unset);
			sm.setReturnType(new SpokeType(ObjectType.Array));
			sm.getReturnType().ArrayItemType = sm.ForYieldArray;
		} else {
			sm.setReturnType(new SpokeType(ObjectType.Unset));
		}
		fm.OnlyMethodParse = sm;
		fm.ParentVariableRefs = parent.Variables;
		fm.MethodVariableInfo = variables;

		ArrayList<SpokeInstruction> toCotninue = SpokeInstruction.ins;

		SpokeInstruction.Beginner();
		Tuple2<SpokeType, SpokeLine[]> de;

		de = evaluateLines(fm.Lines, sm, variables, false);

		fm.Lines = de.Item2;
		fm.Instructions = SpokeInstruction.Ender();
		SpokeInstruction.ins = toCotninue;

		fm.NumOfVars = variables.index;

		setanonMethodsEntered(db);

		currentlyStaticMethod = lastStatic;
		stackTrace.remove(stackTrace.size() - 1);
		return de.Item1;
	}

	boolean currentlyStaticMethod;

	public SpokeType evaluateItem(SpokeItem condition,
			SpokeMethodParse currentObject, boolean alreadyEvaluated,
			SpokeVariableInfo variables) throws SpokeException {
		return evaluateItem(condition, currentObject, alreadyEvaluated,
				variables, false);
	}

	private Tuple2<SpokeType, SpokeLine[]> evaluateLines(SpokeLine[] lines,
			SpokeMethodParse currentObject, SpokeVariableInfo variables,
			boolean alreadyEvaluated) throws SpokeException {
		ArrayList<SpokeLine> ln = new ArrayList<SpokeLine>();
		for (SpokeLine s : lines) {
			ln.add(s);
		}

		int jumpIndex = 0;

		for (int index = 0; index < lines.length; index++) {
			SpokeLine spokeLine = lines[index];
			try {

				SpokeType df;
				ArrayList<SpokeInstruction> ddsf;

				SpokeInstruction.RaiseDebugging(((SpokeBasic) spokeLine));

				switch (spokeLine.getLType()) {
				case If:
					evaluateIf(currentObject, variables, alreadyEvaluated,
							(SpokeIf) spokeLine);

					break;
				case Switch:
					evaluateSwitch(currentObject, variables, alreadyEvaluated,
							(SpokeSwitch) spokeLine);

					break;
				case Return:
					int va;
					if (((SpokeReturn) spokeLine).Return == null) {
						currentObject.setReturnType(new SpokeType(
								ObjectType.Null));
						new SpokeInstruction(SpokeInstructionType.Null);

						if (getanonMethodsEntered().size() == 0
								|| ALH.All(getanonMethodsEntered(),
										new HashFinder<String, Boolean>() {

											@Override
											public boolean Find(String item,
													Boolean item2) {
												return !item2;
											}
										}))
							new SpokeInstruction(SpokeInstructionType.Return);
						else {
							new SpokeInstruction(
									SpokeInstructionType.StoreLocalObject,
									va = variables.Add(
											"__tmpReturn"
													+ ((SpokeReturn) spokeLine)
															.getGuid(),
											currentObject.getReturnType(),
											null, alreadyEvaluated));
							new SpokeInstruction(SpokeInstructionType.GetLocal,
									va);
							new SpokeInstruction(SpokeInstructionType.Goto,
									ALH.Last(getanonMethodsEntered(),
											new HashFinder<String, Boolean>() {
												@Override
												public boolean Find(
														String item,
														Boolean item2) {
													return item2;
												}
											}).Item2);
						}
					} else {
						df = evaluateItem(((SpokeReturn) spokeLine).Return,
								currentObject, alreadyEvaluated, variables);

						if (currentObject.getReturnType().CompareTo(df, true)) {
							if (currentObject.getReturnType().Type == ObjectType.Unset) {
								currentObject.setReturnType(df);
							}
						} else {
							throw new Exception("for return:    Expected "
									+ currentObject.getReturnType().Type
									+ " Got" + df.Type);
						}

						if (getanonMethodsEntered().size() == 0
								|| ALH.All(getanonMethodsEntered(),
										new HashFinder<String, Boolean>() {

											@Override
											public boolean Find(String item,
													Boolean item2) {
												return !item2;
											}
										}))
							new SpokeInstruction(SpokeInstructionType.Return);
						else {
							setInstru(
									currentObject.getReturnType().Type,
									va = variables.Add(
											"__tmpReturn"
													+ ((SpokeReturn) spokeLine)
															.getGuid(),
											currentObject.getReturnType(),
											null, alreadyEvaluated));

							new SpokeInstruction(SpokeInstructionType.GetLocal,
									va);

							Tuple2<String, Boolean> gc = ALH.Last(
									getanonMethodsEntered(),
									new HashFinder<String, Boolean>() {
										@Override
										public boolean Find(String item,
												Boolean item2) {
											return item2;
										}
									});
							new SpokeInstruction(SpokeInstructionType.Goto,
									gc.Item1);
						}

					}

					break;
				case Yield:
					ddsf = new ArrayList<SpokeInstruction>(SpokeInstruction.ins);
					df = evaluateItem(((SpokeYield) spokeLine).Yield,
							currentObject, alreadyEvaluated, variables);
					SpokeInstruction.ins = ddsf;
					if (currentObject.ForYieldArray.CompareTo(df, true)) {
						if (currentObject.ForYieldArray.Type == ObjectType.Unset) {
							currentObject.ForYieldArray = df;
						}
					} else {
						throw new Exception("for yield:    Expected "
								+ currentObject.ForYieldArray.Type + " Got"
								+ df.Type);
					}

					break;
				case YieldReturn:
					ddsf = new ArrayList<SpokeInstruction>(SpokeInstruction.ins);
					df = evaluateItem(
							((SpokeYieldReturn) spokeLine).YieldReturn,
							currentObject, alreadyEvaluated, variables);
					SpokeInstruction.ins = ddsf;
					if (currentObject.ForYieldArray.CompareTo(df, true)) {
						if (currentObject.ForYieldArray.Type == ObjectType.Unset) {
							currentObject.ForYieldArray = df;
						}
					} else {
						throw new Exception("for yield:    Expected "
								+ currentObject.ForYieldArray.Type + " Got"
								+ df.Type);
					}

					break;
				case MethodCall:
					SpokeType def = evaluateItem((SpokeItem) spokeLine,
							currentObject, alreadyEvaluated, variables, true);
					if (def == null) {
						// Console.WriteLine("A");
					} else
						// if (def.Type == ObjectType.Void)
						// {
						// new SpokeInstruction(SpokeInstructionType.PopStack);
						// }
						new SpokeInstruction(SpokeInstructionType.PopStack).DebugToks = null;

					// no care about the typiola
					break;
				case AnonMethod:
					SpokeType arm = evaluateItem((SpokeItem) spokeLine,
							currentObject, alreadyEvaluated, variables);

					if (((SpokeAnonMethod) spokeLine).HasReturn) {

						if (arm == null) {
							throw new Exception("AIDS");
						}
						if (currentObject.getReturnType().CompareTo(arm, true)) {
							if (currentObject.getReturnType().Type == ObjectType.Unset) {
								currentObject.setReturnType(arm);
							}
						} else {
							throw new Exception("for anonMethod:    Expected "
									+ currentObject.getReturnType().Type
									+ " Got" + arm.Type);
						}
					} else if (((SpokeAnonMethod) spokeLine).HasYield
							|| ((SpokeAnonMethod) spokeLine).HasYieldReturn) {
						// ln[index] = new SpokeReturn() { Return =
						// (SpokeAnonMethod)spokeLine };

						// if (!getanonMethodsEntered().Any())
						// new SpokeInstruction(SpokeInstructionType.Return);
						// else
						// {
						// new SpokeInstruction(SpokeInstructionType.Goto,
						// lastAnonMethodEntered().Item1);
						// }

						ln.add(index + 1, new SpokeReturn(
								((SpokeAnonMethod) spokeLine)));
						ln.remove(spokeLine);
						jumpIndex++;

					}
					// else
					// new SpokeInstruction(SpokeInstructionType.PopStack);

					// if ((((SpokeAnonMethod)spokeLine).Lines[0] instanceof
					// SpokeEqual &&
					// ((SpokeEqual)((SpokeAnonMethod)spokeLine).Lines[0]).RightSide.IType==ISpokeItem.Array))
					// {

					// }

					// ln.Insert(index + jumpIndex,
					// ((SpokeAnonMethod)spokeLine).Lines[0]);

					// jumpIndex++;

					// var dfe = new
					// ArrayList<SpokeLine>(((SpokeAnonMethod)spokeLine).Lines);
					// dfe.RemoveAt(0);
					// ((SpokeAnonMethod)spokeLine).Lines = dfe.ToArray();

					if (arm == null) {
						continue;
					}
					// var barm = new SpokeType(ObjectType.Array) {
					// ArrayItemType =
					// arm, ClassName = "Array" };

					if (currentObject.getReturnType().CompareTo(arm, true)) {
						if (currentObject.getReturnType().Type == ObjectType.Unset) {
							currentObject.setReturnType(arm);
						}
					} else {
						throw new Exception("for anonMethod:    Expected "
								+ currentObject.ForYieldArray.Type + " Got"
								+ arm.Type);
					}

					break;
				case Construct:

					SpokeType d = evaluateItem((SpokeItem) spokeLine,
							currentObject, alreadyEvaluated, variables);

					SpokeInstruction.LowerDebugging();

					return new Tuple2<SpokeType, SpokeLine[]>(d, lines);

				case Set:

					SpokeEqual grf = ((SpokeEqual) spokeLine);

					SpokeType right = evaluateItem(grf.RightSide,
							currentObject, alreadyEvaluated, variables);

					forSet = true;
					SpokeType left;
					switch (right.Type) {
					case Null:
						throw new Exception("Set bad");
					case Int:
						left = evaluateItem(grf.LeftSide, currentObject,
								alreadyEvaluated, variables);
						if (!left.CompareTo(right, true)) {
							// throw new Exception("Set bad");
						}

						break;
					case String:
						left = evaluateItem(grf.LeftSide, currentObject,
								alreadyEvaluated, variables);
						if (!left.CompareTo(right, true))
							throw new Exception("Set bad");

						break;
					case Float:
						left = evaluateItem(grf.LeftSide, currentObject,
								alreadyEvaluated, variables);
						if (!left.CompareTo(right, true)) {
							// throw new Exception("Set bad");
						}

						break;
					case Bool:
						left = evaluateItem(grf.LeftSide, currentObject,
								alreadyEvaluated, variables);
						if (!left.CompareTo(right, true))
							throw new Exception("Set bad");
						break;
					case Array:
						left = evaluateItem(grf.LeftSide, currentObject,
								alreadyEvaluated, variables);
						if (!left.CompareTo(right, true))
							throw new Exception("Set bad");
						break;
					case Object:
						left = evaluateItem(grf.LeftSide, currentObject,
								alreadyEvaluated, variables);
						if (!left.CompareTo(right, true))
							throw new Exception("Set bad");
						left.ClassName = right.ClassName;
						if (right.Variables != null)
							left.Variables = new SpokeVariableInfo(
									right.Variables);

						break;
					case Method:
						left = evaluateItem(grf.LeftSide, currentObject,
								alreadyEvaluated, variables);
						if (!left.CompareTo(right, true))
							if (!(left.Type == ObjectType.Object && left.Variables.index == 0)) {
								throw new Exception("Set bad");
							} else {
								left.ClassName = right.ClassName;
								left.AnonMethod = right.AnonMethod;
								left.Type = right.Type;
								left.ArrayItemType = right.ArrayItemType;
								if (right.Variables != null)
									left.Variables = new SpokeVariableInfo(
											right.Variables);
								left.MethodType = right.MethodType;

							}

						break;
					default:
						throw new Exception("bad");
					}

					switch (ALH.Last(SpokeInstruction.ins).Type) {
					case GetGlobal:
						switch (right.Type) {
						case Null:
							SpokeInstruction.ins.get(SpokeInstruction.ins
									.size() - 1).Type = SpokeInstructionType.StoreGlobalObject;
							break;
						case Int:
							SpokeInstruction.ins.get(SpokeInstruction.ins
									.size() - 1).Type = SpokeInstructionType.StoreGlobalInt;
							break;
						case Float:
							SpokeInstruction.ins.get(SpokeInstruction.ins
									.size() - 1).Type = SpokeInstructionType.StoreGlobalFloat;
							break;
						case String:
							SpokeInstruction.ins.get(SpokeInstruction.ins
									.size() - 1).Type = SpokeInstructionType.StoreGlobalString;
							break;
						case Bool:
							SpokeInstruction.ins.get(SpokeInstruction.ins
									.size() - 1).Type = SpokeInstructionType.StoreGlobalBool;
							break;
						case Array:
							SpokeInstruction.ins.get(SpokeInstruction.ins
									.size() - 1).Type = SpokeInstructionType.StoreGlobalObject;
							break;
						case Object:
							SpokeInstruction.ins.get(SpokeInstruction.ins
									.size() - 1).Type = SpokeInstructionType.StoreGlobalObject;
							break;
						default:
							throw new Exception("bad");
						}

						break;
					case GetLocal:

						if (left.ByRef) {
							SpokeInstruction.ins.get(SpokeInstruction.ins
									.size() - 1).Type = SpokeInstructionType.StoreLocalRef;
						} else {
							switch (right.Type) {
							case Null:
								SpokeInstruction.ins.get(SpokeInstruction.ins
										.size() - 1).Type = SpokeInstructionType.StoreLocalObject;
								break;
							case Int:
								SpokeInstruction.ins.get(SpokeInstruction.ins
										.size() - 1).Type = SpokeInstructionType.StoreLocalInt;
								break;
							case Float:
								SpokeInstruction.ins.get(SpokeInstruction.ins
										.size() - 1).Type = SpokeInstructionType.StoreLocalFloat;
								break;
							case String:
								SpokeInstruction.ins.get(SpokeInstruction.ins
										.size() - 1).Type = SpokeInstructionType.StoreLocalString;
								break;
							case Bool:
								SpokeInstruction.ins.get(SpokeInstruction.ins
										.size() - 1).Type = SpokeInstructionType.StoreLocalBool;
								break;
							case Array:
								SpokeInstruction.ins.get(SpokeInstruction.ins
										.size() - 1).Type = SpokeInstructionType.StoreLocalObject;
								break;
							case Object:
								SpokeInstruction.ins.get(SpokeInstruction.ins
										.size() - 1).Type = SpokeInstructionType.StoreLocalObject;
								break;
							case Method:
								SpokeInstruction.ins.get(SpokeInstruction.ins
										.size() - 1).Type = SpokeInstructionType.StoreLocalMethod;
								break;
							case Void:
								break;
							default:
								throw new Exception("bad");
							}
						}

						break;
					case GetField:

						switch (right.Type) {
						case Null:
							SpokeInstruction.ins.get(SpokeInstruction.ins
									.size() - 1).Type = SpokeInstructionType.StoreFieldObject;
							break;
						case Int:
							SpokeInstruction.ins.get(SpokeInstruction.ins
									.size() - 1).Type = SpokeInstructionType.StoreFieldInt;
							break;
						case Float:
							SpokeInstruction.ins.get(SpokeInstruction.ins
									.size() - 1).Type = SpokeInstructionType.StoreFieldFloat;
							break;
						case String:
							SpokeInstruction.ins.get(SpokeInstruction.ins
									.size() - 1).Type = SpokeInstructionType.StoreFieldString;
							break;
						case Bool:
							SpokeInstruction.ins.get(SpokeInstruction.ins
									.size() - 1).Type = SpokeInstructionType.StoreFieldBool;
							break;
						case Array:
							SpokeInstruction.ins.get(SpokeInstruction.ins
									.size() - 1).Type = SpokeInstructionType.StoreFieldObject;
							break;
						case Object:
							SpokeInstruction.ins.get(SpokeInstruction.ins
									.size() - 1).Type = SpokeInstructionType.StoreFieldObject;
							break;
						case Method:
							SpokeInstruction.ins.get(SpokeInstruction.ins
									.size() - 1).Type = SpokeInstructionType.StoreFieldMethod;
							break;
						case Void:
							break;
						default:
							throw new Exception("bad");
						}

						break;
					case ArrayElem:
						SpokeInstruction.ins
								.get(SpokeInstruction.ins.size() - 1).Type = SpokeInstructionType.StoreArrayElem;
						break;
					}

					if (left.Type == ObjectType.Unset) {
						left.ClassName = right.ClassName;
						left.AnonMethod = right.AnonMethod;
						left.Type = right.Type;
						left.ArrayItemType = right.ArrayItemType;
						if (right.Variables != null)
							left.Variables = new SpokeVariableInfo(
									right.Variables);
						left.MethodType = right.MethodType;
					}

					else {
						if (!left.CompareTo(right, false)) {
							// throw new Exception("hmm");
						}
					}
					forSet = false;

					break;

				default:
					throw new Exception("bad");
				}
			} catch (SpokeException egh) {
				throw egh;
			} catch (Exception egh) {
				throw new SpokeException(egh,
						((SpokeBasic) spokeLine).getTokens());
			}
			SpokeInstruction.LowerDebugging();

		}

		SpokeLine[] sl = new SpokeLine[ln.size()];
		ln.toArray(sl);
		lines = sl;

		if (currentObject.getReturnType() != null)

			return new Tuple2<SpokeType, SpokeLine[]>(
					currentObject.getReturnType(), lines);
		return new Tuple2<SpokeType, SpokeLine[]>(null, lines);

	}

	private void evaluateIf(SpokeMethodParse currentObject,
			SpokeVariableInfo variables, boolean alreadyEvaluated,
			SpokeIf spokeLine) throws Exception {
		SpokeType df;
		SpokeType b = evaluateItem(spokeLine.Condition, currentObject,
				alreadyEvaluated, variables);

		if (b.Type != ObjectType.Bool) {
			throw new Exception("Expected bool");
		}

		new SpokeInstruction(
				SpokeInstructionType.IfTrueContinueElseGoto,
				(spokeLine.ElseLines == null && spokeLine.ElseIf == null) ? "EndIf"
						+ spokeLine.getGuid()
						: "ElseIf" + spokeLine.getGuid());

		variables.IncreaseState();
		Tuple2<SpokeType, SpokeLine[]> dfc = evaluateLines(spokeLine.IfLines,
				currentObject, variables, alreadyEvaluated);
		spokeLine.IfLines = dfc.Item2;
		df = dfc.Item1;

		variables.DecreaseState();
		if (currentObject.getReturnType() != null
				&& !currentObject.getReturnType().CompareTo(df, true)) {
			throw new Exception("for return:    Expected "
					+ currentObject.getReturnType().Type + " Got" + df.Type);
		}
		if (spokeLine.ElseIf != null) {
			new SpokeInstruction(SpokeInstructionType.Goto, "EndIf"
					+ spokeLine.getGuid());
			new SpokeInstruction(SpokeInstructionType.Label, "ElseIf"
					+ spokeLine.getGuid());

			evaluateIf(currentObject, variables, alreadyEvaluated,
					spokeLine.ElseIf);

		}
		if (spokeLine.ElseLines != null) {
			new SpokeInstruction(SpokeInstructionType.Goto, "EndIf"
					+ spokeLine.getGuid());
			new SpokeInstruction(SpokeInstructionType.Label, "ElseIf"
					+ spokeLine.getGuid());

			dfc = evaluateLines(spokeLine.ElseLines, currentObject, variables,
					alreadyEvaluated);
			spokeLine.ElseLines = dfc.Item2;
			df = dfc.Item1;
			{
				if (currentObject.getReturnType() != null
						&& !currentObject.getReturnType().CompareTo(df, true)) {
					throw new Exception("for return:    Expected "
							+ currentObject.getReturnType().Type + " Got"
							+ df.Type);
				}
			}
		}
		new SpokeInstruction(SpokeInstructionType.Label, "EndIf"
				+ spokeLine.getGuid());
	}

	private void evaluateSwitch(SpokeMethodParse currentObject,
			SpokeVariableInfo variables, boolean alreadyEvaluated,
			SpokeSwitch spokeLine) throws Exception {
		SpokeType df;
		SpokeType b = evaluateItem(spokeLine.Condition, currentObject,
				alreadyEvaluated, variables);

		for (int i = 0; i < spokeLine.Cases.length; i++) {
			Case c = spokeLine.Cases[i];

			if (i != 0) {
				new SpokeInstruction(SpokeInstructionType.Label, "Case" + i
						+ "_" + spokeLine.getGuid());
			}

			SpokeType curCase = evaluateItem(c.Item, currentObject,
					alreadyEvaluated, variables);

			if (b.Type != curCase.Type) {
				throw new Exception("Expected " + b.Type);
			}

			if ((i + 1 == spokeLine.Cases.length)) {

				new SpokeInstruction(SpokeInstructionType.Equal);

				new SpokeInstruction(
						SpokeInstructionType.IfTrueContinueElseGoto,
						"EndSwitch" + spokeLine.getGuid());

			} else {
				new SpokeInstruction(
						SpokeInstructionType.IfEqualsContinueAndPopElseGoto,
						"Case" + (i + 1) + "_" + spokeLine.getGuid());

				if (c.Lines.length == 0) {

					for (int j = i + 1; j < spokeLine.Cases.length; j++) {
						Case cj = spokeLine.Cases[j];
						if (cj.Lines.length > 0) {
							cj.NeedsTop=true;
							new SpokeInstruction(SpokeInstructionType.Goto,"top"+"Case" + (j) + "_" + spokeLine.getGuid());
							break;
						}
					}
				}
			}
			
			
			if (c.NeedsTop){
				new SpokeInstruction(SpokeInstructionType.Label,"top"+"Case" + (i) + "_" + spokeLine.getGuid());
				
			}
			variables.IncreaseState();
			Tuple2<SpokeType, SpokeLine[]> dfc = evaluateLines(c.Lines,
					currentObject, variables, alreadyEvaluated);
			c.Lines = dfc.Item2;
			df = dfc.Item1;

			variables.DecreaseState();
			if (currentObject.getReturnType() != null
					&& !currentObject.getReturnType().CompareTo(df, true)) {
				throw new Exception("for return:    Expected "
						+ currentObject.getReturnType().Type + " Got" + df.Type);
			}

			new SpokeInstruction(SpokeInstructionType.Goto, "EndSwitch"
					+ spokeLine.getGuid());

		}

		new SpokeInstruction(SpokeInstructionType.Label, "EndSwitch"
				+ spokeLine.getGuid());

	}

	private void setInstru(ObjectType type, int index) throws Exception {
		switch (type) {
		case Null:
			new SpokeInstruction(SpokeInstructionType.StoreLocalObject, index);

			break;
		case Int:
			new SpokeInstruction(SpokeInstructionType.StoreLocalInt, index);

			break;
		case Float:
			new SpokeInstruction(SpokeInstructionType.StoreLocalFloat, index);

			break;
		case String:
			new SpokeInstruction(SpokeInstructionType.StoreLocalString, index);

			break;
		case Bool:
			new SpokeInstruction(SpokeInstructionType.StoreLocalBool, index);

			break;
		case Array:
			new SpokeInstruction(SpokeInstructionType.StoreLocalObject, index);

			break;
		case Object:
			new SpokeInstruction(SpokeInstructionType.StoreLocalObject, index);

			break;
		case Method:
			new SpokeInstruction(SpokeInstructionType.StoreLocalMethod, index);

			break;
		case Void:
			break;
		default:
			throw new Exception("bad");
		}
	}

	private boolean forSet;

	private void resetGuids(SpokeLine l) {
		if (l instanceof SpokeBasic) {
			((SpokeBasic) l).resetGUID();
		}
		if (l instanceof SpokeLines) {
			SpokeLine[] gm = ((SpokeLines) l).getLines();

			for (int i = 0; i < gm.length; i++) {
				resetGuids(gm[i]);
			}
		}

	}

	private void fuix(ArrayList<SpokeLine> lines, SpokeVariable vr,
			boolean remove) {
		for (int index = lines.size() - 1; index >= 0; index--) {
			SpokeLine e = lines.get(index);

			if (e instanceof SpokeYield) {
				if (remove) {
					// lines.Add(new SpokeReturn() { Return = vr });
					lines.remove(e);
				} else {
					SpokeItem sv = new SpokeVariable("add",
							(SpokeItem) new SpokeVariable(vr.VariableIndex,
									vr.VariableName, vr.getParent()));
					lines.add(index, new SpokeMethodCall(sv, new SpokeItem[] {
							new SpokeCurrent(), ((SpokeYield) e).Yield }));
				}

			} else if (e instanceof SpokeYieldReturn) {
				if (remove) {
					// lines.Add(new SpokeReturn() { Return = vr });
					lines.remove(e);
				} else {
					SpokeItem sv = new SpokeVariable("add", new SpokeVariable(
							vr.VariableIndex, vr.VariableName, vr.getParent()));
					lines.add(index, new SpokeMethodCall(sv, new SpokeItem[] {
							new SpokeCurrent(), ((SpokeYield) e).Yield }));
				}
			}

			if (e instanceof SpokeLines && (!(e instanceof SpokeAnonMethod))) {

				if (e instanceof SpokeIf) {

					ArrayList<SpokeLine> df = new ArrayList<SpokeLine>(
							Arrays.asList(((SpokeIf) e).IfLines));
					fuix(df, vr, remove);

					SpokeLine[] sl = new SpokeLine[df.size()];
					df.toArray(sl);
					((SpokeIf) e).IfLines = sl;

					if (((SpokeIf) e).ElseLines != null) {
						df = new ArrayList<SpokeLine>(
								Arrays.asList(((SpokeIf) e).ElseLines));
						fuix(df, vr, remove);

						sl = new SpokeLine[df.size()];
						df.toArray(sl);
						((SpokeIf) e).ElseLines = sl;

					}

				} else {
					ArrayList<SpokeLine> df = new ArrayList<SpokeLine>(
							Arrays.asList(((SpokeLines) e).getLines()));
					fuix(df, vr, remove);
					SpokeLine[] fc = new SpokeLine[df.size()];
					df.toArray(fc);
					((SpokeLines) e).setLines(fc);
				}
			}
		}
	}

	public SpokeType evaluateItem(SpokeItem condition,
			final SpokeMethodParse currentObject, boolean alreadyEvaluated,
			SpokeVariableInfo variables, boolean parentIsNull)
			throws SpokeException {
		SpokeVariable fyv = null;
		SpokeType r;
		SpokeType l;

		try {
			SpokeInstruction.RaiseDebugging(((SpokeBasic) condition));
			SpokeItem[] ij;
			switch (condition.getIType()) {

			case Current:
				new SpokeInstruction(SpokeInstructionType.GetLocal, 0);
				SpokeInstruction.LowerDebugging();
				return currentObject.RunningClass;
			case Null:
				new SpokeInstruction(SpokeInstructionType.Null);
				SpokeInstruction.LowerDebugging();
				return new SpokeType(ObjectType.Null);
			case String:
				new SpokeInstruction(SpokeInstructionType.StringConstant,
						((SpokeString) condition).Value);
				SpokeInstruction.LowerDebugging();
				return new SpokeType(ObjectType.String);
			case Bool:
				new SpokeInstruction(SpokeInstructionType.BoolConstant,
						((SpokeBool) condition).Value);
				SpokeInstruction.LowerDebugging();
				return new SpokeType(ObjectType.Bool);
			case Float:
				new SpokeInstruction(SpokeInstructionType.FloatConstant,
						((SpokeFloat) condition).Value);
				SpokeInstruction.LowerDebugging();
				return new SpokeType(ObjectType.Float);
			case Int:

				new SpokeInstruction(SpokeInstructionType.IntConstant,
						((SpokeInt) condition).Value);
				SpokeInstruction.LowerDebugging();
				return new SpokeType(ObjectType.Int);

			case Array:
				SpokeType ar = new SpokeType(ObjectType.Array);
				new SpokeInstruction(SpokeInstructionType.CreateArray);

				if (((SpokeArray) condition).Type != null) {
					if (ar.ArrayItemType.CompareTo(
							((SpokeArray) condition).Type, true)) {
						if (ar.ArrayItemType.Type == ObjectType.Unset) {
							ar.ArrayItemType = ((SpokeArray) condition).Type;
						}
						SpokeInstruction.LowerDebugging();

						return ar;
					} else
						throw new Exception("bad array");
				}

				for (int i = 0; i < ((SpokeArray) condition).Parameters.length; i++) {
					SpokeItem spokeItem = ((SpokeArray) condition).Parameters[i];

					SpokeType grb = evaluateItem(spokeItem, currentObject,
							alreadyEvaluated, variables);
					/*
					 * if (grb.Type == ObjectType.Array) { if
					 * (ar.ArrayItemType.CompareTo(grb.ArrayItemType, true)) {
					 * if (ar.ArrayItemType.Type == ObjectType.Unset) {
					 * ar.ArrayItemType = grb.ArrayItemType; } } else throw new
					 * Exception("bad array");
					 * 
					 * new
					 * SpokeInstruction(SpokeInstructionType.AddRangeToArray);
					 * 
					 * }
					 * 
					 * else
					 */{
						if (ar.ArrayItemType.CompareTo(grb, true)) {
							if (ar.ArrayItemType.Type == ObjectType.Unset) {
								ar.ArrayItemType = grb;
							}
						} else
							throw new Exception("bad array");

						new SpokeInstruction(SpokeInstructionType.AddToArray);

					}
				}
				SpokeInstruction.LowerDebugging();

				return ar;
			case Variable:
				SpokeType g;
				final SpokeVariable mv = ((SpokeVariable) condition);

				if (mv.getParent() != null) {
					// enum code
					if (mv.getParent() instanceof SpokeVariable) {
						SpokeEnum enu;
						if ((enu = ALH.FirstOrDefault(_enu,
								new Finder<SpokeEnum>() {
									@Override
									public boolean Find(SpokeEnum item) {
										return item.Name.equals(((SpokeVariable) mv
												.getParent()).VariableName);
									}
								})) != null) {

							((TokenWord) (((SpokeVariable) mv.getParent())
									.getTokens()[0]))
									.SetTokenType(SpokeTokenType.Enum);

							Object gc;
							if ((gc = enu.Variables.get(mv.VariableName)) != null) {

								SpokeInstruction.LowerDebugging();
								SpokeInstruction
										.RaiseDebugging(((SpokeBasic) mv
												.getParent()));

								switch (enu.Type) {
								case Int:
									new SpokeInstruction(
											SpokeInstructionType.IntConstant,
											(Integer) gc);
									break;
								case Float:
									new SpokeInstruction(
											SpokeInstructionType.FloatConstant,
											(Float) gc);
									break;
								case String:
									new SpokeInstruction(
											SpokeInstructionType.StringConstant,
											(String) gc);
									break;
								}
								SpokeInstruction.LowerDebugging();

								return new SpokeType(enu.Type);
							}

						}
					}

					boolean fs = forSet;
					forSet = false;

					SpokeType ca = evaluateItem(mv.getParent(), currentObject,
							alreadyEvaluated, variables);

					if (ca.Type == ObjectType.Global) {
						SpokeGlobalVariable gc;
						if ((gc = ALH.FirstOrDefault(_glo.Variables,
								new Finder<SpokeGlobalVariable>() {
									@Override
									public boolean Find(SpokeGlobalVariable item) {
										return item.Name
												.equals(mv.VariableName);
									}
								})) != null) {

							new SpokeInstruction(
									SpokeInstructionType.GetGlobal, gc.Index,
									mv.VariableName);
							SpokeInstruction.LowerDebugging();

							return gc.Type;
						}
						throw new Exception("bad global " + mv.VariableName);
					}
					if (ca.Variables == null) {
						throw new Exception("Cannot find " + mv);
					}
					if ((g = ca.Variables.TryGetValue(mv.VariableName, mv)) != null) {
						if (fs && !g.ByRef) {
							mv.ForSet = true;
							ca.Variables.Reset(mv.VariableName,
									g = new SpokeType(ObjectType.Unset), mv);
						}

						new SpokeInstruction(SpokeInstructionType.GetField,
								mv.VariableIndex, mv.VariableName);
						SpokeInstruction.LowerDebugging();

						return g;
					}

					mv.VariableIndex = ca.Variables.Add(mv.VariableName,
							g = new SpokeType(ObjectType.Unset), mv,
							alreadyEvaluated);

					new SpokeInstruction(SpokeInstructionType.GetField,
							mv.VariableIndex, mv.VariableName);
					SpokeInstruction.LowerDebugging();

					return g;

				}

				if (mv.VariableName.equals("this")) {
					new SpokeInstruction(SpokeInstructionType.GetLocal, 0,
							"this");
					((TokenWord) mv.getTokens()[0])
							.SetTokenType(SpokeTokenType.Keyword);
					SpokeInstruction.LowerDebugging();

					return currentObject.RunningClass;
				}
				if (mv.VariableName.toLowerCase().equals("global")) {
					SpokeInstruction.LowerDebugging();

					return new SpokeType(ObjectType.Global);
				}

				if ((g = currentObject.RunningClass.Variables.TryGetValue(
						mv.VariableName, mv)) != null) {
					mv.This = true;
					new SpokeInstruction(SpokeInstructionType.GetLocal, 0,
							"this").DebugToks = null;

					if (forSet && g.ByRef == false) {
						forSet = false;
						SpokeType ddd = currentObject.RunningClass.Variables
								.Get(mv.VariableName, mv);

						mv.ForSet = true;
						currentObject.RunningClass.Variables.Reset(
								mv.VariableName, g = new SpokeType(ddd), mv);

					}
					new SpokeInstruction(SpokeInstructionType.GetField,
							mv.VariableIndex, mv.VariableName);
					SpokeInstruction.LowerDebugging();

					return g;
				}

				if ((g = variables.TryGetValue(mv.VariableName, mv)) != null) {
					if (forSet && g.ByRef == false) {
						forSet = false;
						mv.ForSet = true;
						variables.Reset(mv.VariableName, g = new SpokeType(
								ObjectType.Unset), mv);
					}

					((TokenWord) new SpokeInstruction(
							SpokeInstructionType.GetLocal, mv.VariableIndex,
							mv.VariableName).DebugToks[0]).VariableIndex = mv.VariableIndex;

					SpokeInstruction.LowerDebugging();

					return g;
				}

				if (forSet) {
					forSet = false;
					mv.ForSet = true;
				}

				((SpokeVariable) condition).VariableIndex = variables.Add(
						((SpokeVariable) condition).VariableName,
						g = new SpokeType(ObjectType.Unset), mv,
						alreadyEvaluated);

				new SpokeInstruction(SpokeInstructionType.GetLocal,
						mv.VariableIndex, mv.VariableName);
				SpokeInstruction.LowerDebugging();

				return g;
			case ArrayIndex:

				boolean fs_ = forSet;
				forSet = false;
				SpokeType pa = evaluateItem(
						((SpokeArrayIndex) condition).getParent(),
						currentObject, alreadyEvaluated, variables);

				SpokeType ind = evaluateItem(
						((SpokeArrayIndex) condition).Index, currentObject,
						alreadyEvaluated, variables);

				if (ind.ArrayItemType != null
						&& ind.ArrayItemType.Type == ObjectType.Int) {

					int dexes;
					int orig;
					SpokeType gde;
					new SpokeInstruction(SpokeInstructionType.StoreLocalObject,
							dexes = variables.Add(
									"_dexes" + condition.getGuid(),
									gde = new SpokeType(ObjectType.Array),
									null, alreadyEvaluated));
					gde.ArrayItemType = new SpokeType(ObjectType.Int);

					new SpokeInstruction(SpokeInstructionType.StoreLocalObject,
							orig = variables.Add("_orig" + condition.getGuid(),
									pa, null, alreadyEvaluated));

					int arg;
					new SpokeInstruction(SpokeInstructionType.CreateArray);
					new SpokeInstruction(SpokeInstructionType.StoreLocalObject,
							arg = variables.Add("_rev" + condition.getGuid(),
									pa, null, alreadyEvaluated));

					int loc;

					new SpokeInstruction(SpokeInstructionType.IntConstant, 0);
					new SpokeInstruction(SpokeInstructionType.StoreLocalInt,
							loc = variables.Add("__ind" + condition.getGuid(),
									new SpokeType(ObjectType.Int), null,
									alreadyEvaluated));// {
					// DEBUG
					// =
					// "__ind"
					// +
					// condition.getGuid()
					// };

					new SpokeInstruction(SpokeInstructionType.Label,
							"_topOfForeachback_" + condition.getGuid());

					new SpokeInstruction(SpokeInstructionType.GetLocal, loc);
					new SpokeInstruction(SpokeInstructionType.GetLocal, dexes);
					new SpokeInstruction(SpokeInstructionType.CallMethodFunc,
							ALH.IndexOf(Methods, ALH.First(Methods,
									new Finder<SpokeMethod>() {
										@Override
										public boolean Find(SpokeMethod item) {
											return item.Class.Name
													.equals("Array")
													&& item.MethodName
															.equals("length");
										}
									})), 0, 1);// { DEBUG = "length" };
					new SpokeInstruction(SpokeInstructionType.LessIntInt);
					SpokeInstruction ifTrdue = new SpokeInstruction(
							SpokeInstructionType.IfTrueContinueElseGoto,
							"EndLoop" + condition.getGuid());

					new SpokeInstruction(SpokeInstructionType.GetLocal, arg);
					new SpokeInstruction(SpokeInstructionType.GetLocal, orig);

					new SpokeInstruction(SpokeInstructionType.GetLocal, dexes);
					new SpokeInstruction(SpokeInstructionType.GetLocal, loc);
					new SpokeInstruction(SpokeInstructionType.ArrayElem);

					new SpokeInstruction(SpokeInstructionType.ArrayElem);
					new SpokeInstruction(SpokeInstructionType.CallMethodFunc,
							ALH.IndexOf(Methods, ALH.First(Methods,
									new Finder<SpokeMethod>() {
										@Override
										public boolean Find(SpokeMethod item) {
											return item.Class.Name
													.equals("Array")
													&& item.MethodName
															.equals("length");
										}
									})), 0, 1);// { DEBUG = "length" };

					new SpokeInstruction(SpokeInstructionType.PopStack);

					new SpokeInstruction(SpokeInstructionType.GetLocal, loc,
							"__ind" + condition.getGuid());
					new SpokeInstruction(SpokeInstructionType.IntConstant, 1);
					new SpokeInstruction(SpokeInstructionType.AddIntInt);
					new SpokeInstruction(SpokeInstructionType.StoreLocalInt,
							loc, "__ind" + condition.getGuid());

					new SpokeInstruction(SpokeInstructionType.Goto,
							"_topOfForeachback_" + condition.getGuid());

					new SpokeInstruction(SpokeInstructionType.Label, "EndLoop"
							+ condition.getGuid());

					new SpokeInstruction(SpokeInstructionType.GetLocal, arg);

					// throw new Exception("ADSd");
					if (fs_ && pa.ByRef == false) {
						throw new Exception("wat");
					}
					SpokeInstruction.LowerDebugging();

					return pa;

				} else if (ind.Type != ObjectType.Int) {
					throw new Exception();

				}

				// new SpokeInstruction(SpokeInstructionType.FloatConstant,
				// ((SpokeFloat)condition).Value);
				new SpokeInstruction(SpokeInstructionType.ArrayElem);

				if (fs_ && pa.ByRef == false) {
					((SpokeArrayIndex) condition).ForSet = true;
					pa.ArrayItemType = new SpokeType(pa.ArrayItemType);
					SpokeInstruction.LowerDebugging();

					return pa.ArrayItemType;
				}
				if (pa.ByRef == true) {
					throw new Exception();
				}
				SpokeInstruction.LowerDebugging();

				return pa.ArrayItemType;
			case AnonMethod:

				if (((SpokeAnonMethod) condition).getParent() != null) {
					SpokeMethodParse df = null;

					if (((SpokeAnonMethod) condition).HasYield
							|| ((SpokeAnonMethod) condition).HasYieldReturn) {
						ArrayList<SpokeLine> drf = new ArrayList<SpokeLine>(
								Arrays.asList(((SpokeAnonMethod) condition).lines));
						drf.add(0, new SpokeEqual(fyv = new SpokeVariable("__"
								+ condition.getGuid()), new SpokeArray(
								new SpokeItem[0])));

						fyv = new SpokeVariable("__" + condition.getGuid());
						variables.Add("__" + condition.getGuid(),
								new SpokeType(ObjectType.Array), fyv,
								alreadyEvaluated);
						((SpokeAnonMethod) condition).ReturnYield = fyv;

						new SpokeInstruction(SpokeInstructionType.CreateArray);
						new SpokeInstruction(
								SpokeInstructionType.StoreLocalObject,
								fyv.VariableIndex);

						fuix(drf, fyv, false);

						((SpokeAnonMethod) condition).lines = drf
								.toArray(new SpokeLine[drf.size()]);

					}

					new SpokeInstruction(SpokeInstructionType.Label,
							"_topOfWhile_" + condition.getGuid());

					if (((SpokeAnonMethod) condition).RunOnVar == null) {
						df = new SpokeMethodParse(currentObject.RunningClass);
						if (((SpokeAnonMethod) condition).HasYield
								|| ((SpokeAnonMethod) condition).HasYieldReturn) {
							df.ForYieldArray = new SpokeType(ObjectType.Unset);
						}
					}
					if (((SpokeAnonMethod) condition).HasReturn) {
						df.setReturnType(new SpokeType(ObjectType.Unset));
					}

					SpokeType rl = evaluateItem(
							((SpokeAnonMethod) condition).getParent(),
							currentObject, alreadyEvaluated, variables);

					if (rl.Type == ObjectType.Array) {
						if (((SpokeAnonMethod) condition).RunOnVar != null) {
							throw new Exception("");
							/*
							 * 
							 * ArrayList<SpokeInstruction> dmei =
							 * SpokeInstruction.ins;
							 * 
							 * df = new SpokeMethodParse(evaluateItem(
							 * ((SpokeAnonMethod) condition).RunOnVar,
							 * currentObject, variables)); if
							 * (df.RunningClass.AnonMethod.HasYield ||
							 * df.RunningClass.AnonMethod.HasYieldReturn) {
							 * df.ForYieldArray = new
							 * SpokeType(ObjectType.Unset); }
							 * SpokeInstruction.ins = dmei;
							 * 
							 * if (((SpokeAnonMethod) condition).HasReturn) {
							 * df.setReturnType(new
							 * SpokeType(ObjectType.Unset)); }
							 * 
							 * for (int i = 0; i <
							 * df.RunningClass.AnonMethod.Parameters.length;
							 * i++) {
							 * df.RunningClass.AnonMethod.Parameters[i].Index =
							 * variables
							 * .Add(df.RunningClass.AnonMethod.Parameters
							 * [i].Name, rl.ArrayItemType, null);
							 * 
							 * if
							 * (df.RunningClass.AnonMethod.Parameters[i].ByRef)
							 * { throw new Exception("idk"); //
							 * spokeObject.ByRef // = true; } }
							 * getanonMethodsEntered().put( "_anons1_" +
							 * condition.getGuid(), false);
							 * 
							 * for (SpokeLine spokeLine :
							 * df.RunningClass.AnonMethod.Lines) {
							 * resetGuids(spokeLine); }
							 * 
							 * Tuple2<SpokeType, SpokeLine[]> rmec =
							 * evaluateLines( df.RunningClass.AnonMethod.Lines,
							 * df, variables); df.RunningClass.AnonMethod.Lines
							 * = rmec.Item2; SpokeType rme = rmec.Item1;
							 * 
							 * new SpokeInstruction(SpokeInstructionType.Label,
							 * lastAnonMethodEntered().Item1); if
							 * (lastAnonMethodEntered().Item1 != "_anons1_" +
							 * condition.getGuid()) { throw new Exception("");
							 * 
							 * } getanonMethodsEntered().remove(
							 * lastAnonMethodEntered().Item1);
							 * 
							 * if (rme != null) if
							 * (df.RunningClass.AnonMethod.HasReturn) { for (int
							 * i = 0; i <
							 * df.RunningClass.AnonMethod.Parameters.length;
							 * i++) { variables
							 * .Remove(df.RunningClass.AnonMethod.Parameters
							 * [i].Name); }
							 * 
							 * return rme; }
							 * 
							 * if (df.RunningClass.AnonMethod.HasYield ||
							 * df.RunningClass.AnonMethod.HasYieldReturn) {
							 * 
							 * }
							 * 
							 * for (int i = 0; i <
							 * df.RunningClass.AnonMethod.Parameters.length;
							 * i++) { String c =
							 * df.RunningClass.AnonMethod.Parameters[i].Name;
							 * variables.Get(c, null).ByRef = false;
							 * variables.Remove(c); }
							 * 
							 * return new SpokeType(ObjectType.Array,
							 * df.ForYieldArray);
							 */
						} else {
							int loc;
							int arr;

							new SpokeInstruction(
									SpokeInstructionType.StoreLocalObject,
									arr = variables.Add(
											"__arr" + condition.getGuid(), rl,
											null, alreadyEvaluated));// ,
							// "__arr"
							// +
							// condition.getGuid()

							new SpokeInstruction(
									SpokeInstructionType.IntConstant, 0);
							new SpokeInstruction(
									SpokeInstructionType.StoreLocalInt,
									loc = variables.Add(
											"__ind" + condition.getGuid(),
											new SpokeType(ObjectType.Int),
											null, alreadyEvaluated));// ,
							// "__ind"
							// +
							// condition.getGuid()

							new SpokeInstruction(SpokeInstructionType.Label,
									"_topOfForeach_" + condition.getGuid());

							new SpokeInstruction(SpokeInstructionType.GetLocal,
									loc, "__ind" + condition.getGuid());
							new SpokeInstruction(SpokeInstructionType.GetLocal,
									arr, "__arr" + condition.getGuid());

							new SpokeInstruction(
									SpokeInstructionType.CallMethodFunc,
									ALH.IndexOf(Methods, ALH.First(Methods,
											new Finder<SpokeMethod>() {
												@Override
												public boolean Find(
														SpokeMethod item) {
													return item.Class.Name
															.equals("Array")
															&& item.MethodName
																	.equals("length");
												}
											})), 0, 1);

							new SpokeInstruction(
									SpokeInstructionType.LessIntInt);
							SpokeInstruction ifTrdue = new SpokeInstruction(
									SpokeInstructionType.IfTrueContinueElseGoto,
									"EndLoop" + condition.getGuid());

							if (((SpokeAnonMethod) condition).Parameters != null) {

								((SpokeAnonMethod) condition).Parameters[0].Index = variables
										.Add(((SpokeAnonMethod) condition).Parameters[0].Name,
												rl.ArrayItemType, null,
												alreadyEvaluated);

								new SpokeInstruction(
										SpokeInstructionType.GetLocal, arr,
										"__arr" + condition.getGuid());
								new SpokeInstruction(
										SpokeInstructionType.GetLocal, loc,
										"__ind" + condition.getGuid());
								new SpokeInstruction(
										SpokeInstructionType.ArrayElem);
								new SpokeInstruction(
										SpokeInstructionType.StoreLocalObject,
										((SpokeAnonMethod) condition).Parameters[0].Index,
										((SpokeAnonMethod) condition).Parameters[0].Name);

								if (((SpokeAnonMethod) condition).Parameters.length == 2) {

									((SpokeAnonMethod) condition).Parameters[1].Index = variables
											.Add(((SpokeAnonMethod) condition).Parameters[1].Name,
													new SpokeType(
															ObjectType.Int),
													null, alreadyEvaluated);

									new SpokeInstruction(
											SpokeInstructionType.GetLocal, loc,
											"__ind" + condition.getGuid());
									new SpokeInstruction(
											SpokeInstructionType.StoreLocalInt,
											((SpokeAnonMethod) condition).Parameters[1].Index,
											((SpokeAnonMethod) condition).Parameters[1].Name);

								}

							}

							getanonMethodsEntered().put(
									"_anons2_" + condition.getGuid(), false);

							Tuple2<SpokeType, SpokeLine[]> rmec = evaluateLines(
									((SpokeAnonMethod) condition).lines, df,
									variables, alreadyEvaluated);

							((SpokeAnonMethod) condition).lines = rmec.Item2;
							SpokeType rme = rmec.Item1;

							new SpokeInstruction(SpokeInstructionType.Label,
									lastAnonMethodEntered().Item1);
							if (lastAnonMethodEntered().Item1 != "_anons2_"
									+ condition.getGuid()) {

							}
							getanonMethodsEntered().remove(
									lastAnonMethodEntered().Item1);

							new SpokeInstruction(SpokeInstructionType.GetLocal,
									loc, "__ind" + condition.getGuid());
							new SpokeInstruction(
									SpokeInstructionType.IntConstant, 1);
							new SpokeInstruction(SpokeInstructionType.AddIntInt);
							new SpokeInstruction(
									SpokeInstructionType.StoreLocalInt, loc,
									"__ind" + condition.getGuid());

							new SpokeInstruction(SpokeInstructionType.Goto,
									"_topOfForeach_" + condition.getGuid());

							new SpokeInstruction(SpokeInstructionType.Label,
									"EndLoop" + condition.getGuid());

							// yield return
							if (rme != null)
								if (((SpokeAnonMethod) condition).HasReturn) {
									if (((SpokeAnonMethod) condition).Parameters != null) {

										variables
												.Remove(((SpokeAnonMethod) condition).Parameters[0].Name);
										if (((SpokeAnonMethod) condition).Parameters.length == 2)
											variables
													.Remove(((SpokeAnonMethod) condition).Parameters[1].Name);

									}
									if (df.getReturnType()
											.CompareTo(rme, false)) {
										if (rme.Type == ObjectType.Unset) {
											df.setReturnType(rme);
										}
									} else {
										df.ForYieldArray = rme;
									}

								}

							if (((SpokeAnonMethod) condition).Parameters != null) {

								variables
										.Remove(((SpokeAnonMethod) condition).Parameters[0].Name);
								if (((SpokeAnonMethod) condition).Parameters.length == 2)
									variables
											.Remove(((SpokeAnonMethod) condition).Parameters[1].Name);

							}
							variables.Remove("__arr" + condition.getGuid());
							variables.Remove("__ind" + condition.getGuid());

							if (((SpokeAnonMethod) condition).HasYield
									|| ((SpokeAnonMethod) condition).HasYieldReturn) {

								ArrayList<SpokeLine> drf = new ArrayList<SpokeLine>(
										Arrays.asList(((SpokeAnonMethod) condition).lines));

								fuix(drf, fyv, true);

								((SpokeAnonMethod) condition).lines = drf
										.toArray(new SpokeLine[drf.size()]);

								new SpokeInstruction(
										SpokeInstructionType.GetLocal,
										fyv.VariableIndex, fyv.VariableName);
								SpokeInstruction.LowerDebugging();

								return new SpokeType(ObjectType.Array,
										df.ForYieldArray);

							} else if (((SpokeAnonMethod) condition).HasReturn) {
								SpokeInstruction.LowerDebugging();

								return df.getReturnType();
							}

						}
					} else if (rl.Type == ObjectType.Bool) {

						SpokeInstruction ifTrdue = new SpokeInstruction(
								SpokeInstructionType.IfTrueContinueElseGoto,
								"EndLoop" + condition.getGuid());

						getanonMethodsEntered().put(
								"_anons3_" + condition.getGuid(), false);

						Tuple2<SpokeType, SpokeLine[]> defc = evaluateLines(
								((SpokeAnonMethod) condition).lines, df,
								variables, alreadyEvaluated);

						((SpokeAnonMethod) condition).lines = defc.Item2;
						SpokeType def = defc.Item1;
						new SpokeInstruction(SpokeInstructionType.Label,
								lastAnonMethodEntered().Item1);
						if (lastAnonMethodEntered().Item1 != "_anons3_"
								+ condition.getGuid()) {

						}

						getanonMethodsEntered().remove(
								lastAnonMethodEntered().Item1);

						new SpokeInstruction(SpokeInstructionType.Goto,
								"_topOfWhile_" + condition.getGuid());

						new SpokeInstruction(SpokeInstructionType.Label,
								"EndLoop" + condition.getGuid());

						if (def != null) {
							if (((SpokeAnonMethod) condition).HasReturn) {

								if (df.getReturnType().CompareTo(def, false)) {
									if (def.Type == ObjectType.Unset) {
										currentObject.setReturnType(def);
									}
								} else {
									df.setReturnType(def);
								}
								SpokeInstruction.LowerDebugging();

								return df.getReturnType();

							}
						}

						if (((SpokeAnonMethod) condition).HasYield
								|| ((SpokeAnonMethod) condition).HasYieldReturn) {

							ArrayList<SpokeLine> drf = new ArrayList<SpokeLine>(
									Arrays.asList(((SpokeAnonMethod) condition).lines));

							fuix(drf, fyv, true);

							((SpokeAnonMethod) condition).lines = drf
									.toArray(new SpokeLine[drf.size()]);

							new SpokeInstruction(SpokeInstructionType.GetLocal,
									fyv.VariableIndex, fyv.VariableName);
							new SpokeInstruction(SpokeInstructionType.Return);
							SpokeInstruction.LowerDebugging();

							return new SpokeType(ObjectType.Array,
									df.ForYieldArray);
						}

					}

				} else {

					if (((SpokeAnonMethod) condition).HasYield
							|| ((SpokeAnonMethod) condition).HasYieldReturn) {
						ArrayList<SpokeLine> drf = new ArrayList<SpokeLine>(
								Arrays.asList(((SpokeAnonMethod) condition).lines));
						drf.add(0, new SpokeEqual(fyv = new SpokeVariable("__"
								+ condition.getGuid()), new SpokeArray(
								new SpokeItem[0])));

						fyv = new SpokeVariable("__" + condition.getGuid());
						variables.Add("__" + condition.getGuid(),
								new SpokeType(ObjectType.Array), fyv,
								alreadyEvaluated);
						fyv.ForSet = true;
						((SpokeAnonMethod) condition).ReturnYield = fyv;

						fuix(drf, fyv, false);
						fuix(drf, fyv, true);

						((SpokeAnonMethod) condition).lines = drf
								.toArray(new SpokeLine[drf.size()]);
					}

					SpokeObjectMethod ce;
					new SpokeInstruction(
							SpokeInstructionType.CreateMethod,
							ce = new SpokeObjectMethod(
									((SpokeAnonMethod) condition).lines,
									((SpokeAnonMethod) condition).Parameters,
									((SpokeAnonMethod) condition).HasReturn,
									((SpokeAnonMethod) condition).HasYield,
									((SpokeAnonMethod) condition).HasYieldReturn,
									variables));

					SpokeType stp = new SpokeType(ObjectType.Method);
					stp.Type = ObjectType.Method;
					stp.Variables = new SpokeVariableInfo();
					stp.ClassName = currentObject.RunningClass.ClassName;
					stp.AnonMethod = ce;
					SpokeInstruction.LowerDebugging();

					return stp;
				}

				break;
			case MethodCall:

				SpokeMethodCall gf = ((SpokeMethodCall) condition);

				if (gf.getParent() instanceof SpokeAnonMethod) {

					SpokeAnonMethod anm = new SpokeAnonMethod(null);

					anm.HasReturn = ((SpokeAnonMethod) gf.getParent()).HasReturn;
					anm.HasYield = ((SpokeAnonMethod) gf.getParent()).HasYield;
					anm.HasYieldReturn = ((SpokeAnonMethod) gf.getParent()).HasYieldReturn;
					anm.setLines(((SpokeLine[]) ((SpokeAnonMethod) gf
							.getParent()).getLines().clone()));

					anm.Parameters = ((SpokeAnonMethod) gf.getParent()).Parameters
							.clone();
					anm.setParent(((SpokeAnonMethod) gf.getParent())
							.getParent());
					anm.RunOnVar = ((SpokeAnonMethod) gf.getParent()).RunOnVar;

					gf.setParent(anm);

					for (int index = 1; index < gf.Parameters.length; index++) {
						SpokeItem spokeItem = gf.Parameters[index];
						SpokeType eh;

						ArrayList<SpokeInstruction> dims = new ArrayList<SpokeInstruction>(
								SpokeInstruction.ins);

						((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].Index = variables
								.Add(((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].Name,
										eh = evaluateItem(spokeItem,
												currentObject,
												alreadyEvaluated, variables),
										null, alreadyEvaluated);

						eh.ByRef = ((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].ByRef;

						if (eh.ByRef) {
							new SpokeInstruction(
									SpokeInstructionType.StoreLocalObject,
									((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].Index,
									((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].Name);
						} else

							switch (eh.Type) {
							case Null:
								new SpokeInstruction(
										SpokeInstructionType.StoreLocalObject,
										((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].Index,
										((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].Name);

								break;
							case Int:
								new SpokeInstruction(
										SpokeInstructionType.StoreLocalInt,
										((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].Index,
										((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].Name);

								break;
							case Float:
								new SpokeInstruction(
										SpokeInstructionType.StoreLocalFloat,
										((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].Index,
										((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].Name);

								break;
							case String:
								new SpokeInstruction(
										SpokeInstructionType.StoreLocalString,
										((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].Index,
										((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].Name);

								break;
							case Bool:
								new SpokeInstruction(
										SpokeInstructionType.StoreLocalBool,
										((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].Index,
										((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].Name);

								break;
							case Array:
								new SpokeInstruction(
										SpokeInstructionType.StoreLocalObject,
										((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].Index,
										((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].Name);

								break;
							case Object:
								new SpokeInstruction(
										SpokeInstructionType.StoreLocalObject,
										((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].Index,
										((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].Name);

								break;
							case Method:
								new SpokeInstruction(
										SpokeInstructionType.StoreLocalMethod,
										((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].Index,
										((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].Name);

								break;
							case Void:
								break;
							default:
								throw new Exception("bad");
							}

					}

					SpokeMethodParse df = new SpokeMethodParse(
							currentObject.RunningClass);
					SpokeType oldRF = null;

					if (((SpokeAnonMethod) gf.getParent()).HasYieldReturn
							|| ((SpokeAnonMethod) gf.getParent()).HasYield) {
						df.ForYieldArray = new SpokeType(ObjectType.Unset);
						fyv = new SpokeVariable("__" + condition.getGuid());
						variables.Add("__" + condition.getGuid(),
								new SpokeType(ObjectType.Array), fyv,
								alreadyEvaluated);
						// fyv.ForSet = true;
						((SpokeAnonMethod) condition).ReturnYield = fyv;

						new SpokeInstruction(SpokeInstructionType.CreateArray);
						new SpokeInstruction(
								SpokeInstructionType.StoreLocalObject,
								fyv.VariableIndex);

					} else if (((SpokeAnonMethod) gf.getParent()).HasReturn) {
						oldRF = df.getReturnType();
						df.setReturnType(new SpokeType(ObjectType.Unset));
					}

					getanonMethodsEntered().put(
							"_anonMethod_" + gf.getParent().getGuid(), true);

					variables.IncreaseState();
					Tuple2<SpokeType, SpokeLine[]> fdc = evaluateLines(
							((SpokeAnonMethod) gf.getParent()).lines, df,
							variables, alreadyEvaluated);

					((SpokeAnonMethod) gf.getParent()).lines = fdc.Item2;
					SpokeType fd = fdc.Item1;
					variables.DecreaseState();

					new SpokeInstruction(SpokeInstructionType.Label,
							lastAnonMethodEntered().Item1);

					if (lastAnonMethodEntered().Item1 != "_anonMethod_"
							+ gf.getParent().getGuid()) {

					}
					getanonMethodsEntered().remove(
							lastAnonMethodEntered().Item1);

					if (((SpokeAnonMethod) gf.getParent()).SpecAnon
							&& parentIsNull) {
						df.setReturnType(oldRF);
					}

					if (((SpokeAnonMethod) gf.getParent()).HasYieldReturn
							|| ((SpokeAnonMethod) gf.getParent()).HasYield) {

						new SpokeInstruction(SpokeInstructionType.GetLocal,
								fyv.VariableIndex, fyv.VariableName);

						fd = new SpokeType(ObjectType.Array);
						fd.ArrayItemType = fd;
					} else if (((SpokeAnonMethod) gf.getParent()).HasReturn) {
						String fe = "_ret_"
								+ ((SpokeAnonMethod) gf.getParent()).getGuid();
						int de = variables.Add(fe, df.getReturnType(), null,
								alreadyEvaluated);
						switch (df.getReturnType().Type) {
						case Null:
							new SpokeInstruction(
									SpokeInstructionType.StoreLocalObject, de,
									fe);

							break;
						case Int:
							new SpokeInstruction(
									SpokeInstructionType.StoreLocalInt, de, fe);

							break;
						case Float:
							new SpokeInstruction(
									SpokeInstructionType.StoreLocalFloat, de,
									fe);

							break;
						case String:
							new SpokeInstruction(
									SpokeInstructionType.StoreLocalString, de,
									fe);

							break;
						case Bool:
							new SpokeInstruction(
									SpokeInstructionType.StoreLocalBool, de, fe);

							break;
						case Array:
							new SpokeInstruction(
									SpokeInstructionType.StoreLocalObject, de,
									fe);

							break;
						case Object:
							new SpokeInstruction(
									SpokeInstructionType.StoreLocalObject, de,
									fe);

							break;
						case Method:
							new SpokeInstruction(
									SpokeInstructionType.StoreLocalMethod, de,
									fe);

							break;
						case Void:
							break;
						default:
							throw new Exception();
						}

						new SpokeInstruction(SpokeInstructionType.GetLocal, de,
								fe);

					}

					for (int index = 1; index < gf.Parameters.length; index++) {

						String m = ((SpokeAnonMethod) gf.getParent()).Parameters[index - 1].Name;
						variables.Get(m, null).ByRef = false;
						variables.Remove(m);
					}
					SpokeInstruction.LowerDebugging();

					return fd;
				} else {
					SpokeInstruction.LowerDebugging();
					final SpokeVariable d = ((SpokeVariable) gf.getParent());
					if (d.getParent() == null) {

						SpokeMethod meth;

						if (currentObject != null
								&& (meth = ALH.FirstOrDefault(Methods,
										new Finder<SpokeMethod>() {
											@Override
											public boolean Find(SpokeMethod item) {
												return item.Class.Name
														.equals(currentObject.RunningClass.ClassName)
														&& item.MethodName
																.equals(d.VariableName);
											}
										})) != null) {

							if (meth.Static) {
								SpokeItem[] sc = new SpokeItem[gf.Parameters.length - 1];
								for (int i = 1; i < gf.Parameters.length; i++) {
									sc[i - 1] = gf.Parameters[i];
								}
								gf.Parameters = sc;
							}

							SpokeType[] parms = new SpokeType[gf.Parameters.length];
							d.VariableIndex = ALH.IndexOf(Methods, meth);
							for (int i = 0; i < parms.length; i++) {
								parms[i] = evaluateItem(gf.Parameters[i],
										currentObject, alreadyEvaluated,
										variables);
							}
							SpokeInstruction ii = null;
							if (meth.MethodFunc == null)
								ii = new SpokeInstruction(
										SpokeInstructionType.CallMethod,
										d.VariableIndex, d.VariableName);
							else
								ii = new SpokeInstruction(
										SpokeInstructionType.CallMethodFunc,
										d.VariableIndex, d.VariableName);

							ii.DebugToks = d.getTokens();

							d.VType = SpokeVType.MethodName;
							SpokeType demf = evaluateMethod(meth,
									currentObject.RunningClass, parms);
							ii.Index2 = meth.NumOfVars;
							ii.Index3 = meth.Parameters.length;

							return demf;
						} else {

							if ((g = variables.TryGetValue(d.VariableName, d)) != null
									&& g.Type == ObjectType.Method) {
								int indexBefore = 0;
								for (int i = 0; i < g.AnonMethod.Parameters.length; i++) {

									SpokeType eh;
									g.AnonMethod.Parameters[i].Index = g.AnonMethod.CurrentStack
											.Add(g.AnonMethod.Parameters[i].Name,
													eh = evaluateItem(
															gf.Parameters[i + 1],
															currentObject,
															alreadyEvaluated,
															variables), null,
													alreadyEvaluated);
									if (i == 0) {
										indexBefore = g.AnonMethod.Parameters[i].Index;
									}
									System.out.print(eh);
								}
								SpokeMethodParse df = new SpokeMethodParse(
										currentObject.RunningClass);

								if (g.AnonMethod.HasYieldReturn
										|| g.AnonMethod.HasYield) {
									df.ForYieldArray = new SpokeType(
											ObjectType.Unset);
								} else if (g.AnonMethod.HasReturn) {
									df.setReturnType(new SpokeType(
											ObjectType.Unset));
								}

								new SpokeInstruction(
										SpokeInstructionType.GetLocal,
										d.VariableIndex).DebugToks = null;
								SpokeInstruction ii = new SpokeInstruction(
										SpokeInstructionType.CallAnonMethod);
								ii.DebugToks = d.getTokens();
								ii.Index3 = g.AnonMethod.Parameters.length;
								ii.Index = indexBefore;
								ii.AnonMethod = g.AnonMethod;
								if (g.AnonMethod.Instructions == null) {

									ArrayList<SpokeInstruction> dims = SpokeInstruction.ins;
									SpokeInstruction.ins = new ArrayList<SpokeInstruction>();

									Tuple2<SpokeType, SpokeLine[]> rmec = evaluateLines(
											g.AnonMethod.Lines, df,
											g.AnonMethod.CurrentStack,
											alreadyEvaluated);
									SpokeType rme = rmec.Item1;
									g.AnonMethod.Instructions = SpokeInstruction.ins
											.toArray(new SpokeInstruction[SpokeInstruction.ins
													.size()]);
									SpokeInstruction.ins = dims;

									g.AnonMethod.Lines = rmec.Item2;
									g.AnonMethod.ReturnType = rme;
									// gay if its an actual method not anon

									for (int i = 0; i < g.AnonMethod.Parameters.length; i++) {
										String m = g.AnonMethod.Parameters[i].Name;
										g.AnonMethod.CurrentStack.Get(m, null).ByRef = false;
										g.AnonMethod.CurrentStack.Remove(m);

									}

									if (g.AnonMethod.ReturnType == null)
										new SpokeInstruction(
												SpokeInstructionType.PopStack).DebugToks = null;

									d.VType = SpokeVType.ThisV;

									return rme;
								} else

								if (g.AnonMethod.ReturnType == null)
									new SpokeInstruction(
											SpokeInstructionType.PopStack).DebugToks = null;

								return g.AnonMethod.ReturnType;

							}

							if ((g = currentObject.RunningClass.Variables
									.TryGetValue(d.VariableName, d)) != null
									&& g.Type == ObjectType.Method) {
								int indexBefore = 0;
								for (int i = 0; i < g.AnonMethod.Parameters.length; i++) {
									ArrayList<SpokeInstruction> dims = new ArrayList<SpokeInstruction>(
											SpokeInstruction.ins);

									SpokeType eh;
									g.AnonMethod.Parameters[i].Index = g.AnonMethod.CurrentStack
											.Add(g.AnonMethod.Parameters[i].Name,
													eh = evaluateItem(
															gf.Parameters[i + 1],
															currentObject,
															alreadyEvaluated,
															variables), null,
													alreadyEvaluated);
									if (i == 0) {
										indexBefore = g.AnonMethod.Parameters[i].Index;
									}

								}
								SpokeMethodParse df = new SpokeMethodParse(
										currentObject.RunningClass);

								if (g.AnonMethod.HasYieldReturn
										|| g.AnonMethod.HasYield) {
									df.ForYieldArray = new SpokeType(
											ObjectType.Unset);
								} else if (g.AnonMethod.HasReturn) {
									df.setReturnType(new SpokeType(
											ObjectType.Unset));
								}

								new SpokeInstruction(
										SpokeInstructionType.GetLocal,
										d.VariableIndex).DebugToks = null;
								SpokeInstruction ii = new SpokeInstruction(
										SpokeInstructionType.CallAnonMethod);
								ii.DebugToks = d.getTokens();

								ii.Index3 = g.AnonMethod.Parameters.length;
								ii.Index = indexBefore;
								ii.AnonMethod = g.AnonMethod;
								if (g.AnonMethod.Instructions == null) {

									ArrayList<SpokeInstruction> dims = SpokeInstruction.ins;
									SpokeInstruction.ins = new ArrayList<SpokeInstruction>();

									Tuple2<SpokeType, SpokeLine[]> rmec = evaluateLines(
											g.AnonMethod.Lines, df,
											g.AnonMethod.CurrentStack,
											alreadyEvaluated);
									SpokeType rme = rmec.Item1;
									g.AnonMethod.Instructions = SpokeInstruction.ins
											.toArray(new SpokeInstruction[SpokeInstruction.ins
													.size()]);
									SpokeInstruction.ins = dims;

									g.AnonMethod.Lines = rmec.Item2;
									g.AnonMethod.ReturnType = rme;
									// gay if its an actual method not anon

									for (int i = 0; i < g.AnonMethod.Parameters.length; i++) {
										String m = g.AnonMethod.Parameters[i].Name;
										g.AnonMethod.CurrentStack.Get(m, null).ByRef = false;
										g.AnonMethod.CurrentStack.Remove(m);

									}

									d.VType = SpokeVType.ThisV;

									return rme;
								} else {

									return g.AnonMethod.ReturnType;
								}

							}

							d.VType = SpokeVType.InternalMethodName;

							d.VariableIndex = InternalMethodsTypes
									.get(d.VariableName).Item2;

							SpokeType[] parms = new SpokeType[gf.Parameters.length];

							for (int i = 0; i < parms.length; i++) {
								parms[i] = evaluateItem(
										i == 0 ? (currentlyStaticMethod ? new SpokeNull()
												: gf.Parameters[i])
												: gf.Parameters[i],
										currentObject, alreadyEvaluated,
										variables);
							}

							new SpokeInstruction(
									SpokeInstructionType.CallInternal,
									d.VariableIndex, 0, parms.length).DebugToks = d
									.getTokens();// {
							// DEBUG
							// =
							// d.VariableName
							// };

							// return new SpokeType(ObjectType.Void);

							return InternalMethodsTypes.get(d.VariableName).Item1;
						}
					} else {
						final SpokeType pp = evaluateItem(d.getParent(),
								currentObject, alreadyEvaluated, variables);
						if (pp == null) {
							// Console.WriteLine("A");
						}

						SpokeMethod fm = ALH.FirstOrDefault(Methods,
								new Finder<SpokeMethod>() {
									@Override
									public boolean Find(SpokeMethod item) {
										return item.MethodName
												.equals(d.VariableName)
												&& item.Class.Name
														.equals(pp.ClassName);
									}
								});

						if (fm != null) {

							d.VariableIndex = ALH.IndexOf(Methods, fm);
							if (fm.MethodFunc != null) {
								SpokeType[] gm = new SpokeType[gf.Parameters.length];

								gm[0] = pp;

								for (int index = 1; index < gf.Parameters.length; index++) {
									gm[index] = evaluateItem(
											gf.Parameters[index],
											currentObject, alreadyEvaluated,
											variables);
								}

								if (fm.Class.Name.equals("Array")
										&& fm.MethodName.equals("add")) {
									pp.ArrayItemType = gm[1];
								}

								new SpokeInstruction(
										SpokeInstructionType.CallMethodFunc,
										d.VariableIndex, 0,
										gf.Parameters.length).DebugToks = d
										.getTokens();// {
								// DEBUG
								// =
								// d.VariableName
								// };

								// hmmm

								if (fm.returnType.Type == ObjectType.Null) {
									return pp.ArrayItemType;
								} else {
									return fm.returnType;
								}

								// return new SpokeType(ObjectType.Void);
							} else {
								SpokeType[] parms = new SpokeType[gf.Parameters.length];
								parms[0] = pp;
								// 0 instanceof evaluated up top
								for (int i = 1; i < parms.length; i++) {
									parms[i] = evaluateItem(gf.Parameters[i],
											currentObject, alreadyEvaluated,
											variables);
								}

								SpokeType deds = evaluateMethod(fm, pp, parms);
								new SpokeInstruction(
										SpokeInstructionType.CallMethod,
										d.VariableIndex, fm.NumOfVars,
										parms.length).DebugToks = d.getTokens();// {
								// DEBUG
								// =
								// d.VariableName
								// };

								return deds;
							}

						}

						if (pp.Variables != null
								&& ((g = pp.Variables.TryGetValue(
										d.VariableName, d)) != null && g.Type == ObjectType.Method)) {

							for (int i = 0; i < g.AnonMethod.Parameters.length; i++) {
								ArrayList<SpokeInstruction> dims = new ArrayList<SpokeInstruction>(
										SpokeInstruction.ins);
								SpokeType eh;
								g.AnonMethod.Parameters[i].Index = variables
										.Add(g.AnonMethod.Parameters[i].Name,
												eh = evaluateItem(
														gf.Parameters[i + 1],
														currentObject,
														alreadyEvaluated,
														variables), null,
												alreadyEvaluated);
								eh.ByRef = g.AnonMethod.Parameters[i].ByRef;
								if (eh.ByRef) {
									new SpokeInstruction(
											SpokeInstructionType.StoreLocalObject,
											g.AnonMethod.Parameters[i].Index,
											g.AnonMethod.Parameters[i].Name);
								} else

									switch (eh.Type) {
									case Null:
										new SpokeInstruction(
												SpokeInstructionType.StoreLocalObject,
												g.AnonMethod.Parameters[i].Index,
												g.AnonMethod.Parameters[i].Name);

										break;
									case Int:
										new SpokeInstruction(
												SpokeInstructionType.StoreLocalInt,
												g.AnonMethod.Parameters[i].Index,
												g.AnonMethod.Parameters[i].Name);

										break;
									case Float:
										new SpokeInstruction(
												SpokeInstructionType.StoreLocalFloat,
												g.AnonMethod.Parameters[i].Index,
												g.AnonMethod.Parameters[i].Name);

										break;
									case String:
										new SpokeInstruction(
												SpokeInstructionType.StoreLocalString,
												g.AnonMethod.Parameters[i].Index,
												g.AnonMethod.Parameters[i].Name);

										break;
									case Bool:
										new SpokeInstruction(
												SpokeInstructionType.StoreLocalBool,
												g.AnonMethod.Parameters[i].Index,
												g.AnonMethod.Parameters[i].Name);

										break;
									case Array:
										new SpokeInstruction(
												SpokeInstructionType.StoreLocalObject,
												g.AnonMethod.Parameters[i].Index,
												g.AnonMethod.Parameters[i].Name);

										break;
									case Object:
										new SpokeInstruction(
												SpokeInstructionType.StoreLocalObject,
												g.AnonMethod.Parameters[i].Index,
												g.AnonMethod.Parameters[i].Name);

										break;
									case Method:
										new SpokeInstruction(
												SpokeInstructionType.StoreLocalMethod,
												g.AnonMethod.Parameters[i].Index,
												g.AnonMethod.Parameters[i].Name);

										break;
									case Void:
										break;
									default:
										throw new Exception();
									}

							}
							SpokeMethodParse df = new SpokeMethodParse(pp);

							if (g.AnonMethod.HasYieldReturn
									|| g.AnonMethod.HasYield) {
								df.ForYieldArray = new SpokeType(
										ObjectType.Unset);
							} else if (g.AnonMethod.HasReturn) {
								df.setReturnType(new SpokeType(ObjectType.Unset));
							}

							new SpokeInstruction(
									SpokeInstructionType.CallMethod,
									d.VariableIndex, d.VariableName);
							// throw new Exception("DOES NOT WORK");
							getanonMethodsEntered().put(
									"__anonVar__" + condition.getGuid(), true);
							Tuple2<SpokeType, SpokeLine[]> rmec = evaluateLines(
									g.AnonMethod.Lines, df, variables,
									alreadyEvaluated);
							SpokeType rme = rmec.Item1;
							g.AnonMethod.Lines = rmec.Item2;

							new SpokeInstruction(SpokeInstructionType.Label,
									lastAnonMethodEntered().Item1);
							getanonMethodsEntered().remove(
									lastAnonMethodEntered().Item1);

							for (int i = 0; i < g.AnonMethod.Parameters.length; i++) {
								String m = g.AnonMethod.Parameters[i].Name;
								variables.Get(m, null).ByRef = false;
								variables.Remove(m);

							}
							SpokeInstruction.LowerDebugging();

							return rme;

						}

						else

							throw new Exception("no method: " + d.VariableName);

					}
				}
			case Construct:
				SpokeInstruction.RaiseDebugging(((SpokeBasic) condition));

				SpokeType cons = new SpokeType(ObjectType.Object);
				final SpokeConstruct rf = (SpokeConstruct) condition;
				cons.Variables = new SpokeVariableInfo();

				if (rf.ClassName != null) {
					cons.ClassName = rf.ClassName;
					SpokeClass drj = ALH.First(_cla, new Finder<SpokeClass>() {
						@Override
						public boolean Find(SpokeClass a) {
							return a.Name.equals(rf.ClassName);
						}
					});

					SpokeMethod fm = ALH.First(Methods,
							new Finder<SpokeMethod>() {
								@Override
								public boolean Find(SpokeMethod a) {
									return a.MethodName.equals(".ctor")
											&& a.Class.Name
													.equals(rf.ClassName)
											&& a.Parameters.length == rf.Parameters.length + 1;

								}
							});

					if (fm.ParentVariableRefs != null)
						cons.Variables = fm.ParentVariableRefs;

					rf.MethodIndex = ALH.IndexOf(Methods, fm);

					new SpokeInstruction(SpokeInstructionType.CreateReference,
							drj.Variables.length, rf.ClassName);

					if (fm.ParentVariableRefs == null) {
						for (int i = 0; i < drj.Variables.length; i++) {
							String v = drj.Variables[i];
							cons.Variables.Add(v, new SpokeType(
									ObjectType.Unset), null, alreadyEvaluated);
						}
					}
					SpokeType[] parms = new SpokeType[rf.Parameters.length + 1];
					parms[0] = cons;

					for (int i = 1; i < parms.length; i++) {
						parms[i] = evaluateItem(rf.Parameters[i - 1],
								currentObject, alreadyEvaluated, variables);
					}

					if (fm.MethodFunc == null)
						new SpokeInstruction(SpokeInstructionType.CallMethod,
								rf.MethodIndex, fm.NumOfVars, parms.length).DebugToks = rf
								.getTokens();
					else
						new SpokeInstruction(
								SpokeInstructionType.CallMethodFunc,
								rf.MethodIndex, fm.NumOfVars, parms.length).DebugToks = rf
								.getTokens();

					for (int i = 0; i < rf.SetVars.length; i++) {
						SVarItems spokeItem = rf.SetVars[i];

						spokeItem.Index = cons.Variables.Add(
								spokeItem.Name,
								evaluateItem(spokeItem.Item, currentObject,
										alreadyEvaluated, variables), null,
								alreadyEvaluated);

						new SpokeInstruction(
								SpokeInstructionType.StoreToReference,
								spokeItem.Index);
					}
					if (fm.Instructions == null && fm.MethodFunc == null) {
						evaluateMethod(fm, cons, parms);

						ArrayList<SpokeInstruction> def = new ArrayList<SpokeInstruction>(
								Arrays.asList(fm.Instructions));
						def.add(new SpokeInstruction(
								SpokeInstructionType.GetLocal, 0));
						def.add(new SpokeInstruction(
								SpokeInstructionType.Return));

						SpokeInstruction[] sf = new SpokeInstruction[def.size()];
						def.toArray(sf);
						fm.Instructions = sf;

						SpokeInstruction.ins
								.remove(SpokeInstruction.ins.size() - 1);
						SpokeInstruction.ins
								.remove(SpokeInstruction.ins.size() - 1);

					} else {
					}
					rf.NumOfVars = cons.Variables.index;
				} else {

					new SpokeInstruction(SpokeInstructionType.CreateReference,
							rf.SetVars.length);// {
												// DEBUG
												// =
												// "{}"
												// };
					for (int i = 0; i < rf.SetVars.length; i++) {
						SVarItems spokeItem = rf.SetVars[i];

						spokeItem.Index = cons.Variables.Add(
								spokeItem.Name,
								evaluateItem(spokeItem.Item, currentObject,
										alreadyEvaluated, variables), null,
								alreadyEvaluated);

						new SpokeInstruction(
								SpokeInstructionType.StoreToReference,
								spokeItem.Index);

					}
				}
				SpokeInstruction.LowerDebugging();

				return cons;

			case Addition:

				ij = ((SpokeMathItem) condition).getItems();

				l = evaluateItem(ij[0], currentObject, alreadyEvaluated,
						variables);

				for (int i = 1; i < ij.length; i++) {
					SpokeItem spokeItem = ij[i];

					r = evaluateItem(spokeItem, currentObject,
							alreadyEvaluated, variables);

					switch (l.Type) {

					case Int:
						switch (r.Type) {
						case Int:
							new SpokeInstruction(SpokeInstructionType.AddIntInt);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(l.Type);
							break;

						case Float:
							new SpokeInstruction(
									SpokeInstructionType.AddIntFloat);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(r.Type);
							break;

						case String:
							new SpokeInstruction(
									SpokeInstructionType.AddIntString);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(r.Type);
							break;

						default:
							throw new Exception();
						}
						break;
					case Float:
						switch (r.Type) {
						case Int:
							new SpokeInstruction(
									SpokeInstructionType.AddFloatInt);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(l.Type);
							break;

						case Float:
							new SpokeInstruction(
									SpokeInstructionType.AddFloatFloat);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(l.Type);
							break;

						case String:
							new SpokeInstruction(
									SpokeInstructionType.AddFloatString);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(r.Type);
							break;
						default:
							throw new Exception();
						}
						break;
					case String:
						switch (r.Type) {

						case Int:
							new SpokeInstruction(
									SpokeInstructionType.AddStringInt);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(l.Type);
							break;
						case Float:
							new SpokeInstruction(
									SpokeInstructionType.AddStringFloat);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(l.Type);
							break;
						case String:
							new SpokeInstruction(
									SpokeInstructionType.AddStringString);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(l.Type);
							break;
						case Method:
							new SpokeInstruction(
									SpokeInstructionType.AddStringMethod);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(ObjectType.String);
							break;

						case Object:
							new SpokeInstruction(
									SpokeInstructionType.AddStringObject);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(ObjectType.String);
							break;
						case Bool:
							new SpokeInstruction(
									SpokeInstructionType.AddStringBool);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(ObjectType.String);
							break;
						default:
							throw new Exception();
						}
						break;
					case Object:
						switch (r.Type) {
						case String:
							new SpokeInstruction(
									SpokeInstructionType.AddObjectString);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(ObjectType.String);
							break;
						default:
							throw new Exception();
						}
						break;
					case Method:
						switch (r.Type) {
						case String:
							new SpokeInstruction(
									SpokeInstructionType.AddMethodString);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(ObjectType.String);
							break;

						default:
							throw new Exception();
						}
						break;
					case Bool:
						switch (r.Type) {
						case String:
							new SpokeInstruction(
									SpokeInstructionType.AddBoolString);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(ObjectType.String);
							break;
						default:
							throw new Exception();
						}
						break;

					default:
						throw new Exception();
					}
				}

				return l;

			case Mod:

				ij = ((SpokeMathItem) condition).getItems();

				l = evaluateItem(ij[0], currentObject, alreadyEvaluated,
						variables);

				for (int i = 1; i < ij.length; i++) {
					SpokeItem spokeItem = ij[i];

					r = evaluateItem(spokeItem, currentObject,
							alreadyEvaluated, variables);
					switch (l.Type) {

					case Int:
						switch (r.Type) {
						case Int:
							new SpokeInstruction(SpokeInstructionType.Mod);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(l.Type);
							break;
						default:
							throw new Exception();
						}
						break;
					default:
						throw new Exception();
					}
				}
				return l;

			case Subtraction:
				ij = ((SpokeMathItem) condition).getItems();

				if (ij[0] == null) {
					ij[0] = new SpokeInt(0);
				}

				l = evaluateItem(ij[0], currentObject, alreadyEvaluated,
						variables);

				for (int i = 1; i < ij.length; i++) {
					SpokeItem spokeItem = ij[i];

					r = evaluateItem(spokeItem, currentObject,
							alreadyEvaluated, variables);

					switch (l.Type) {

					case Int:
						switch (r.Type) {
						case Int:
							new SpokeInstruction(
									SpokeInstructionType.SubtractIntInt);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(l.Type);
							break;
						case Float:
							new SpokeInstruction(
									SpokeInstructionType.SubtractIntFloat);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(r.Type);
							break;
						default:
							throw new Exception();
						}
						break;
					case Float:
						switch (r.Type) {
						case Int:
							new SpokeInstruction(
									SpokeInstructionType.SubtractFloatInt);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(l.Type);
							break;
						case Float:
							new SpokeInstruction(
									SpokeInstructionType.SubtractFloatFloat);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(l.Type);
							break;

						default:
							throw new Exception();
						}
						break;
					default:
						throw new Exception();
					}
				}
				return l;
			case Multiplication:
				ij = ((SpokeMathItem) condition).getItems();

				l = evaluateItem(ij[0], currentObject, alreadyEvaluated,
						variables);

				for (int i = 1; i < ij.length; i++) {
					SpokeItem spokeItem = ij[i];

					r = evaluateItem(spokeItem, currentObject,
							alreadyEvaluated, variables);

					switch (l.Type) {

					case Int:
						switch (r.Type) {
						case Int:
							new SpokeInstruction(
									SpokeInstructionType.MultiplyIntInt);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(l.Type);
							break;
						case Float:
							new SpokeInstruction(
									SpokeInstructionType.MultiplyIntFloat);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(r.Type);
							break;

						default:
							throw new Exception();
						}
						break;
					case Float:
						switch (r.Type) {
						case Int:
							new SpokeInstruction(
									SpokeInstructionType.MultiplyFloatInt);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(l.Type);
							break;

						case Float:
							new SpokeInstruction(
									SpokeInstructionType.MultiplyFloatFloat);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(l.Type);
							break;

						default:
							throw new Exception();
						}
						break;

					default:
						throw new Exception();
					}
				}
				return l;

			case Division:
				ij = ((SpokeMathItem) condition).getItems();

				l = evaluateItem(ij[0], currentObject, alreadyEvaluated,
						variables);

				for (int i = 1; i < ij.length; i++) {
					SpokeItem spokeItem = ij[i];

					r = evaluateItem(spokeItem, currentObject,
							alreadyEvaluated, variables);

					switch (l.Type) {

					case Int:
						switch (r.Type) {
						case Int:
							new SpokeInstruction(
									SpokeInstructionType.DivideIntInt);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(ObjectType.Int);
							break;

						case Float:
							new SpokeInstruction(
									SpokeInstructionType.DivideIntFloat);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(l.Type);
							break;

						default:
							throw new Exception();
						}
						break;

					case Float:
						switch (r.Type) {
						case Int:
							new SpokeInstruction(
									SpokeInstructionType.DivideFloatInt);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(l.Type);
							break;

						case Float:
							new SpokeInstruction(
									SpokeInstructionType.DivideFloatFloat);
							SpokeInstruction.LowerDebugging();

							l = new SpokeType(l.Type);
							break;

						default:
							throw new Exception();
						}
						break;

					default:
						throw new Exception();
					}
				}

				return l;
			case Greater:
				l = evaluateItem(((SpokeGreaterThan) condition).LeftSide,
						currentObject, alreadyEvaluated, variables);
				r = evaluateItem(((SpokeGreaterThan) condition).RightSide,
						currentObject, alreadyEvaluated, variables);
				switch (l.Type) {

				case Int:
					switch (r.Type) {
					case Int:
						new SpokeInstruction(SpokeInstructionType.GreaterIntInt);
						SpokeInstruction.LowerDebugging();

						return new SpokeType(ObjectType.Bool);

					case Float:
						new SpokeInstruction(
								SpokeInstructionType.GreaterIntFloat);
						SpokeInstruction.LowerDebugging();

						return new SpokeType(ObjectType.Bool);

					default:

						throw new Exception();
					}

				case Float:
					switch (r.Type) {
					case Int:
						new SpokeInstruction(
								SpokeInstructionType.GreaterFloatInt);
						SpokeInstruction.LowerDebugging();

						return new SpokeType(ObjectType.Bool);

					case Float:
						new SpokeInstruction(
								SpokeInstructionType.GreaterFloatFloat);
						SpokeInstruction.LowerDebugging();

						return new SpokeType(ObjectType.Bool);

					default:
						throw new Exception();
					}

				default:
					throw new Exception();
				}

			case Ternary:

				l = evaluateItem(((SpokeTernary) condition).Condition,
						currentObject, alreadyEvaluated, variables);

				if (l.Type != ObjectType.Bool) {
					throw new SpokeException("ternary condition isnt bool",
							((SpokeTernary) condition).getTokens());
				}

				new SpokeInstruction(
						SpokeInstructionType.IfTrueContinueElseGoto, "tern"
								+ condition.getGuid());

				l = evaluateItem(((SpokeTernary) condition).LeftSide,
						currentObject, alreadyEvaluated, variables);
				new SpokeInstruction(SpokeInstructionType.Goto, "endtern"
						+ condition.getGuid());

				new SpokeInstruction(SpokeInstructionType.Label, "tern"
						+ condition.getGuid());

				r = evaluateItem(((SpokeTernary) condition).RightSide,
						currentObject, alreadyEvaluated, variables);
				if (!l.Type.equals(r.Type)) {
					throw new SpokeException("tern types not equal",
							((SpokeTernary) condition).getTokens());
				}

				new SpokeInstruction(SpokeInstructionType.Label, "endtern"
						+ condition.getGuid());

				return new SpokeType(l.Type);

			case Less:

				l = evaluateItem(((SpokeLessThan) condition).LeftSide,
						currentObject, alreadyEvaluated, variables);
				r = evaluateItem(((SpokeLessThan) condition).RightSide,
						currentObject, alreadyEvaluated, variables);

				switch (l.Type) {

				case Int:
					switch (r.Type) {
					case Int:
						new SpokeInstruction(SpokeInstructionType.LessIntInt);
						SpokeInstruction.LowerDebugging();

						return new SpokeType(ObjectType.Bool);

					case Float:
						new SpokeInstruction(SpokeInstructionType.LessIntFloat);
						SpokeInstruction.LowerDebugging();

						return new SpokeType(ObjectType.Bool);

					default:
						throw new Exception();
					}

				case Float:
					switch (r.Type) {
					case Int:
						new SpokeInstruction(SpokeInstructionType.LessFloatInt);
						SpokeInstruction.LowerDebugging();

						return new SpokeType(ObjectType.Bool);

					case Float:
						new SpokeInstruction(
								SpokeInstructionType.LessFloatFloat);
						SpokeInstruction.LowerDebugging();

						return new SpokeType(ObjectType.Bool);

					default:
						throw new Exception();
					}

				default:
					throw new Exception();
				}

			case And:

				l = evaluateItem(((SpokeAnd) condition).LeftSide,
						currentObject, alreadyEvaluated, variables);
				if (l.Type != ObjectType.Bool) {
					throw new Exception("Expected bool");
				}

				new SpokeInstruction(
						SpokeInstructionType.IfTrueContinueElseGoto,
						"FakeShort" + condition.getGuid());
				r = evaluateItem(((SpokeAnd) condition).RightSide,
						currentObject, alreadyEvaluated, variables);
				if (r.Type != ObjectType.Bool) {
					throw new Exception("Expected bool");
				}

				new SpokeInstruction(SpokeInstructionType.Goto, "EndShort"
						+ condition.getGuid()).DebugToks = null;
				new SpokeInstruction(SpokeInstructionType.Label, "FakeShort"
						+ condition.getGuid()).DebugToks = null;
				new SpokeInstruction(SpokeInstructionType.BoolConstant, false).DebugToks = null;

				new SpokeInstruction(SpokeInstructionType.Label, "EndShort"
						+ condition.getGuid()).DebugToks = null;
				SpokeInstruction.LowerDebugging();

				return new SpokeType(ObjectType.Bool);

			case Or:
				new SpokeInstruction(SpokeInstructionType.Comment, "OR BEGIN").DebugToks = null;

				l = evaluateItem(((SpokeOr) condition).LeftSide, currentObject,
						alreadyEvaluated, variables);

				if (l.Type != ObjectType.Bool) {
					throw new Exception("Expected bool");
				}

				new SpokeInstruction(SpokeInstructionType.Not).DebugToks = null;
				new SpokeInstruction(
						SpokeInstructionType.IfTrueContinueElseGoto,
						"FakeShort" + condition.getGuid());

				r = evaluateItem(((SpokeOr) condition).RightSide,
						currentObject, alreadyEvaluated, variables);
				if (r.Type != ObjectType.Bool) {
					throw new Exception("Expected bool");
				}

				new SpokeInstruction(SpokeInstructionType.Goto, "EndShort"
						+ condition.getGuid()).DebugToks = null;
				new SpokeInstruction(SpokeInstructionType.Label, "FakeShort"
						+ condition.getGuid()).DebugToks = null;
				new SpokeInstruction(SpokeInstructionType.BoolConstant, true).DebugToks = null;

				new SpokeInstruction(SpokeInstructionType.Label, "EndShort"
						+ condition.getGuid()).DebugToks = null;
				SpokeInstruction.LowerDebugging();

				return new SpokeType(ObjectType.Bool);

			case GreaterEqual:
				l = evaluateItem(
						((SpokeGreaterThanOrEqual) condition).LeftSide,
						currentObject, alreadyEvaluated, variables);
				r = evaluateItem(
						((SpokeGreaterThanOrEqual) condition).RightSide,
						currentObject, alreadyEvaluated, variables);

				switch (l.Type) {

				case Int:
					switch (r.Type) {
					case Int:
						new SpokeInstruction(
								SpokeInstructionType.GreaterEqualIntInt);
						SpokeInstruction.LowerDebugging();

						return new SpokeType(ObjectType.Bool);

					case Float:
						new SpokeInstruction(
								SpokeInstructionType.GreaterEqualIntFloat);
						SpokeInstruction.LowerDebugging();

						return new SpokeType(ObjectType.Bool);

					default:
						throw new Exception();
					}

				case Float:
					switch (r.Type) {
					case Int:
						new SpokeInstruction(
								SpokeInstructionType.GreaterEqualFloatInt);
						SpokeInstruction.LowerDebugging();

						return new SpokeType(ObjectType.Bool);

					case Float:
						new SpokeInstruction(
								SpokeInstructionType.GreaterEqualFloatFloat);
						SpokeInstruction.LowerDebugging();

						return new SpokeType(ObjectType.Bool);

					default:
						throw new Exception();
					}

				default:
					throw new Exception();
				}

			case LessEqual:
				l = evaluateItem(((SpokeLessThanOrEqual) condition).LeftSide,
						currentObject, alreadyEvaluated, variables);
				r = evaluateItem(((SpokeLessThanOrEqual) condition).RightSide,
						currentObject, alreadyEvaluated, variables);

				switch (l.Type) {

				case Int:
					switch (r.Type) {
					case Int:
						new SpokeInstruction(
								SpokeInstructionType.LessEqualIntInt);
						SpokeInstruction.LowerDebugging();

						return new SpokeType(ObjectType.Bool);

					case Float:
						new SpokeInstruction(
								SpokeInstructionType.LessEqualIntFloat);
						SpokeInstruction.LowerDebugging();

						return new SpokeType(ObjectType.Bool);

					default:
						throw new Exception();
					}

				case Float:
					switch (r.Type) {
					case Int:
						new SpokeInstruction(
								SpokeInstructionType.LessEqualFloatInt);
						SpokeInstruction.LowerDebugging();

						return new SpokeType(ObjectType.Bool);

					case Float:
						new SpokeInstruction(
								SpokeInstructionType.LessEqualFloatFloat);
						SpokeInstruction.LowerDebugging();

						return new SpokeType(ObjectType.Bool);

					default:
						throw new Exception();
					}

				default:
					throw new Exception();
				}

			case Equality:
				l = evaluateItem(((SpokeEquality) condition).LeftSide,
						currentObject, alreadyEvaluated, variables);
				r = evaluateItem(((SpokeEquality) condition).RightSide,
						currentObject, alreadyEvaluated, variables);
				if (!l.CompareTo(r, false)) {
					throw new Exception(l.toString() + " not equal to "
							+ r.toString() + "    " + condition.toString());
				}
				new SpokeInstruction(SpokeInstructionType.Equal);
				SpokeInstruction.LowerDebugging();

				return new SpokeType(ObjectType.Bool);

			case NotEqual:
				l = evaluateItem(((SpokeNotEqual) condition).LeftSide,
						currentObject, alreadyEvaluated, variables);
				r = evaluateItem(((SpokeNotEqual) condition).RightSide,
						currentObject, alreadyEvaluated, variables);
				if (!l.CompareTo(r, false)) {
					throw new Exception(l.toString() + " not equal to "
							+ r.toString());
				}

				new SpokeInstruction(SpokeInstructionType.Equal);
				new SpokeInstruction(SpokeInstructionType.Not);
				SpokeInstruction.LowerDebugging();

				return new SpokeType(ObjectType.Bool);
			case Not:
				l = evaluateItem(((SpokeNot) condition).NotValue,
						currentObject, alreadyEvaluated, variables);
				if (l.Type != ObjectType.Bool)
					throw new Exception("Can only not bool    "
							+ (((SpokeNot) condition).NotValue));
				new SpokeInstruction(SpokeInstructionType.Not);
				SpokeInstruction.LowerDebugging();

				return new SpokeType(ObjectType.Bool);

			default:
				throw new Exception();
			}
		} catch (SpokeException egh) {
			throw egh;
		} catch (Exception eg) {
			throw new SpokeException(eg, ((SpokeBasic) condition).getTokens());
		}
		return null;
	}

	private Tuple2<String, Boolean> lastAnonMethodEntered() {
		Set<String> keys = getanonMethodsEntered().keySet();
		Collection<Boolean> vals = getanonMethodsEntered().values();

		Iterator<Boolean> i2 = vals.iterator();

		Tuple2<String, Boolean> last = null;
		for (Iterator<String> i1 = keys.iterator(); i1.hasNext()
				&& i2.hasNext();) {
			last = new Tuple2<String, Boolean>(i1.next(), i2.next());
		}
		return last;

	}
}
