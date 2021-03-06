package com.sk89q.craftbook.mech.crafting;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.sk89q.craftbook.BaseConfiguration;

public class RecipeManager extends BaseConfiguration {

    public static RecipeManager INSTANCE;
    private final Collection<Recipe> recipes;
    private final File config;

    public RecipeManager(FileConfiguration cfg, File dataFolder) {

	super(cfg, dataFolder);
	recipes = new ArrayList<Recipe>();
	config = new File(dataFolder, "crafting-recipes.yml");
	load(cfg.getConfigurationSection("crafting-recipes"));
	INSTANCE = this;
    }

    public void reload() {

	recipes.clear();
	load(YamlConfiguration.loadConfiguration(config).getConfigurationSection("crafting-recipes"));
    }

    private void load(ConfigurationSection cfg) {
	// lets load all recipes
	if(cfg == null) return; //If the config is null, it can't continue.
	Set<String> keys = cfg.getKeys(false);
	if (keys != null) {
	    for (String key : keys) {
		recipes.add(new Recipe(key, cfg));
	    }
	}
    }

    public static final class Recipe {

	private final String id;
	private final ConfigurationSection config;

	private String name;
	private String type;
	private Collection<CraftingItemStack> ingredients;
	private Collection<CraftingItemStack> results;

	private Recipe(String id, ConfigurationSection cfg) {

	    this.id = id;
	    config = cfg.getConfigurationSection(id);
	    ingredients = new ArrayList<CraftingItemStack>();
	    results = new ArrayList<CraftingItemStack>();
	    load();
	}

	private void load() {

	    name = config.getString("name");
	    type = config.getString("type");
	    ingredients = getItems(config.getConfigurationSection("ingredients"));
	    results = getItems(config.getConfigurationSection("results"));
	}

	private Collection<CraftingItemStack> getItems(ConfigurationSection section) {

	    Collection<CraftingItemStack> items = new ArrayList<CraftingItemStack>();
	    for (String item : section.getKeys(false)) {
		String[] split = item.split(":");
		Material material;
		try {
		    material = Material.getMaterial(Integer.parseInt(split[0]));
		} catch (NumberFormatException e) {
		    // use the name
		    material = Material.getMaterial(split[0].toUpperCase());
		}
		if (material != null) {
		    CraftingItemStack itemStack = new CraftingItemStack(material);
		    if (split.length > 1) {
			itemStack.setData(Short.parseShort(split[1]));
		    } else {
			itemStack.setData((short) -1);
		    }
		    itemStack.setAmount(section.getInt(item, 1));
		    items.add(itemStack);
		}
	    }
	    return items;
	}

	public String getId() {

	    return id;
	}

	public String getName() {

	    return name;
	}

	public String getType() {

	    return type;
	}

	/**
	 * Checks if the recipe
	 *
	 * @param items
	 *
	 * @return
	 */
	public boolean checkIngredients(Collection<CraftingItemStack> items) {

	    if (items.size() <= 0) return false;
	    for (CraftingItemStack item : items) {
		if (!ingredients.contains(item)) {
		    return false;
		}
	    }
	    return true;
	}

	public Collection<CraftingItemStack> getResults() {

	    return results;
	}
    }
}
