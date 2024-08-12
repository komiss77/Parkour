package me.Romindous.ParkHub.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.builder.SetupMode;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.*;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import me.Romindous.ParkHub.Main;
import me.Romindous.ParkHub.Trasse;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.meta.ItemMeta;
import ru.komiss77.Timer;









public class TrasseSelect implements InventoryProvider{

    //private static final ItemStack fill = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).build();;
    private final SetupMode sm;
    
    TrasseSelect(final SetupMode sm) {
        this.sm = sm;
    }
        
    
    
    @Override
    public void init(final Player p, final InventoryContent content) {
        
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        
        final Pagination pagination = content.pagination();
        final ArrayList<ClickableItem> entry = new ArrayList<>();         
        

        
        for (Trasse t : Main.trasses.values()) {
            
            final List<Component> lore = Arrays.asList(
                Component.text(t.disabled ? "§4Выключен" : "§2Включен"),
                Component.text("§3Мир: "+t.worldName),
                Component.text("§7Создан: §3"+TimeUtil.dateFromStamp(t.createAt)),
                Component.text("§7Создатель: §f"+t.creator),
                Component.text("§7Сложность: §5"+t.level.name()),
                Component.text(t.descr),
                Component.text(t.inProgress.isEmpty() ? "§7никого нет" : ("§7Проходят: "+t.inProgress.size())),
                Component.text("§7⚐: "+t.size()),
                Component.text("§7Пройден: "+t.totalDone+" раз."),
                Component.text("§7⌚ "+TimeUtil.secondToTime(t.totalTime)),
                Component.text("§7⇪: "+t.totalFalls),
                Component.text("§7☠: "+t.totalJumps),
                Component.text("§7ЛКМ - редактировать")
            );

            final ItemStack is = new ItemStack(t.mat);
            final ItemMeta im = is.getItemMeta();
            im.displayName(Component.text(t.displayName));
            im.lore(lore);
            is.setItemMeta(im);
            
            /*final ItemStack is = new ItemBuilder(t.mat)
                .name(t.displayName)
                .lore("t.disabled ? "§4Выключен" : "§2Включен")
                .lore("§3Мир: "+t.worldName)
                .lore("§7Создан: §3"+TimeUtil.dateFromStamp(t.createAt))
                .lore("§7Создатель: §f"+t.creator)
                .lore("§7Сложность: §5"+t.level.name())
                .lore("t.descr)
                .lore("t.inProgress.isEmpty() ? "§7никого нет" : ("§7Проходят: "+t.inProgress.size()) )
                .lore("§7⚐: "+t.size())
                .lore("§7Пройден: "+t.totalDone+" раз.")
                .lore("§7⌚ "+TimeUtil.secondToTime(t.totalTime))
                .lore("§7⇪: "+t.totalFalls)
                .lore("§7☠: "+t.totalJumps)
                .lore("§7ЛКМ - редактировать")
                .build();*/
            
            entry.add(ClickableItem.of(is, e-> {
                if (e.getClick()==ClickType.LEFT) {
                    //t.service = true;
                    sm.arena = t;
                    LocalBuilder.openTrasseEditor(p, sm);
                } else if (e.getClick()==ClickType.DROP) {
                    
                }
            }));
            
        }      
       
        
        entry.add(ClickableItem.of(  ItemUtil.add, e-> {
                    final Trasse t = new Trasse(ApiOstrov.generateId(), "новая трасса", p.getWorld().getName(), new LinkedList<>() );
                    t.creator = p.getName();
                    t.createAt = Timer.getTime();
                    t.disabled = true;
                    t.changed = true;
                    Main.trasses.put(t.id, t);
                    sm.arena = t;
                    LocalBuilder.openTrasseEditor(p, sm);
                }
            )
        );        
        
        
        
        pagination.setItems(entry.toArray(new ClickableItem[entry.size()]));
        pagination.setItemsPerPage(45);    


        if (!pagination.isLast()) {
            content.set(0, 8, ClickableItem.of(ItemUtil.nextPage, e 
                    -> {
                content.getHost().open(p, pagination.next().getPage()) ;
            }
            ));
        }
                
        content.set( 0, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR)
                .name("Закончить режим Билдера")
                .build(), e -> {
                    for (Trasse t:Main.trasses.values()) {
                        if (t.changed) {
                            p.sendMessage("§6Внимание! Трасса "+t.displayName+" §cне сохранена!");
                        }
                    }
                    p.performCommand("builder end");
                    Main.lobbyPlayer(p);
            }
        ));
        
        
        if (!pagination.isFirst()) {
            content.set(0, 0, ClickableItem.of(ItemUtil.previosPage, e 
                    -> {
                content.getHost().open(p, pagination.previous().getPage()) ;
               })
            );
        }

        pagination.addToIterator(content.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1, 0)).allowOverride(false));
        


        
        
        
    }
    
    
    
    
    
    

    
    
    
    
    
    
    
}