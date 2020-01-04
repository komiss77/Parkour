
package me.A5H73Y.Parkour;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;


public class Files extends Parkour{
    
    static File pluginFolder;
    static File UserDataFolder;
    static File courseFile;
    static File leaderFile;
    static File stringFile;
    static File checkFile;
    static File upgFile;
    //static File econFile;
    public static FileConfiguration courseData;
    static FileConfiguration leaderData;
    public static FileConfiguration stringData;
    static FileConfiguration usersData;
    static FileConfiguration checkData;
    static FileConfiguration upgData;
    //static FileConfiguration econData;
    private static HashMap<String,YamlConfiguration> fileData;  //хранит файлы
    
        static void Create() {
        pluginFolder = Parkour.plugin.getDataFolder();
        UserDataFolder = new File(pluginFolder + File.separator + "player_data");
        courseFile = new File(pluginFolder, "courses.yml");
        courseData = new YamlConfiguration();
        leaderFile = new File(pluginFolder, "leaderboards.yml");
        leaderData = new YamlConfiguration();
        stringFile = new File(pluginFolder, "strings.yml");
        stringData = new YamlConfiguration();
        checkFile = new File(pluginFolder, "checkpoints.yml");
        checkData = new YamlConfiguration();
        upgFile = new File(pluginFolder, "upgrades.yml");
        upgData = new YamlConfiguration();
        
        fileData = new HashMap ();
  
        
        if (!courseFile.exists()) {Parkour.logger.info("[Parkour] Creating courses.yml");

            try {courseFile.createNewFile();Parkour.logger.info("[Parkour] Done");
            } catch (Exception exception) {Parkour.logger.info("[Parkour] Failed!");}}

        try {courseData.load(courseFile);} catch (IOException | InvalidConfigurationException exception1) {}

        
        
        if (!leaderFile.exists()) {
            Parkour.logger.info("[Parkour] Creating leaderboards.yml");
            try {
                leaderFile.createNewFile();Parkour.logger.info("[Parkour] Done");
            } catch (Exception exception2) {
                Parkour.logger.info("[Parkour] Failed!");
            }
        }

        try {
            leaderData.load(leaderFile);
        } catch (IOException | InvalidConfigurationException exception3) {}
        
        

        if (!stringFile.exists()) {Parkour.logger.info("[Parkour] Creating strings.yml");

            try {stringFile.createNewFile();Parkour.logger.info("[Parkour] Done"); saveStrings();
            } catch (Exception exception4) {Parkour.logger.info("[Parkour] Failed!");}}

        try {stringData.load(stringFile);} catch (IOException | InvalidConfigurationException exception5) {}
        
        
        
        
        
                	            if (!UserDataFolder.exists() && !UserDataFolder.mkdirs()) {
	                System.out.println(" Could not create player_data directory.");
	                return;
	            }



        if (!checkFile.exists()) {Parkour.logger.info("[Parkour] Creating checkpoints.yml");

            try {checkFile.createNewFile();Parkour.logger.info("[Parkour] Done");
            } catch (Exception exception10) {Parkour.logger.info("[Parkour] Failed!");}}

        try {checkData.load(checkFile);} catch (IOException | InvalidConfigurationException exception11) {}
        
        

        if (!upgFile.exists()) {Parkour.logger.info("[Parkour] Creating upgrades.yml");

            try {upgFile.createNewFile();Parkour.logger.info("[Parkour] Done");
            } catch (Exception exception12) {Parkour.logger.info("[Parkour] Failed!");}}

        try {upgData.load(upgFile);} catch (IOException | InvalidConfigurationException exception13) {}


        }
        
   
   
        
        
        
        
     static void CreateUserFile (final Player p) {    
         
        File usersFile = new File(UserDataFolder, p.getName()+".yml");
         
           if (!usersFile.exists()) {

            try {usersFile.createNewFile();
            Parkour.logger.log(Level.INFO, "[Parkour] Создаётся файл конфигурации {0}.yml", p.getName());
                 YamlConfiguration data = new YamlConfiguration();
                            
           data.set( ".Rank", "Новичок");
           data.set(".XP", 0);
           data.set( ".Level", 0);
           data.set( ".JumpTotal", 0);
           data.set( ".FallTotal", 0);
           data.set( ".TimeTotal", 0);
           data.set( ".Selected", "-");

            fileData.put(p.getName(), data);
            
            saveUsers(p);
            
            } catch (Exception e) {
            Parkour.logger.log(Level.INFO, "[Parkour] Ну удалось создать файл {0}.yml !", p.getName());
            }
        }

     }
        
 
 static void loadUsers(final Player p ) {
        File usersFile = new File (UserDataFolder, p.getName()+".yml");
        
       // YamlConfiguration data = YamlConfiguration.loadConfiguration(usersFile); 
        
        fileData.put(p.getName(), YamlConfiguration.loadConfiguration(usersFile) );

    }
     

 
     
     
 static String getStringVar (final Player p , final String var) {
        //final YamlConfiguration data =  fileData.get(p.getName());
                    if (fileData.get(p.getName()).contains(var)) return (String) fileData.get(p.getName()).get(var);
                    else return "";
    }        
  
 
  static int getIntVar (final Player p , final String var) {
        //final YamlConfiguration data =  fileData.get(p.getName());
            if (fileData.get(p.getName()).contains(var)) return (int) fileData.get(p.getName()).get(var);
            else return 0;
        
    }         
        

 static void setStringVar (final Player p, final String var , final String value) {
        //final YamlConfiguration data = (YamlConfiguration) fileData.get(p.getName());
         fileData.get(p.getName()).set(var, value);
       
    }        
  
 
 static void setIntVar (final Player p, final String var , final int value) {
        //final YamlConfiguration data = (YamlConfiguration) fileData.get(p.getName());
       fileData.get(p.getName()).set(var, value);
      
    }  
          
     
 
 static void saveUsers(final Player p ) {
        File usersFile = new File(UserDataFolder, p.getName()+".yml");
     
       // final YamlConfiguration data = (YamlConfiguration) fileData.get(p.getName());
        
        try {
            fileData.get(p.getName()).save(usersFile);
        } catch (IOException e) {Parkour.logger.log(Level.INFO, "[Parkour] Ну удалось создать файл {0}.yml !", p.getName());}
    }
     

 
 
 
 
 
  
        static void addDefaulf () {
        //FileConfiguration config = plugin.getConfig();
  
        config.options().header("==== Parkour Config ==== #");
        //config.addDefault("Prize.Use", true);
        //config.addDefault("Prize.DefaultID", 264);
        //config.addDefault("Prize.DefaultAmount", 1);
        //config.addDefault("Prize.DefaultXP", 0);
        config.addDefault("PlatePointID", 1);
        config.addDefault("LeaveID", 6);
        config.addDefault("Other.Log", true);
        config.addDefault("Other.MaxFall", 100);
        config.addDefault("Other.LeaderboardType", 1);
        config.addDefault("Other.Use.Economy", true);
        config.addDefault("Other.Use.ConfirmCJ", false);
        config.addDefault("Other.Use.ForceFullCompletion", true);
        config.addDefault("Other.Use.PlatePoints", true);
        config.addDefault("Other.Use.Prefix", false);
        config.addDefault("Other.Use.ParkourBlocks", true);
        config.addDefault("Other.Use.SignProtection", true);
        config.addDefault("Other.Use.InvManagement", true);
        config.addDefault("Other.Use.Scoreboard", false);
        config.addDefault("Other.Use.PlayerDamage", true);
        config.addDefault("Other.Use.Sounds", true);
        config.addDefault("Other.Use.ForceWorld", false);
        config.addDefault("Other.Use.OldStatsSigns", false);
        config.addDefault("Other.Use.SetLevelAsXPBar", false);
        config.addDefault("Other.Use.CmdPermission", false);
        config.addDefault("Other.Use.ForceParkourSigns", true);
        config.addDefault("Other.onJoin.forceNoFly", true);
        config.addDefault("Other.onJoin.forceFinished", false);
        config.addDefault("Other.onLeave.ResetPlayer", false);
        config.addDefault("Other.onDie.SetAsXPBar", false);
        config.addDefault("Other.onFinish.tptoLobby", true);
        config.addDefault("Other.onFinish.broadcastInfo", false);
        config.addDefault("Other.DisableCommands.OnParkour", false);
        config.addDefault("Other.Display.WelcomeMessage", true);
        config.addDefault("Other.Display.CreatorJoin", true);
        config.addDefault("Other.Display.FinishedError", true);
        config.addDefault("Other.Display.XPReward", false);
        config.addDefault("Other.Display.LevelReward", true);
        config.addDefault("Other.Display.TitleOnJoin", true);
        config.addDefault("Database.Use", false);
        config.addDefault("Database.Host", "Host");
        config.addDefault("Database.User", "Username");
        config.addDefault("Database.Password", "Password");
        config.addDefault("Database.Db", "Database");
        config.addDefault("Database.Table", "Table");
        config.addDefault("=== Do NOT Edit anything below here ===", "");
        config.addDefault("Lobby.Set", false);
        config.options().copyDefaults(true);
        plugin.saveConfig();
            
        }

  
        
        
       static void saveStrings() {
        try {
            stringData.addDefault("===| README |===", "!!! TO SAVE THESE VALUES, YOU MUST: STOP THE SERVER. SAVE YOUR CHANGES. START THE SERVER !!!");
            stringData.addDefault("Parkour.PrefixColour", "b");
            if (stringData.getString("Parkour.PrefixColour").length() != 1) {
                stringData.set("Parkour.PrefixColour", "b");
            }

            stringData.addDefault("Event.Join", "&0[&bThis server uses &3Parkour &b%VERSION%&0]");
            stringData.addDefault("Event.Checkpoint", "Checkpoint set to: ");
            stringData.addDefault("Event.AllCheckpoints", "All check points achieved!");
            stringData.addDefault("Event.HideAll1", "All players have magically reappeared!");
            stringData.addDefault("Event.HideAll2", "All players have magically disappeared!");
            stringData.addDefault("Parkour.Join", "Joined &b%COURSE%");
            stringData.addDefault("Parkour.Leave", "You have left &b%COURSE%");
            stringData.addDefault("Parkour.Created", "Created and Selected &b%COURSE%");
            stringData.addDefault("Parkour.Delete", "&b%COURSE% &fhas been deleted!");
            stringData.addDefault("Parkour.Reset", "&b%COURSE% &fscores have been reset!");
            stringData.addDefault("Parkour.Finish", "&b%COURSE% &fhas been set to finished!");
            stringData.addDefault("Parkour.FinishBroadcast", "&3%PLAYER% &ffinished &b%COURSE% &fwith &b%DEATHS% &fdeaths, in &b%TIME%&f!");
            stringData.addDefault("Parkour.FinishEconomy", "You earned &b%AMOUNT% &ffor completing &b%COURSE%&f!");
            stringData.addDefault("Parkour.Lobby", "You have joined the lobby");
            stringData.addDefault("Parkour.LobbyOther", "You have joined the &b%LOBBY% lobby");
            stringData.addDefault("Parkour.Continue", "Continuing Parkour on &b%COURSE%");
            stringData.addDefault("Parkour.Teleport", "You have teleported to &b%COURSE%");
            stringData.addDefault("Parkour.Invite.Send", "Invitation to &b%COURSE% &fsent to &b%TARGET%");
            stringData.addDefault("Parkour.Invite.Recieve1", "&b%PLAYER% &fhas invited you to &b%COURSE%");
            stringData.addDefault("Parkour.Invite.Recieve2", "To accept, type &3/pa join %COURSE%");
            stringData.addDefault("Parkour.MaxDeaths", "Sorry, you reached the maximum amount of deaths: &b%AMOUNT%");
            stringData.addDefault("Parkour.Die1", "You died! Going back to the Start!");
            stringData.addDefault("Parkour.Die2", "You died! Going back to checkpoint &b%POINT%");
            stringData.addDefault("Parkour.Win1", "You placed 1st on &b%COURSE%&f! New highscore: &3%TIME%");
            stringData.addDefault("Parkour.Win2", "You placed 2nd on &b%COURSE%&f!");
            stringData.addDefault("Parkour.Win3", "You placed 3rd on &b%COURSE%&f!");
            stringData.addDefault("Error.NotOnCourse", "You are not on this course!");
            stringData.addDefault("Error.TooMany", "Too many arguments!");
            stringData.addDefault("Error.TooLittle", "Not enough arguments!");
            stringData.addDefault("Error.Exist", "This course already exists!");
            stringData.addDefault("Error.NoExist", "%COURSE% doesn\'t exist!");
            stringData.addDefault("Error.Unknown", "Unknown course!");
            stringData.addDefault("Error.Command", "Commands have been disabled!");
            stringData.addDefault("Error.Selected", "You have not selected a course!");
            stringData.addDefault("Error.Something", "Something went wrong: %ERROR%");
            stringData.addDefault("Error.RequiredLvl", "You need to be a required level of &b%LEVEL%");
            stringData.addDefault("Error.Finished1", "This course is not ready for you to play yet!");
            stringData.addDefault("Error.Finished2", "The creator of this course has not set it to finished.");
            stringData.addDefault("Other.Item_Suicide", "&7SHIFT + &6Right click to commit suicide");
            stringData.addDefault("Other.Item_HideAll", "&7SHIFT + &6Right click to toggle visibility");
            stringData.addDefault("Other.Item_Leave", "&7SHIFT + &6Right click to leave course");
            stringData.addDefault("Other.Item_Book", "&6View course stats");
            stringData.addDefault("Other.Reload", "Config Reloaded!");
            stringData.addDefault("Other.Kit", "Kit Given!");
            stringData.addDefault("Kit.Speed", "&bSpeed Block");
            stringData.addDefault("Kit.Climb", "&bClimb Block");
            stringData.addDefault("Kit.Launch", "&bLaunch Block");
            stringData.addDefault("Kit.Finish", "&bFinish Block");
            stringData.addDefault("Kit.Repulse", "&bRepulse Block");
            stringData.addDefault("Kit.NoRun", "&bNoRun Block");
            stringData.addDefault("Kit.NoFall", "&bNoFall Block");
            stringData.addDefault("Kit.NoPotion", "&bNoPotion Block");
            stringData.addDefault("Kit.Sign", "&bSign");
            stringData.addDefault("Kit.Death", "&bDeath Block");
            stringData.addDefault("Spectate.AlertPlayer", "You are now being spectated by &b%PLAYER%");
            stringData.addDefault("Spectate.FinishedSpec", "You are no longer being spectated");
            stringData.addDefault("Title.Joining", "Joining");
            stringData.addDefault("Title.Checkpoint", "checkpoint");
            stringData.addDefault("Title.Checkpoints", "checkpoints");
            stringData.addDefault("NoPermission", "You do not have Permission: &b%PERMISSION%");
            stringData.addDefault("====| README |====", "!!! TO SAVE THESE VALUES, YOU MUST: STOP THE SERVER. SAVE YOUR CHANGES. START THE SERVER !!!");
            stringData.options().copyDefaults(true);
            stringData.save(stringFile);
        } catch (IOException i) {
        }

    }

        
}
