package me.Romindous.ParkHub;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.komiss77.LocalDB;





public class PD {
    int tick;
    public final String name;
    
    //общая стата - собирать при загрузке прогресса
    public int totalCheckPoints;
    public int totalFalls;
    public int totalTime;
    public int totalJumps;
    public boolean cheat; //было нарушение при прохождении какой-то трассы

    public final HashMap<Integer,Progress> progress; //прогресс игрока по каждой трассе
    
    public Trasse current; //текущий паркур, или null если не на трассе
    public int nextCpX,nextCpY,nextCpZ; //коорд.след.чекпоинта
    public int stageTime, stageJump, stageFall; //стата между двумя чекпоинтами для сравнения с контрольными данными при достижении следующего.
    
    //для меню
    boolean hideCompleted;
    Level showLevel;
    
    public PD(final Player p) {
        name = p.getName();
        progress = new HashMap<>();
    }

    
    
    final Player getPlayer() {
        return Bukkit.getPlayer(name);
    }

    
    //null не возвращаем. если нет прогресса - создать и сделать запись в БД
    public Progress getProgress(final int parkID) { 
        Progress go = progress.get(parkID);
        if (go==null) {
            go = new Progress();
            progress.put(parkID, go);
            //!INSERT
            LocalDB.executePstAsync(Bukkit.getConsoleSender(), "INSERT INTO `playerData` (hash,name,trasseID) VALUES ('"+(parkID^name.hashCode())+"','"+name+"','"+parkID+"');");
        }
        return go;
    }

    public void saveProgress(final int parkID) { 
        Progress go = progress.get(parkID);
        //!UPDATE
        LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `playerData` SET `checkPoint`='"+go.checkPoint+"',`trasseTime`='"+go.trasseTime+"',`trasseJump`='"+go.trasseJump+"',`trasseFalls`='"+go.trasseFalls+"',`cheat`='"+(go.cheat?1:0)+"' WHERE `hash`='"+(parkID^name.hashCode())+"';");
    }

    public void resetTrasse() {
        if (current!=null) { //есть проходимый паркур
            saveProgress(current.id);
            current.inProgress.remove(name);
            current = null;
        }
    }
    
    public void resetStage() { //сброс прогресса между двумя точками
        stageTime = 0;
        stageFall = 0;
        stageJump = 0;
    }

    public void setNextPoint(final CheckPoint next) { //установить коорд. след.контрольной точки
        resetStage();
        nextCpX = next.x;
        nextCpY = next.z;
        nextCpZ = next.z;
    }

    
}
