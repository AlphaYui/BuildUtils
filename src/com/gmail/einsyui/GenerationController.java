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
	public GenerationController(int blocksPerPeriod, Plugin plugin, int period){
		this.blocksPerPeriod = blocksPerPeriod;
		this.plugin = plugin;
		this.period = period;
		todo = new ArrayDeque<Struct>();
		taskId=-1;
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

	@Override
	public void run() {
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
	}
}
