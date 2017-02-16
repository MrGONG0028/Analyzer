package analyzer2;

import skadistats.clarity.model.Entity;

public class Ability {
	private double cooldown;
	private double manaCost;
	public Ability(Entity entity){
		this.cooldown = Double.parseDouble(entity.getProperty("m_fCooldown").toString());
		this.manaCost = Double.parseDouble(entity.getProperty("m_iManaCost").toString());
	}
	public double getCooldown(){
		return this.cooldown;
	}
	public double getManaCost(){
		return this.manaCost;
	}
	public void setCooldown(double newCooldown){
		if(newCooldown!=this.cooldown)
			this.cooldown = newCooldown;
	}
	public void setManaCost(double newManaCost){
		if(newManaCost != this.manaCost)
			this.manaCost = newManaCost;
	}
}
