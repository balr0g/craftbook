package com.sk89q.craftbook.Spoke.SpokeInstructions;

import java.util.ArrayList;
import java.util.Iterator;

import com.sk89q.craftbook.Spoke.SpokeExpressions.ObjectType;
import com.sk89q.craftbook.Spoke.SpokeExpressions.SpokeObjectMethod;

public class SpokeObject {
	public int IntVal;
	public String StringVal;
	public boolean BoolVal;
	public float FloatVal;
	public ObjectType Type;
	public SpokeObject[] Variables;
	public ArrayList<SpokeObject> ArrayItems;
	public String ClassName;
	public transient SpokeObjectMethod AnonMethod;
	public boolean ByRef;
	public transient Object javaObject;

	public transient SpokeObject[] CurrentVariableStack;

	public SpokeObject() {

	}

	public SpokeObject(int inde) {
		IntVal = inde;
		Type = ObjectType.Int;
	}

	public SpokeObject(SpokeObject[] inde) {

		Variables = inde;
		Type = ObjectType.Object;
	}

	public SpokeObject(ArrayList<SpokeObject> inde) {
		ArrayItems = inde;
		Type = ObjectType.Array;

	}

	public SpokeObject(float inde) {
		FloatVal = inde;
		Type = ObjectType.Float;

	}

	public SpokeObject(String inde) {
		StringVal = inde;
		Type = ObjectType.String;

	}

	public SpokeObject(boolean inde) {
		BoolVal = inde;
		Type = ObjectType.Bool;

	}

	public SpokeObject(ObjectType type) {

		Type = type;
	}


	public SpokeObject(ObjectType type, Object obj) {
		Type = type;
		javaObject = obj;
	}

	public SpokeObject(ObjectType type, SpokeObjectMethod obj, SpokeObject[] variables2) {
		Type = type;
		AnonMethod = obj;
		CurrentVariableStack = variables2;
	}

	public void SetVariable(int name, SpokeObject obj) {
		Variables[name] = obj;

	}

	public SpokeObject GetVariable(int name, boolean forSet) {
		SpokeObject g = Variables[name];

		if (g == null || forSet) {
			return Variables[name] = new SpokeObject();
		}
		return g;
	}

	/*
	 * public bool TryGetVariable(int name, out SpokeObject obj) { return (obj =
	 * Variables[name]) != null; }
	 */

	public boolean Compare(SpokeObject obj) {
		return Compare(this, obj);
	}

	public static boolean Compare(SpokeObject left, SpokeObject right) {
		if (left == null) {
			if (right == null) {
				return true;
			}
			if (right.Type == ObjectType.Null) {
				return true;
			}
		} else {
			if (left.Type == ObjectType.Null && right == null) {
				return true;
			}
		}
		if (left.Type != right.Type) {
			return false;
		}
		switch (left.Type) {
		case Null:
			return true;
		case Int:
			return left.IntVal == right.IntVal;
		case Float:
			return left.FloatVal == right.FloatVal;
		case String:
			return left.StringVal.equals(right.StringVal);
		case Bool:
			return left.BoolVal == right.BoolVal;

		case Object:
			// throw new AbandonedMutexException("not yet cowbow");

			for (int i = 0; i < right.Variables.length; i++) {
				if (!Compare(left.Variables[i], right.Variables[i])) {
					return false;
				}
			}
			return true;
		case Array:

		case Method:
		default:

			// todo bad
			return false;
		}

	}

	@Override
	public String toString() {
		StringBuilder sb;
		switch (Type) {
		case Unset:
			return "fail";
		case Null:
			return "NULL";
		case Int:
			return Integer.toString(IntVal);
		case Float:
			return Float.toString(FloatVal);
		case String:
			return StringVal;
		case Bool:
			return Boolean.toString(BoolVal);
		case Object:

			sb = new StringBuilder();
			sb.append("{" + this.ClassName == null ? "" : this.ClassName);
			for (int index = 0; index < Variables.length; index++) {
				sb.append(",  " + index + ": " + Variables[index]);
			}
			return sb.toString()+"}";

		case Array:
			sb = new StringBuilder();

			sb.append("[");

			for (Iterator<SpokeObject> iterator = ArrayItems.iterator(); iterator.hasNext();) {
				SpokeObject o = (SpokeObject) iterator.next();
				sb.append("  " + o.toString());
			}
			sb.append("]");

			return sb.toString();
		case Method:
			sb = new StringBuilder();
			sb.append("(");
			for (ParamEter p : AnonMethod.Parameters) {
				sb.append(p.Name + ",");
			}
			sb.append(")"+AnonMethod.ReturnType);
			return sb.toString();
		default:
			// throw new ArgumentOutOfRangeException();
			return "";
		}

	}

	public void AddRangeArray(SpokeObject lastStack) {
		ArrayItems.addAll(lastStack.ArrayItems);
	}

	public SpokeObject AddArray(SpokeObject lastStack) {

		ArrayItems.add(lastStack);
		return this;
	}

	public SpokeObject AddArrayRange(SpokeObject lastStack) {
		ArrayItems.addAll(lastStack.ArrayItems);
		return this;
	}
}
