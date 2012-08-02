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

package com.sk89q.craftbook.bukkit;

import org.bukkit.event.Listener;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.Location;

import com.sk89q.bukkit.migration.*;
import com.sk89q.craftbook.CircuitsConfiguration;
import com.sk89q.craftbook.MechanicManager;
import com.sk89q.craftbook.circuits.*;
import com.sk89q.craftbook.gates.logic.*;
import com.sk89q.craftbook.gates.world.*;
import com.sk89q.craftbook.ic.ChipState;
import com.sk89q.craftbook.ic.IC;
import com.sk89q.craftbook.ic.ICFamily;
import com.sk89q.craftbook.ic.ICManager;
import com.sk89q.craftbook.ic.ICMechanicFactory;
import com.sk89q.craftbook.ic.families.*;
import com.sk89q.craftbook.Spoke.SpokeInternalMethod;
import com.sk89q.craftbook.Spoke.SpokeLoader;
import com.sk89q.craftbook.Spoke.Tuple2;
import com.sk89q.craftbook.Spoke.Tuple3;
import com.sk89q.craftbook.Spoke.SpokeExpressions.ObjectType;
import com.sk89q.craftbook.Spoke.SpokeExpressions.SpokeGlobal;
import com.sk89q.craftbook.Spoke.SpokeExpressions.SpokeType;
import com.sk89q.craftbook.Spoke.SpokeInstructions.SpokeObject;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.material.Button;

/**
 * Plugin for CraftBook's redstone additions.
 * 
 * @author sk89q
 */
public class CircuitsPlugin extends BaseBukkitPlugin {

	protected CircuitsConfiguration config;
	protected ICManager icManager;
	private PermissionsResolverManager perms;
	private MechanicManager manager;
	public boolean dontLoadSigns;

	@Override
	public void onEnable() {
		super.onEnable();

		createDefaultConfiguration("custom-ics.txt");
		createDefaultConfiguration("spokeGlobals.txt");

		config = new CircuitsConfiguration() {
			@Override
			public void loadConfiguration() {
			}
		};

		config.loadConfiguration();

		final Server server = getServer();

		// Prepare to answer permissions questions.
		perms = new PermissionsResolverManager(getConfiguration(), // FIXME this
																	// uh, isn't
																	// right.
				server, getDescription().getName(), logger);
		new PermissionsResolverServerListener(perms).register(this);

		manager = new MechanicManager(this);
		MechanicListenerAdapter adapter = new MechanicListenerAdapter(this);
		adapter.register(manager);

		registerICs();

		registerSpokeGlobals();

		// Let's register mechanics!
		manager.register(new Netherrack.Factory());
		manager.register(new JackOLantern.Factory());
		manager.register(new ICMechanicFactory(this, icManager));

		setupSelfTriggered();

	}

	public void onDisable() {
	}

	private void registerSpokeGlobals() {
		String total = OpenFile("spokeGlobals.txt");
		sl.loadGlobalsFile(total);
	}

	private String OpenFile(String fileName) {
		String total = "";
		try {

			// Open the file that is the first
			// command line parameter
			File actual = new File(getDataFolder(), fileName);

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
			System.out.println("Fail in globs" + exceptionStackTrace(e));
		}
		return total;
	}

	public String exceptionStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);

		return sw.toString();
	}

	/**
	 * Register ICs.
	 */
	private void registerICs() {
		Server server = getServer();

		// Let's register ICs!
		icManager = new ICManager();
		ICFamily familySISO = new FamilySISO();
		ICFamily family3ISO = new Family3ISO();
		ICFamily familySI3O = new FamilySI3O();
		ICFamily family3I3O = new Family3I3O();
		setupSpoke();

		// SISOs
		icManager.register("MC5002", new SpokeGate.Factory(server, sl, this),
				family3I3O);
		icManager.register("MC9999", new ResurrectDumbledore.Factory(server,
				true), familySISO);
		icManager.register("MC1000", new Repeater.Factory(server), familySISO);
		icManager.register("MC1001", new Inverter.Factory(server), familySISO);
		icManager.register("MC1017", new ToggleFlipFlop.Factory(server, true),
				familySISO);
		icManager.register("MC1018", new ToggleFlipFlop.Factory(server, false),
				familySISO);
		icManager.register("MC1020", new RandomBit.Factory(server, true),
				familySISO);
		icManager.register("MC1025",
				new ServerTimeModulus.Factory(server, true), familySISO);
		icManager.register("MC1110", new WirelessTransmitter.Factory(server),
				familySISO);
		icManager.register("MC1111",
				new WirelessReceiver.Factory(server, true), familySISO);
		icManager.register("MC1200", new CreatureSpawner.Factory(server, true),
				familySISO); // REQ PERM
		icManager.register("MC1201", new ItemDispenser.Factory(server, true),
				familySISO);
		icManager.register("MC1207", new FlexibleSetBlock.Factory(server),
				familySISO); // REQ PERM
		// Missing: 1202 (replaced by dispenser?) // REQ PERM
		icManager.register("MC1203", new LightningSummon.Factory(server, true),
				familySISO); // REQ PERM
		// Missing: 1205 // REQ PERM
		// Missing: 1206 // REQ PERM
		icManager.register("MC1230", new DaySensor.Factory(server, true),
				familySISO);
		icManager.register("MC1231", new TimeControl.Factory(server, true),
				familySISO); // REQ PERM
		icManager.register("MC1260", new WaterSensor.Factory(server, true),
				familySISO);
		icManager.register("MC1261", new LavaSensor.Factory(server, true),
				familySISO);
		icManager.register("MC1262", new LightSensor.Factory(server, true),
				familySISO);
		// Missing: 1240 (replaced by dispenser?) // REQ PERM
		// Missing: 1241 (replaced by dispenser?) // REQ PERM
		// Missing: 1420
		icManager.register("MC1510", new MessageSender.Factory(server, true),
				familySISO);

		// SI3Os
		// Missing: 2020 (?)
		icManager.register("MC2999", new Marquee.Factory(server), familySI3O);

		// 3ISOs
		icManager.register("MC3002", new AndGate.Factory(server), family3ISO);
		icManager.register("MC3003", new NandGate.Factory(server), family3ISO);
		icManager.register("MC3020", new XorGate.Factory(server), family3ISO);
		icManager.register("MC3021", new XnorGate.Factory(server), family3ISO);
		icManager.register("MC3030", new RsNorFlipFlop.Factory(server),
				family3ISO);
		icManager.register("MC3031", new InvertedRsNandLatch.Factory(server),
				family3ISO);
		icManager
				.register("MC3032", new JkFlipFlop.Factory(server), family3ISO);
		icManager.register("MC3033", new RsNandLatch.Factory(server),
				family3ISO);
		icManager.register("MC3034", new EdgeTriggerDFlipFlop.Factory(server),
				family3ISO);
		icManager.register("MC3036",
				new LevelTriggeredDFlipFlop.Factory(server), family3ISO);
		icManager.register("MC3040", new Multiplexer.Factory(server),
				family3ISO);
		icManager.register("MC3101", new DownCounter.Factory(server),
				family3ISO);
		// Missing: 3231 // REQ PERM

		// 3I3Os
		// Missing: 4000
		// Missing: 4010
		// Missing: 4100
		// Missing: 4110
		// Missing: 4200

		// Self triggered
		icManager.register("MC0111", new WirelessReceiverST.Factory(server),
				familySISO);
		icManager.register("MC0260", new WaterSensorST.Factory(server),
				familySISO);
		icManager.register("MC0261", new LavaSensorST.Factory(server),
				familySISO);

		// Missing: 0020
		// Missing: 0230
		// Missing: 0262
		// Missing: 0420

	}

	private void setupSpoke() {

		String total = OpenFile("WorldGen.Spoke");

		sl = new SpokeLoader();

		sl.addInternalMethod(new SpokeInternalMethod() {
			@Override
			public SpokeObject Evaluate(SpokeObject[] parameters) {
				System.out.print(parameters[1].toString());
				return null;
			}

			@Override
			public String getMethodName() {
				return "print";
			}

			@Override
			public SpokeType getMethodType() {
				return new SpokeType(ObjectType.Void);
			}

			@Override
			public Integer getMethodIndex() {
				return 0;
			}
		});
		sl.addInternalMethod(new SpokeInternalMethod() {
			@Override
			public SpokeObject Evaluate(SpokeObject[] parameters) {


				try {
					getServer().getPlayer(parameters[1].toString())
							.sendMessage(parameters[2].toString());
				} catch (Exception ec) {
					System.out.println("bad " + ec.getStackTrace());
				}
				return null;
			}

			@Override
			public String getMethodName() {
				return "sendMessage";
			}

			@Override
			public SpokeType getMethodType() {
				return new SpokeType(ObjectType.Void);
			}

			@Override
			public Integer getMethodIndex() {
				return 1;
			}
		});
		sl.addInternalMethod(new SpokeInternalMethod() {
			@Override
			public SpokeObject Evaluate(SpokeObject[] parameters) {

				return new SpokeObject(Math.abs(parameters[1].IntVal));
			}

			@Override
			public String getMethodName() {
				return "MathABS";
			}

			@Override
			public SpokeType getMethodType() {
				return new SpokeType(ObjectType.Float);
			}

			@Override
			public Integer getMethodIndex() {
				return 2;
			}
		});
		sl.addInternalMethod(new SpokeInternalMethod() {
			@Override
			public SpokeObject Evaluate(SpokeObject[] parameters) {

				return new SpokeObject((int) Math.sqrt(parameters[1].FloatVal));
			}

			@Override
			public String getMethodName() {
				return "MathSQRT";
			}

			@Override
			public SpokeType getMethodType() {
				return new SpokeType(ObjectType.Int);
			}

			@Override
			public Integer getMethodIndex() {
				return 3;
			}
		});

		try {

			sl.load(total, getServer(), this, getServer().getWorlds().get(0)
					.getName()/*
							 * TODO fixname
							 */);
		} catch (Exception e) {
			dontLoadSigns = true;
e.printStackTrace();
			System.out.println(e.getMessage());
		}

		ms = new MiniScheduler();
		this.getServer().getScheduler()
				.scheduleSyncRepeatingTask(this, ms, 1, 1);
 
	}

	MiniScheduler ms;

	public MiniScheduler getScheduler() {
		return ms;
	}

	public class MiniScheduler implements Runnable {

		ArrayList<SchedulePiece> statesToUpdate = new ArrayList<SchedulePiece>();

		public class SchedulePiece {
			public ChipState State;
			public IC Chip;
			public Location Location;

			public SchedulePiece(IC chip, ChipState cs, Location l) {
				State = cs;
				Location = l;
				Chip = chip;
			}
		}

		int ticks = 0;

		@Override
		public void run() {
			if (statesToUpdate.size() > 0) {
				for (int i = statesToUpdate.size() - 1; i >= 0; i--) {
					SchedulePiece cm = statesToUpdate.get(i);
					cm.Chip.trigger(cm.State);
					statesToUpdate.remove(i);
				}
			}

			if (blocks.size() > 0 && pushChanges) {

				ArrayList<AsyncBlockChange> gm = blocks.get(0);
				if (gm.size() > 0) {
					System.out.println("printing " + gm.size() + " blocks");
					World w = getServer().getWorld(
							getServer().getWorlds().get(0).getName());

					Object[] d = gm.toArray();
					blocks.remove(0);

					for (int i = 0; i < d.length; i++) {
						AsyncBlockChange tuple2 = (AsyncBlockChange) d[i];

						Block block = w.getBlockAt(tuple2.Location);
						if (tuple2.Face != null) {
							block.setType(tuple2.Material);

							Button bc = new Button();
							bc.setFacingDirection(tuple2.Face);
							block.setData(bc.getData());

						} else if (tuple2.Lines != null) {
							block.setTypeIdAndData(tuple2.TypeID, tuple2.Data1,
									false);
							block.setData(tuple2.Data2);
							CraftSign s = new CraftSign(block);
							for (int ir = 0; ir < tuple2.Lines.length; ir++) {
								s.setLine(ir, tuple2.Lines[ir]);
							}

						} else if (tuple2.Lines == null) {
							w.getBlockAt(tuple2.Location).setTypeIdAndData(
									tuple2.TypeID, tuple2.Data1, false);
						}

					}
				} else {
					pushChanges = false;
				}
			}

			ticks++;
		}

		public void push(IC chip, ChipState ms, Sign state) {
			Location dmc = new Location(state.getWorld(), state.getX(),
					state.getY(), state.getZ());

			for (int i = statesToUpdate.size() - 1; i >= 0; i--) {
				SchedulePiece sp = statesToUpdate.get(i);
				if (sp.Location.equals(dmc)) {
					return;
				}
			}
			statesToUpdate.add(new SchedulePiece(chip, ms, dmc));
		}

		final int Thresh = 20000;
		ArrayList<ArrayList<AsyncBlockChange>> blocks = new ArrayList<ArrayList<AsyncBlockChange>>(
				20);

		public void push(Location loc, int typeID) {
			ArrayList<AsyncBlockChange> cc;
			if (blocks.size() == 0) {
				blocks.add(cc = new ArrayList<AsyncBlockChange>(Thresh));
			} else
				cc = blocks.get(blocks.size() - 1);

			if (cc.size() > Thresh) {
				blocks.add(cc = new ArrayList<AsyncBlockChange>(Thresh));
			}

			cc.add(new AsyncBlockChange(loc, typeID, (byte) 0));

		}

		public void push(Location loc, int typeID, byte byteID) {
			ArrayList<AsyncBlockChange> cc;
			if (blocks.size() == 0) {
				blocks.add(cc = new ArrayList<AsyncBlockChange>(Thresh));
			} else
				cc = blocks.get(blocks.size() - 1);

			if (cc.size() > Thresh) {
				blocks.add(cc = new ArrayList<AsyncBlockChange>(Thresh));
			}

			cc.add(new AsyncBlockChange(loc, typeID, byteID));

		}

		public void pushSign(Location loc, byte direction, String[] lines) {
			ArrayList<AsyncBlockChange> cc;
			if (blocks.size() == 0) {
				blocks.add(cc = new ArrayList<AsyncBlockChange>(Thresh));
			} else
				cc = blocks.get(blocks.size() - 1);

			if (cc.size() > Thresh) {
				blocks.add(cc = new ArrayList<AsyncBlockChange>(Thresh));
			}

			cc.add(new AsyncBlockChange(loc, 68, (byte) 1, direction, lines));

		}

		public void pushAsyncChange(AsyncBlockChange dov) {
			ArrayList<AsyncBlockChange> cc;
			if (blocks.size() == 0) {
				blocks.add(cc = new ArrayList<AsyncBlockChange>(Thresh));
			} else
				cc = blocks.get(blocks.size() - 1);

			if (cc.size() > Thresh) {
				blocks.add(cc = new ArrayList<AsyncBlockChange>(Thresh));
			}

			cc.add(dov);

		}

		boolean pushChanges = false;

		public void pushChanges() {
			pushChanges = true;

		}
	}

	SpokeLoader sl;

	/**
	 * Setup the required components of self-triggered ICs.
	 */
	private void setupSelfTriggered() {
		logger.info("CraftBook: Enumerating chunks for self-triggered components...");

		long start = System.currentTimeMillis();
		int numWorlds = 0;
		int numChunks = 0;

		for (World world : getServer().getWorlds()) {
			for (Chunk chunk : world.getLoadedChunks()) {
				manager.enumerate(chunk);
				numChunks++;
			}

			numWorlds++;
		}

		long time = System.currentTimeMillis() - start;

		logger.info("CraftBook: " + numChunks + " chunk(s) for " + numWorlds
				+ " world(s) processed " + "(" + Math.round(time / 1000.0 * 10)
				/ 10 + "s elapsed)");

		// Set up the clock for self-triggered ICs.
		getServer().getScheduler().scheduleSyncRepeatingTask(this,
				new MechanicClock(manager), 0, 2);
	}

	@Override
	protected void registerEvents() {

		registerEvent(Type.PLAYER_COMMAND_PREPROCESS, new PlayerListener() {
			@Override
			public void onPlayerCommandPreprocess(PlayerChatEvent event) {
				String msg = event.getMessage().toLowerCase(); 
				if (msg.startsWith("/spoke ")) {
					if (msg.equals("/spoke reload")) {
						setupSpoke();
						event.getPlayer().sendMessage("Spoke reloaded");
					}
				}
			}
		}, Priority.Normal);

	}

	public CircuitsConfiguration getLocalConfiguration() {
		return config;
	}

	public PermissionsResolverManager getPermissionsResolver() {
		return perms;
	}

}
