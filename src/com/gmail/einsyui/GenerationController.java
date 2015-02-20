package com.gmail.einsyui;

import java.util.ArrayDeque;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class GenerationController implements Runnable {
	
	ArrayDeque<Struct> todo;
	int blocksPerPeriod;
	Plugin plugin; 
	int period;
	int taskId;
	int maxTimeInMilliseconds;
	int minTimeInMilliseconds;
	public GenerationController(int initialBlocksPerPeriod, 
			Plugin plugin, int period, int minTimeMS, int maxTimeMS){
		this.blocksPerPeriod = initialBlocksPerPeriod;
		this.plugin = plugin;
		this.period = period;
		todo = new ArrayDeque<Struct>();
		taskId=-1;
	}
	public GenerationController(int initialBlocksPerPeriod, 
			Plugin plugin, int period){
		this(initialBlocksPerPeriod, plugin, period, 1000/20/5, 1000/20/2);
	}
	
	public void startGenerating(){
		if(taskId!=-1)
			stopGenerating();
		taskId=Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, period);
		System.out.println("Start generating...");
	}
	public void stopGenerating(){
		if(taskId!=-1){
			Bukkit.getScheduler().cancelTask(taskId);
			taskId=-1;
		}
		System.out.println("Stopped generating.");
	} 
	public boolean isGenerating(){ return taskId!=-1; }
	
	public void generate(Struct struct, boolean start){
		todo.add(struct);
		if(start && !isGenerating()) 
			startGenerating();
	}
	
	public void setPeriod(int period){ this.period=period; }
	public void setBlocksPerPreiod(int blocksPerPeriod){ 
		this.blocksPerPeriod=blocksPerPeriod; 
	}
	public int period(){ return period; }
	public int blocksPerPeriod(){ return blocksPerPeriod; }

	@Override
	public void run() {
		long startTime = System.nanoTime(); 
		int numberOfStructs = todo.size();
		if(numberOfStructs==0){ //nothing to do
			stopGenerating();
			return;
		}
		int blocksPerStruct = blocksPerPeriod/numberOfStructs;
		int structsToGenerate = numberOfStructs;
		if(blocksPerStruct==0){
			structsToGenerate=blocksPerPeriod;
			blocksPerStruct=1;
		}
		for(int i=0;i<structsToGenerate;i++){
			Struct s = todo.poll(); 
			if(s!=null){
				s.generate(blocksPerStruct);
				if(!s.isReady())
					todo.offer(s);
			}
		}
		long time = ((System.nanoTime()-startTime));
		if(time==0) time=1;
		if(time >= 1000000*maxTimeInMilliseconds){
			blocksPerPeriod=(int) (((blocksPerPeriod*1000000*maxTimeInMilliseconds)
					/time)-1);
		}else if(time < 1000000*minTimeInMilliseconds){
			blocksPerPeriod=(int) (((blocksPerPeriod*1000000*minTimeInMilliseconds)
					/time));
		}
	}

	public int remainingStructs() {
		return todo.size();
	}
}
