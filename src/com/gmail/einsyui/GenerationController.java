package com.gmail.einsyui;

import java.util.ArrayDeque;

import org.bukkit.Bukkit;

public class GenerationController implements Runnable {

	protected ArrayDeque<Struct> todo;
	protected long blocksPerPeriod;
	protected Main plugin;
	protected int period;
	protected int taskId;

	public GenerationController(int blocksPerPeriod, int period, Main plugin) {
		this.blocksPerPeriod=blocksPerPeriod;
		this.period=period;
		this.plugin=plugin;
		todo = new ArrayDeque<Struct>();
		taskId=-1;
	}

	public void startGenerating() {
		if(taskId!=-1)
			stopGenerating();
		taskId=Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, period);
		System.out.println("Start generating...");
	}

	public void stopGenerating() {
		if(taskId!=-1){
			Bukkit.getScheduler().cancelTask(taskId);
			taskId=-1;
		}
		System.out.println("Stopped generating.");
	}

	public boolean isGenerating() { return taskId!=-1; }

	public void generate(Struct struct, boolean start) {
		todo.add(struct);
		if(start && !isGenerating()) 
			startGenerating();
	}

	public void setPeriod(int period) { this.period=period; }

	public void setBlocksPerPreiod(int blocksPerPeriod) { 
		this.blocksPerPeriod=blocksPerPeriod; 
	}

	public int period() { return period; }

	public long blocksPerPeriod() { return blocksPerPeriod; }

	@Override
	public void run() {
		int numberOfStructs = todo.size();
		if(numberOfStructs==0){ //nothing to do
			stopGenerating();
			return;
		}
		long blocksPerStruct = blocksPerPeriod/numberOfStructs;
		long structsToGenerate = numberOfStructs;
		if(blocksPerStruct==0){
			structsToGenerate=blocksPerPeriod;
			blocksPerStruct=1;
		}
		for(int i=0;i<structsToGenerate;i++){
			Struct s = todo.poll(); 
			if(s!=null){
				s.generate((int) blocksPerStruct);
				if(!s.isReady())
					todo.offer(s);
			}
		}
	}

	public int remainingStructs() {
		return todo.size();
	}

}