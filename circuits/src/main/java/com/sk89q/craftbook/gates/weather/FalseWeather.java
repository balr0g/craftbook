package com.sk89q.craftbook.gates.weather;


import java.util.List;

import org.bukkit.Server;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.sk89q.craftbook.ic.AbstractIC;
import com.sk89q.craftbook.ic.AbstractICFactory;
import com.sk89q.craftbook.ic.ChipState;
import com.sk89q.craftbook.ic.IC;
import com.sk89q.craftbook.ic.RestrictedIC;

public class FalseWeather extends AbstractIC {

    protected boolean risingEdge;

    public FalseWeather(Server server, Sign sign, boolean risingEdge) {
        super(server, sign);
        this.risingEdge = risingEdge;
    }

    @Override
    public String getTitle() {
        return "False Weather";
    }

    @Override
    public String getSignTitle() {
        return "FALSE WEATHER";
    }

    @Override
    public void trigger(ChipState chip) {
    	//FIXME
    	if(chip.getInput(0)) 
    	;
    }
    
    
    public boolean setPlayerWeather() {
    	boolean isGroup = false;
    	String name = "";

    	try {
    		String[] st = getSign().getLine(2).split(":");
    		if(st.length > 1) {
 //   			if(st[0].equals("g")) isGroup=true;
    			if(st[0].equals("p")) isGroup=false;
    			else return false;
    			name = st[1];
    		} else
    			name = st[0];
    	} catch(Exception e) { return false; }
    	
    	List<Player> players = getSign().getWorld().getPlayers();
    	for(Player p : players) {
    		if(p.getName().equals(name))
    			;
    	}
    	
    	
    	//FIXME
    	return false;
    }

    public static class Factory extends AbstractICFactory implements RestrictedIC {

        protected boolean risingEdge;

        public Factory(Server server, boolean risingEdge) {
            super(server);
            this.risingEdge = risingEdge;
        }

        @Override
        public IC create(Sign sign) {
            return new FalseWeather(getServer(), sign, risingEdge);
        }
    }

}
