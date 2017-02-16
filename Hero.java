package analyzer2;

import java.util.ArrayList;
import java.util.HashMap;

import skadistats.clarity.model.Entity;

public class Hero {
	private String id;
	private double health;
	private double mana;
	private String orientation;
	private double cellX;
	private double cellY;
	private double cellZ;
	private double vecX;
	private double vecY;
	private double vecZ;
	private HashMap<String, Ability> abilities = new HashMap<String, Ability>();
	private ArrayList<Hero> nearHeroes = new ArrayList<Hero>();
	
	public Hero(Entity entity){
		this.id = entity.getDtClass().getDtName();
		this.health = Double.parseDouble(entity.getProperty("m_iHealth").toString());
		this.mana = Double.parseDouble(entity.getProperty("m_flMana").toString());
		this.orientation = entity.getProperty("CBodyComponent.m_angRotation").toString();
		this.cellX = Double.parseDouble(entity.getProperty("CBodyComponent.m_cellX").toString());
		this.cellY = Double.parseDouble(entity.getProperty("CBodyComponent.m_cellY").toString());
		this.cellZ = Double.parseDouble(entity.getProperty("CBodyComponent.m_cellZ").toString());
		this.vecX = Double.parseDouble(entity.getProperty("CBodyComponent.m_vecX").toString());
		this.vecY = Double.parseDouble(entity.getProperty("CBodyComponent.m_vecX").toString());
		this.vecZ = Double.parseDouble(entity.getProperty("CBodyComponent.m_vecX").toString());
	}
	public ArrayList<Hero> getNearHero(){
		return this.nearHeroes;
	}
	public void emptyNearHerolist(){
		this.nearHeroes = new ArrayList<Hero>();
	}
	public void setNearHero(Hero hero){
		//condition
		double distance = Math.sqrt((this.cellX-hero.getCellX())*(this.cellX-hero.getCellX())+(this.cellY-hero.getCellY())*(this.cellY-hero.getCellY()));
		
		if(distance<=2)
			this.nearHeroes.add(hero);
		else
			this.nearHeroes.remove(hero);
	}
	public HashMap<String, Ability> getAbilities(){
		return this.abilities;
	}
	public void addAbility(String key, Ability a){
		this.abilities.put(key, a);
	}
	//getter
	public String getId(){
		return this.id;
	}
	public double getHealth(){
		return this.health;
	}
	public double getMana(){
		return this.mana;
	}
	public double getCellX(){
		return this.cellX;
	}
	public double getCellY(){
		return this.cellY;
	}
	public double getCellZ(){
		return this.cellZ;
	}
	public double getVecX(){
		return this.vecX;
	}
	public double getVecY(){
		return this.vecY;
	}
	public double getVecZ(){
		return this.vecZ;
	}
	public String getOrientation(){
		return this.orientation;
	}
	//setter
	public void setHealth(double health){
		this.health = health;
	}
	public void setMana(double mana){
		this.mana = mana;
	}
	public void setCellX(double cellX){
		this.cellX = cellX;
	}
	public void setCellY(double cellY){
		this.cellY = cellY;
	}
	public void setCellZ(double cellZ){
		this.cellZ = cellZ;
	}
	public void setVecX(double vecX){
		this.vecX = vecX;
	}
	public void setVecY(double vecY){
		this.vecY = vecY;
	}
	public void setVecZ(double vecZ){
		this.vecZ =vecZ;
	}
	public void setOrientation(String orientation){
		this.orientation = orientation;
	}
}
