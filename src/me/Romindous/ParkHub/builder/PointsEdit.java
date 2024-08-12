package me.Romindous.ParkHub.builder;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import ru.komiss77.builder.SetupMode;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import me.Romindous.ParkHub.CheckPoint;
import me.Romindous.ParkHub.Trasse;
import ru.komiss77.utils.TimeUtil;










public class PointsEdit implements InventoryProvider{

    private static final ClickableItem fill = ClickableItem.empty(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).build());
    private final SetupMode sm;
    
    
    PointsEdit(final SetupMode sm) {
        this.sm = sm;
    }
        
    
    
    @Override
    public void init(final Player p, final InventoryContent content) {
        
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        
        content.fillRow(4, fill);
        
        final Trasse t = (Trasse) sm.arena;
        

        
        
        int i = 0;
        for (CheckPoint cp : t.points) {
            final ItemStack is;
            
            if (i==0) {
                is = new ItemBuilder(Material.WARPED_FENCE_GATE)
                    .name("§f#"+i)
                    .lore("")
                    .lore("§eСтартовая точка")
                    .lore("§7"+cp.x+","+cp.y+","+cp.z)
                    .lore("")
                    .lore("§7ЛКМ - ТП к точке")
                    .lore("§7Q - удалить")
                    .build();     
            } else {
                is = new ItemBuilder((i+1==t.points.size()) ? Material.LIME_BANNER : Material.TURTLE_SCUTE)
                    .name("§f#"+i)
                    .lore((i+1==t.points.size()) ? "§aФиниш" : "") //нулевая сюда и не придёт
                    .lore("§7"+cp.x+","+cp.y+","+cp.z)
                    .lore("")
                    .lore("§8Чтобы добраться до этой точки,")
                    .lore("§8от предыдущего, должно быть")
                    .lore("§8затрачено:")
                    .lore("§7⌚ §3"+TimeUtil.secondToTime(cp.controlTime))
                    .lore("§7⇪: §6"+cp.controlJump)
                    .lore("§7☠: §4"+cp.controlFall)
                    .lore("§8Меньшие данные ставят метки §cчиты")
                    .lore("")
                    .lore("§7ЛКМ - ТП к точке")
                    .lore("§7ПКМ - настройки")
                    .lore("§7Q - удалить")
                    .build();     
            }
            

            
            content.add(ClickableItem.of(is, e-> {
                switch (e.getClick()) {
                    
                    case LEFT:
                        p.closeInventory();
                        final CheckPoint next = t.getNextCp(cp);
                        final Location nextLoc = next.getLocation(t.worldName);
                        final Location loc = cp.getLocation(t.worldName);
                        loc.setDirection(nextLoc.toVector().subtract(loc.toVector()));
                        p.teleport(loc);
                        break;
                        
                    case RIGHT:
                        LocalBuilder.openPointSettings(p, sm, cp);
                        break;
                        
                    case DROP:
                        cp.getLocation(t.worldName).getBlock().setType(Material.AIR);
                        t.points.remove(cp);
                        t.changed = true;
                        reopen(p, content);
                        break;

                }
            }));
            
            i++;
            
        }
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        content.set(5, 0, ClickableItem.empty(new ItemBuilder(Material.MOJANG_BANNER_PATTERN)
            .name("§7Как делать?")
            .lore("§7Когда в билдере выбрано")
            .lore("§7это меню, просто ставьте")
            .lore("§7или убирайте любые нажимные")
            .lore("§7плиты в мире карты,")
            .lore("§7чекпоинты будут добавляться")
            .lore("§7или удаляться соотв.")
            .lore("§7ПКМ на плиту-чекпоинт -")
            .lore("§7настройки чекпоинта.")
            .lore("§7")
            .build()));
        
        
        
        
        
        
        if (t.points.size()<2) t.disabled = true;
        
        if (t.disabled) {
           content.set(5, 1, ClickableItem.of( new ItemBuilder(Material.RED_CONCRETE)
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
            content.set(5, 1, ClickableItem.of( new ItemBuilder(Material.GREEN_CONCRETE)
                    .name("§7выключение трассы")
                    .lore("§7сейчас §2включена")
                    .build(), e -> {
                       p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                       t.disabled=true;
                       t.changed = true;
                       reopen(p, content);
                   }));
        }        
            
                
        content.set( 5, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR)
                .name("назад")
                .build(), e -> {
                    LocalBuilder.openTrasseEditor(p, sm);
            }
        ));
                 
        
        if (t.changed) {
            content.set(5, 6, ClickableItem.of(new ItemBuilder(Material.JUKEBOX)
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