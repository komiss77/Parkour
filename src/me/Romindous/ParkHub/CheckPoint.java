package me.Romindous.ParkHub;

import org.bukkit.Bukkit;
import org.bukkit.Location;


public class CheckPoint {
    
    //public int order;
    public int x,y,z; //коорд
    //public BlockFace bf = BlockFace.SELF; //куда повернуть рожу
    
    
    //контрольки от пркдыдущей точки. если столько не набрано, значит читер
    public int controlTime = 1;
    public int controlJump = 10;
    public int controlFall = 0;

    CheckPoint(final int x, final int y, final int z, final int controlTime, final int controlJump, final int controlFall) {
        this.x = x;
        this.y = y;
        this.z = z;
        //this.bf = bf;
        this.controlTime = controlTime;
        this.controlJump = controlJump;
        this.controlFall = controlFall;
    }
    
    public Location getLocation(final String worldName) {
        return new Location(Bukkit.getWorld(worldName), (double)x+.5, (double)y+.5, (double)z+.5);
    }
    
    
    
    @Override
    public String toString() {
        return x+","+y+","+z+",SELF,"+controlTime+","+controlJump+","+controlFall;
    }
    
}
