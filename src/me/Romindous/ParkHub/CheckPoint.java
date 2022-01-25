package me.Romindous.ParkHub;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;


public class CheckPoint {
    
    //public int order;
    public int x,y,z; //коорд
    public BlockFace bf = BlockFace.SELF; //куда повернуть рожу
    
    
    //контрольки от пркдыдущей точки. если столько не набрано, значит читер
    public int controlTime = 1;
    public int controlJump = 1;
    public int controlFall = 1;

    CheckPoint(final int x, final int y, final int z, final BlockFace bf, final int controlTime, final int controlJump, final int controlFall) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.bf = bf;
        this.controlTime = controlTime;
        this.controlJump = controlJump;
        this.controlFall = controlFall;
    }
    
    public Location getLocation(final String worldName) {
        return new Location(Bukkit.getWorld(worldName), (double)x+.5, (double)y+.5, (double)z+.5);
    }
    
    
    
    @Override
    public String toString() {
        return x+","+y+","+z+","+bf.name()+","+controlTime+","+controlJump+","+controlFall;
    }
    
}