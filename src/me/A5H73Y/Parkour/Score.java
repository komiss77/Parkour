package me.A5H73Y.Parkour;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;



//ранг     деньги сколько проходят/сколько онлайн   Views: 52  Completed: 24

                  
 
public class Score {

 //static Scoreboard score;
 static Objective obj;
  
 
 
    public static void InitScoreboard(final Player p, String arena) {

        int ingame=Parkour.inProgress.size();
        int jumpTotal = (int) Parkour.jumpTotal.get(p.getName());
        int fallTotal = (int) Parkour.fallTotal.get(p.getName());
        String rank = Files.getStringVar(p, ".Rank");
        
        if (Parkour.boards.containsKey(p.getName())) Parkour.boards.remove(p.getName());
            
           Scoreboard score = Bukkit.getScoreboardManager().getNewScoreboard();
        //score = Parkour.boards.get(p.getName());
        
            obj = score.registerNewObjective("Parkour", "dummy");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            obj.setDisplayName(new StringBuilder().append(ChatColor.DARK_GREEN).append(ChatColor.BOLD).append("Остров Свободы").toString());

            obj.getScore(new StringBuilder().append(ChatColor.GRAY).append(ChatColor.STRIKETHROUGH).append("----------------").toString()).setScore(16);
            obj.getScore (new StringBuilder().append(ChatColor.AQUA).append("Ранг: ").append(ChatColor.WHITE).append(rank).toString()).setScore(15);
            obj.getScore (new StringBuilder().append(ChatColor.RED).append("Падения (всего): ").append(ChatColor.WHITE).append(fallTotal).toString()).setScore(14);
            obj.getScore (new StringBuilder().append(ChatColor.GREEN).append("Прыжки (всего): ").append(ChatColor.WHITE).append(jumpTotal).toString()).setScore(13);
            
            obj.getScore(new StringBuilder().append(ChatColor.GRAY).append(ChatColor.STRIKETHROUGH).append("--------------").toString()).setScore(12);
            obj.getScore (new StringBuilder().append(ChatColor.GOLD).append(ChatColor.BOLD).append("Трасса: ").toString()).setScore(11);
            obj.getScore (new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append(">   ").append(arena).toString()).setScore(10);
            obj.getScore (new StringBuilder().append(ChatColor.RED).append("Падения: ").append(ChatColor.WHITE).append("-").toString()).setScore(9);
            obj.getScore (new StringBuilder().append(ChatColor.GREEN).append("Прыжки: ").append(ChatColor.WHITE).append("-").toString()).setScore(8);
           obj.getScore (new StringBuilder().append(ChatColor.LIGHT_PURPLE).append("Точка/осталось: ").append(ChatColor.WHITE).append("-").append (" / ").append ("-").toString()).setScore(7);
            obj.getScore (new StringBuilder().append(ChatColor.GRAY).append("Испытали: ").append(ChatColor.WHITE).append("-").toString()).setScore(6);
            obj.getScore (new StringBuilder().append(ChatColor.GRAY).append("Прошли: ").append(ChatColor.WHITE).append("-").toString()).setScore(5);
            obj.getScore(new StringBuilder().append(ChatColor.GRAY).append(ChatColor.STRIKETHROUGH).append("---------------").toString()).setScore(4);
            
            obj.getScore(new StringBuilder().append(ChatColor.GREEN).append("На паркурах: ").append(ChatColor.WHITE).append(ingame).toString()).setScore(0);

            p.setScoreboard(score);
            Parkour.boards.put(p.getName(), score);
    }


    
    
    public static void UpdateScoreboard (final Player p, String arena, int point, int points, int views, int Completed) {

        int ingame=Parkour.inProgress.size();
        String rank = Files.getStringVar(p, ".Rank");
        int jumpTotal = (int) Parkour.jumpTotal.get(p.getName());
        int fallTotal = (int) Parkour.fallTotal.get(p.getName());
        int jumpCurrent = (int) Parkour.jumpCurrent.get(p.getName());
        int fallCurrent = (int) Parkour.fallCurrent.get(p.getName());
        
         if (Parkour.boards.containsKey(p.getName())) Parkour.boards.remove(p.getName());
        
           Scoreboard score = Bukkit.getScoreboardManager().getNewScoreboard();
         //score = Parkour.boards.get(p.getName());
         
            obj = score.registerNewObjective("Parkour", "dummy");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            obj.setDisplayName(new StringBuilder().append(ChatColor.DARK_GREEN).append(ChatColor.BOLD).append("Остров Свободы").toString());

            obj.getScore(new StringBuilder().append(ChatColor.GRAY).append(ChatColor.STRIKETHROUGH).append("----------------").toString()).setScore(16);
            obj.getScore (new StringBuilder().append(ChatColor.AQUA).append("Ранг: ").append(ChatColor.WHITE).append(rank).toString()).setScore(15);
            obj.getScore (new StringBuilder().append(ChatColor.RED).append("Падения (всего): ").append(ChatColor.WHITE).append(fallTotal).toString()).setScore(14);
            obj.getScore (new StringBuilder().append(ChatColor.GREEN).append("Прыжки (всего): ").append(ChatColor.WHITE).append(jumpTotal).toString()).setScore(13);
            
            obj.getScore(new StringBuilder().append(ChatColor.GRAY).append(ChatColor.STRIKETHROUGH).append("--------------").toString()).setScore(12);
            obj.getScore (new StringBuilder().append(ChatColor.GOLD).append(ChatColor.BOLD).append("Трасса: ").toString()).setScore(11);
            obj.getScore (new StringBuilder().append(ChatColor.WHITE).append(ChatColor.BOLD).append(">   ").append(arena).toString()).setScore(10);
            obj.getScore (new StringBuilder().append(ChatColor.RED).append("Падения: ").append(ChatColor.WHITE).append(fallCurrent).toString()).setScore(9);
            obj.getScore (new StringBuilder().append(ChatColor.GREEN).append("Прыжки: ").append(ChatColor.WHITE).append(jumpCurrent).toString()).setScore(8);
            obj.getScore (new StringBuilder().append(ChatColor.LIGHT_PURPLE).append("Точка/осталось: ").append(ChatColor.WHITE).append(point).append (" / ").append (points-point).toString()).setScore(7);
            obj.getScore (new StringBuilder().append(ChatColor.GRAY).append("Испытали: ").append(ChatColor.WHITE).append(views).toString()).setScore(6);
            obj.getScore (new StringBuilder().append(ChatColor.GRAY).append("Прошли: ").append(ChatColor.WHITE).append(Completed).toString()).setScore(5);
            obj.getScore(new StringBuilder().append(ChatColor.GRAY).append(ChatColor.STRIKETHROUGH).append("---------------").toString()).setScore(4);
            
            obj.getScore(new StringBuilder().append(ChatColor.GREEN).append("На паркурах: ").append(ChatColor.WHITE).append(ingame).toString()).setScore(0);

            p.setScoreboard(score);
             Parkour.boards.put(p.getName(), score);
    }    
    
    
    
    
    
    public static void AddJump1 (final Player p) {
      
        int jumpTotal = (int) Parkour.jumpTotal.get(p.getName());
        Parkour.jumpTotal.put ( p.getName(), jumpTotal+1 );
        int jumpCurrent = (int) Parkour.jumpCurrent.get(p.getName());
        Parkour.jumpCurrent.put ( p.getName(), jumpCurrent+1 );
        
       // score = p.getScoreboard();
       Scoreboard score = Parkour.boards.get(p.getName());
        
        score.resetScores(new StringBuilder().append(ChatColor.GREEN).append("Прыжки (всего): ").append(ChatColor.WHITE).append(jumpTotal-1).toString());
        obj.getScore (new StringBuilder().append(ChatColor.GREEN).append("Прыжки (всего): ").append(ChatColor.WHITE).append(jumpTotal).toString()).setScore(13);

        score.resetScores(new StringBuilder().append(ChatColor.GREEN).append("Прыжки: ").append(ChatColor.WHITE).append(jumpCurrent-1).toString());
        obj.getScore (new StringBuilder().append(ChatColor.GREEN).append("Прыжки: ").append(ChatColor.WHITE).append(jumpCurrent).toString()).setScore(8);
        
        
       
        p.setScoreboard(score);
         Parkour.boards.put(p.getName(), score);
    }


    public static void AddFall1 (final Player p) {
       // score = p.getScoreboard();
        Scoreboard score = Parkour.boards.get(p.getName());

        int fallTotal = (int) Parkour.fallTotal.get(p.getName());
        Parkour.fallTotal.put ( p.getName(), fallTotal+1 );
        int fallCurrent = (int) Parkour.fallCurrent.get(p.getName());
        Parkour.fallCurrent.put ( p.getName(), fallCurrent+1 );

    
        score.resetScores(new StringBuilder().append(ChatColor.RED).append("Падения (всего): ").append(ChatColor.WHITE).append(fallTotal-1).toString());
        obj.getScore (new StringBuilder().append(ChatColor.RED).append("Падения (всего): ").append(ChatColor.WHITE).append(fallTotal).toString()).setScore(14);

        score.resetScores(new StringBuilder().append(ChatColor.RED).append("Падения: ").append(ChatColor.WHITE).append(fallCurrent-1).toString());
        obj.getScore (new StringBuilder().append(ChatColor.RED).append("Падения: ").append(ChatColor.WHITE).append(fallCurrent).toString()).setScore(9);
        
        
        p.setScoreboard(score);
         Parkour.boards.put(p.getName(), score);
    }




    
    
    
}
