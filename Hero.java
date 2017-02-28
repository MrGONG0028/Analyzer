package analyzer2;

import java.util.ArrayList;
import java.util.HashMap;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import skadistats.clarity.model.Entity;

public class Hero {
	private String id;
	private double health;
	private double mana;
	private String orientation;
	private double cordX;
	private double cordY;
	private HashMap<String, Ability> abilities = new HashMap<String, Ability>();
	private ArrayList<Hero> nearHeroes = new ArrayList<Hero>();
	private int[] options = new int[5];
	private int side;
	public Hero(Entity entity){
		this.id = entity.getDtClass().getDtName();
		this.health = Double.parseDouble(entity.getProperty("m_iHealth").toString());
		this.mana = Double.parseDouble(entity.getProperty("m_flMana").toString());
		this.orientation = entity.getProperty("CBodyComponent.m_angRotation").toString();
		this.cordX = this.getX(entity);
		this.cordY = this.getY(entity);
		this.side = Integer.parseInt(entity.getProperty("m_iTaggedAsVisibleByTeam").toString());
	}
    private static final int MAX_COORD_INTEGER = 16384; 

    public static float getVecOrigin(Entity e, int idx) {
        Object v = e.getProperty("m_vecOrigin");
        if (v instanceof Vector2f) {
            float[] v2 = new float[2];
            ((Vector2f) v).get(v2);
            return v2[idx];
        } else if (v instanceof Vector3f) {
            float[] v3 = new float[3];
            ((Vector3f) v).get(v3);
            return v3[idx];
        } else {
            throw new RuntimeException("unsupported vector found");
        }
    }
    
    public static float getX(Entity e) {
        int cellBits = 7;
        int cellX = e.getProperty("CBodyComponent.m_cellX");
        return cellX * (1 << cellBits) - MAX_COORD_INTEGER + getVecOrigin(e, 0) / 128.0f;
    }

    public static float getY(Entity e) {
        int cellBits = 7;
        int cellY = e.getProperty("CBodyComponent.m_cellX");
        return cellY * (1 << cellBits) - MAX_COORD_INTEGER + getVecOrigin(e, 1) / 128.0f;
    } 
	public ArrayList<Hero> getNearHero(){
		return this.nearHeroes;
	}
	public void emptyNearHerolist(){
		this.nearHeroes = new ArrayList<Hero>();
	}
	public void setNearHero(Hero hero){
		//condition
		double distance = Math.sqrt((this.cordX-hero.getCordX())*(this.cordX-hero.getCordX())+(this.cordY-hero.getCordY())*(this.cordY-hero.getCordY()));
		
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
		if(a.getLevel()>0){
			int[] temp = a.getCategory();
			for(int i=0; i<temp.length;i++){
				options[i] = options[i] | temp[i];
			}
		}
	}
	//getter
	public int[] getOptions(){
		return this.options;
	}
	public String getId(){
		return this.id;
	}
	public double getHealth(){
		return this.health;
	}
	public double getMana(){
		return this.mana;
	}
	public double getCordX(){
		return this.cordX;
	}
	public double getCordY(){
		return this.cordY;
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
	public void setCordX(double cordX){
		this.cordX = cordX;
	}
	public void setCordY(double cordY){
		this.cordY = cordY;
	}
	public void setOrientation(String orientation){
		this.orientation = orientation;
	}
	public int getSide(){
		return this.side;
	}
}
