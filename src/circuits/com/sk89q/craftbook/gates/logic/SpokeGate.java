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

package com.sk89q.craftbook.gates.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.sk89q.craftbook.LocalPlayer;
import com.sk89q.craftbook.ic.AbstractIC;
import com.sk89q.craftbook.ic.AbstractICFactory;
import com.sk89q.craftbook.ic.ChipState;
import com.sk89q.craftbook.ic.IC;
import com.sk89q.craftbook.ic.ICUtil;
import com.sk89q.craftbook.ic.MemoryManaged;
import com.sk89q.craftbook.ic.MultiInputIC;
import com.sk89q.craftbook.ic.PersistentIC;

import org.bukkit.entity.Player;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

import com.sk89q.craftbook.ic.PersistentIC;
import com.sk89q.craftbook.Spoke.SpokeExpressions.SpokeMethod;
import com.sk89q.craftbook.Spoke.SpokeExpressions.SpokeType;
import com.sk89q.craftbook.Spoke.IToken;
import com.sk89q.craftbook.Spoke.SpokeCommon;
import com.sk89q.craftbook.Spoke.SpokeException;
import com.sk89q.craftbook.Spoke.SpokeLoader;
import com.sk89q.craftbook.Spoke.SpokeLoader.InstructionsFinished;
import com.sk89q.craftbook.Spoke.SpokeMethodParameter;
import com.sk89q.craftbook.Spoke.SpokeInstructions.SpokeObject;
import com.sk89q.craftbook.Spoke.Tuple2;

import org.bukkit.block.Sign;
import com.sk89q.craftbook.bukkit.CircuitsPlugin;

import com.sk89q.craftbook.util.BlockWorldVector;
import com.sk89q.craftbook.util.SignUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import com.sk89q.craftbook.Spoke.SpokeExpressions.ObjectType;

import java.io.PrintWriter;
import java.io.StringWriter;

public class SpokeGate extends AbstractIC implements MultiInputIC,
		MemoryManaged {

	String ClassName;
	String MethodName;

	CircuitsPlugin plugin;

	@Override
	public boolean isActive() {
		return true;
	}

	public SpokeGate(Server server, Sign sign, SpokeLoader ldr,
			CircuitsPlugin cls) {
		super(server, sign);
		plugin = cls;
		loader = ldr;

		if (cls.dontLoadSigns) {

			return;
		}
		theInts.put(getSignVal(sign), new ArrayList<SpokeIntPiece>());

		String[] dm = sign.getLine(2).split("-");

		if (dm.length == 2) {
			ClassName = dm[0];
			MethodName = dm[1];
		}
		signParams = new String[0];
		if (getSign().getLine(3).length() > 0) {
			signParams = getSign().getLine(3).split("-");
		}
		try {
			smethod = loader.getMethod(ClassName, MethodName);
		} catch (Exception e) {
			System.out.println("Method throwy " + ClassName + " " + MethodName);
		}

		if (smethod == null) {
			System.out.println("Method no existy " + ClassName + " "
					+ MethodName);

			SpokeCommon.dropSign(sign.getWorld(), sign.getX(), sign.getY(),
					sign.getZ());
			return;
		}

	}

	String[] signParams;

	@Override
	public String getTitle() {
		return "Spoke Gate";
	}

	@Override
	public String getSignTitle() {
		return "SPOKE";
	}

	public String exceptionStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);

		return sw.toString();
	}

	HashMap<String, ArrayList<SpokeIntPiece>> theInts = new HashMap<String, ArrayList<SpokeIntPiece>>();

	public class SpokeIntPiece {
		public boolean[] Integer = new boolean[32];
		public int Position = 0;
	}

	public static String getSignVal(Sign s) {
		String r = Integer.toString(s.getX()) + Integer.toString(s.getY())
				+ Integer.toString(s.getZ());
		for (int i = 0; i < s.getLines().length; i++) {
			r += s.getLines()[i];
		}
		return r;
	}

	int booleanArrayToInt(boolean[] a) {
		int result = 0;
		for (int i = 0; i < a.length; i++) {
			int value = intValue(a[i]) << i;
			result = result | value;
		}

		return result;
	}

	int intValue(boolean b) {
		if (b)
			return 1;
		else
			return 0;
	}

	SpokeMethod smethod;

	@Override
	public void trigger(final ChipState chip) {
		try {
			if (plugin.dontLoadSigns) {

				return;
			}

 

			Block bsign = getSign().getBlock();

			BlockFace fback = SignUtil.getBack(bsign);

			Block back = bsign.getRelative(fback);

			SpokeObject[] vals = new SpokeObject[smethod.Parameters.length];
			Block cur;

			int counter;
			for (counter = 0; counter < signParams.length; counter++) {

				switch (smethod.Parameters[counter].Type.Type) {
				case Int:
					vals[counter] = new SpokeObject(
							Integer.parseInt(signParams[counter]));
					break;
				case Float:
					vals[counter] = new SpokeObject(
							Float.parseFloat(signParams[counter]));
					break;
				case String:
					vals[counter] = new SpokeObject(signParams[counter]);
					break;
				case Bool:
					vals[counter] = new SpokeObject(
							Boolean.parseBoolean(signParams[counter]));
					break;
				}
			}
			boolean shouldReturn = false;
			boolean first = false;
			counter = signParams.length - 1;
			for (Block inputBlock : inputBlocks) {

				if (!first && needsHead) {
					first = true;
					continue;
				}
				first = true;
				counter++;

				boolean isPowered = (inputBlock.isBlockIndirectlyPowered());

				switch (smethod.Parameters[counter].Type.Type) {
				case Int:
					ArrayList<SpokeIntPiece> jm = theInts
							.get(getSignVal(getSign()));

					if (jm.size() == (smethod.Parameters.length - signParams.length)) {
						SpokeIntPiece jc = jm.get(counter - signParams.length);
						if (jc.Position == 31) {
							jc.Integer[jc.Position++] = isPowered;
							vals[counter] = new SpokeObject(
									booleanArrayToInt(jc.Integer));
							jc.Integer = new boolean[32];
							jc.Position = -2;
						} else {
							if (jc.Position < 0) {
								jc.Position++;
								shouldReturn = true;
								continue;
							}
							shouldReturn = true;
							jc.Integer[jc.Position++] = isPowered;
							continue;
						}
					} else {
						shouldReturn = true;
						SpokeIntPiece jc;
						jm.add((jc = new SpokeIntPiece()));

						continue;
					}
					break;
				case Bool:
					vals[counter] = new SpokeObject(isPowered);
					break;
				}
			}

			if (shouldReturn) {
				return;
			}
			SpokeObject rtn = null;

			loader.Run(ClassName, MethodName, vals, getMemoryBank(),
					new InstructionsFinished() {

						@Override
						public void Do(SpokeObject d) {
							scriptDone(d);

						}

						@Override
						public void Bad(SpokeException e, IToken[] file) {
							System.out.print(e.Print(file));
							e.Exc.printStackTrace();

						}
					});

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Scriptccc failed    " + exceptionStackTrace(e));
		}
	}

	private void scriptDone(SpokeObject rtn) {

		plugin.getScheduler().pushChanges();

		Block bsign = getSign().getBlock();

		BlockFace fback = SignUtil.getBack(bsign);

		Block back;
		ArrayList<Block> levers = new ArrayList<Block>();

		if (rtn == null)
			return;

		int farBack = 0;
		back = bsign.getRelative(fback);
		BlockFace frontface = fback.getOppositeFace();

		Material original = back.getType();

		Block lastBlock;
		while (true) {
			lastBlock = back;
			back = back.getRelative(fback);
			farBack++;
			if (back.getType() == Material.LEVER) {
				levers.add(back);
				back = back.getRelative(frontface);
				break;
			}
			if (back.getType() != original) {
				if (lastBlock.getRelative(SignUtil.getCounterClockWise(fback))
						.getType() == Material.LEVER
						|| lastBlock.getRelative(SignUtil.getClockWise(fback))
								.getType() == Material.LEVER) {
					break;
				} else {
					System.out.println("not setup right");
					return;
				}
			}
		}

		if (rtn.Type == ObjectType.Bool
				|| (rtn.Type == ObjectType.Array && rtn.ArrayItems.size() > 0 && rtn.ArrayItems
						.get(0).Type == ObjectType.Bool)) {
			for (int i = farBack; i >= 0; i--) {
				if (back.getRelative(SignUtil.getCounterClockWise(fback))
						.getType() == Material.LEVER) {
					levers.add(back.getRelative(SignUtil
							.getCounterClockWise(fback)));
				}
				if (back.getRelative(SignUtil.getClockWise(fback)).getType() == Material.LEVER) {
					levers.add(back.getRelative(SignUtil.getClockWise(fback)));
				}
				back = back.getRelative(frontface);
			}

		} else if (rtn.Type == ObjectType.Int
				|| (rtn.Type == ObjectType.Array && rtn.ArrayItems.size() > 0 && rtn.ArrayItems
						.get(0).Type == ObjectType.Int)) {
			for (int i = farBack; i >= 0; i--) {
				if (back.getRelative(SignUtil.getCounterClockWise(fback))
						.getType() == Material.LEVER) {
					levers.add(back.getRelative(SignUtil
							.getCounterClockWise(fback)));
				}
				if (back.getRelative(SignUtil.getClockWise(fback)).getType() == Material.LEVER) {
					levers.add(back.getRelative(SignUtil.getClockWise(fback)));
				}
				back = back.getRelative(frontface);
			}

		}

		int secs = 6;

		if (rtn != null) {
			switch (rtn.Type) {
			case Int:
				if ((rtn.IntVal == 0))
					return;
				boolean[] dc = toBytes(rtn.IntVal);
				boolean last = false;

				getServer().getScheduler().scheduleSyncDelayedTask(plugin,
						new TurnBlockState(levers.get(0), true), secs * (0));
				getServer().getScheduler().scheduleSyncDelayedTask(plugin,
						new TurnBlockState(levers.get(1), true), secs * (0));
				for (int a = 0; a < dc.length; a++) {
					getServer().getScheduler().scheduleSyncDelayedTask(plugin,
							new TurnBlockState(levers.get(0), dc[a]),
							secs * (a + 1));
					getServer().getScheduler().scheduleSyncDelayedTask(plugin,
							new TurnBlockState(levers.get(1), last),
							secs * (a + 1));
					last = !last;
				}
				getServer().getScheduler().scheduleSyncDelayedTask(plugin,
						new TurnBlockState(levers.get(0), false),
						secs * (dc.length + 1));
				getServer().getScheduler().scheduleSyncDelayedTask(plugin,
						new TurnBlockState(levers.get(1), false),
						secs * (dc.length + 1));

				break;
			case Bool:
				new TurnBlockState(levers.get(0), rtn.BoolVal).run();
				break;
			case Array:
				if (rtn.ArrayItems.size() > 0) {

					switch (rtn.ArrayItems.get(0).Type) {
					case Int:

						boolean built = false;
						boolean good = true;
						for (int a = 0; a < rtn.ArrayItems.size(); a++) {
							if (rtn.ArrayItems.get(a).IntVal != 0) {
								continue;
							}
							good = false;
						}
						if (!good) {
							return;
						}

						int leverNumber = 1;
						for (int a = 0; a < rtn.ArrayItems.size(); a++) {
							boolean[] dc2 = toBytes(rtn.ArrayItems.get(a).IntVal);

							last = false;

							if (!built) {
								getServer().getScheduler()
										.scheduleSyncDelayedTask(
												plugin,
												new TurnBlockState(levers
														.get(0), true),
												secs * (0));
							}

							getServer().getScheduler().scheduleSyncDelayedTask(
									plugin,
									new TurnBlockState(levers.get(leverNumber),
											true), secs * (0));
							for (int ac = 0; ac < dc2.length; ac++) {
								if (!built) {
									getServer().getScheduler()
											.scheduleSyncDelayedTask(
													plugin,
													new TurnBlockState(levers
															.get(0), last),
													secs * (ac + 1));
								}
								getServer().getScheduler()
										.scheduleSyncDelayedTask(
												plugin,
												new TurnBlockState(levers
														.get(leverNumber),
														dc2[ac]),
												secs * (ac + 1));
								last = !last;
							}
							if (!built) {
								getServer().getScheduler()
										.scheduleSyncDelayedTask(
												plugin,
												new TurnBlockState(levers
														.get(0), false),
												secs * (dc2.length + 1));
							}
							getServer().getScheduler().scheduleSyncDelayedTask(
									plugin,
									new TurnBlockState(levers.get(leverNumber),
											false), secs * (dc2.length + 1));

							leverNumber++;
							built = true;
						}
						break;
					case Bool:

						for (int a = 0; a < rtn.ArrayItems.size(); a++) {
							new TurnBlockState(levers.get(a),
									rtn.ArrayItems.get(a).BoolVal).run();
						}

						break;
					}
				}
				break;
			}
		}
	}

	public static boolean[] toBytes(int n) {
		boolean[] barrz = new boolean[32];

		for (int a = 0; a < 32; a++) {
			barrz[a] = ((n & (1 << a)) != 0);

		}
		return barrz;
	}

	public class TurnBlockState implements Runnable {
		Block Cs;
		boolean Value;

		public TurnBlockState(Block cs, boolean value) {
			Value = value;
			Cs = cs;
		}

		public void run() {
			ICUtil.setState(Cs, Value);
		}
	}

	SpokeLoader loader;

	public static class Factory extends AbstractICFactory {

		SpokeLoader ldr;
		CircuitsPlugin cls;

		public Factory(Server server, SpokeLoader sl, CircuitsPlugin pls) {
			super(server);
			cls = pls;

			ldr = sl;
		}

		@Override
		public IC create(Sign sign) {

			return new SpokeGate(getServer(), sign, ldr, cls);
		}
	}

	ArrayList<Block> inputBlocks = new ArrayList<Block>();

	boolean needsHead = false;

	@Override
	public BlockWorldVector[] getTriggerPositions(BlockWorldVector position,
			LocalPlayer player, Sign sign) {

		ArrayList<BlockWorldVector> inputs = new ArrayList<BlockWorldVector>();
		if (plugin.dontLoadSigns) {

			return new BlockWorldVector[0];
		}

		int numOfParams = 0;

		for (int i = signParams.length; i < smethod.Parameters.length; i++) {
			SpokeMethodParameter param = smethod.Parameters[i];
			switch (param.Type.Type) {
			case Bool:
				numOfParams++;
				break;
			case Int:
				if (!needsHead)
					numOfParams++;
				needsHead = true;
				numOfParams++;
				break;
			}
		}

		Block bsign = sign.getBlock();

		inputs.add(new BlockWorldVector(bsign.getWorld(), bsign.getX(), bsign
				.getY(), bsign.getZ()));
		Block cur;
		cur = SignUtil.getFrontBlock(bsign);

		if (cur.getType() == Material.REDSTONE_WIRE) {

			inputBlocks.add(cur);
			if (inputBlocks.size() == numOfParams) {
				BlockWorldVector[] vc = new BlockWorldVector[inputs.size()];
				inputs.toArray(vc);
				return vc;
			}

		}
		cur = SignUtil.getLeftBlock(bsign);
		if (cur.getType() == Material.REDSTONE_WIRE) {

			inputBlocks.add(cur);

			if (inputBlocks.size() == numOfParams) {
				BlockWorldVector[] vc = new BlockWorldVector[inputs.size()];
				inputs.toArray(vc);
				return vc;
			}

		}
		cur = SignUtil.getRightBlock(bsign);
		if (cur.getType() == Material.REDSTONE_WIRE) {

			inputBlocks.add(cur);
			if (inputBlocks.size() == numOfParams) {
				BlockWorldVector[] vc = new BlockWorldVector[inputs.size()];
				inputs.toArray(vc);
				return vc;
			}

		}

		BlockFace fback = SignUtil.getBack(bsign);

		Block back = bsign.getRelative(fback);

		Material original = back.getType();
		int farBack = 1;
		while (true) {
			back = back.getRelative(fback);
			farBack++;
			if (farBack % 2 == 0) {
				if (back.getType() == original) {
					boolean okay = false;
					if ((cur = back.getRelative(SignUtil.getClockWise(fback)))
							.getType() == Material.WALL_SIGN) {

						inputBlocks.add(cur.getRelative(SignUtil
								.getClockWise(fback)));

						inputs.add(new BlockWorldVector(cur.getWorld(), cur
								.getX(), cur.getY(), cur.getZ()));
						if (inputBlocks.size() == numOfParams) {
							BlockWorldVector[] vc = new BlockWorldVector[inputs
									.size()];
							inputs.toArray(vc);
							return vc;
						}
						okay = true;
					}
					if ((cur = back.getRelative(SignUtil
							.getCounterClockWise(fback))).getType() == Material.WALL_SIGN) {
						inputBlocks.add(cur.getRelative(SignUtil
								.getCounterClockWise(fback)));

						inputs.add(new BlockWorldVector(cur.getWorld(), cur
								.getX(), cur.getY(), cur.getZ()));
						if (inputBlocks.size() == numOfParams) {
							BlockWorldVector[] vc = new BlockWorldVector[inputs
									.size()];
							inputs.toArray(vc);
							return vc;
						}
						okay = true;
					}

					if (okay)
						continue;
					else
						break;

				} else
					break;
			} else
				continue;
		}

		System.out.println("not enough parameters  " + inputs.size() + " "
				+ ClassName + " " + MethodName);
		return new BlockWorldVector[] { position };
	}

	@Override
	public String getMemoryBank() {
		return getSign().getLine(0).replace(getSignTitle(), "");
	}
}
