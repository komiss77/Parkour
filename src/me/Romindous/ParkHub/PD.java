package me.Romindous.ParkHub;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import ru.komiss77.LocalDB;





public class PD {
    
    int tick;
    public final String name;
    public BukkitTask task;
    
    //общая стата - собирать при загрузке прогрессов
    public int totalCheckPoints;
    public int totalFalls;
    public int totalTime;
    public int totalJumps;
    public boolean cheat; //было нарушение при прохождении какой-то трассы. флаг поставится при след.загрузке данных, чтобы меньше 

    public final HashMap<Integer,Progress> progress; //прогресс игрока по каждой трассе
    public final HashMap<Integer,CompleteInfo> completions; //завершенные паркуры
    
    public Trasse current; //текущий паркур, или null если не на трассе
    public int nextCpX,nextCpY,nextCpZ; //коорд.след.чекпоинта
    public int stageTime, stageJump, stageFall; //стата между двумя чекпоинтами для сравнения с контрольными данными при достижении следующего.
    
    //для меню
    boolean hideCompleted; //скрыть пройденные
    Level showLevel; //какую сложность показывать
    
    public PD(final Player p) {
        name = p.getName();
        progress = new HashMap<>();
        completions = new HashMap<>();
    }

    
    
    final Player getPlayer() {
        return Bukkit.getPlayer(name);
    }

    
    //null не возвращаем. если нет прогресса - создать и сделать запись в БД
    public Progress getProgress(final int parkID) { 
        Progress go = progress.get(parkID);
        if (go==null) {
            go = new Progress(parkID^name.hashCode());
            progress.put(parkID, go);
            //!INSERT
            //LocalDB.executePstAsync(Bukkit.getConsoleSender(), "INSERT INTO `parkData` (hash,name,trasseID) VALUES ('"+(parkID^name.hashCode())+"','"+name+"','"+parkID+"');");
        }
        return go;
    }

    public void saveProgress(final int parkID) {
        Progress go = getProgress(parkID);//progress.get(parkID);
        //!UPDATE
        if (go.haProgress()) {
            final String querry = "INSERT INTO `parkData` (hash,name,trasseID,cheat) VALUES ('"+go.hash+"','"+name+"','"+parkID+"','"+(go.cheat?1:0)+"') ON DUPLICATE KEY "+
                "UPDATE `checkPoint`='"+go.checkPoint+"',`trasseTime`='"+go.trasseTime+"',`trasseJump`='"+go.trasseJump+"',`trasseFalls`='"+go.trasseFalls+"',`cheat`='"+(go.cheat?1:0)+"' ;";
            LocalDB.executePstAsync(Bukkit.getConsoleSender(), querry );
//Ostrov.log_warn("querry="+querry);
        }
        //LocalDB.executePstAsync(Bukkit.getConsoleSender(), "UPDATE `parkData` SET `checkPoint`='"+go.checkPoint+"',`trasseTime`='"+go.trasseTime+"',`trasseJump`='"+go.trasseJump+"',`trasseFalls`='"+go.trasseFalls+"',`cheat`='"+(go.cheat?1:0)+"' WHERE `hash`='"+(parkID^name.hashCode())+"';");
    }

    public void resetTrasse(final boolean saveProgress) {
        if (task!=null) {
            task.cancel();
            task = null;
        }
        if (current!=null) { //есть проходимый паркур
            if (saveProgress) {
                saveProgress(current.id);//сохранить, что пройдено
            } 
            current.inProgress.remove(name);
            current = null;
            resetStage();
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
        nextCpY = next.y;
        nextCpZ = next.z;
    }

    public void fall() {
        if (current!=null) {
            totalFalls++;
            stageFall++;
            getProgress(current.id).trasseFalls++;
        }
    }

    public void jump() {
        if (current!=null) {
            totalJumps++;
            stageJump++;
            getProgress(current.id).trasseJump++;
        }
    }

    
}
