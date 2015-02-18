package com.gmail.einsyui.argumentreader;

import java.util.List;
import java.util.Map;

public interface Command extends Describable {
	public String execute(Map<String, Object> args, Context ctx);
	public List<Argument> args();
	
	
	public static class Alias implements Command{
		Command cmd; String name;
		public Alias (String name, Command cmd){
			this.name=name; this.cmd=cmd;
		}
		@Override
		public String name() {
			return name;
		}
		@Override
		public String description() {
			return cmd.description();
		}
		@Override
		public String execute(Map<String, Object> args, Context ctx) {
			return cmd.execute(args, ctx);
		}
		@Override
		public List<Argument> args() {
			return cmd.args();
		}
	};
	public static class AliasWithStrongPresetArgs extends Alias{
		// "strong" meaning that it replaces existing values...
		Map<String, Object> presetArgs;
		public AliasWithStrongPresetArgs(String name, Command cmd, 
				Map<String, Object> presetArgs){
			super(name,cmd);
			this.presetArgs=presetArgs;
		}
		@Override
		public String execute(Map<String, Object> args, Context ctx){
			args.putAll(presetArgs);
			return super.execute(args, ctx);
		}
	};
}
