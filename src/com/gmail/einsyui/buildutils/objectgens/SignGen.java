package com.gmail.einsyui.buildutils.objectgens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.gmail.einsyui.buildutils.ObjectGen;
import com.gmail.einsyui.buildutils.argumentreader.Argument;
import com.gmail.einsyui.buildutils.argumentreader.ArgumentType;
import com.gmail.einsyui.buildutils.argumentreader.BukkitArgumentType;
import com.gmail.einsyui.buildutils.argumentreader.ArgumentType.TConstructorArgumentType;
import com.gmail.einsyui.buildutils.argumentreader.Context;

public class SignGen implements ObjectGen{

	public final static TSignGen SIGN_GEN_AT = new TSignGen();
	
	String[] lines;
	boolean isWallSign;
	BlockFace faceDirection;
	
	public SignGen( boolean isOnWall, List<String> lines, BlockFace orientation )
	{
		this.lines = new String[4];
		for( int i=0; i<4; ++i )
			if( lines.get(i) == null )
				this.lines[i] = "";
			else
				this.lines[i] = lines.get( i );
	}
	
	@Override
	public void generateAt(Location l) {
		if( isWallSign )
			generateWallSign(l);
		else
			generateSignPost(l);
	}
	
	public void generateSignPost( Location l )
	{
		Block b = l.getBlock();
		b.setType( Material.SIGN_POST );
		org.bukkit.material.Sign sign = (org.bukkit.material.Sign) b.getState().getData();
		
		if( faceDirection == null )
			faceDirection = BlockFace.SOUTH;
		sign.setFacingDirection(faceDirection);
		
		org.bukkit.block.Sign signBlock = (org.bukkit.block.Sign) b;
		
		for( int i = 0; i < 4; ++i )
		signBlock.setLine( i, lines[i] );
	}
	
	public void generateWallSign( Location l )
	{
		Block b = l.getBlock();
		b.setType( Material.WALL_SIGN );
		org.bukkit.material.Sign sign = (org.bukkit.material.Sign) b.getState().getData();
		
		if( faceDirection == null)
		{
			World w = l.getWorld();
			int x = l.getBlockX();
			int y = l.getBlockY();
			int z = l.getBlockZ();
			
			if( new Location(w,x,y,z-1).getBlock().getType().isSolid() )
				faceDirection = BlockFace.NORTH;
			else if( new Location(w,x,y,z+1).getBlock().getType().isSolid() )
				faceDirection = BlockFace.SOUTH;
			else if( new Location(w,x-1,y,z).getBlock().getType().isSolid() )
				faceDirection = BlockFace.WEST;
			else if( new Location(w,x+1,y,z).getBlock().getType().isSolid() )
				faceDirection = BlockFace.EAST;
			else faceDirection = BlockFace.SOUTH;
		}
		
		sign.setFacingDirection( faceDirection );
		
		org.bukkit.block.Sign signBlock = (org.bukkit.block.Sign) b;
		
		for( int i = 0; i < 4; ++i )
		signBlock.setLine( i, lines[i] );
	}
	
	public static class TSignGen extends TConstructorArgumentType
	{

		@Override
		public String name() {
			return "sign";
		}

		@Override
		public List<Argument> args() {
			return Arrays.asList( 	new Argument( "onWall", ArgumentType.BOOLEAN ),
									new Argument( "orientation", BukkitArgumentType.BLOCK_FACE),
									new Argument( "line1", ArgumentType.STRING ),
									new Argument( "line2", ArgumentType.STRING ),
									new Argument( "line3", ArgumentType.STRING ),
									new Argument( "line4", ArgumentType.STRING ));
		}

		@Override
		public Object construct(Map<String, Object> args, Context ctx) {
			
			boolean wallSign = (boolean) Argument.getWithDefault( args, "onWall", true );
			BlockFace orientation = (BlockFace) Argument.getWithDefault( args, "orientation", -1 );
			
			List<String> lines = new ArrayList<String>();
			for( int i=0; i<4; ++i )
			{
				lines.add( (String) Argument.getWithDefault( args, "line"+i, "" ) );
			}
			
			return new SignGen( wallSign, lines, orientation);
		}

		@Override
		public String description() {
			return "Generates a sign. Syntax: sign<onWall(Boolean) orientation(BlockFace) line1-3(strings)>";
		}
		
	};

}
