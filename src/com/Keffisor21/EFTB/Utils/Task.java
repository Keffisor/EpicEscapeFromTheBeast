package com.Keffisor21.EFTB.Utils;

import org.bukkit.scheduler.BukkitRunnable;

import com.Keffisor21.EFTB.EFTB;

public abstract class Task {
	public BukkitRunnable runnable = null;
	public long firstParameter = 20L;
	public long secondParameter = 20L;
	
      public Task(long first, long second) {
		  runnable = new BukkitRunnable() {
    		  @Override
    		  public void run() {
    			  toExecute();
    		  }
    	  };
    	  firstParameter = first;
    	  secondParameter = second;
      }

      public void run() {
    	  runnable.runTaskTimer(EFTB.instance, firstParameter, secondParameter);
      }
      protected abstract void toExecute();
}