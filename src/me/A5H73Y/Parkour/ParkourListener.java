package me.A5H73Y.Parkour;

import bsign.spigot.Bsign;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.FluidLevelChangeEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Enums.UniversalArenaState;
import ru.komiss77.Events.BungeeDataRecieved;
import ru.komiss77.Events.FriendTeleportEvent;
import static ru.komiss77.Listener.ServerListener.block_nether_portal;
import static ru.komiss77.Listener.ServerListener.disable_blockspread;

public class ParkourListener implements Listener {

    public String perm;

    public ParkourListener(Parkour instance) {
        this.perm = "";
    }

    
@EventHandler
    public void FriendTeleport(FriendTeleportEvent e) {
        e.Set_canceled(true, "§cПаркуры, однако!");
    }
    
    
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
                event.setJoinMessage(null);
        Player p = event.getPlayer();
        p.setGameMode(GameMode.SURVIVAL);
        p.setFlying(false);
        p.setAllowFlight(false);
        p.setFoodLevel(20);

        if (!p.hasPlayedBefore())  event.getPlayer().sendMessage((Files.stringData.getString("Event.Join")));
        

        final String nik = p.getName();
        
         File usersFile = new File(Files.UserDataFolder, nik+".yml");
        

         
         if (!usersFile.exists()) {                  //регистрация нового игрока
            Files.CreateUserFile(p);
                Parkour.jumpTotal.put(nik, 0);
                Parkour.fallTotal.put(nik, 0);
                Parkour.jumpCurrent.put(nik, 0);
                Parkour.fallCurrent.put(nik, 0);
                Parkour.timeTotal.put(nik, 0);
                Parkour.timeCurrent.put(nik, 0);
            Score.InitScoreboard(p, "Не начинал");
            Parkour.ToLobby(p, "");}
        else {
            Files.loadUsers(p);
                //возвращаем поля
            Parkour.jumpTotal.put(nik, Files.getIntVar (p, ".JumpTotal"));                                                 
            Parkour.fallTotal.put(nik, Files.getIntVar (p, ".FallTotal"));                                                 
            Parkour.timeTotal.put(nik, Files.getIntVar (p, ".TimeTotal"));                                                 
            
            String course = Files.getStringVar(p, ".Selected");          //если нет активного,в лобби
            
                 if ("".equals(course) | "".equals(course) | "-".equals(course)) {
                    p.sendMessage(Parkour.Prefix + ChatColor.RED+"Не выбран маршрут! В лобби!");
                        Score.InitScoreboard(p, "Не выбрана");
                    Parkour.ToLobby(p, "");
                   }
                 else if    (Files.courseData.getInt(course + ".Points") == 0 ) {
          p.sendMessage(Parkour.Prefix +  ("Пока Вас не было, трассу удалили :("));
         Parkour.CourseLeave(p, course);  
        } 
                 else Parkour.Continue (p, course);
            
        }
                       /* new BukkitRunnable() {
                             @Override 
                             public void run()  {
////////////////////////////////////////////////////////////////////////////////
                                Bsign.Send_data(
                                          "",
                                          "§b§lП А Р К У Р Ы",
                                          "§1На сервере:",
                                          "§1§l"+Bukkit.getOnlinePlayers().size(),
                                          "§a§lРаботает",
                                          13
                                  );
////////////////////////////////////////////////////////////////////////////////
                        }}.runTaskLater(Parkour.instance, 5L);*/
        
    }


@EventHandler(priority = EventPriority.MONITOR) 
    public void onBungeeDataRecieved(BungeeDataRecieved e) {
           ApiOstrov.sendArenaData(
                    "any",
                    "§b§lП А Р К У Р Ы",
                    "§1На сервере:",
                    "§1§l"+Bukkit.getOnlinePlayers().size(),
                    "§a§lРаботает",
                    "",
                    Bukkit.getOnlinePlayers().size(),
                    UniversalArenaState.ОЖИДАНИЕ,
                    false,
                    true
            );
    }


    

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
         if (e.getFrom().getBlockX()==e.getTo().getBlockX() && e.getFrom().getBlockY()==e.getTo().getBlockY() && e.getFrom().getBlockZ()==e.getTo().getBlockZ()  ) return;
            
             if (e.getPlayer().getLocation().getY()<0 ) {
             
               //  event.setCancelled(true); //фикс двойного падения
                 e.getPlayer().teleport(e.getPlayer().getLocation().clone().add(0.0D, 10.0D, 0.0D));//фикс двойного падения
                 
                 if ( Parkour.inProgress.contains(e.getPlayer().getName())) {  //если в игре, отрабатываем падение
                       
                          String arenain = Files.getStringVar(e.getPlayer(), ".Selected");
                 
                                if (!"".equals(arenain) && !"-".equals(arenain) && e.getPlayer().getLocation().getWorld().getName().equals(Files.courseData.getString(arenain + ".World"))) {
                                Parkour.Fall(e.getPlayer());
                                } else {
                            e.getPlayer().sendMessage(Parkour.Prefix + "Авария! Мир не соответствует проходимому паркуру!");
                            Parkour.CourseLeave(e.getPlayer(),"");
                            Parkour.ToLobby(e.getPlayer(), "");
                                } 
                                   
                                
                       }  else  Parkour.ToLobby(e.getPlayer(), ""); //если нет, просто в лобби
                       
              } else if (e.getFrom().getY() + 0.419 < e.getTo().getY()) {
                    if (Parkour.inProgress.contains(e.getPlayer().getName()) && !e.getPlayer().isFlying()) {   
                        
        String course =  Files.getStringVar(e.getPlayer(), ".Selected");               
        int cps = Files.courseData.getInt(course + ".Points");
        int wiev = Files.courseData.getInt(course + ".Views");
        int complete = Files.courseData.getInt(course + ".Completed");
        int playerpoint = Files.getIntVar (e.getPlayer(),  ".Progress." + course + ".Point"); 
 
        int jumpTotal = (int) Parkour.jumpTotal.get(e.getPlayer().getName());
        Parkour.jumpTotal.put ( e.getPlayer().getName(), jumpTotal+1 );
        int jumpCurrent = (int) Parkour.jumpCurrent.get(e.getPlayer().getName());
        Parkour.jumpCurrent.put ( e.getPlayer().getName(), jumpCurrent+1 );    
        Score.UpdateScoreboard(e.getPlayer(), course, playerpoint, cps-playerpoint, wiev, complete);  
                       // Score.AddJump (player);
                    }
               }
             
             
    }

  
    
    
    
    
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void itemUse(PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL || e.getPlayer().isOp()) return;
        if (e.getPlayer().getInventory().getItemInMainHand()!=null && e.getPlayer().getInventory().getItemInMainHand().getType()!=Material.AIR && !e.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) {
//System.out.println("me.A5H73Y.Parkour.ParkourListener.itemUse()"+e.getPlayer().getInventory().getItemInMainHand());        
            e.setCancelled(true);
            e.getPlayer().getInventory().setItemInMainHand(null);
            e.getPlayer().updateInventory();
        }
        if (e.getPlayer().getInventory().getItemInOffHand()!=null && e.getPlayer().getInventory().getItemInOffHand().getType()!=Material.AIR && !e.getPlayer().getInventory().getItemInOffHand().hasItemMeta()) {
//System.out.println("me.A5H73Y.Parkour.ParkourListener.itemUse()"+e.getPlayer().getInventory().getItemInOffHand());        
            e.setCancelled(true);
            e.getPlayer().getInventory().setItemInOffHand(null);
            e.getPlayer().updateInventory();
        }
    }
    
    
    
    
    
    
    
    
    
    @EventHandler (ignoreCancelled = true)
    public void PressurePlate(PlayerInteractEvent e) {
            if (e.getAction() != Action.PHYSICAL ) return;
            Player player = e.getPlayer();
            String arenain = Files.getStringVar(player, ".Selected");
            
            if (!Parkour.inProgress.contains(e.getPlayer().getName()) && !"-".equals(arenain)) {  //если не в игре, сообщ о тесте
                player.sendMessage (ChatColor.AQUA+"Тестовый режим. Для начала прохождения используйте табличку."); 
                ApiOstrov.sendTitle(player, "&7Тестовый режим.","&7Для старта используйте табличку.." );
                return;                        
            }
            Block plate = e.getClickedBlock(); 
            if (plate == null)  return;
            
            e.setCancelled(true);
                  //берём название арены
                if ("".equals(arenain) | "-".equals(arenain)) return;   //проверка арены
                
                int previos = Files.getIntVar (player, ".Progress." + arenain + ".Point"); //берём предыдущую точку из файла игрока
                int currentnum = previos + 1; //по идее,это текущая точка
                int pointcount = Files.courseData.getInt(arenain + "." + "Points"); //сколько всего точек на трассе
                 
                     Block down = plate.getRelative(BlockFace.DOWN);  //берём блок под плитой
                    if (down == null) return;

                    
                    if ( plate.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE && pointcount > currentnum) {  //если на финише и разница между всего точек и предпоследней  больше 1
                                e.setCancelled(true);                              
                                player.sendMessage(Parkour.Prefix + ("Пропущена контрольная точка. Вы читер???"));
                                Parkour.CourseLeave(player, arenain);
                                player.getWorld().strikeLightningEffect(player.getLocation());
                                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 20.0F, 20.0F); 
                    } 
                    else if (Files.checkData.contains(arenain + "." + currentnum)) { //если есть координаты текущей точки
                            double X = (double) down.getLocation().getBlockX();  //берём координаты блока под плитой
                            double Y = (double) down.getLocation().getBlockY();
                            double Z = (double) down.getLocation().getBlockZ();

                            if (X == Files.checkData.getDouble(arenain + "." + currentnum + ".X")        //если коорд совпадают- мы на валидной точке
                                    && Y == Files.checkData.getDouble(arenain + "." + currentnum + ".Y") 
                                    && Z == Files.checkData.getDouble(arenain + "." + currentnum + ".Z")) {
                                
                                
                                
                                
                                   if ( plate.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE && pointcount == currentnum) {  //если на финише //если честный финиш!
                                       try {
                                            Parkour.CourseFinish(player, arenain);
                                            } catch (Exception exception) {
                                             player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Что-то пошло не так. Ошибка: %ERROR%").replaceAll("%ERROR%", exception.getMessage())));
                                            Parkour.ToLobby(player, "");
                                            }    
                            
                                } else if (plate.getType() == Material.STONE_PRESSURE_PLATE)  {//player.sendMessage(Parkour.Prefix + "Номер точки больше , чем их есть на этой трассе..");
                                
                                                           //если плата обычная
                               Files.setIntVar(player,  ".Progress." + arenain + ".Point", currentnum); //пишем точку игроку
                                int wiev = Files.courseData.getInt(arenain + ".Views");
                                int complete = Files.courseData.getInt(arenain + ".Completed");
                                Score.UpdateScoreboard(player, arenain, currentnum, pointcount, wiev, complete);                                
                                    
                                  //  Files.saveUsers(player);  
                                    if (currentnum == pointcount-1) {  //если предпоследняя
                                    player.sendMessage(Parkour.Prefix + ChatColor.DARK_GREEN + "Пройдены все точки! Остался финиш!");
                                    ApiOstrov.sendTitle(player, "&aПройдены все точки!", "&aОстался финиш.." );
                                    
                                }  else {
              player.sendMessage(Parkour.Prefix + ChatColor.DARK_GREEN+"Пройдена точка " + currentnum + " из " + pointcount+".");
              ApiOstrov.sendTitle(player, "&aПройдено " + currentnum + " из "+ pointcount + " !", "");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM , 1.0F, 1.0F);        
                                    }                              
                      }
                               
 

                            } //else player.sendMessage(Parkour.Prefix + "Ожидается другая точка! Вы идёте назад??");
                        } else player.sendMessage(Parkour.Prefix + "Точка не найдена!");
    }
                    
                    
                    
                    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @EventHandler
    public void signHandler(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();

            //if (clickedBlock.getType() == Material.SIGN || clickedBlock.getType() == Material.SIGN_POST || clickedBlock.getType() == Material.WALL_SIGN) {
            if (ApiOstrov.isSign(clickedBlock.getType())) {
                Sign sign = (Sign) clickedBlock.getState();
                String[] lines = sign.getLines();
                int loc;

                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
               //     if (Parkour.getConfig().getBoolean("Other.Use.SignProtection") && lines[1].contains("Пытались:") && lines[2].contains("Точек:")) {
                        if (event.getPlayer().hasPermission("Parkour.Admin")) {
                            for (loc = 1; loc < Files.leaderData.getInt("SignNumber") + 1; ++loc) {
                                try {
                                    if (Files.leaderData.getInt("Signs." + loc + ".X") == sign.getX() && Files.leaderData.getInt("Signs." + loc + ".Y") == sign.getY() && Files.leaderData.getInt("Signs." + loc + ".Z") == sign.getZ()) {
                                        player.sendMessage(Parkour.Prefix + "Stat sign removed!");
                                        Files.leaderData.set("Signs." + loc, "");
                                        Parkour.saveLeaders();
                                        sign.getBlock().breakNaturally();
                                        break;
                                    }
                                } catch (Exception exception) {
                                    player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.Something").replaceAll("%ERROR%", exception.getMessage())));
                                }
                            }
                        } else {
                            event.getPlayer().sendMessage(Parkour.Prefix + "This Sign is protected!");
                            event.setCancelled(true);
                        }
               //     }

                 //   if (lines[0].contains(ChatColor.AQUA + "Parkour") && Parkour.getConfig().getBoolean("Other.Use.SignProtection")) {
                        if (event.getPlayer().hasPermission("Parkour.Admin")) {
                            sign.getBlock().breakNaturally();
                            event.getPlayer().sendMessage(Parkour.Prefix + "Sign Removed!");
                        } else {
                            event.getPlayer().sendMessage(Parkour.Prefix + "This Sign is protected!");
                            event.setCancelled(true);
                        }
                //    }
                }

                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                   // if (Parkour.getConfig().getBoolean("Other.Use.ForceParkourSigns") && Parkour.inProgress.contains(player.getName()) && !lines[0].contains(ChatColor.AQUA + "Parkour")) {
                    if ( !lines[0].contains("Паркур")) {
                        player.sendMessage(Parkour.Prefix + "Не паркурная табличка!");
                        event.setCancelled(true);
                        return;
                    }

             //       if (lines[0].contains( "Паркур")) {
                        if (lines[1].contains("Сохраниться")) {
                            if (lines[2].equalsIgnoreCase(Files.usersData.getString("PlayerInfo." + player.getName() + ".Selected"))) {
                                if (this.isNumber(lines[3])) {
                                    Files.usersData.set("PlayerInfo." + player.getName() + ".Point", lines[3]);
            Files.saveUsers(player);
                                    Parkour.instance.saveConfig();
                                    Integer integer = Files.courseData.getInt(lines[2] + "." + "Points");

                                    player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Event.Checkpoint")) + lines[3] + " / " + integer);
                                } else {
                                    player.sendMessage(Parkour.Prefix + "Invalid Point");
                                }
                            } else {
                                player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.NotOnCourse")));
                            }
                        } 
                        
                        else if (lines[1].contains("Трассу")) {
                            final String cur = ChatColor.stripColor(lines[2]);
                            if (Files.courseData.getStringList("Courses").contains(cur)) {

                        Files.saveUsers(player);
                      Parkour.CourseStart(player,cur);
                            } else {
                                player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.NoExist").replaceAll("%COURSE%", cur)));
                            }
                        }
                        
                        else if (lines[1].contains("спавн")) {
                            if (!lines[2].isEmpty()) {
                                Parkour.ToLobby(player, lines[2]);
                            } else {
                                Parkour.ToLobby(player, "");
                            }
                        }
                        
                        else {
                            int i;
                            String itemstack;

                            if (lines[1].contains("Статистика")) {
                                if (Files.courseData.getStringList("Courses").contains(lines[2])) {
                                    loc = Files.leaderData.getInt(lines[2] + ".1.deaths");
                                    String jumpheight = Files.leaderData.getString(lines[2] + ".1.player");
                                    int inv = Files.leaderData.getInt(lines[2] + ".2.deaths");
                                    String slot = Files.leaderData.getString(lines[2] + ".2.player");

                                    i = Files.leaderData.getInt(lines[2] + ".3.deaths");
                                    itemstack = Files.leaderData.getString(lines[2] + ".3.player");
                                    player.sendMessage("---" + ChatColor.BLACK + "[ " + ChatColor.DARK_AQUA + lines[2] + ChatColor.BLACK + " ]" + ChatColor.WHITE + "---");
                                    player.sendMessage("Пытались пройти: " + ChatColor.AQUA + Files.courseData.getInt(lines[2] + ".Views"));
                                    player.sendMessage("Контрольных точек: " + ChatColor.AQUA + Files.courseData.getInt(lines[2] + ".Points"));
                                    player.sendMessage("Автор: " + ChatColor.AQUA + Files.courseData.getString(lines[2] + ".Creator"));
                                    player.sendMessage("ТОП прошедших:");
                                    player.sendMessage("   " + ChatColor.DARK_AQUA + "1) " + ChatColor.WHITE + "Падения: " + ChatColor.AQUA + loc + ChatColor.WHITE + " Ник: " + ChatColor.AQUA + jumpheight);
                                    player.sendMessage("   " + ChatColor.DARK_AQUA + "2) " + ChatColor.WHITE + "Падения: " + ChatColor.AQUA + inv + ChatColor.WHITE + " Ник: " + ChatColor.AQUA + slot);
                                    player.sendMessage("   " + ChatColor.DARK_AQUA + "3) " + ChatColor.WHITE + "Падения: " + ChatColor.AQUA + i + ChatColor.WHITE + " Ник: " + ChatColor.AQUA + itemstack);
                                } else {
                                    player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.Unknown")));
                                }
                            } else {

                                if (lines[1].contains("Сброс")) {
                                    Parkour.CourseLeave(player,"");
                                } 
                                
                                else if (lines[1].equalsIgnoreCase("финиш")) {
                                    if (Files.courseData.getStringList("Courses").contains(lines[2])) {
                                        try {
                                            if (Files.usersData.getString("PlayerInfo." + player.getName() + ".Selected").equals(lines[2])) {
                                                if (!Files.usersData.getString("PlayerInfo." + player.getName() + ".Course").equals("TestMode")) {
                                                    Parkour.CourseFinish(player, lines[2]);
                                                }
                                            } else {
                                                player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.NotOnCourse")));
                                            }
                                        } catch (Exception exception1) {
                                            player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.NotOnCourse")));
                                        }
                                    } else {
                                        player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.Unknown")));
                                    }
                                } 
                                
                                else if (lines[1].equalsIgnoreCase("Бонус")) {
                                    if (lines[2].equalsIgnoreCase("Лечение")) {
                                        player.setHealth(player.getMaxHealth());
                                        player.setFoodLevel(20);
                                        player.sendMessage(Parkour.Prefix + "Исцелён!");
                                    } else if (lines[2].equalsIgnoreCase("прыжки")) {
                                        if (this.isNumber(lines[3])) {
                                            Location location = player.getLocation();

                                            i = Integer.parseInt(lines[3]);
                                            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 300, i));
                                            player.sendMessage(Parkour.Prefix + "Усилитель прыжка!");
                                        } else {
                                            player.sendMessage(Parkour.Prefix + "Invalid Number");
                                        }
                                    } else if (lines[2].equalsIgnoreCase("скорость")) {
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 6));
                                        player.sendMessage(Parkour.Prefix + "Speed Effect Applied!");
                                    } else if (lines[2].equalsIgnoreCase("fire")) {
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 500, 6));
                                        player.sendMessage(Parkour.Prefix + "Fire Resistance Applied!");
                                    } else if (lines[2].equalsIgnoreCase("pain")) {
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 500, 10));
                                        player.sendMessage(Parkour.Prefix + "Pain Resistance Applied!");
                                    } else if (lines[2].equalsIgnoreCase("gamemode")) {
                                        if (lines[3].equalsIgnoreCase("creative")) {
                                            if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                                                player.setGameMode(GameMode.CREATIVE);
                                                player.sendMessage(Parkour.Prefix + "Gamemode set to Creative!");
                                            }
                                        } else if (!player.getGameMode().equals(GameMode.SURVIVAL)) {
                                            player.setGameMode(GameMode.SURVIVAL);
                                            player.sendMessage(Parkour.Prefix + "Gamemode set to Survival!");
                                        }
                                    } else {
                                        player.sendMessage(Parkour.Prefix + "Unknown Effect!");
                                    }
                                }
                            }
                        }
                  //  }
                }

            }
        }
    }

    
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        //FileConfiguration config = Parkour.instance.getConfig();

        if (event.getLine(0).equalsIgnoreCase("[parkour]") || event.getLine(0).equalsIgnoreCase("[pa]")) {
            event.setLine(0, ChatColor.BLACK + "[" + ChatColor.DARK_AQUA+""+ChatColor.BOLD + "Паркур" + ChatColor.BLACK + "]");
            
            if (!event.getLine(1).equalsIgnoreCase("setpoint") && !event.getLine(1).equalsIgnoreCase("sp")) {
                
                if (!event.getLine(1).equalsIgnoreCase("join") && !event.getLine(1).equalsIgnoreCase("j")) {
                    
                    if (!event.getLine(1).equalsIgnoreCase("finish") && !event.getLine(1).equalsIgnoreCase("f")) {
                        
                        if (!event.getLine(1).equalsIgnoreCase("lobby") && !event.getLine(1).equalsIgnoreCase("l")) {
                            
                            if (!event.getLine(1).equalsIgnoreCase("store") && !event.getLine(1).equalsIgnoreCase("s")) {
                                
                                if (event.getLine(1).equalsIgnoreCase("leave")) {
                                    
                                    if (!player.hasPermission("Parkour.Sign.Leave") && !player.hasPermission("Parkour.Sign.*") && !player.hasPermission("Parkour.*")) {
                                        this.perm = "Parkour.Sign.Leave";
                                        player.sendMessage((Files.stringData.getString("NoPermission").replace("%PERMISSION%", this.perm)));
                                        event.setCancelled(true);
                                        event.getBlock().breakNaturally();
                                    } else {
                                        
                      event.setLine(1, ChatColor.DARK_RED+""+ChatColor.BOLD+"Сброс");
                      
                        if (Files.courseData.getStringList("Courses").contains(event.getLine(2))) {
                           // event.setLine(2, ChatColor.BLUE+"данных о");
                           // //event.setLine(3, ChatColor.WHITE+""+ChatColor.BOLD+Files.courseData.getStringList("Courses"));
                            player.sendMessage(Parkour.Prefix + "Finish for " + event.getLine(2) + " created!");
                                         event.setLine(2, ChatColor.BLUE+"активного");
                                        event.setLine(3, ChatColor.BLUE+"паркура.");
                        } else {
                          //  player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.Unknown")));
                          //  event.setLine(2, ChatColor.RED + "Unknown Course!");
                         //   event.setLine(3, "");
                                        event.setLine(1, ChatColor.DARK_RED+""+ChatColor.BOLD+"Сброс");
                                        event.setLine(2, ChatColor.BLUE+"активного");
                                        event.setLine(3, ChatColor.BLUE+"паркура");
                         }
          
                                   }
                                    
                                } else if (!event.getLine(1).equalsIgnoreCase("joinall") && !event.getLine(1).equalsIgnoreCase("ja")) {
                                    if (!event.getLine(1).equalsIgnoreCase("effect") && !event.getLine(1).equalsIgnoreCase("e")) {
                                        if (!event.getLine(1).equalsIgnoreCase("stats") && !event.getLine(1).equalsIgnoreCase("s")) {
                                            player.sendMessage(Parkour.Prefix + "Unknown Command");
                                            event.setLine(0, ChatColor.BLACK + "[" + ChatColor.AQUA + "Паркур" + ChatColor.BLACK + "]");
                                            event.setLine(1, ChatColor.RED + "Unknown cmd");
                                            event.setLine(2, "");
                                            event.setLine(3, "");
                                        }
                                        
                                        
                                        else if (!player.hasPermission("Parkour.Sign.Stats") && !player.hasPermission("Parkour.Sign.*") && !player.hasPermission("Parkour.*")) {
                                            this.perm = "Parkour.Sign.Stats";
                                            player.sendMessage((Files.stringData.getString("NoPermission").replace("%PERMISSION%", this.perm)));
                                            event.setCancelled(true);
                                            event.getBlock().breakNaturally();
                                        } else if (Files.courseData.getStringList("Courses").contains(event.getLine(2))) {
                                            
                                            if (Parkour.instance.getConfig().getBoolean("Other.Use.OldStatsSigns")) {
                                                event.setLine(1, ChatColor.BLUE+""+ChatColor.BOLD+ "Статистика");
                                                event.setLine(3, "");
                                                player.sendMessage(Parkour.Prefix + "Stats sign for " + event.getLine(2) + " created!");
                                            } else {
                                                
                                                event.setLine(3, "");
                                                player.sendMessage(Parkour.Prefix + "Stats sign for " + event.getLine(2) + " created!");
                                                Location loc = event.getBlock().getLocation();
                                                int x = (int) loc.getX();
                                                int y = (int) loc.getY();
                                                int z = (int) loc.getZ();
                                                String world = event.getBlock().getWorld().getName();
                                                int signnumber = Files.leaderData.getInt("SignNumber") + 1;

                                                Files.leaderData.set("Signs." + signnumber + ".X", x);
                                                Files.leaderData.set("Signs." + signnumber + ".Y", y);
                                                Files.leaderData.set("Signs." + signnumber + ".Z", z);
                                                Files.leaderData.set("Signs." + signnumber + ".World", world);
                                                Files.leaderData.set("Signs." + signnumber + ".Course", event.getLine(2));
                                                Files.leaderData.set("SignNumber", signnumber);
                                                Parkour.saveLeaders();
                                            }
                                        } else {
                                            player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.Unknown")));
                                            event.setLine(2, ChatColor.RED + "Unknown Course!");
                                            event.setLine(3, "");
                                        }
                                    } else if (!player.hasPermission("Parkour.Sign.Effects") && !player.hasPermission("Parkour.Sign.*") && !player.hasPermission("Parkour.*")) {
                                        this.perm = "Parkour.Sign.Effects";
                                        player.sendMessage((Files.stringData.getString("NoPermission").replace("%PERMISSION%", this.perm)));
                                        event.setCancelled(true);
                                        event.getBlock().breakNaturally();
                                    } else {
                                        event.setLine(1, "Бонус");
                                        if (event.getLine(2).equalsIgnoreCase("heal")) {
                                            event.setLine(2, "Лечение");
                                            event.setLine(3, "-----");
                                            player.sendMessage(Parkour.Prefix + "Created Heal sign");
                                        } else if (event.getLine(2).equalsIgnoreCase("jump")) {
                                            event.setLine(2, "Прыжки");
                                            if (event.getLine(3).isEmpty()) {
                                                player.sendMessage(Parkour.Prefix + "Invalid Jump Height");
                                                event.setLine(3, ChatColor.RED + "Invalid Number");
                                            } else if (this.isNumber(event.getLine(3))) {
                                                player.sendMessage(Parkour.Prefix + "Created Jump sign");
                                            } else {
                                                player.sendMessage(Parkour.Prefix + "Invalid Jump Height");
                                                event.setLine(3, ChatColor.RED + "Invalid Number");
                                            }
                                        } else if (event.getLine(2).equalsIgnoreCase("speed")) {
                                            event.setLine(2, "Скорость");
                                            event.setLine(3, "-----");
                                            player.sendMessage(Parkour.Prefix + "Created Speed sign");
                                        } else if (event.getLine(2).equalsIgnoreCase("pain")) {
                                            event.setLine(2, "Pain");
                                            event.setLine(3, "-----");
                                            player.sendMessage(Parkour.Prefix + "Created Pain Resistance sign");
                                        } else if (event.getLine(2).equalsIgnoreCase("fire")) {
                                            event.setLine(2, "Fire");
                                            event.setLine(3, "-----");
                                            player.sendMessage(Parkour.Prefix + "Created Fire Resistance sign");
                                        } else if (event.getLine(2).equalsIgnoreCase("gamemode")) {
                                            event.setLine(2, "Gamemode");
                                            if (!event.getLine(3).equalsIgnoreCase("creative") && !event.getLine(3).equalsIgnoreCase("c")) {
                                                event.setLine(3, "Survival");
                                            } else {
                                                event.setLine(3, "Creative");
                                            }
                                        } else {
                                            event.setLine(2, "Unknown Effect");
                                            event.setLine(3, "");
                                            player.sendMessage(Parkour.Prefix + "Unknown Effect");
                                        }
                                    }
                                } else if (!player.hasPermission("Parkour.Admin") && !player.hasPermission("Parkour.*")) {
                                    this.perm = "Parkour.Admin";
                                    player.sendMessage((Files.stringData.getString("NoPermission").replace("%PERMISSION%", this.perm)));
                                    event.setCancelled(true);
                                    event.getBlock().breakNaturally();
                                } else {
                                    event.setLine(1, "JoinAll");
                                    event.setLine(2, "");
                                    event.setLine(3, "-----");
                                    player.sendMessage(Parkour.Prefix + "Join All sign created!");
                                }
                            } else if (!player.hasPermission("Parkour.Sign.Store") && !player.hasPermission("Parkour.Sign.*") && !player.hasPermission("Parkour.*")) {
                                this.perm = "Parkour.Sign.Lobby";
                                player.sendMessage((Files.stringData.getString("NoPermission").replace("%PERMISSION%", this.perm)));
                                event.setCancelled(true);
                                event.getBlock().breakNaturally();
                            } else {
                                event.setLine(1, "Магазин");
                                event.setLine(2, "");
                                event.setLine(3, "-----");
                            }
                        } else if (!player.hasPermission("Parkour.Sign.Lobby") && !player.hasPermission("Parkour.Sign.*") && !player.hasPermission("Parkour.*")) {
                            this.perm = "Parkour.Sign.Lobby";
                            player.sendMessage((Files.stringData.getString("NoPermission").replace("%PERMISSION%", this.perm)));
                            event.setCancelled(true);
                            event.getBlock().breakNaturally();
                        } else {
                            event.setLine(1, ChatColor.DARK_PURPLE+""+ChatColor.BOLD+"На спавн");
                            if (!"".equals(event.getLine(2))) {
                                if (Parkour.GetConfig().contains("Lobby." + event.getLine(2))) {
                                    if (Parkour.GetConfig().contains("Lobby." + event.getLine(2) + ".Level")) {
                                        event.setLine(3, ChatColor.RED + Parkour.GetConfig().getString("Lobby." + event.getLine(2) + ".Level"));
                                    }
                                } else {
                                    player.sendMessage(Parkour.Prefix + "That lobby does not exist!");
                                    event.setLine(2, "");
                                    event.setLine(3, "-----");
                                }
                            }
                        }
                    } else if (!player.hasPermission("Parkour.Sign.Finish") && !player.hasPermission("Parkour.Sign.*") && !player.hasPermission("Parkour.*")) {
                        this.perm = "Parkour.Sign.Finish";
                        player.sendMessage((Files.stringData.getString("NoPermission").replace("%PERMISSION%", this.perm)));
                        event.setCancelled(true);
                        event.getBlock().breakNaturally();
                        
                    } 
                } else if (!player.hasPermission("Parkour.Sign.Join") && !player.hasPermission("Parkour.Sign.*") && !player.hasPermission("Parkour.*")) {
                    this.perm = "Parkour.Sign.Join";
                    player.sendMessage((Files.stringData.getString("NoPermission").replace("%PERMISSION%", this.perm)));
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                } else {
                    
                    event.setLine(1, ChatColor.DARK_GREEN+""+ChatColor.BOLD+"На Трассу");
                    if (Files.courseData.getStringList("Courses").contains(event.getLine(2))) {
                        if (event.getLine(3).equalsIgnoreCase("cj")) {
                            event.setLine(1, event.getLine(1) + " (CJ)");
                        } else if (event.getLine(3).equalsIgnoreCase("c")) {
                            event.setLine(1, event.getLine(1) + " (C)");
                        } 
                        event.setLine(2, ""+ChatColor.DARK_PURPLE+ChatColor.BOLD+ event.getLine(2));

                        player.sendMessage(Parkour.Prefix + "Join for " + ChatColor.AQUA + event.getLine(2) + ChatColor.WHITE + " created!");
                        if (Files.courseData.contains(event.getLine(2) + ".MinimumLevel")) {
                            event.setLine(3, ""+ChatColor.DARK_RED+ChatColor.BOLD+"Уровень: " +ChatColor.RED + Files.courseData.get(event.getLine(2) + ".MinimumLevel"));
                        }
                    } else {
                        player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.Unknown")));
                        event.setLine(2, ChatColor.RED + "Unknown Course!");
                        event.setLine(3, "");
                    }
                }
            } else if (!player.hasPermission("Parkour.Sign.SetPoint") && !player.hasPermission("Parkour.Sign.*") && !player.hasPermission("Parkour.*")) {
                this.perm = "Parkour.Sign.SetPoint";
                player.sendMessage((Files.stringData.getString("NoPermission").replace("%PERMISSION%", this.perm)));
                event.setCancelled(true);
                event.getBlock().breakNaturally();
            } else {
                event.setLine(1, ChatColor.DARK_BLUE+""+ChatColor.BOLD+"Сохраниться");
                if (Files.courseData.getStringList("Courses").contains(event.getLine(2))) {
                    if (event.getLine(3).equals("")) {
                        player.sendMessage(Parkour.Prefix + "Invalid Point");
                        event.setLine(3, ChatColor.RED + "Invalid Point");
                    } else if (this.isNumber(event.getLine(3))) {
                        player.sendMessage(Parkour.Prefix + "Checkpoint " + ChatColor.DARK_AQUA + event.getLine(3) + ChatColor.WHITE + " for " + ChatColor.AQUA + event.getLine(2) + ChatColor.WHITE + " created!");
                    } else {
                        player.sendMessage(Parkour.Prefix + "Invalid Point");
                        event.setLine(3, ChatColor.RED + "Invalid Point");
                    }
                } else {
                    player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.Unknown")));
                    event.setLine(2, ChatColor.RED + "Unknown Course!");
                    event.setLine(3, "");
                }
            }
        }

    }
    
    
   /* @EventHandler(priority = EventPriority.MONITOR)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        boolean paDisabled = Parkour.instance.getConfig().getBoolean("Other.DisableCommands.OnParkour");
        boolean commandIsPa = event.getMessage().startsWith("/pa");

        if (!commandIsPa && Parkour.inProgress.contains(player.getName()) && paDisabled && !player.hasPermission("Parkour.Admin") && !player.hasPermission("Parkour.*")) {
            boolean allowed1 = false;
            boolean stillrunning1 = true;
            
            for (String word : Parkour.instance.getConfig().getStringList("Other.Commands.Whitelist")) {
                if (stillrunning1) {
                    if (event.getMessage().startsWith("/" + word)) {
                        allowed1 = true;
                        stillrunning1 = false;
                    } else {
                        allowed1 = false;
                    }
                }
            }

            if (!allowed1) {
                event.setCancelled(true);
                player.sendMessage(Parkour.Prefix + (Files.stringData.getString("Error.Command")));
            }
        }
    }*/

    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (Parkour.instance.getConfig().getBoolean("Other.Use.Prefix")) {
            String message = event.getMessage();
            Player player = event.getPlayer();
           // String rank = "Newbie";

           // if (Files.usersData.contains("PlayerInfo." + player.getName() + ".Rank")) {
              String rank =  Files.getStringVar(player,  ".Rank");
          //  }

            event.setFormat(ChatColor.BLACK + "[" + ChatColor.WHITE + (rank) + ChatColor.BLACK + "] " + ChatColor.WHITE + player.getName() + ": " + ChatColor.RESET + message);
        }

    }
   
    
 
    

    @EventHandler
    public void onTestEntityDamage(EntityDamageByEntityEvent event) {
        if (Parkour.inProgress.contains(event.getEntity().getName()) && event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }

    }

    
    @EventHandler
    public void onPlayerDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            
            if (Parkour.inProgress.contains(player.getName())) {
                    if ( player.getHealth() <= event.getDamage()) {
                    player.setFallDistance(0.0F);
                    event.setCancelled(true);
                    Parkour.Fall(player);
                    }
            } else event.setCancelled(true);
        }
    }

 
    @EventHandler
    public void onPlayerDeath (PlayerDeathEvent pde) { 
        
                final Player player = pde.getEntity();
                pde.setDeathMessage(null);
 		pde.setDroppedExp(0);
                        
    if (Parkour.inProgress.contains(player.getName())) {
        Parkour.Fall(player);
    } else {
        pde.setDeathMessage(null);
        player.setHealth(20);
        player.setFoodLevel(20);
        Parkour.ToLobby(player, "");
    }
             
    }   
    

    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        final String nik = player.getName();
        
                 if((Parkour.boards.containsKey(nik))) Parkour.boards.remove(nik);  //убираем табло
        
           Files.setIntVar(player, ".JumpTotal", (int) Parkour.jumpTotal.get(nik));
           Files.setIntVar(player, ".FallTotal", (int) Parkour.fallTotal.get(nik));
           Files.setIntVar(player, ".TimeTotal", (int) Parkour.timeTotal.get(nik));
                    Parkour.jumpTotal.remove(nik);
                    Parkour.fallTotal.remove(nik);
                    Parkour.timeTotal.remove(nik);

        
        if (Parkour.inProgress.contains(nik)) {
                Parkour.inProgress.remove(nik);

            String current = Files.getStringVar(player, ".Selected");
               if ("".equals(current) | "-".equals(current)) return;
             
             Files.setIntVar(player, ".Progress." + current + ".Time", (int) Parkour.timeCurrent.get(nik) );
             Files.setIntVar(player, ".Progress." + current + ".Jump", (int) Parkour.jumpCurrent.get (nik));
             Files.setIntVar(player, ".Progress." + current + ".Falls", (int) Parkour.fallCurrent.get (nik));
                    Parkour.timeCurrent.remove(nik);
                    Parkour.fallCurrent.remove(nik);
                    Parkour.jumpCurrent.remove(nik);
  
            Files.saveUsers(player);
            
        }
                         //new BukkitRunnable() {
                             //@Override 
                            // public void run()  {
                            int online=Bukkit.getOnlinePlayers().size()-1;
                            if (online<0) online=0;
////////////////////////////////////////////////////////////////////////////////
           ApiOstrov.sendArenaData(
                    "any",
                    "§b§lП А Р К У Р Ы",
                    "§1На сервере:",
                    "§1§l"+online,
                    "§a§lРаботает",
                    "",
                    online,
                    UniversalArenaState.ОЖИДАНИЕ,
                    false,
                    true
            );
 ////////////////////////////////////////////////////////////////////////////////
                        //}}.runTaskLater(Parkour.instance, 5L);
   }

    
//@EventHandler
//public void onInventoryDrag(InventoryDragEvent e) {e.setCancelled(true);}    
    
    
    
    
    
    
    
    @EventHandler(ignoreCancelled = true,priority=EventPriority.LOWEST)
    public void onWeatherChange(WeatherChangeEvent event) {
        boolean rain = event.toWeatherState();
        if(rain) event.setCancelled(true);}
 
    @EventHandler(ignoreCancelled = true,priority=EventPriority.LOWEST)
    public void onThunderChange(ThunderChangeEvent event) {
        boolean storm = event.toThunderState();
        if(storm) event.setCancelled(true); } 
    
    @EventHandler(ignoreCancelled = true,priority=EventPriority.LOWEST)
   public void onNetherCreate(PortalCreateEvent event) { event.setCancelled(true); }
   
    @EventHandler(ignoreCancelled = true,priority=EventPriority.LOWEST)
    public void onBlockSpread(BlockSpreadEvent e) { 
        e.setCancelled(true);
    }  
        
    @EventHandler(ignoreCancelled = true,priority=EventPriority.LOWEST)
    public void onBlockGrowth(BlockGrowEvent e) { 
      e.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true,priority=EventPriority.LOWEST)
    public void BlockFadeEvent(BlockFadeEvent e) { 
      e.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true,priority=EventPriority.LOWEST)
    public void BlockFromToEvent(BlockFromToEvent e) { 
      e.setCancelled(true);
    }
    
    
    @EventHandler(ignoreCancelled = true,priority=EventPriority.LOWEST)
	public void onPlace(BlockPlaceEvent e) { if (!e.getPlayer().isOp()) e.setCancelled(true);}
    
    @EventHandler(ignoreCancelled = true,priority=EventPriority.LOWEST)
	public void onBreak(BlockBreakEvent bbe) { if (!bbe.getPlayer().isOp()) bbe.setCancelled(true);}
    
 //@EventHandler
//	public void onDrop(PlayerDropItemEvent e) {e.setCancelled(true);}
    
// @EventHandler
//	public void onPickUp(PlayerPickupItemEvent e) { if (!e.getPlayer().isOp()) e.setCancelled(true);}
    
    @EventHandler(ignoreCancelled = true,priority=EventPriority.LOWEST)
        public void onPlayerLoseFood(FoodLevelChangeEvent foodlevelchangeevent) {foodlevelchangeevent.setCancelled(true);}


    @EventHandler(ignoreCancelled = true,priority=EventPriority.LOWEST)
        public void onHungerChange(FoodLevelChangeEvent e) {e.setCancelled(true); ((Player)e.getEntity()).setFoodLevel(20);}
        
    @EventHandler(ignoreCancelled = true,priority=EventPriority.LOWEST)
    public void BlockSpreadEvent(BlockSpreadEvent e) { e.setCancelled(true);}   
        
    @EventHandler(ignoreCancelled = true,priority=EventPriority.LOWEST)
    public void FluidLevelChangeEvent(FluidLevelChangeEvent e) { e.setCancelled(true);}   
        

    
    public boolean isNumber(String args) {
        try {
            Integer.parseInt(args);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }


}
