package com.sk89q.craftbook.Spoke.SpokeExpressions;

import java.util.ArrayList;
import java.util.HashMap;

import com.sk89q.craftbook.Spoke.SpokeException;
import com.sk89q.craftbook.Spoke.Tuple3;

public class SpokeVariableInfo {
	public HashMap<String, SpokeType> Variables;

	public SpokeVariableInfo(SpokeVariableInfo variables) {
		if (variables == null) {
			allVariables = new ArrayList<Tuple3<String, Integer, SpokeType>>();
			Variables = new HashMap<String, SpokeType>();
			return;
		}

		allVariables = variables.allVariables;
		index = variables.index;
		indeces = variables.indeces;
		Variables = new HashMap<String, SpokeType>(variables.Variables);
	}

	public void Done() {
		VariableNames = new String[allVariables.size()];
		for (Tuple3<String, Integer, SpokeType> v : allVariables) {
			VariableNames[v.Item2] = v.Item1;
		}
	}

	public String[] VariableNames;

	public SpokeVariableInfo() {
		allVariables = new ArrayList<Tuple3<String, Integer, SpokeType>>();
		Variables = new HashMap<String, SpokeType>();
	}

	private HashMap<String, Integer> indeces = new HashMap<String, Integer>();
	public int index;

	public ArrayList<Tuple3<String, Integer, SpokeType>> allVariables;

	public int Add(String s, SpokeType spokeType, SpokeVariable sv,
			boolean already) throws SpokeException {

		if (Variables.containsKey(s)) {
			if (!already) {
				throw new SpokeException("Variable already exists " + s,
						((SpokeBasic) sv).getTokens());
			} else {
				Variables.remove(s);
				Variables.put(s, spokeType);
				return indeces.get(s);
			}

		} else {

			for (Tuple3<String, Integer, SpokeType> av : allVariables)
				if (av.Item1.equals(s)) {
					{// its been removed, safe to reuse
						av.Item3 = spokeType;
						Variables.put(av.Item1, spokeType);
						indeces.put(av.Item1, av.Item2);
						return av.Item2;
					}
				}

		}
		allVariables.add(new Tuple3<String, Integer, SpokeType>(s, index,
				spokeType));
		Variables.put(s, spokeType);
		indeces.put(s, index++);
		if (sv != null) {
			sv.VariableIndex = indeces.get(s);
		}

		return index - 1;
	}

	public boolean Set(int index, SpokeType spokeType) {

		for (Tuple3<String, Integer, SpokeType> av : allVariables) {
			if (av.Item2.equals(index)) {
				av.Item3 = spokeType;
				Variables.remove(av.Item1);
				Variables.put(av.Item1, spokeType);
				return true;
			}
		}
		return false;

	}

	public void Remove(String s) {
		Variables.remove(s);
		indeces.remove(s);
	}

	public SpokeType TryGetValue(String s, SpokeVariable sv) {
		SpokeType spokeType = Variables.get(s);

		if (spokeType != null && sv != null)
			sv.VariableIndex = indeces.get(s);

		return spokeType;
	}

	public SpokeType Get(String variableName, SpokeVariable mv) {
		SpokeType def = Variables.get(variableName);
		if (def != null && mv != null)
			mv.VariableIndex = indeces.get(variableName);

		return def;
	}

	public void IncreaseState() {
	}

	public void DecreaseState() {

	}

	public void Reset(String variableName, SpokeType spokeType, SpokeVariable sv) {
		if (Variables.containsKey(variableName)) {
			Variables.remove(variableName);
			Variables.put(variableName, spokeType);

			if (sv != null) {
				sv.VariableIndex = indeces.get(variableName);
			}
		}
	}
}
