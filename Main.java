package analyzer2;
import skadistats.clarity.model.EngineType;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.processor.entities.OnEntityCreated;
import skadistats.clarity.processor.entities.OnEntityUpdated;
import skadistats.clarity.processor.entities.UsesEntities;
import skadistats.clarity.processor.runner.Context;
import skadistats.clarity.processor.runner.ControllableRunner;
import skadistats.clarity.source.MappedFileSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Chen Youfu on 7/2/2017.
 */
public class Main {

    private final ReentrantLock lock = new ReentrantLock();
    private Entity[] entities;
    private Entity[] changes;
    
    //private HashMap<String, Ability> abilities = new HashMap<String,Ability>();
    private ArrayList<String> heroList = new ArrayList<String>();
    private HashMap<String, Hero> Heroes = new HashMap<String, Hero>();
    private HashSet<String> events = new HashSet<String>();
    private ControllableRunner runner;
    static final String filename="/Users/wenchengong/Desktop/2883529346.dem";

    static int counter = 0;

    public static void main(String args[]) throws Exception{
        new Main();
    }
    
    public String findHeroName(String complexName){
    	int count = 0;
		char underscope = '_';
		for(int i = 0;i<complexName.length()-1;i++){
			if(complexName.charAt(i)==underscope){
				count++;
			}
			if(count>=3){
				return complexName.substring(i+1);
			}
		}
		return null;
	}
    
    @OnEntityCreated
    public void onCreate(Context ctx, Entity entity) {
        lock.lock();
        try {
            int i = entity.getIndex();
            String name = entity.getDtClass().getDtName();
            if(name.startsWith("CDOTA_Unit_Hero_")) {
            	//save the name in HeroList
            	heroList.add(findHeroName(name));
            	Heroes.put(name, new Hero(entity));
            }else if(name.startsWith("CDOTA_Ability_")){
            	for(String s: heroList){
            		if(name.contains(s)){
            			String key = new String("CDOTA_Unit_Hero_"+s);
            			Heroes.get(key).addAbility(name, new Ability(entity));
            		}
            	}
            }
            
        } finally {
            lock.unlock();
        }
    }
    public void printStatus(Hero hero){
    	System.out.print(hero.getId()+" ");
    	System.out.print(hero.getHealth()+" ");
    	System.out.print(hero.getMana()+" ");
    	System.out.print(hero.getCellX()+" ");
    	System.out.print(hero.getCellY()+" ");
    	System.out.print(hero.getCellZ()+" ");
    	System.out.print(hero.getVecX()+" ");
    	System.out.print(hero.getVecY()+" ");
    	System.out.print(hero.getVecZ()+" ");
    	System.out.print(hero.getOrientation()+" ");
    	for(Ability a: hero.getAbilities().values()){
    		System.out.print(a.getCooldown()+" ");
    		System.out.print(a.getManaCost()+" ");
    	}
    }
    @OnEntityUpdated
    public void onEntityUpdated(Context ctx, Entity entity, FieldPath[] changedPaths, int numChangedPaths){
        lock.lock();
        try{
        	for(int i=0; i<numChangedPaths; i++)
        		events.add(entity.getDtClass().getNameForFieldPath(changedPaths[i]));
            String name = entity.getDtClass().getDtName();
            if(name.startsWith("CDOTA_Unit_Hero_")) {
            	//update hero status
            	//if(entity.getProperty("m_lifeState").equals("0")){
            	double health = Double.parseDouble(entity.getProperty("m_iHealth").toString());
            	double mana = Double.parseDouble(entity.getProperty("m_flMana").toString());
            	String orientation = entity.getProperty("CBodyComponent.m_angRotation").toString();
            	double cellX = Double.parseDouble(entity.getProperty("CBodyComponent.m_cellX").toString());
            	double cellY = Double.parseDouble(entity.getProperty("CBodyComponent.m_cellY").toString());
            	double cellZ = Double.parseDouble(entity.getProperty("CBodyComponent.m_cellZ").toString());
            	double vecX = Double.parseDouble(entity.getProperty("CBodyComponent.m_vecX").toString());
            	double vecY = Double.parseDouble(entity.getProperty("CBodyComponent.m_vecX").toString());
            	double vecZ = Double.parseDouble(entity.getProperty("CBodyComponent.m_vecX").toString());
            	
            	//entity.getDtClass().getValueForFieldPath(fp, state);
            	//entity.getPropertyForFieldPath(changedPaths[0]);
            	Heroes.get(name).setHealth(health);
            	Heroes.get(name).setMana(mana);
            	Heroes.get(name).setOrientation(orientation);
            	Heroes.get(name).setCellX(cellX);
            	Heroes.get(name).setCellY(cellY);
            	Heroes.get(name).setCellZ(cellZ);
            	Heroes.get(name).setVecX(vecX);
            	Heroes.get(name).setVecY(vecY);
            	Heroes.get(name).setVecZ(vecZ);
            	
            	//update nearby heroes
            	Heroes.get(name).emptyNearHerolist();
            	for(Hero h: Heroes.values()){
            		Heroes.get(name).setNearHero(h);
            	}
            	//print nearby heroes
            	if(Heroes.get(name).getNearHero().size()>1){
            		System.out.println("#"+counter+"#"+" ");
            		
                	System.out.println();
            		//System.out.println(Heroes.get(name).getNearHero().size());
	            	for(Hero h: Heroes.get(name).getNearHero()){
	            		printStatus(h);
	            		
	            	}
	            	
	            	//Scanner sc = new Scanner(System.in);
	            	//sc.nextLine();
	            	System.out.println();
            	}
            	if(entity.getProperty("m_lifeState").toString().equals("1")){
            		System.out.println(name+" just died!");
            		return;
            	}
            	/*}else{
            		if(Heroes.size()>=10){
            		System.out.println(name + " just died!");
            		//update near heroes
            		for(Hero h: Heroes.get(name).getNearHero()){
            			h.setNearHero(Heroes.get(name));
            		}
            		Heroes.remove(name);
            		}
            	}*/
                
                
                counter ++ ;
            }else if(name.startsWith("CDOTA_Ability_")){
            	for(String s: heroList){
            		if(name.contains(s)){
            			String key = new String("CDOTA_Unit_Hero_"+s);
            			double newCooldown = Double.parseDouble(entity.getProperty("m_fCooldown").toString());
            			double newManaCost = Double.parseDouble(entity.getProperty("m_iManaCost").toString());
            			Heroes.get(key).getAbilities().get(name).setCooldown(newCooldown);
            			Heroes.get(key).getAbilities().get(name).setManaCost(newManaCost);
            		}
            	}
            }
        } finally {
            lock.unlock();
        }
        
    }
    @OnEntityDied
    public void onEntityDied(){
    	
    }
    public Main() throws Exception{
        runner = new ControllableRunner(new MappedFileSource(filename)).runWith(this);
        EngineType et = runner.getEngineType();
        if(et!=EngineType.SOURCE2)
            throw new RuntimeException("Not Source 2 Replay!");
        entities = new Entity[1 << et.getIndexBits()];
        changes = new Entity[1 << et.getIndexBits()];
        while(!runner.isAtEnd()){
            runner.tick();
            
        }
        runner.tick();
        System.out.println("Finished " + runner.getLastTick() + "  " + counter);
        /*for(String s: events){
        	System.out.println(s);
        }*/
    }
}
