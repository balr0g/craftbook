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
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.sk89q.craftbook.Spoke.ALH.DictionaryKey;
import com.sk89q.craftbook.Spoke.ALH.DictionaryValue;
import com.sk89q.craftbook.Spoke.ALH.ManySelector;
import com.sk89q.craftbook.Spoke.ClassInfo.ClassWithLines;
import com.sk89q.craftbook.Spoke.SpokeExpressions.ObjectType;
import com.sk89q.craftbook.Spoke.SpokeExpressions.SpokeClass;
import com.sk89q.craftbook.Spoke.SpokeExpressions.SpokeGlobal;
import com.sk89q.craftbook.Spoke.SpokeExpressions.SpokeGlobalVariable;
import com.sk89q.craftbook.Spoke.SpokeExpressions.SpokeMethod;
import com.sk89q.craftbook.Spoke.SpokeExpressions.SpokeType;
import com.sk89q.craftbook.Spoke.SpokeInstructions.SpokeObject;

/**
 * Plugin for the Spoke Language.
 * 
 * @author Dested
 */
public class SpokeApplication {

	SpokeMethod[] allMethods;
	SpokeGlobal Global;
	HashMap<String, SpokeObject[]> GlobalVars = new HashMap<String, SpokeObject[]>();

	public String File;
	public final IToken[] Tokens;
	public Tuple3<ArrayList<SpokeClass>, ArrayList<SpokeEnum>, SpokeGlobal> PasserInfo;

	public SpokeApplication(SpokeMethod[] sm, SpokeGlobal item3, String total,
			IToken[] tokens) {
		allMethods = sm;
		Global = item3;
		File = total;
		Tokens = tokens;
	}

	public void loadGlobalsFile(String total) {

	}

	public SpokeObject[] getGlobal(String globalBank,
			GetGlobalObject getGlobalObject) {

		SpokeObject[] gc;
		if ((gc = GlobalVars.get(globalBank)) != null) {
			return gc;
		} else {

			SpokeObject[] so = new SpokeObject[Global.Variables.size()];
			for (SpokeGlobalVariable sv : Global.Variables) {
				so[sv.Index] = getGlobalObject
						.evalInstructions(sv.Instructions);
			}
			GlobalVars.put(globalBank, so);
			return so;
		}
	}
}