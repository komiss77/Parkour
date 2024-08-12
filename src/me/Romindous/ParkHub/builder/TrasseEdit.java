package me.Romindous.ParkHub.builder;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.komiss77.ApiOstrov;
import ru.komiss77.LocalDB;
import ru.komiss77.builder.SetupMode;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.TCUtil;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.ConfirmationGUI;
import ru.komiss77.utils.inventory.InputButton;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import me.Romindous.ParkHub.CheckPoint;
import me.Romindous.ParkHub.Level;
import me.Romindous.ParkHub.Main;
import me.Romindous.ParkHub.PD;
import me.Romindous.ParkHub.Trasse;
import me.Romindous.ParkHub.builder.LocalBuilder.EditMode;










public class TrasseEdit implements InventoryProvider{

    private static final ItemStack fill = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).build();;
    private final SetupMode sm;
    
    
    TrasseEdit(final SetupMode sm) {
        this.sm = sm;
    }
        
    
    
    @Override
    public void init(final Player p, final InventoryContent content) {
        
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        

        
        final Trasse t = (Trasse) sm.arena;
        
        
        
        
        content.set( 0, 0, ClickableItem.of(new ItemBuilder(t.mat)
            .name("§fЛоготип трассы")
            .lore("§7")
            .lore("§7Положите сюда предмет,")
            .lore("§7и он станет иконкой.")
            .lore("§7")
            .build(), e -> {
                 if (e.isLeftClick() && e.getCursor() != null && e.getCursor().getType() != Material.AIR) {
                    t.mat = e.getCursor().getType();
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                    e.getView().getBottomInventory().addItem(new ItemStack[] { e.getCursor() });
                    e.getView().setCursor(new ItemStack(Material.AIR));
                    t.changed = true;
                    reopen(p, content);
                }      
                

            }));  
        
        
        content.set(0, 1, new InputButton(InputButton.InputType.ANVILL, new ItemBuilder(Material.NAME_TAG)
                .name("§fНазвание трассы")
                .lore("§7")
                .lore("§7Сейчас: "+t.displayName)
                .lore("§7Можно добавлять цветовые коды!")
                .lore("§7")
                .lore("§7ЛКМ - изменить")
                .lore("§7")
                .build(),  t.displayName.replaceAll("§", "&"), msg -> {
                    //msg = msg.replaceAll("&k", "").replaceAll("&", "§");
                    
                    if(msg.length()>24 ) {
                        p.sendMessage("§cЛимит 24 символа!");
                        PM.soundDeny(p);
                        return;
                    }
                    
                    for (final Trasse tr : Main.trasses.values()) {
                        if ( t.id!= tr.id && TCUtil.strip(tr.displayName).equalsIgnoreCase(TCUtil.strip(msg))) {
                            p.sendMessage("§cТрасса с таким названием уже есть!");
                            PM.soundDeny(p);
                            return;
                        }
                    }
                    
                    if (TCUtil.strip(t.displayName).equals(TCUtil.strip(msg))) {
                        p.sendMessage("§cНичего не изменилось..");
                        PM.soundDeny(p);
                        return;
                    }
                    
                    t.displayName = msg.replaceAll("&", "§");
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5f, 1);
                    t.changed = true;
                    reopen(p, content);
                }));           
            
        
        content.set(0, 2, new InputButton(InputButton.InputType.ANVILL, new ItemBuilder(Material.NAME_TAG)
                .name("§fКраткое описание трассы")
                .lore("§7")
                .lore("§7Сейчас: "+t.descr)
                .lore("§7")
                .lore("§7ЛКМ - изменить")
                .lore("§7")
                .build(),  t.descr.replaceAll("§", "&"), msg -> {
                    //msg = msg.replaceAll("&k", "").replaceAll("&", "§");
                    
                    if(msg.length()>24 ) {
                        p.sendMessage("§cЛимит 24 символа!");
                        PM.soundDeny(p);
                        return;
                    }
                    
                    t.descr = msg.replaceAll("&", "§");
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5f, 1);
                    t.changed = true;
                    reopen(p, content);
                })); 
        
        

        
        
        
        
        
        
        
        
         content.set( 0, 3, ClickableItem.of( new ItemBuilder(t.level.mat)
                .name("сложность")
                .lore("§7Сейчас - "+t.level)
                 .lore("§7ЛКМ - менять")
                .build(), e -> 
        {
            switch (t.level) {
                case Легко:
                    t.level = Level.Нормально;
                    break;
                case Нормально:
                    t.level = Level.Трудно;
                    break;
               case Трудно:
                    t.level = Level.Нереально;
                    break;
               case Нереально:
                    t.level = Level.Легко;
                    break;
            }
            t.changed = true;
            reopen(p, content);
        }
        ));
        
         
         
         
        content.set(0, 4, new InputButton(InputButton.InputType.ANVILL, new ItemBuilder(Material.GOLD_INGOT)
                .name("§fНаграда за прохождение")
                .lore("§7")
                .lore("§7Сейчас: "+t.pay+" лони")
                .lore("§7")
                .lore("§7ЛКМ - изменить")
                .lore("§7")
                .build(),  ""+t.pay, msg -> {

                    final int amm = ApiOstrov.getInteger(msg);
                    
                    if (amm == Integer.MIN_VALUE) {
                        p.sendMessage("§cДолжно быть число!");
                        PM.soundDeny(p);
                        return;
                    }
                    if (amm<1 || amm>1000) {
                        p.sendMessage("§cот 1 до 1000");
                        PM.soundDeny(p);
                        return;
                    }
                    t.pay = amm;
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5f, 1);
                    t.changed = true;
                    reopen(p, content);
                }));
        
        
        
        final PD pd = Main.data.get(p.getName());
        if (pd.task==null) {
            content.set( 0, 5, ClickableItem.of( new ItemBuilder(Material.GLOW_BERRIES)
                    .name("§3Включить трассировку")
                    .lore("§7Будет прорисовываться")
                    .lore("§7луч, соединяя по очереди")
                    .lore("§7все чекпоинты.")
                    .build(), e -> {
                        if (pd.task!=null) return;
                final String name = p.getName();
                pd.task = new BukkitRunnable() {
                    int n = 0;
                    CheckPoint cp;
                    CheckPoint nextCp;
                    @Override
                    public void run() {
                        final Player p = Bukkit.getPlayerExact(name);
                        if (!Main.trasses.containsKey(t.id) || p==null || !p.isOnline() || PM.getOplayer(name)==null || PM.getOplayer(name).setup==null || PM.getOplayer(name).setup.arena==null) {
                            this.cancel();
                            pd.task = null;
                            return;
                        }
                        if (t.points.size()>=2) {
                            if (n>=(t.points.size()-1)) {
                                n=0;
                            }
                            cp = t.getCp(n);
                            nextCp = t.getNextCp(n);
                            if (cp==null || nextCp==null) {
                                n=0;
                            } else {
                                Location from = cp.getLocation(t.worldName);
                                final Location to = nextCp.getLocation(t.worldName);
                                final Vector v = to.clone().subtract(from).toVector().normalize();
                                from.add(v);
                                int limit = 48;
                                while (from.getBlockX()!=to.getBlockX() && from.getBlockY()!=to.getBlockY() && from.getBlockZ()!=to.getBlockZ()) {
                                    from.add(v);
                                    from.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, from, 1, 0, 0, 0);
                                    limit--;
                                    if (limit==0) break;
                                }
                            }
                            n++;
                        }
                    }
                }.runTaskTimer(Main.plug, 1,  10 + 100/t.points.size() );
                reopen(p, content);
            }
            ));               
        } else {
            content.set( 0, 5, ClickableItem.of( new ItemBuilder(Material.SWEET_BERRIES)
                    .name("§6Выключить трассировку")
                    .build(), e -> {
                pd.task.cancel();
                pd.task = null;
                reopen(p, content);
            }
            ));
        }
   
                
        content.set( 0, 6, ClickableItem.of( new ItemBuilder(Material.REPEATER)
                .name("редактор чекпоинтов")
                .build(), e -> {
            LocalBuilder.openCheckPointEditor(p, sm);
        }
        ));
                 
        
                
        content.set( 0, 8, ClickableItem.of( new ItemBuilder(Material.REDSTONE)
                .name("§4Клав.Q - Удалить")
                .build(), e -> {
            if (e.getClick()==ClickType.DROP) {
                ConfirmationGUI.open(p, "§4Удалить паркур?", (confirm)-> {
                            
                    if (confirm) {
                        for (Player pl : t.getPlayers()) {
                            Main.lobbyPlayer(pl);
                        }
                        Main.trasses.remove(t.id);
                        
                        Main.parkData.set("trasses."+t.id, null);
                        Main.parkData.saveConfig();
                        Main.parkStat.set("stat."+t.id, null);
                        Main.parkStat.saveConfig();
                        
                        LocalDB.executePstAsync(p, "DELETE FROM `parkData` WHERE `trasseID`='"+t.id+"'; ");
                        
                        p.sendMessage("§cТрасса "+t.displayName+" §cудалена.");
                        sm.loacalEditMode = EditMode.Main;
                        LocalBuilder.open(p, sm);
                    } else {
                        p.closeInventory();
                    }

                });
            }
        }
        ));        
        
        
        
        
        
        
        if (t.points.size()<2) t.disabled = true;
        
        if (t.disabled) {
           content.set(1, 1, ClickableItem.of( new ItemBuilder(Material.RED_CONCRETE)
                .name("§7включение трассы")
                .lore("§7сейчас §4выключена")
                .lore(t.points.size()<2 ? "§cВключение невозможно -" : "")
                .lore(t.points.size()<2 ? "§cМеньше 2 чекпоинтов!" : "§7ЛКМ - включить")
                .build(), e -> {
                    if (t.points.size()>2) {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        t.disabled=false;
                        t.changed = true;
                        reopen(p, content);
                     } else {
                        PM.soundDeny(p);
                    }
               }));            
        } else {
            content.set(1, 1, ClickableItem.of( new ItemBuilder(Material.GREEN_CONCRETE)
                    .name("§7выключение трассы")
                    .lore("§7сейчас §2включена")
                    .build(), e -> {
                       p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                       t.disabled=true;
                       t.changed = true;
                       reopen(p, content);
                   }));
        }        
             
        
        
        
        content.set( 1, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR)
                .name("назад")
                .build(), e -> {
                    sm.loacalEditMode = EditMode.Main;
                    LocalBuilder.open(p, sm);
            }
        ));
                         
        
        
        
        
        
        if (t.changed) {
            content.set(1, 7, ClickableItem.of(new ItemBuilder(Material.JUKEBOX)
                .name("§aСохранить изменения")
                .lore("§7")
                .lore("§7Вы внесли изменения,")
                .lore("§7рекомендуется сохранение.")
                .lore("§7")
                .lore("§cБез сохранения все изменения будут")
                .lore("§cутеряны после перезагрузки сервера!")
                .lore("§7")
                .build(), e -> {
                    t.saveConfig(p);
                    reopen(p, content);
                }));
        }        
        
        
        


        
        

 
        
    
    
    
    }
    

    
    

    
    

    
    
    
    
    
    
    
}