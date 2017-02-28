package analyzer2;
import skadistats.clarity.model.EngineType;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.processor.entities.OnEntityCreated;
import skadistats.clarity.processor.entities.OnEntityUpdated;
import skadistats.clarity.processor.runner.Context;
import skadistats.clarity.processor.runner.ControllableRunner;
import skadistats.clarity.source.MappedFileSource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;
import javax.vecmath.Vector3f;
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
    private HashMap<String, int[]> abilityCategory= new HashMap<String,int[]>();
    private ControllableRunner runner;
    static final String filename="/Users/wenchengong/Desktop/2883529346.dem";
    static final String csvFile ="/Users/wenchengong/Desktop/category.csv";
    private BufferedReader br = null;
    String line = "";
    String cvsSplitBy = ",";
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
            			int[] kind = new int[5];
            			kind = abilityCategory.get(name);
            			//System.out.println(name);
            			/*if(name.startsWith("CDOTA_Ability_Mirana_")){
            				for(Integer o: kind){
            					System.out.println(o);
            				}
            				Scanner sc = new Scanner(System.in);
        	            	sc.nextLine();
            			}*/

            			Heroes.get(key).addAbility(name, new Ability(entity,kind));
            		}
            	}
            }
            
        } finally {
            lock.unlock();
        }
    }
    public void printStatus(Hero hero){
    	
    	/*
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
    	
    	for(Integer i: hero.getOptions()){
    		System.out.print(i);
    	}*/
    	
    }
    @OnEntityUpdated
    public void onEntityUpdated(Context ctx, Entity entity, FieldPath[] changedPaths, int numChangedPaths) throws IOException{
        lock.lock();
        try{
        	for(int i=0; i<numChangedPaths; i++)
        		events.add(entity.getDtClass().getNameForFieldPath(changedPaths[i]));
            String name = entity.getDtClass().getDtName();
            if(name.startsWith("CDOTA_Unit_Hero_")) {
            	//update hero status
            	
            	double health = Double.parseDouble(entity.getProperty("m_iHealth").toString());
            	double mana = Double.parseDouble(entity.getProperty("m_flMana").toString());
            	String orientation = entity.getProperty("CBodyComponent.m_angRotation").toString();
            	
            	//entity.getDtClass().getValueForFieldPath(fp, state);
            	//entity.getPropertyForFieldPath(changedPaths[0]);
            	Heroes.get(name).setHealth(health);
            	Heroes.get(name).setMana(mana);
            	Heroes.get(name).setOrientation(orientation);
            	Heroes.get(name).setCordX(Hero.getX(entity));
            	Heroes.get(name).setCordY(Hero.getY(entity));
            	
            	//update nearby heroes
            	Heroes.get(name).emptyNearHerolist();
            	for(Hero h: Heroes.values()){
            		Heroes.get(name).setNearHero(h);
            	}
            	//print nearby heroes
            	/*if(Heroes.get(name).getNearHero().size()==2){
            		System.out.print("#"+counter+"#"+" ");
            		//printStatus(h);
            		String fileName = "/Users/wenchengong/Desktop/battleInfo.txt";
                	BufferedWriter bw = null;
            		FileWriter fw = null;
            		try {
	            		
	            		StringBuilder content = new StringBuilder();
	            		content.append("#"+counter+ " ");
		            	for(Hero h: Heroes.get(name).getNearHero()){
	            			content.append(h.getId()+" ");
	            			content.append(h.getHealth()+" ");
	            			content.append(h.getMana()+" ");
	            			content.append(h.getCellX()+" ");
	            			content.append(h.getCellY()+" ");
	            			content.append(h.getCellZ()+" ");
	            			content.append(h.getVecX()+" ");
	            			content.append(h.getVecY()+" ");
	            			content.append(h.getVecZ()+" ");
	            			content.append(h.getOrientation()+" ");
	            			for(Ability a: h.getAbilities().values()){
	            				content.append(a.getCooldown()+" ");
	            				content.append(a.getManaCost()+" ");
	            	    	}
	            	    	
	            	    	for(Integer i: h.getOptions()){
	            	    		content.append(i);
	            	    	}
	            	    	content.append(" ");
		            	}
		            	content.append("\n");
		            	fw = new FileWriter(fileName,true);
	        			bw = new BufferedWriter(fw);
	        			bw.write(content.toString());
		            	System.out.println();
	            	} catch (IOException e) {
	            			e.printStackTrace();
	            	} finally {
	            			if (bw != null)
	            				bw.close();
	            			if (fw != null)
	            				fw.close();	
	            	}
	            }
	            counter ++ ;*/
            }
            else if(name.startsWith("CDOTA_Ability_")){
            	for(String s: heroList){
            		if(name.contains(s)){
            			String key = new String("CDOTA_Unit_Hero_"+s);
            			double newCooldown = Double.parseDouble(entity.getProperty("m_fCooldown").toString());
            			double newManaCost = Double.parseDouble(entity.getProperty("m_iManaCost").toString());
            			int newLevel = Integer.parseInt(entity.getProperty("m_iLevel").toString());
            			Heroes.get(key).getAbilities().get(name).setCooldown(newCooldown);
            			Heroes.get(key).getAbilities().get(name).setManaCost(newManaCost);
            			Heroes.get(key).getAbilities().get(name).setLevel(newLevel);
            			
            			if(newCooldown > 0){
            				
            			}
            			/*if(name.startsWith("CDOTA_Ability_")){
            				for(Integer o: Heroes.get(key).getAbilities().get(name).getCategory()){
            					System.out.println(o);
            				}
            				Scanner sc = new Scanner(System.in);
        	            	sc.nextLine();
            			}*/
            			//print nearby heroes
                    	if(Heroes.get(key).getNearHero().size()==2){
                    		System.out.print("#"+counter+"#"+" ");
                    		//printStatus(h);
                    		String fileName = "/Users/wenchengong/Desktop/battleInfo.txt";
                        	BufferedWriter bw = null;
                    		FileWriter fw = null;
                    		int side=0;
                    		try {
        	            		
        	            		StringBuilder content = new StringBuilder();
        	            		content.append("#"+counter+ " ");
        	            		side = (Heroes.get(key).getNearHero().get(0).getSide()-Heroes.get(key).getNearHero().get(1).getSide()>0) ? 1:0;
        	            		content.append(side+ " ");
        	            		
        		            	for(Hero h: Heroes.get(key).getNearHero()){
        		            		
        	            			content.append(h.getId()+" ");
        	            			content.append(h.getHealth()+" ");
        	            			content.append(h.getMana()+" ");
        	            			content.append(h.getCordX()+" ");
        	            			content.append(h.getCordY()+" ");
        	            			content.append(h.getOrientation()+" ");
        	            			for(Ability a: h.getAbilities().values()){
        	            				content.append(a.getCooldown()+" ");
        	            				content.append(a.getManaCost()+" ");
        	            	    	}
        	            	    	
        	            	    	for(Integer i: h.getOptions()){
        	            	    		content.append(i);
        	            	    	}
        	            	    	content.append(" ");
        		            	}
        		            	content.append("\n");
        		            	fw = new FileWriter(fileName,true);
        	        			bw = new BufferedWriter(fw);
        	        			bw.write(content.toString());
        		            	System.out.println();
        	            	} catch (IOException e) {
        	            			e.printStackTrace();
        	            	} finally {
        	            			if (bw != null)
        	            				bw.close();
        	            			if (fw != null)
        	            				fw.close();	
        	            	}
        	            }
        	            counter ++ ;
            		}
            	}
            }
        } finally {
            lock.unlock();
        }
        
    }
    /*@OnEntityDied
    public void onDied(Entity entity){
    	String name = entity.getDtClass().getDtName();
    	System.out.println(name + " just died!");
    	return;
    }*/
    public Main() throws Exception{
    	//read category of abilities
    	try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
            	//Hero	Ability	Damage	Heal	Buff	Debuff	Displacement
                // use comma as separator
                String[] category = line.split(cvsSplitBy);
                String key = "CDOTA_Ability_" + category[0]+"_"+category[1].replace(" ", "_");
                //System.out.println(key);
                //System.out.println(category[6]);
                int[] kind = new int[]{Integer.parseInt(category[2]),Integer.parseInt(category[3]),Integer.parseInt(category[4]),Integer.parseInt(category[5]),Integer.parseInt(category[6])};
                abilityCategory.put(key, kind);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    	/*
        for(int[] s: abilityCategory.values()){
        	for(Integer p: s){
        		System.out.print(p);
        	}
        	System.out.println();
        }*/
        
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
        
    }
}
