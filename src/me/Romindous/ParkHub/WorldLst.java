package me.Romindous.ParkHub;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.komiss77.ApiOstrov;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.TCUtil;
import me.Romindous.ParkHub.builder.LocalBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import ru.komiss77.Ostrov;
import ru.komiss77.events.ChatPrepareEvent;






public class WorldLst implements Listener {
    


    
    
    

    
    
    
    
  /*  @EventHandler (ignoreCancelled = true)
    public void onPlayerChat(AsyncChatEvent e) { //только когда новичёк пишет в чат
        final Player p = e.getPlayer();
        final PD pd = Main.data.get(p.getName());
        

    }  */    

    //***************************        
    @EventHandler 
    public void chat(ChatPrepareEvent e) {
        final Player p = e.getPlayer();
        final PD pd = Main.data.get(p.getName());
        final TextComponent tc = Component.text(pd.cheat ? "§4<§cЧитак§4> §7" : ("§7<§6"+Main.getRank(pd.totalCheckPoints))+"§7> §7");
        e.setSenderGameInfo(tc);
        e.setViewerGameInfo(tc);//e.getDeluxeFormat().setPrefix(  pd.cheat ? "§4<§cЧитак§4> §7" : ("§7<§6"+Main.getRank(pd.totalCheckPoints))+"§7> §7" );
    }

    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)    
    public void onRightClick(final PlayerInteractEvent e) {
        
        if (e.getAction()==Action.RIGHT_CLICK_BLOCK && (Tag.SIGNS.isTagged(e.getClickedBlock().getType()) || Tag.WALL_SIGNS.isTagged(e.getClickedBlock().getType()) 
                || Tag.STANDING_SIGNS.isTagged(e.getClickedBlock().getType()) )   ) {
            final Sign s = (Sign) e.getClickedBlock().getState();
            final Player p = e.getPlayer();
            final List<Component>lines = s.lines();
            //if (TCUtil.strip(s.getLine(1)).equalsIgnoreCase("на трассу") && !s.getLine(2).isEmpty()) {
            if (TCUtil.strip(TCUtil.deform(lines.get(1))).equalsIgnoreCase("на трассу") && !TCUtil.deform(lines.get(2)).isEmpty()) {
                //final String trasseName = TCUtil.strip(s.getLine(2));
                final String trasseName = TCUtil.strip(TCUtil.deform(lines.get(2)));
                for (Trasse t : Main.trasses.values()) {
                    if (TCUtil.strip(t.displayName).equalsIgnoreCase(trasseName)) {
                        Main.joinParkur(p, t.id);
                        break;
                    }
                }
            }
            return;
        }
        
        if (!ApiOstrov.isLocalBuilder(e.getPlayer()) || e.getAction()!=Action.RIGHT_CLICK_BLOCK || !e.getClickedBlock().getType().name().contains("_PRESSURE_")) {
            return;
        }
        final Player p = e.getPlayer();
        final Oplayer op = PM.getOplayer(p.getName());
        if (op.setup!=null && op.setup.arena!=null) {
            final Trasse t = (Trasse) op.setup.arena;
            if (!t.worldName.equals(p.getWorld().getName())) {
                p.sendMessage("§6В редактор загружена трасса "+t.displayName+" §6(мир="+t.worldName+"), а эта в другом мире!");
                e.setCancelled(true);
                return;
            }
            final CheckPoint cp = t.getCp(e.getClickedBlock().getLocation());
            if (cp!=null) {
                LocalBuilder.openPointSettings(p, op.setup, cp);
            } else {
                p.sendMessage("§7Это не чекпоинт редактируемой трассы "+t.displayName+"!");
            }
        }
    }
    
    
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onPlace(final BlockPlaceEvent e) {
        if (!ApiOstrov.isLocalBuilder(e.getPlayer()) ) {
            e.setCancelled(true);
            return;
        }
        if (!e.getBlock().getType().name().contains("_PRESSURE_")) return;
        final Player p = e.getPlayer();
        final Oplayer op = PM.getOplayer(p.getName());
        if (op.setup!=null && op.setup.arena!=null) {
            final Trasse t = (Trasse) op.setup.arena;
            if (!t.worldName.equals(p.getWorld().getName())) {
                 p.sendMessage("§6В редактор загружена трасса "+t.displayName+" §6(мир="+t.worldName+"), но вы ставите плиту в другом мире!");
                 e.setCancelled(true);
                return;
            }
            final CheckPoint cp = new CheckPoint(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ(), 10, 2, 0);
            t.points.add(cp);
            t.changed = true;
            p.sendMessage("§aВы добавили чекпоинт §f#"+t.points.indexOf(cp)+" §aдля трассы "+t.displayName+"§a.");
            p.sendMessage("§3ПКМ на плиту - настройки чекпоинта. §6Сохраните изменения!");
        }
        
        
    }
    
    
    
    
    
    
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onBreak(final BlockBreakEvent e) {
        if (!ApiOstrov.isLocalBuilder(e.getPlayer()) ) {
            e.setCancelled(true);
            return;
        }
        if (!e.getBlock().getType().name().contains("_PRESSURE_")) return;
        final Player p = e.getPlayer();
        
        for (Trasse t : Main.trasses.values()) {
            if (t.worldName.equals(p.getWorld().getName())) { //перебираем трассы в этом мире
                final CheckPoint cp = t.getCp(e.getBlock().getLocation()); 
                if (cp!=null) { //если это чекпоинт какой-то трассы
                    final Oplayer op = PM.getOplayer(p.getName());
                    if (op.setup==null || op.setup.arena==null || ((Trasse)op.setup.arena).id!=t.id ) {
                        p.sendMessage("§cЭта плита - чекпоинт трассы "+t.displayName+"§c, чтобы удалить чекпоинт загрузите трассу в редактор.");
                        e.setCancelled(true);
                        return;
                    }
                    p.sendMessage("§eВы удалили чекпоинт §f#"+t.points.indexOf(cp)+" §eу трассы "+t.displayName+"§e. Сохраните изменения!");
                    t.points.remove(cp);
                    t.changed = true;
                }
            }
        }

    }

    
    
    
    
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onHangingBreakByEntityEvent(final HangingBreakByEntityEvent e) {
        if (e.getRemover().getType()==EntityType.PLAYER ) {
                if (!ApiOstrov.isLocalBuilder((Player) e.getRemover()) ) e.setCancelled(true);
        } 

    }
       
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onHangingBreakEvent(final HangingBreakEvent e) {
        if ( e.getEntity().getType()==EntityType.PLAYER) {
                if (!ApiOstrov.isLocalBuilder((Player) e.getEntity()) ) e.setCancelled(true);
        } 
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void onPlayerInteractAtEntityEvent(final PlayerInteractAtEntityEvent e) {
        final Player p = e.getPlayer();
        if( e.getRightClicked().getType() ==EntityType.ARMOR_STAND && !ApiOstrov.isLocalBuilder(p) ) {
            e.setCancelled(true);
        }

    }


   

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
    public void PlayerArmorStandManipulateEvent(final PlayerArmorStandManipulateEvent e){
        if (!ApiOstrov.isLocalBuilder(e.getPlayer())) e.setCancelled(true);
    }        
    
    
    
   // @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)    
//    public void onPlayerRespawn(final PlayerRespawnEvent e) {
 //        if (!e.getPlayer().getWorld().getName().equals("world")) return;
 //   }
    
    
    
  //  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  //  public void ProjectileHitEvent(final ProjectileHitEvent e) {
  //      if (!e.getEntity().getWorld().getName().equals("world")) return;
 //   }    





    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent e) { 
        
       
        if (e.getEntity() instanceof final Player p) {
            //final LivingEntity le = (LivingEntity) e.getEntity();
            switch (e.getCause()) {
                case VOID:
                    e.setDamage(0);
                    p.setFallDistance(0);
                    Ostrov.sync( () -> {
                    final PD pd = Main.data.get(p.getName());
                        if (pd.current==null) {
                            Main.lobbyPlayer(p);
                            p.playSound(p.getEyeLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
                        } else {
                            //p.performCommand("pk suicide"); бесконечное падение!
                            pd.fall();
                            Main.joinParkur(p, pd.current.id);
                            p.playSound(p.getEyeLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
                        }
                    }, 2); //на некоторых клиентах из-за пинга бесконечное падение
                    return;
                    
                case FALL:
                case THORNS:        //чары шипы на оружие-ранит нападающего
                case LIGHTNING:     //молния
                case DRAGON_BREATH: //дыхание дракона
                case CONTACT:       //кактусы
                case FIRE:          //огонь
                case FIRE_TICK:     //горение
                case HOT_FLOOR:     //BlockMagma
                case CRAMMING:      //EntityVex
                case DROWNING:      //утопление
                case STARVATION:    //голод
                case LAVA:	    //лава
                    e.setCancelled(true);
                    return;
                    //break;
               // case SUFFOCATION:   //зажатие в стене
                	//le.teleport(Main.getLocation(LocType.Spawn));
                	//le.sendMessage("§6[§eОстров§6] §eТебя зажала стена и переместила обратно на спавн!");
                	//e.setCancelled(false);
                  // return;

                case ENTITY_ATTACK: //ентити ударяет
                    if (e instanceof EntityDamageByEntityEvent ee) {
                        if (ee.getDamager().getType() == EntityType.PLAYER) {
                            e.setCancelled(true);
                        }
                    }

                default:
                    e.setCancelled(true);
                    //return;
            }
            
        } else {
            e.setCancelled(true);
        }
                

    }






    //@EventHandler (ignoreCancelled = true)
  //  public void onPlayerPickUpItem(final EntityPickupItemEvent e) {
  //      if (!e.getEntity().getWorld().getName().equals("world")) return;
  //  }


   // @EventHandler(ignoreCancelled = true)
   // public void onPlayerSwapoffHand(PlayerSwapHandItemsEvent e) {
   //     if (!e.getPlayer().getWorld().getName().equals("world")) return;
   //     e.setCancelled(true);
  //  }
   
  
    @EventHandler  (ignoreCancelled = true)
    public void onHungerChange(final FoodLevelChangeEvent e) {
        e.setCancelled(true); 
        ((Player)e.getEntity()).setFoodLevel(20);
    }
        

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    //@EventHandler (priority = EventPriority.NORMAL, ignoreCancelled = true)
   // public void onMerge(final ItemMergeEvent e) {
    //    if (!e.getEntity().getWorld().getName().equals("world")) return;
    //    e.setCancelled(true);
   // }

   
  //  @EventHandler (priority = EventPriority.NORMAL, ignoreCancelled = true)
   // public void onDespawn(final ItemDespawnEvent e) {
//log("onDespawn nb?"+e.getEntity().getWorld().getName().startsWith("newbie"));
        //if (!e.getEntity().getWorld().getName().equals("world")) return;
        //if (e.getEntity().getItemStack().getType()==Material.NETHER_STAR) {
        //    e.setCancelled(true);
        //}
   // }

   


    @EventHandler (ignoreCancelled = true)
    public void onBlockFade(final BlockFadeEvent e) {
        if( e.getBlock().getType() == Material.ICE || e.getBlock().getType() == Material.PACKED_ICE || e.getBlock().getType() == Material.SNOW || e.getBlock().getType() == Material.SNOW_BLOCK) 
        e.setCancelled(true);
    }


    
    
    @EventHandler (ignoreCancelled = true)
    public void onCreatureSpawnEvent(final CreatureSpawnEvent e) {
        e.setCancelled(true);
    }
  
    
    @EventHandler (ignoreCancelled = true)
    public void onWeatherChange(final WeatherChangeEvent e) {
        if (e.toWeatherState()) e.setCancelled(true);
    }


          
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockSpread(final BlockSpreadEvent e) {
        e.setCancelled(true);
    }  
        
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockGrowth(final BlockGrowEvent e) { 
        e.setCancelled(true);
    }    

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onStructureGrow(final StructureGrowEvent e) { 
        e.setCancelled(true);
    }    


















        
  
}
