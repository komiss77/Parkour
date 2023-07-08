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
import ru.komiss77.ApiOstrov;
import me.Romindous.ParkHub.CheckPoint;
import me.Romindous.ParkHub.Trasse;










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
                    .addLore("")
                    .addLore("§eСтартовая точка")
                    .addLore("§7"+cp.x+","+cp.y+","+cp.z)
                    .addLore("")
                    .addLore("§7ЛКМ - ТП к точке")
                    .addLore("§7Q - удалить")
                    .build();     
            } else {
                is = new ItemBuilder((i+1==t.points.size()) ? Material.LIME_BANNER : Material.SCUTE)
                    .name("§f#"+i)
                    .addLore((i+1==t.points.size()) ? "§aФиниш" : "") //нулевая сюда и не придёт
                    .addLore("§7"+cp.x+","+cp.y+","+cp.z)
                    .addLore("")
                    .addLore("§8Чтобы добраться до этой точки,")
                    .addLore("§8от предыдущего, должно быть")
                    .addLore("§8затрачено:")
                    .addLore("§7⌚ §3"+ApiOstrov.secondToTime(cp.controlTime))
                    .addLore("§7⇪: §6"+cp.controlJump)
                    .addLore("§7☠: §4"+cp.controlFall)
                    .addLore("§8Меньшие данные ставят метки §cчиты")
                    .addLore("")
                    .addLore("§7ЛКМ - ТП к точке")
                    .addLore("§7ПКМ - настройки")
                    .addLore("§7Q - удалить")
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
            .addLore("§7Когда в билдере выбрано")
            .addLore("§7это меню, просто ставьте")
            .addLore("§7или убирайте любые нажимные")
            .addLore("§7плиты в мире карты,")
            .addLore("§7чекпоинты будут добавляться")
            .addLore("§7или удаляться соотв.")
            .addLore("§7ПКМ на плиту-чекпоинт -")
            .addLore("§7настройки чекпоинта.")
            .addLore("§7")
            .build()));
        
        
        
        
        
        
        if (t.points.size()<2) t.disabled = true;
        
        if (t.disabled) {
           content.set(5, 1, ClickableItem.of( new ItemBuilder(Material.RED_CONCRETE)
                .name("§7включение трассы")
                .addLore("§7сейчас §4выключена")
                .addLore(t.points.size()<2 ? "§cВключение невозможно -" : "")
                .addLore(t.points.size()<2 ? "§cМеньше 2 чекпоинтов!" : "§7ЛКМ - включить")
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
                    .addLore("§7сейчас §2включена")
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
                .addLore("§7")
                .addLore("§7Вы внесли изменения,")
                .addLore("§7рекомендуется сохранение.")
                .addLore("§7")
                .addLore("§cБез сохранения все изменения будут")
                .addLore("§cутеряны после перезагрузки сервера!")
                .addLore("§7")
                .build(), e -> {
                    t.saveConfig(p);
                    reopen(p, content);
                }));
        }        
        
        
        


        
        

 
        
    
    
    
    }
    

    
    

    
    

    
    
    
    
    
    
    
}