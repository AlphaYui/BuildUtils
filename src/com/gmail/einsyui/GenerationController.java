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
	public int maxTimeInNanoseconds;
	public int minTimeInNanoseconds;
	public int maxBlocksPerPeriod;
	public long lastTimeInNanoseconds;
	public GenerationController(int initialBlocksPerPeriod, 
			Plugin plugin, int period, int minTimeNS, int maxTimeNS,
			int maxBlocksPerPeriod){
		this.blocksPerPeriod = initialBlocksPerPeriod;
		this.maxBlocksPerPeriod=maxBlocksPerPeriod;
		this.minTimeInNanoseconds=minTimeNS;
		this.maxTimeInNanoseconds=maxTimeNS;
		this.plugin = plugin;
		this.period = period;
		todo = new ArrayDeque<Struct>();
		taskId=-1;
	}
	public GenerationController(int initialBlocksPerPeriod, 
			Plugin plugin, int period){
		this(initialBlocksPerPeriod, plugin, period, 
				1000000000/20/4, // fourth of a tick 
				1000000000/20/2, // half a tick
				2000); // 2000 blocks
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
		lastTimeInNanoseconds = ((System.nanoTime()-startTime));
		if(lastTimeInNanoseconds==0) lastTimeInNanoseconds=1;
		if(lastTimeInNanoseconds > maxTimeInNanoseconds){
			blocksPerPeriod=(int) (((blocksPerPeriod*maxTimeInNanoseconds)
					/lastTimeInNanoseconds)-1);
			if(blocksPerPeriod<=0){ 
				blocksPerPeriod=1;
			}
		}else if(lastTimeInNanoseconds < minTimeInNanoseconds){
			blocksPerPeriod=(int) (((blocksPerPeriod*minTimeInNanoseconds)
					/lastTimeInNanoseconds));
			if(blocksPerPeriod>maxBlocksPerPeriod)
				blocksPerPeriod=maxBlocksPerPeriod;
		}
	}

	public int remainingStructs() {
		return todo.size();
	}
}
