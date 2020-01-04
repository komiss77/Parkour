package me.A5H73Y.Parkour;


import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class Commands implements CommandExecutor {

    Commands(Parkour aThis) {
    }




    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

            if ( !(sender instanceof Player) ) {System.out.println("YAAAAAAAAAY");return true;}
            Player player = (Player) sender;
            
            if (args.length < 1) {
                player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "cmds" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Список команд");
                return true;
            }


                //FileConfiguration config =  Parkour.plugin.getConfig();

                if (commandLabel.equalsIgnoreCase("parkour") || commandLabel.equalsIgnoreCase("pa") ) {     
                        //Parkour.plugin.perm = "Parkour.Basic.Commands";
                
                        String s;
                        int i; 
                        int j;
                        int k;
                        int l;
                        int arenaname;
                        List list;
        

switch (args[0].toLowerCase()) {
    
    
case "kill":
         if ( Parkour.inProgress.contains(player.getName())) {  //если в игре, отрабатываем падение
                       
            String arenain = Files.getStringVar(player, ".Selected");
                if (!"".equals(arenain) && !"-".equals(arenain) && player.getLocation().getWorld().getName().equals(Files.courseData.getString(arenain + ".World"))) {
                    Parkour.Fall(player);
                }
         } else player.sendMessage(Parkour.Prefix +  " Эта команда работает только на паркуре!");
        break;

   
        
        
case "select":
       if (args.length <= 2) {
           if (args.length != 1) {
                if (!Files.courseData.getStringList("Courses").contains(args[1])) {
                    player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.Unknown")));
                } else {
                    s = args[1];
                    player.sendMessage(Parkour.Prefix + "Now Editing: "+ s);
                    int integer = Files.courseData.getInt(s + "." + "Points");
                    player.sendMessage(Parkour.Prefix + "Checkpoints created: " + integer);
                    Files.setStringVar(player,  ".Selected" , s);
                    Files.saveUsers(player);
                }
            } else {player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.TooLittle")));}
        } else {player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.TooMany")));}
        break;

       
       
    
case "join":
                         if (args.length <= 2) {
                            if (args.length != 1) {
                                s = args[1];
                                if (isNumber(s) && Integer.parseInt(s) > 0) {
                                    list = Files.courseData.getStringList("Courses");
                                    s = (String) list.get(Integer.parseInt(s) - 1);
                                }

                                if (!Files.courseData.getStringList("Courses").contains(s)) {
                                    player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.Unknown")));
                                } else {
                                    Parkour.CourseStart(player, s);
                                }
                            } else {
                                player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.TooLittle")));
                            }
                        } else {
                            player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.TooMany")));
                        }
        break; 	
        
 
             
             
case "leave":
              Parkour.CourseLeave(player,"");
        break;            
        
   
             
              
case "lobby":
            Parkour.ToLobby(player, "");
        break;            
        
              
             
case "create":
                            if (!player.isOp()) {
                                player.sendMessage("NoPermission");
                                return false;
                            } 
                            if (args.length <= 2) {
                                if (args.length != 1) {
                                    if (!Files.courseData.getStringList("Courses").contains(args[1])) {
                                        if (args[1].length() <= 15) {
                                            if (!args[1].equalsIgnoreCase("TestMode")) {
                                                if (!args[1].contains(".")) {
                                                    if (isNumber(args[1])) {
                                                        player.sendMessage(Parkour.Prefix + "Course name can not only be numeric");
                                                        return false;
                                                    }

                                                    arenaname = Files.courseData.getStringList("Courses").size();
                                                    if (Files.courseData.getStringList("Courses").isEmpty())   arenaname = 0;

                                                    list = Files.courseData.getStringList("Courses");
                                                    list.add(arenaname, args[1]);
                                                    Files.courseData.set("Courses", list);
                                                    Integer integer1 = Parkour.plugin.getConfig().getInt(args[1] + "." + "Points");

                                                    if (Parkour.config.getBoolean("Database.Use")) {
                                                        try {
                                                            MySQL.createCourse(args[1]);
                                                            player.sendMessage(Parkour.Prefix + "Added " + args[1] + " to Database!");
                                                        } catch (Exception exception18) {
                                                            player.sendMessage(Parkour.Prefix + "Failed to add " + args[1] + " to Database!");
                                                            player.sendMessage(ChatColor.RED + "Error: " + ChatColor.WHITE + exception18.getMessage());
                                                        }
                                                    }

                                                    Files.courseData.set(args[1] + ".World", player.getLocation().getWorld().getName());
                                                    Files.courseData.set(args[1] + ".Creator", player.getName());
                                                    Files.courseData.set(args[1] + ".Views", 0);
                                                    Files.courseData.set(args[1] + ".Completed", 0);
                                                    Files.courseData.set(args[1] + ".XP", 0);
                                                    Files.courseData.set(args[1] + ".Points", integer1);
                                                    Files.courseData.set(args[1] + ".Reward", 3000);
                                                    Files.courseData.set(args[1] + "." + integer1 + ".X", player.getLocation().getX());
                                                    Files.courseData.set(args[1] + "." + integer1 + ".Y", player.getLocation().getY());
                                                    Files.courseData.set(args[1] + "." + integer1 + ".Z", player.getLocation().getZ());
                                                    Files.courseData.set(args[1] + "." + integer1 + ".Yaw", player.getLocation().getYaw());
                                                    Files.courseData.set(args[1] + "." + integer1 + ".Pitch", player.getLocation().getPitch());
                                                   
                                                    Files.leaderData.set(args[1] + ".1.time", 0);
                                                    Files.leaderData.set(args[1] + ".1.deaths", "0");
                                                    Files.leaderData.set(args[1] + ".1.player", "N/A");
                                                    Files.leaderData.set(args[1] + ".2.time", 0);
                                                    Files.leaderData.set(args[1] + ".2.deaths", "0");
                                                    Files.leaderData.set(args[1] + ".2.player", "N/A");
                                                    Files.leaderData.set(args[1] + ".3.time", 0);
                                                    Files.leaderData.set(args[1] + ".3.deaths", "0");
                                                    Files.leaderData.set(args[1] + ".3.player", "N/A");
                                                    
                                                    Files.setStringVar(player, ".Selected", args[1]);

                                                    Parkour.plugin.saveLeaders();
                                                    Parkour.plugin.saveConfig();
                                                    Parkour.saveCourses();
                                                    player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Parkour.Created").replace("%COURSE%", args[1])));
                                                    
                                                } else player.sendMessage(Parkour.Prefix + "Course names can not contain \'.\'");
                                            } else player.sendMessage(Parkour.Prefix + "This name is already taken!");
                                        } else player.sendMessage(Parkour.Prefix + "Course name is too long!");
                                    } else player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.Exist")));
                                } else player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.TooLittle")));
                            } else   player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.TooMany")));

            break; 	
        
        
            
             
             
             
          //checkpoint   
case "cp":
                            Integer integer;
                            String sel = Files.getStringVar(player, ".Selected");
                            
                            if (!player.isOp()) {
                                player.sendMessage("NoPermission");
                                return false;
                            } 

                            if (sel.isEmpty() | "".equals(sel) | "-".equals(sel)) {
                                                player.sendMessage(Parkour.Prefix + "You need to select a course first!");
                                                player.sendMessage(Parkour.Prefix + "Usage: " + ChatColor.WHITE + "/pa select (course)");
                                            } else {
                             integer = Files.courseData.getInt(sel + "." + "Points");
                                                if (!Files.courseData.getStringList("Courses").contains(sel)) {
                                                    player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.NoExist").replace("%COURSE%", sel)));
                                                } else {
                                                    Location location1;
                                                    Block block;

                                                    if (args.length > 1) {
                                                        
                                                            
                                                            if (args[1].equals("finish")) {
                                                                
                                                        integer = integer + 1;
                                                        Files.courseData.set(sel + ".Points", integer);
                                                        Files.courseData.set(sel + "." + integer + ".X", (double) player.getLocation().getBlockX() + 0.5D);
                                                        Files.courseData.set(sel + "." + integer + ".Y", (double) player.getLocation().getBlockY() + 0.5D);
                                                        Files.courseData.set(sel + "." + integer + ".Z", (double) player.getLocation().getBlockZ() + 0.5D);
                                                        Files.courseData.set(sel + "." + integer + ".Yaw", player.getLocation().getYaw());
                                                        Files.courseData.set(sel + "." + integer + ".Pitch", player.getLocation().getPitch());
                                                        Files.checkData.set(sel + "." + integer + ".X", player.getLocation().getBlockX());
                                                        Files.checkData.set(sel + "." + integer + ".Y", player.getLocation().getBlockY() - 1);
                                                        Files.checkData.set(sel + "." + integer + ".Z", player.getLocation().getBlockZ());
                                                        Parkour.plugin.saveCheck();
                                                        Parkour.plugin.saveConfig();
                                                        Parkour.saveCourses();
                                                        location1 = player.getLocation();
                                                        block = location1.getBlock();
                                                        block.setType(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
                                                        location1.setY((double) (location1.getBlockY() - 1));
                                                        block = location1.getBlock();
                                                      //  block.setType(Material.STONE);
                                                        player.sendMessage(Parkour.Prefix + "Конечная точка " + ChatColor.DARK_AQUA + integer + ChatColor.WHITE + " установлена для " + ChatColor.AQUA + sel);
                                                    Files.courseData.set(sel + ".Finished", true);
                                                            Parkour.plugin.saveCheck();
                                                            Parkour.plugin.saveConfig();
                                                            Parkour.saveCourses();
                                                            
                                                            } else if (!isNumber(args[1])) {
                                                                player.sendMessage(Parkour.Prefix + "Нужно указать номер, либо /pa cp finish");
                                                                
                                                              } else {
                                                            if (integer < Integer.parseInt(args[1])) {
                                                                integer = integer + 1;
                                                                Files.courseData.set(sel + ".Points", integer);
                                                                player.sendMessage(Parkour.Prefix + "Warning: This point does not exist. Created anyway...");
                                                            }

                                                            Files.courseData.set(sel + "." + args[1] + ".X", (double) player.getLocation().getBlockX() + 0.5D);
                                                            Files.courseData.set(sel + "." + args[1] + ".Y",(double) player.getLocation().getBlockY() + 0.5D);
                                                            Files.courseData.set(sel + "." + args[1] + ".Z", (double) player.getLocation().getBlockZ() + 0.5D);
                                                            Files.courseData.set(sel + "." + args[1] + ".Yaw", player.getLocation().getYaw());
                                                            Files.courseData.set(sel + "." + args[1] + ".Pitch", player.getLocation().getPitch());
                                                            Files.checkData.set(sel + "." + args[1] + ".X", player.getLocation().getBlockX());
                                                            Files.checkData.set(sel + "." + args[1] + ".Y", player.getLocation().getBlockY() - 1);
                                                            Files.checkData.set(sel + "." + args[1] + ".Z", player.getLocation().getBlockZ());
                                                            Parkour.plugin.saveCheck();
                                                            Parkour.plugin.saveConfig();
                                                            Parkour.saveCourses();
                                                            location1 = player.getLocation();
                                                            block = location1.getBlock();
                                                            block.setType(Material.STONE_PRESSURE_PLATE);
                                                            location1.setY((double) (location1.getBlockY() - 1));
                                                            block = location1.getBlock();
                                                           // block.setType(Material.STONE);
                                                            player.sendMessage(Parkour.Prefix + "Checkpoint " + ChatColor.DARK_AQUA + args[1] + ChatColor.WHITE + " set on " + ChatColor.AQUA + sel);
                                                        }
                                                        
                                                    } else {
                                                        integer = integer + 1;
                                                        Files.courseData.set(sel + ".Points", integer);
                                                        Files.courseData.set(sel + "." + integer + ".X", (double) player.getLocation().getBlockX() + 0.5D);
                                                        Files.courseData.set(sel + "." + integer + ".Y", (double) player.getLocation().getBlockY() + 0.5D);
                                                        Files.courseData.set(sel + "." + integer + ".Z", (double) player.getLocation().getBlockZ() + 0.5D);
                                                        Files.courseData.set(sel + "." + integer + ".Yaw", player.getLocation().getYaw());
                                                        Files.courseData.set(sel + "." + integer + ".Pitch", player.getLocation().getPitch());
                                                        Files.checkData.set(sel + "." + integer + ".X", player.getLocation().getBlockX());
                                                        Files.checkData.set(sel + "." + integer + ".Y", player.getLocation().getBlockY() - 1);
                                                        Files.checkData.set(sel + "." + integer + ".Z", player.getLocation().getBlockZ());
                                                        Parkour.plugin.saveCheck();
                                                        Parkour.plugin.saveConfig();
                                                        Parkour.saveCourses();
                                                        location1 = player.getLocation();
                                                        block = location1.getBlock();
                                                        block.setType(Material.STONE_PRESSURE_PLATE);
                                                        location1.setY((double) (location1.getBlockY() - 1));
                                                        block = location1.getBlock();
                                                      //  block.setType(Material.STONE);
                                                        player.sendMessage(Parkour.Prefix + "Checkpoint " + ChatColor.DARK_AQUA + integer + ChatColor.WHITE + " set on " + ChatColor.AQUA + sel);
                                                    }
                                                }
                                            }
        break;            
        
        
             
            
case "setstart":
                        s = Files.getStringVar(player, ".Selected");
                            if (!player.isOp()) {
                                player.sendMessage("NoPermission");
                                return false;
                            } 

                            if (args.length != 1) {
                                        player.sendMessage(Parkour.Prefix + "Syntax: /parkour setlobby");
                                    } else if (s.isEmpty() | "".equals(s) | "-".equals(s)) {
                                        player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.Selected")));
                                        
                                    } else {
                                        Files.courseData.set(s + ".0.X", player.getLocation().getX());
                                        Files.courseData.set(s + ".0.Y", player.getLocation().getY());
                                        Files.courseData.set(s + ".0.Z", player.getLocation().getZ());
                                        Files.courseData.set(s + ".0.Yaw", player.getLocation().getYaw());
                                        Files.courseData.set(s + ".0.Pitch", player.getLocation().getPitch());
                                        player.sendMessage(Parkour.Prefix + "Spawn for " + ChatColor.AQUA + s + ChatColor.WHITE + " has been set to your position");
                                    }
        break;            
        
             
        
             
case "setlobby":
                            if (!player.isOp()) {
                                player.sendMessage("NoPermission");
                                return false;
                            } 

                            if (args.length <= 1) {
                                    Parkour.plugin.getConfig().set("Lobby.Set", true);
                                    Parkour.plugin.getConfig().set("Lobby.World", player.getLocation().getWorld().getName());
                                    Parkour.plugin.getConfig().set("Lobby.X", player.getLocation().getX());
                                    Parkour.plugin.getConfig().set("Lobby.Y", player.getLocation().getY());
                                    Parkour.plugin.getConfig().set("Lobby.Z", player.getLocation().getZ());
                                    Parkour.plugin.getConfig().set("Lobby.Yaw", player.getLocation().getYaw());
                                    Parkour.plugin.getConfig().set("Lobby.Pitch", player.getLocation().getPitch());
                                    Parkour.plugin.saveConfig();
                                    player.sendMessage(Parkour.Prefix + "Successfully created Lobby in " + player.getLocation().getWorld().getName());
                                } else {
                                    Parkour.plugin.getConfig().set("Lobby." + args[1] + ".World", player.getLocation().getWorld().getName());
                                    Parkour.plugin.getConfig().set("Lobby." + args[1] + ".X", player.getLocation().getX());
                                    Parkour.plugin.getConfig().set("Lobby." + args[1] + ".Y", player.getLocation().getY());
                                    Parkour.plugin.getConfig().set("Lobby." + args[1] + ".Z", player.getLocation().getZ());
                                    Parkour.plugin.getConfig().set("Lobby." + args[1] + ".Yaw", player.getLocation().getYaw());
                                    Parkour.plugin.getConfig().set("Lobby." + args[1] + ".Pitch", player.getLocation().getPitch());
                                    if (args.length <= 2) {
                                        player.sendMessage(Parkour.Prefix + "Lobby " + args[1] + " created");
                                    } else {
                                        try {
                                            arenaname = Integer.parseInt(args[2]);
                                            Parkour.plugin.getConfig().set("Lobby." + args[1] + ".Level", arenaname);
                                            player.sendMessage(Parkour.Prefix + "Lobby " + args[1] + " created, with a required rank of " + arenaname);
                                        } catch (Exception exception17) {
                                            player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.Something").replaceAll("%ERROR%", exception17.getMessage())));
                                        }
                                    }

                                    Parkour.plugin.saveConfig();
                                }
        break; 	
        
        
             
             
             
             
             
case "reload":
                            if (!player.isOp()) {
                                player.sendMessage("NoPermission");
                                return false;
                            } 

			Parkour.plugin.reloadConfig();
			Parkour.saveCourses();
			player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Other.Reload")));
			break;
            
    
    
case "list":
            if (args.length == 1) {
                player.sendMessage(Parkour.Prefix + "Наберите: /pa list (players / courses)");
                } else if (!args[1].equalsIgnoreCase("players")) {
                if (!args[1].equalsIgnoreCase("courses")) {
                player.sendMessage(Parkour.Prefix + "Наберите: /pa list (players / courses)");    } else {
                List list1 = Files.courseData.getStringList("Courses");
                i = args.length == 3 && args[2] != null ? Integer.parseInt(args[2]) : 1;
                j = 1;int i1 = 0;
                for (k = 0; k < list1.size(); ++k) {++i1;
                if (i1 == 8) {i1 = 0;++j;}}
                player.sendMessage(Parkour.Prefix + list1.size() + " courses:");
                if (i >= 1 && i <= j) {
                for (k = 0; k < list1.size(); ++k) {l = i * 8;if (k >= l - 8 && k < l) {
                player.sendMessage(k + 1 + ") " + ChatColor.AQUA + (String) list1.get(k));}}
                player.sendMessage("== " + i + " / " + j + " ==");} else {
                player.sendMessage(Parkour.Prefix + "Invalid page. Maximum: " + j);}}} else {
                player.sendMessage(Parkour.Prefix + "Players: " + ChatColor.AQUA + "(" + ChatColor.DARK_AQUA + Parkour.inProgress.size() + ChatColor.AQUA + ") " + Parkour.inProgress.toString());}
                    break;

        
case "link":
                            if (!player.isOp()) {
                                player.sendMessage("NoPermission");
                                return false;
                            } 

            if (args.length <= 3) {
                 if (args.length >= 3) {
                    if (!Files.courseData.getStringList("Courses").contains(args[1])) {
                         player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.NoExist").replace("%COURSE%", args[1])));
                     } else if (!Parkour.config.contains("Lobby." + args[2] + ".X")) {
                         player.sendMessage(Parkour.Prefix + "Lobby doesn\'t exist!");
                     } else {
                         Files.courseData.set(args[1] + ".Lobby", args[2]);
                         Parkour.saveCourses();
                        player.sendMessage(Parkour.Prefix + ChatColor.AQUA + args[1] + ChatColor.WHITE + " was successfully linked to " + ChatColor.DARK_AQUA + args[2]);
                     }
                } else  player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.TooLittle")));

            } else player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.TooMany")));

       break;
            
        
    
case "setmaxdeath":
                            if (!player.isOp()) {
                                player.sendMessage("NoPermission");
                                return false;
                            } 

           if (args.length <= 3) {
             if (args.length != 1) {
           if (!Files.courseData.getStringList("Courses").contains(args[1])) {
               player.sendMessage(Parkour.Prefix + "Course does not exist!");
           } else if (args.length != 3) {
             player.sendMessage(Parkour.Prefix + "Invalid Syntax: /pa setmaxdeath (course) (amount)");
              } else {
             try {
                 s = args[1];
                 i = Integer.parseInt(args[2]);
                 Files.courseData.set(args[1] + ".MaxDeaths", i);
                 player.sendMessage(Parkour.Prefix + s + " max deaths was set to " + ChatColor.AQUA + i);
                 Parkour.saveCourses();
             } catch (Exception exception) {
                 player.sendMessage(Parkour.Prefix + "Please use a valid amount");
             }  }
              } else {
         player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.TooLittle")));
              } } else {
                  player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.TooMany")));
                 }
        break;
            
            
            
            
         
case "rewardrank":
                            if (!player.isOp()) {
                                player.sendMessage("NoPermission");
                                return false;
                            } 

            if (args.length <= 3) {
                if (args.length != 1) {
                    if (!isInt(args[1])) {
                        player.sendMessage(Parkour.Prefix + "Needs to be number lol!");
                    } else if (args.length != 3) {
                        player.sendMessage(Parkour.Prefix + "Invalid Syntax: /pa rewardrank (level) (rank)");
                    } else {
                        arenaname = Integer.parseInt(args[1]);
                        player.sendMessage(Parkour.Prefix + "Level " + args[1] + "s rank was set to " + ChatColor.AQUA + args[2]);
                        Parkour.saveCourses();
                    }
                } else player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.TooLittle")));
            } else player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.TooMany"))); 
        break;

       
            
            
case "setminlevel":
                            if (!player.isOp()) {
                                player.sendMessage("NoPermission");
                                return false;
                            } 
        if (args.length <= 3) {
            if (args.length != 1) {
                if (!Files.courseData.getStringList("Courses").contains(args[1])) {
                    player.sendMessage(Parkour.Prefix + "Course does not exist!");
                } else if (args.length != 3) {
                    player.sendMessage(Parkour.Prefix + "Invalid Syntax: /pa setminlevel (course) (amount)");
                } else 
                    {try {s = args[1];
                        i = Integer.parseInt(args[2]);
                        Files.courseData.set(args[1] + ".MinimumLevel", i);
                        player.sendMessage(Parkour.Prefix + s + " minimum level was set to " + ChatColor.AQUA + i);
                        Parkour.saveCourses();
                    } catch (Exception exception1) {
                        player.sendMessage(Parkour.Prefix + "Please use a valid amount");
                    }
                }
            } else player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.TooLittle")));
        } else player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.TooMany"))); 
        break;            
            
        
        
case "rewardxp":
                            if (!player.isOp()) {
                                player.sendMessage("NoPermission");
                                return false;
                            } 
         if (args.length <= 3) {
            if (args.length != 1) {
                if (!Files.courseData.getStringList("Courses").contains(args[1])) {
                    player.sendMessage(Parkour.Prefix + "Course does not exist!");
                } else if (args.length != 3) {
                    player.sendMessage(Parkour.Prefix + "Invalid Syntax: /pa rewardxp (course) (amount)");
                } else {
                    try {
                        s = args[1];i = Integer.parseInt(args[2]);
                        Files.courseData.set(args[1] + ".Level", i);
                        player.sendMessage(Parkour.Prefix + s + "\'s level reward was set to " + ChatColor.AQUA + i);
                        Parkour.saveCourses();
                    } catch (Exception exception2) {player.sendMessage(Parkour.Prefix + "Please use a valid amount");}
                }
            } else player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.TooLittle")));
         } else player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.TooMany")));
        break; 
             
             
         
    
    
case "setrank":
                            if (!player.isOp()) {
                                player.sendMessage("NoPermission");
                                return false;
                            } 
        if (args.length <= 1) player.sendMessage(Parkour.Prefix + "Invalid syntax: /pa setrank (rank) [player]");
        else if (args.length <= 2) {
            try {
                Parkour.plugin.setRank(player, args[1]);player.sendMessage(Parkour.Prefix + "Rank was set to " + args[1]);
            } catch (Exception exception4) {player.sendMessage(Parkour.Prefix + "Please provide valid XP amount!");}
        } else {
            Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    player.sendMessage(Parkour.Prefix + "Player is not online!");
                } else {
                    try {
                        Parkour.plugin.setRank(target, args[1]);
                        player.sendMessage(Parkour.Prefix + target.getName() + "\'s rank was set to " + args[1] + "!");
                        target.sendMessage(Parkour.Prefix + "Your rank was set to " + ChatColor.AQUA + args[1] + ChatColor.WHITE + " by " + player.getName());
                    } catch (Exception exception5) {player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.Something").replaceAll("%ERROR%", exception5.getMessage())));}
                }
        }
        break; 
                         
                         
                         
case "setlevel":
                            if (!player.isOp()) {
                                player.sendMessage("NoPermission");
                                return false;
                            } 
    if (args.length <= 1) {
        player.sendMessage(Parkour.Prefix + "Invalid syntax: /pa setlevel (amount) [player]");
    } else if (args.length <= 2) {
        try {
            arenaname = Integer.parseInt(args[1]);
            Parkour.plugin.setLevel(player, arenaname);
            player.sendMessage(Parkour.Prefix + "Level was set to " + arenaname);
        } catch (Exception exception6) {player.sendMessage(Parkour.Prefix + "Please provide valid XP amount!");}    
    } else {
        Player p = Bukkit.getPlayer(args[2]);
        if (p == null) player.sendMessage(Parkour.Prefix + "Player is not online!");
         else {
            try { arenaname = Integer.parseInt(args[1]);
                Parkour.plugin.setLevel(p, arenaname);
                player.sendMessage(Parkour.Prefix + p.getName() + "\'s Level was set to " + arenaname + "!");
                p.sendMessage(Parkour.Prefix + "Your level was set to " + ChatColor.AQUA + arenaname + ChatColor.WHITE + " by " + player.getName());
            } catch (Exception exception7) {player.sendMessage(Parkour.Prefix + "Please provide valid XP amount!");}
        }
    }
        break;                    
        
        

case "setxp":
                            if (!player.isOp()) {
                                player.sendMessage("NoPermission");
                                return false;
                            } 
    if (args.length <= 1) player.sendMessage(Parkour.Prefix + "Invalid syntax: /pa setxp (amount) [player]");
    else if (args.length <= 2) {
        try { arenaname = Integer.parseInt(args[1]);
            Parkour.plugin.setXP(player, arenaname);
            player.sendMessage(Parkour.Prefix + "XP was set to " + ChatColor.AQUA + arenaname + ChatColor.WHITE + "!");
        } catch (Exception exception8) {player.sendMessage(Parkour.Prefix + "Please provide valid XP amount!");}
    } else {
        Player p = Bukkit.getPlayer(args[2]);
        if (p == null) player.sendMessage(Parkour.Prefix + "Player is not online!");
        else if (!p.getName().equals(player.getName())) {
            try { arenaname = Integer.parseInt(args[1]);
                Parkour.plugin.setXP(p, arenaname);
                player.sendMessage(Parkour.Prefix + p.getName() + "\'s XP was set to " + arenaname + "!");
                p.sendMessage(Parkour.Prefix + "Your XP was set to " + ChatColor.AQUA + arenaname + ChatColor.WHITE + " by " + player.getName());
            } catch (Exception exception9) {player.sendMessage(Parkour.Prefix + "Please provide valid XP amount!");}
        } else player.sendMessage(Parkour.Prefix + "You can\'t invite yourself!");
    }
        break;            
        
         
case "givexp":
                            if (!player.isOp()) {
                                player.sendMessage("NoPermission");
                                return false;
                            } 
    if (args.length <= 1) player.sendMessage(Parkour.Prefix + "Invalid syntax: /pa givexp (amount) [player]");
    else if (args.length <= 2) {
        try {
            arenaname = Integer.parseInt(args[1]);
            Parkour.plugin.GiveXP(player, arenaname);player.sendMessage(Parkour.Prefix + arenaname + "XP was given!");
        } catch (Exception exception10) {player.sendMessage(Parkour.Prefix + "Please provide valid XP amount!");}
    } else {
        Player p = Bukkit.getPlayer(args[2]);
        if (p == null) player.sendMessage(Parkour.Prefix + "Player is not online!");
            if (p == null) player.sendMessage(Parkour.Prefix + "Player is not online!");
             else if (!p.getName().equals(player.getName())) {
                try {arenaname = Integer.parseInt(args[1]);
                    Parkour.plugin.GiveXP(p, arenaname);player.sendMessage(Parkour.Prefix + arenaname + "XP was given to " + p.getName() + "!");
                    p.sendMessage(Parkour.Prefix + "You were given " + ChatColor.AQUA + arenaname + ChatColor.WHITE + "XP from " + player.getName());
                } catch (Exception exception11) {player.sendMessage(Parkour.Prefix + "Please provide valid XP amount!");}
             } else player.sendMessage(Parkour.Prefix + "You can\'t invite yourself!");
    }
        break;            
        
 
    
    
case "course":
    
if (args.length <= 1) {
player.sendMessage(Parkour.Prefix + "Syntax: /Parkour course (course)");
} else {
List list1 = Files.courseData.getStringList("Courses");
if (!list1.contains(args[1])) {
player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.Unknown")));
} else {
i = Files.leaderData.getInt(args[1] + ".1.time");
j = Files.leaderData.getInt(args[1] + ".1.deaths");
String s2 = Files.leaderData.getString(args[1] + ".1.player");
k = Files.leaderData.getInt(args[1] + ".2.time");
l = Files.leaderData.getInt(args[1] + ".2.deaths");
String s3 = Files.leaderData.getString(args[1] + ".2.player");
int worlds = Files.leaderData.getInt(args[1] + ".3.time");
int k1 = Files.leaderData.getInt(args[1] + ".3.deaths");
String s4 = Files.leaderData.getString(args[1] + ".3.player");
player.sendMessage("---" + ChatColor.BLACK + "[ " + ChatColor.DARK_AQUA + args[1] + ChatColor.BLACK + " ]" + ChatColor.WHITE + "---");
player.sendMessage("Views: " + ChatColor.AQUA + Files.courseData.getInt(args[1] + ".Views"));
player.sendMessage("Completed: " + ChatColor.AQUA + Files.courseData.getInt(args[1] + ".Completed"));
player.sendMessage("Checkpoints: " + ChatColor.AQUA + Files.courseData.getInt(args[1] + ".Points"));
if (Files.courseData.contains(args[1] + ".MaxDeaths")) {
player.sendMessage("Max Deaths: " + ChatColor.AQUA + Files.courseData.get(args[1] + ".MaxDeaths"));}
if (Files.courseData.contains(args[1] + ".Lobby")) {
player.sendMessage("Lobby: " + ChatColor.AQUA + Files.courseData.get(args[1] + ".Lobby"));}
if (Files.courseData.contains(args[1] + ".MinimumLevel")) {
player.sendMessage("Min Level: " + ChatColor.AQUA + Files.courseData.get(args[1] + ".MinimumLevel"));}
if (Files.courseData.contains(args[1] + ".Level")) {
player.sendMessage("Level Reward: " + ChatColor.AQUA + Files.courseData.get(args[1] + ".Level"));}
if (Files.courseData.contains(args[1] + ".XP") && Files.courseData.getInt(args[1] + ".XP") != 0) {
player.sendMessage("XP Reward: " + ChatColor.AQUA + Files.courseData.get(args[1] + ".XP"));}
player.sendMessage("Creator: " + ChatColor.AQUA + Files.courseData.getString(args[1] + ".Creator"));
player.sendMessage("Highscores:");
player.sendMessage("   " + ChatColor.DARK_AQUA + "1st) " + ChatColor.WHITE + "Time: " + ChatColor.AQUA + Parkour.plugin.Time((long) i) + ChatColor.WHITE + " Deaths: " + ChatColor.AQUA + j + ChatColor.WHITE + " Player: " + ChatColor.AQUA + s2);
player.sendMessage("   " + ChatColor.DARK_AQUA + "2nd) " + ChatColor.WHITE + "Time: " + ChatColor.AQUA + Parkour.plugin.Time((long) k) + ChatColor.WHITE + " Deaths: " + ChatColor.AQUA + l + ChatColor.WHITE + " Player: " + ChatColor.AQUA + s3);
player.sendMessage("   " + ChatColor.DARK_AQUA + "3rd) " + ChatColor.WHITE + "Time: " + ChatColor.AQUA + Parkour.plugin.Time((long) worlds) + ChatColor.WHITE + " Deaths: " + ChatColor.AQUA + k1 + ChatColor.WHITE + " Player: " + ChatColor.AQUA + s4);
}}
        break;            
        
         
         case "prize":
List list1 = Files.courseData.getStringList("Courses");
if (args.length <= 1) {
player.sendMessage(Parkour.Prefix + "Invalid syntax: /pa prize (course)");
} else if (!list1.contains(args[1])) {
player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.NoExist").replace("%COURSE%", args[1])));
} else if (args.length <= 3) {
player.sendMessage(Parkour.Prefix + "Invalid syntax: /pa prize (course) [[(id) (amount)] | command [command]]");
} else if (             !player.getName().equals(Files.courseData.getString(args[1] + ".Creator")) && !player.hasPermission("Parkour.Admin") && !player.hasPermission("Parkour.*")) {
player.sendMessage(Parkour.Prefix + args[1] + " is not your course!");
} else if (!args[2].equalsIgnoreCase("command")) {
if (isInt(args[2]) && isInt(args[3])) {
i = Integer.parseInt(args[2]);
j = Integer.parseInt(args[3]);
Files.courseData.set(args[1] + ".Prize.ID", i);
Files.courseData.set(args[1] + ".Prize.Amount", j);
Parkour.saveCourses();
player.sendMessage(Parkour.Prefix + "Prize set to " + i + " (" + j + ")");
} else {player.sendMessage(Parkour.Prefix + "Invalid syntax: /pa prize (course) (id) (amount)");}
} else {StringBuilder stringbuilder = new StringBuilder();
for (j = 3; j < args.length; ++j) {
stringbuilder.append(args[j]);
stringbuilder.append(" ");}
String s1 = stringbuilder.toString().replaceAll("/", "");
Files.courseData.set(args[1] + ".Prize.CMD", s1);
Parkour.saveCourses();
player.sendMessage(Parkour.Prefix + "Finish command set to " + stringbuilder.toString());}
        break;            
        
         
         case "finish":
list1 = Files.courseData.getStringList("Courses");
if (args.length == 1) {player.sendMessage(Parkour.Prefix + "Invalid syntax: /pa finish (course)");
} else if (!list1.contains(args[1])) {
player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.NoExist").replace("%COURSE%", args[1])));
} else if (!Files.courseData.contains(args[1] + ".Finished")) {
    if (!player.getName().equals(Files.courseData.getString(args[1] + ".Creator")) && !player.hasPermission("Parkour.Admin") && !player.hasPermission("Parkour.*")) {
            player.sendMessage(Parkour.Prefix + args[1] + " is not your course!");
    } else {    
            Files.courseData.set(args[1] + ".Finished", true);Parkour.saveCourses();
            player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Parkour.Finish").replace("%COURSE%", args[1])));}
} else {player.sendMessage(Parkour.Prefix + args[1] + " has already been set to finished!");}
        break;            
        
         
         case "resetcourse":
list1 = Files.courseData.getStringList("Courses");
if (args.length == 1) {
player.sendMessage(Parkour.Prefix + "Invalid syntax: /pa resetcourse (course)");
} else if (!list1.contains(args[1])) {
player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.NoExist").replace("%COURSE%", args[1])));
} else if (             !player.getName().equals(Files.courseData.getString(args[1] + ".Creator")) && !player.hasPermission("Parkour.Admin") && !player.hasPermission("Parkour.*")) {
player.sendMessage(Parkour.Prefix + args[1] + " is not your course!");
} else {
Files.courseData.set(args[1] + ".Views", 0);
Files.courseData.set(args[1] + ".Lobby", (Object) null);
Files.courseData.set(args[1] + ".Finished", false);
Files.courseData.set(args[1] + ".XP", 0);
Files.courseData.set(args[1] + ".MinimumLevel", 0);
Files.courseData.set(args[1] + ".Views", 0);
Files.leaderData.set(args[1] + ".1.time", 0);
Files.leaderData.set(args[1] + ".1.deaths", 999);
Files.leaderData.set(args[1] + ".1.player", "N/A");
Files.leaderData.set(args[1] + ".2.time", 0);
Files.leaderData.set(args[1] + ".2.deaths", 999);
Files.leaderData.set(args[1] + ".2.player", "N/A");
Files.leaderData.set(args[1] + ".3.time", 0);
Files.leaderData.set(args[1] + ".3.deaths", 999);
Files.leaderData.set(args[1] + ".3.player", "N/A");
Parkour.plugin.saveLeaders();
player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Parkour.Reset").replace("%COURSE%", args[1])));}
        break;            
        
         
         case "reset":
list1 = Files.courseData.getStringList("Courses");
if (args.length == 1) {
player.sendMessage(Parkour.Prefix + "Invalid syntax: /pa reset (course)");
} else if (!list1.contains(args[1])) {
player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.NoExist").replace("%COURSE%", args[1])));
} else if (             !player.getName().equals(Files.courseData.getString(args[1] + ".Creator")) && !player.hasPermission("Parkour.Admin") && !player.hasPermission("Parkour.*")) {
player.sendMessage(Parkour.Prefix + args[1] + " is not your course!");
} else {
Files.leaderData.set(args[1] + ".1.time", 0);
Files.leaderData.set(args[1] + ".1.deaths", 999);
Files.leaderData.set(args[1] + ".1.player", "N/A");
Files.leaderData.set(args[1] + ".2.time", 0);
Files.leaderData.set(args[1] + ".2.deaths", 999);
Files.leaderData.set(args[1] + ".2.player", "N/A");
Files.leaderData.set(args[1] + ".3.time", 0);
Files.leaderData.set(args[1] + ".3.deaths", 999);
Files.leaderData.set(args[1] + ".3.player", "N/A");
Parkour.plugin.saveLeaders();
player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Parkour.Reset").replace("%COURSE%", args[1])));}
        break;            
        
      
     
             
             
case "delete":
 list1 = Files.courseData.getStringList("Courses");
   if (args.length == 1) {
          player.sendMessage(Parkour.Prefix + "Invalid syntax: /pa delete (course)");
                 } else if (!list1.contains(args[1])) {
           player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.NoExist").replace("%COURSE%", args[1])));
               } else if (!player.getName().equals(Files.courseData.getString(args[1] + ".Creator")) && !player.hasPermission("Parkour.Admin") && !player.hasPermission("Parkour.*")) {
             player.sendMessage(Parkour.Prefix + args[1] + " is not your course!");
                    } //else {
                //   player.sendMessage(Parkour.Prefix + "You are about to delete " + ChatColor.AQUA + args[1] + ChatColor.WHITE + "...");
                   //   player.sendMessage("Please enter \'/pa yes\' to confirm!");
                  // if (!Parkour.plugin.questioned.contains(player.getName())) {
                         //   Parkour.plugin.questioned.add(player.getName());
                        // Parkour.plugin.qcommand.put(player.getName(), "delete");
                      //   Parkour.plugin.qargument.put(player.getName(), args[1]);
                 //   }
                     // }
        break;            
        
     
         /*    
             
         case "kit":
                                                             if (!player.hasPermission("Parkour.Basic.Kit") && !player.hasPermission("Parkour.Basic.*") && !player.hasPermission("Parkour.*")) {
                                                                player.sendMessage("NoPermission");
                                                            } else {
                                                                player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Other.Kit")));
                                                                player.getInventory().clear();
                                                                ItemStack itemstack = new ItemStack(Material.getMaterial(Parkour.config.getInt("Block.Run.ID")));
                                                                ItemMeta itemmeta = itemstack.getItemMeta();

                                                                itemmeta.setDisplayName((Files.stringData.getString("Kit.Speed")));
                                                                itemstack.setItemMeta(itemmeta);
                                                                player.getInventory().addItem(new ItemStack[] { itemstack});
                                                                ItemStack itemstack1 = new ItemStack(Material.getMaterial(Parkour.config.getInt("Block.Climb.ID")));
                                                                ItemMeta itemmeta1 = itemstack1.getItemMeta();

                                                                itemmeta1.setDisplayName((Files.stringData.getString("Kit.Climb")));
                                                                itemstack1.setItemMeta(itemmeta1);
                                                                player.getInventory().addItem(new ItemStack[] { itemstack1});
                                                                ItemStack itemstack2 = new ItemStack(Material.getMaterial(Parkour.config.getInt("Block.Launch.ID")));
                                                                ItemMeta itemmeta2 = itemstack2.getItemMeta();

                                                                itemmeta2.setDisplayName((Files.stringData.getString("Kit.Launch")));
                                                                itemstack2.setItemMeta(itemmeta2);
                                                                player.getInventory().addItem(new ItemStack[] { itemstack2});
                                                                ItemStack itemstack3 = new ItemStack(Material.getMaterial(Parkour.config.getInt("Block.Finish.ID")));
                                                                ItemMeta itemmeta3 = itemstack3.getItemMeta();

                                                                itemmeta3.setDisplayName((Files.stringData.getString("Kit.Finish")));
                                                                itemstack3.setItemMeta(itemmeta3);
                                                                player.getInventory().addItem(new ItemStack[] { itemstack3});
                                                                ItemStack itemstack4 = new ItemStack(Material.getMaterial(Parkour.config.getInt("Block.Repulse.ID")));
                                                                ItemMeta itemmeta4 = itemstack4.getItemMeta();

                                                                itemmeta4.setDisplayName((Files.stringData.getString("Kit.Repulse")));
                                                                itemstack4.setItemMeta(itemmeta4);
                                                                player.getInventory().addItem(new ItemStack[] { itemstack4});
                                                                ItemStack itemstack5 = new ItemStack(Material.getMaterial(Parkour.config.getInt("Block.NoRun.ID")));
                                                                ItemMeta itemmeta5 = itemstack5.getItemMeta();

                                                                itemmeta5.setDisplayName((Files.stringData.getString("Kit.NoRun")));
                                                                itemstack5.setItemMeta(itemmeta5);
                                                                player.getInventory().addItem(new ItemStack[] { itemstack5});
                                                                ItemStack itemstack6 = new ItemStack(Material.SPONGE);
                                                                ItemMeta m7 = itemstack6.getItemMeta();

                                                                m7.setDisplayName((Files.stringData.getString("Kit.NoFall")));
                                                                itemstack6.setItemMeta(m7);
                                                                player.getInventory().addItem(new ItemStack[] { itemstack6});
                                                                ItemStack s9 = new ItemStack(Material.getMaterial(Parkour.config.getInt("Block.NoPotion.ID")));
                                                                ItemMeta m9 = s9.getItemMeta();

                                                                m9.setDisplayName((Files.stringData.getString("Kit.NoPotion")));
                                                                s9.setItemMeta(m9);
                                                                player.getInventory().addItem(new ItemStack[] { s9});
                                                                ItemStack s5 = new ItemStack(Material.SIGN);
                                                                ItemMeta m5 = s5.getItemMeta();

                                                                m5.setDisplayName((Files.stringData.getString("Kit.Sign")));
                                                                s5.setItemMeta(m5);
                                                                player.getInventory().addItem(new ItemStack[] { s5});

                                                                for ( i = 0; i < Parkour.config.getIntegerList("Block.Death.ID").size(); ++i) {
                                                                    int db = (Parkour.config.getIntegerList("Block.Death.ID").get(i));

                                                                    try {
                                                                        int ex = player.getInventory().firstEmpty();
                                                                        ItemStack iss = new ItemStack(Material.getMaterial(db));
                                                                        ItemMeta mm = iss.getItemMeta();

                                                                        mm.setDisplayName((Files.stringData.getString("Kit.Death")));
                                                                        iss.setItemMeta(mm);
                                                                        player.getInventory().setItem(ex, iss);
                                                                        player.updateInventory();
                                                                    } catch (Exception exception15) {
                                                                        player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.Something").replaceAll("%ERROR%", exception15.getMessage())));
                                                                    }
                                                                }

                                                                player.updateInventory();
                                                                player.sendMessage("Deathblock(s): " + Parkour.config.getIntegerList("Block.Death.ID"));
                                                            }
        break;            
        
             */
             
             
         
             
         case "cmds":
if (args.length != 1) {
if (args.length != 2) {
if (args.length >= 3) {
player.sendMessage(Parkour.Prefix + "Invalid syntax - /pa cmds [1-3]");
}
} else if (!args[1].equalsIgnoreCase("1")) {
if (!args[1].equalsIgnoreCase("2")) {
if (!args[1].equalsIgnoreCase("3")) {
player.sendMessage(Parkour.Prefix + "Page doesn\'t exist!");
} else {
player.sendMessage("-=- " + Parkour.Prefix + "-=-");
player.sendMessage(ChatColor.GRAY + "Please remember these are setting Parkour levels and XP, not minecrafts.");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "givexp " + ChatColor.YELLOW + "(amount) [player]" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Give yourself / player XP");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "setxp " + ChatColor.YELLOW + "(amount) [player]" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Set yours or others XP");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "setlevel " + ChatColor.YELLOW + "(amount) [player]" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Set yours or others Level");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "rewardxp " + ChatColor.YELLOW + "(course) (amount)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Set the XP reward for a course");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "rewardlevel " + ChatColor.YELLOW + "(course) (level)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Set the level reward for a course");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "rewardrank " + ChatColor.YELLOW + "(course) (rank)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Set the rank reward for a course");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "setminlevel " + ChatColor.YELLOW + "(course) (level)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Set a minimum level requirement for a course");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "setmaxdeath " + ChatColor.YELLOW + "(course) (amount)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Set a maximum death for a course");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "spectate " + ChatColor.YELLOW + "(player) [-alert]" + ChatColor.BLACK + " : " + ChatColor.RED + "BETA " + ChatColor.WHITE + "Spectate the Player");
player.sendMessage(ChatColor.DARK_AQUA + "Remember: " + ChatColor.AQUA + "()" + ChatColor.WHITE + " means required" + ChatColor.YELLOW + " : " + ChatColor.AQUA + "[]" + ChatColor.WHITE + " means optional.");
player.sendMessage("-=- Page " + ChatColor.AQUA + "3" + ChatColor.WHITE + " / " + ChatColor.DARK_AQUA + "3" + ChatColor.WHITE + " -=-");
}
} else {
player.sendMessage("-=- " + Parkour.Prefix + "-=-");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "finish " + ChatColor.YELLOW + "(course)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Set the course to finished");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "add " + ChatColor.YELLOW + "(command/death) (argument)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Add command/deathid");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "setstart" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Set the selected courses new start point");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "setcreator " + ChatColor.YELLOW + "(course) (name)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Set creator of a course");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "link " + ChatColor.YELLOW + "(course) (lobby)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Link a course with a custom lobby");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "reset " + ChatColor.YELLOW + "(course)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Reset the course leaderboards");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "resetall " + ChatColor.BLACK + " : " + ChatColor.WHITE + "Reset all course\'s leaderboards");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "resetcourse " + ChatColor.YELLOW + "(course)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Reset all data about the course");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "resetplayer " + ChatColor.YELLOW + "(player)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Reset all data about the player");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "quiet" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Disable death messages");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "econ | economy" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Display Economy information");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "about | ver | version" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Display plugin about page");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "request | bug" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Request a feature for Parkour");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "help | contact" + ChatColor.BLACK + " : " + ChatColor.WHITE + "To get help or contact me");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "tutorial | tut" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Link to the official tutorial page");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "mysql " + ChatColor.YELLOW + "[connect/status/info/recreate]" + ChatColor.BLACK + " : " + ChatColor.WHITE + "MySQL commands");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "reload" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Reload Parkour config");
player.sendMessage("-=- Page " + ChatColor.AQUA + "2" + ChatColor.WHITE + " / " + ChatColor.DARK_AQUA + "3" + ChatColor.WHITE + " -=-");
}
} else {
player.sendMessage("-=- " + Parkour.Prefix + "-=-");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "join " + ChatColor.YELLOW + "(course)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Join the course");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "leave " + ChatColor.YELLOW + "[player]" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Leave the course");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "create " + ChatColor.YELLOW + "(course)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Create and select a course");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "checkpoint " + ChatColor.YELLOW + "[point]" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Create a checkpoint");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "select " + ChatColor.YELLOW + "(course)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Edit a course, " + ChatColor.GRAY + "done" + ChatColor.WHITE + " to stop");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "delete " + ChatColor.YELLOW + "(course)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Delete the course");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "lobby" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Teleport to the Lobby");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "invite " + ChatColor.YELLOW + "(player)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Invite a player to the course");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "kit" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Fill hotbar with Parkour blocks");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "course " + ChatColor.YELLOW + "(course)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Display the course information");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "info " + ChatColor.YELLOW + "[Player]" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Display your information");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "test " + ChatColor.YELLOW + "(on / off)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Toggle Test mode");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "tp " + ChatColor.YELLOW + "(course)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Teleport to the course");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "tpc " + ChatColor.YELLOW + "(course) " + ChatColor.BLUE + "(number)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Teleport to a specific checkpoint");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "prize " + ChatColor.YELLOW + "(course) " + ChatColor.BLUE + "(id) (amount)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Set a custom prize");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "list " + ChatColor.YELLOW + "(players/courses)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "List all Parkour players/courses");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "cmds [1/3]" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Display the Parkour commands menu");
player.sendMessage("-=- Page " + ChatColor.AQUA + "1" + ChatColor.WHITE + " / " + ChatColor.DARK_AQUA + "3" + ChatColor.WHITE + " -=-");
}
} else {
player.sendMessage("-=- " + Parkour.Prefix + "-=-");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "join " + ChatColor.YELLOW + "(course)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Join the course");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "leave " + ChatColor.YELLOW + "[player]" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Leave the course");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "create " + ChatColor.YELLOW + "(course)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Create and select a course");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "checkpoint " + ChatColor.YELLOW + "[point]" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Create a checkpoint");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "select " + ChatColor.YELLOW + "(course)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Edit a course, " + ChatColor.GRAY + "done" + ChatColor.WHITE + " to stop");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "delete " + ChatColor.YELLOW + "(course)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Delete the course");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "lobby" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Teleport to the Lobby");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "invite " + ChatColor.YELLOW + "(player)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Invite a player to the course");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "kit" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Fill hotbar with Parkour blocks");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "course " + ChatColor.YELLOW + "(course)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Display the course information");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "info " + ChatColor.YELLOW + "[Player]" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Display your information");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "test " + ChatColor.YELLOW + "(on / off)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Toggle Test mode");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "tp " + ChatColor.YELLOW + "(course)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Teleport to the course");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "tpc " + ChatColor.YELLOW + "(course) " + ChatColor.BLUE + "(number)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Teleport to a specific checkpoint");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "prize " + ChatColor.YELLOW + "(course) " + ChatColor.BLUE + "(id) (amount)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Set a custom prize");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "list " + ChatColor.YELLOW + "(players/courses)" + ChatColor.BLACK + " : " + ChatColor.WHITE + "List all Parkour players/courses");
player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + "cmds [1/3]" + ChatColor.BLACK + " : " + ChatColor.WHITE + "Display the Parkour commands menu");
player.sendMessage("-=- Page " + ChatColor.AQUA + "1" + ChatColor.WHITE + " / " + ChatColor.DARK_AQUA + "3" + ChatColor.WHITE + " -=-");
}             
             break;
 
       
             
	
             
	default:
		player.sendMessage(Parkour.Prefix + "Неизвестная команда");
		player.sendMessage( "/pa cmds : Список команд");
			break;   
}


}
    
        return true;
            
    }
    
    
    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException numberformatexception) {
            return false;
        }
    }    
  
    
        public boolean isNumber(String args) {
        try {
            Integer.parseInt(args);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
        
        
    
}