package analyzer2;

import skadistats.clarity.model.Entity;

public class Ability {
	private double cooldown;
	private double manaCost;
	private int[] category;
	private int level;
	public Ability(Entity entity, int[] category){
		this.cooldown = Double.parseDouble(entity.getProperty("m_fCooldown").toString());
		this.manaCost = Double.parseDouble(entity.getProperty("m_iManaCost").toString());
		this.level = Integer.parseInt(entity.getProperty("m_iLevel").toString());
		this.category = category;
	}
	public double getCooldown(){
		return this.cooldown;
	}
	public double getManaCost(){
		return this.manaCost;
	}
	public int[] getCategory(){
		return this.category;
	}
	public int getLevel(){
		return this.level;
	}
	public void setCooldown(double newCooldown){
		if(newCooldown!=this.cooldown)
			this.cooldown = newCooldown;
	}
	public void setManaCost(double newManaCost){
		if(newManaCost != this.manaCost)
			this.manaCost = newManaCost;
	}
	public void setLevel(int newLevel){
		if(newLevel != this.level)
			this.level = newLevel;
	}
}
