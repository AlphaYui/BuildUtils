package com.gmail.einsyui;

import java.util.ArrayDeque;

public class ScalingGenerationController extends GenerationController {
	
	public int maxTimeInNanoseconds;
	public int minTimeInNanoseconds;
	public int maxBlocksPerPeriod;
	public long lastTimeInNanoseconds;
	public ScalingGenerationController(int initialBlocksPerPeriod, 
			Main plugin, int period, int minTimeNS, int maxTimeNS,
			int maxBlocksPerPeriod){
		super(initialBlocksPerPeriod, period, plugin);
		this.maxBlocksPerPeriod=maxBlocksPerPeriod;
		this.minTimeInNanoseconds=minTimeNS;
		this.maxTimeInNanoseconds=maxTimeNS;
		todo = new ArrayDeque<Struct>();
		taskId=-1;
	}
	public ScalingGenerationController(int initialBlocksPerPeriod, 
			Main plugin, int period){
		this(initialBlocksPerPeriod, plugin, period, 
				1000000000/20/4, // fourth of a tick (1000 ms,*1000000->ns,/20->tick)
				1000000000/20/2, // half a tick
				2000); // 2000 blocks
	}
	@Override
	public void run() {
		long startTime = System.nanoTime(); 
		super.run();
		lastTimeInNanoseconds = ((System.nanoTime()-startTime));
		if(lastTimeInNanoseconds==0) lastTimeInNanoseconds=1;
		if(lastTimeInNanoseconds > maxTimeInNanoseconds){
			blocksPerPeriod=(((blocksPerPeriod*maxTimeInNanoseconds)
					/lastTimeInNanoseconds)-1);
			if(blocksPerPeriod<=0){ 
				blocksPerPeriod=1;
			}
		}else if(lastTimeInNanoseconds < minTimeInNanoseconds){
			blocksPerPeriod=(((blocksPerPeriod*minTimeInNanoseconds)
					/lastTimeInNanoseconds));
			if(blocksPerPeriod>maxBlocksPerPeriod)
				blocksPerPeriod=maxBlocksPerPeriod;
		}
	}
}
