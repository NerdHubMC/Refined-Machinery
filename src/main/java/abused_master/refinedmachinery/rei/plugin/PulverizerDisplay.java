package abused_master.refinedmachinery.rei.plugin;

import abused_master.refinedmachinery.registry.PulverizerRecipes;
import abused_master.refinedmachinery.rei.RefinedMachineryPlugin;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.recipe.Recipe;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PulverizerDisplay implements RecipeDisplay {

    private PulverizerRecipes.PulverizerRecipe recipe;
    private List<ItemStack> inputs;
    private int percentDrop;
    private ItemStack randomDrop;

    public PulverizerDisplay(List<ItemStack> inputs, PulverizerRecipes.PulverizerRecipe recipe) {
        this.recipe = recipe;
        this.inputs = inputs;
        this.percentDrop = recipe.getPercentageDrop();
        this.randomDrop = recipe.getRandomDrop();
    }

    @Override
    public Optional<Recipe<?>> getRecipe() {
        return Optional.empty();
    }

    @Override
    public List<List<ItemStack>> getInput() {
        return Collections.singletonList(inputs);
    }

    @Override
    public List<ItemStack> getOutput() {
        return Collections.singletonList(recipe.getOutput());
    }

    @Override
    public Identifier getRecipeCategory() {
        return RefinedMachineryPlugin.PULVERIZER;
    }

    @Override
    public List<List<ItemStack>> getRequiredItems() {
        return Collections.singletonList(inputs);
    }

    public int getPercentDrop() {
        return percentDrop;
    }

    public ItemStack getRandomDrop() {
        return randomDrop;
    }
}
