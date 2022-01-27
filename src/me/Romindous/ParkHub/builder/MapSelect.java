package me.Romindous.ParkHub.builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import me.Romindous.ParkHub.Main;
import me.Romindous.ParkHub.Trasse;
import me.Romindous.ParkHub.builder.LocalBuilder.EditMode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import ru.komiss77.ApiOstrov;
import ru.komiss77.builder.SetupMode;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.LocationUtil;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.ConfirmationGUI;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;










public class MapSelect implements InventoryProvider{

    private static final ItemStack fill = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).build();;
    private final SetupMode sm;
    
    MapSelect(final SetupMode sm) {
        this.sm = sm;
    }
        
    
    
    @Override
    public void init(final Player p, final InventoryContent content) {
        
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        
        final Pagination pagination = content.pagination();
        final ArrayList<ClickableItem> entry = new ArrayList<>();         
        

        
        for (Trasse t : Main.trasses.values()) {
            
            final ItemStack is = new ItemBuilder(t.mat)
                .name(t.displayName)
                .lore("§6----------------------")
                .lore("§7Создан: §3"+ApiOstrov.dateFromStamp(t.createAt))
                .lore("§7Создатель: §f"+t.creator)
                .lore("§7Сложность: §5"+t.level.name())
                .lore(t.descr)
                .lore(t.inProgress.isEmpty() ? "§7никого нет" : ("§7Проходят: "+t.inProgress.size()) )
                .lore("§7⚐: "+t.size())
                .lore("§7Пройден: "+t.totalDone+" раз.")
                .lore("§7⌚ "+ApiOstrov.secondToTime(t.totalTime))
                .lore("§7⇪: "+t.totalFalls)
                .lore("§7☠: "+t.totalJumps)
                .lore("§6----------------------")
                .lore("§7ЛКМ - выключить и редактировать")
                .build();
            
            entry.add(ClickableItem.of(is, e-> {
                if (e.getClick()==ClickType.LEFT) {
                    t.disabled = true;
                    sm.arena = t;
                    LocalBuilder.openTrasseMenu(p, sm);
                } else if (e.getClick()==ClickType.DROP) {
                    
                }
            }));
            
        }      
       
        
        entry.add(ClickableItem.of(  ItemUtils.add, e-> {
                    p.sendMessage("создать");
                    
                }
            )
        );        
        
        
        
        pagination.setItems(entry.toArray(new ClickableItem[entry.size()]));
        pagination.setItemsPerPage(45);    


        if (!pagination.isLast()) {
            content.set(0, 8, ClickableItem.of(ItemUtils.nextPage, e 
                    -> {
                content.getHost().open(p, pagination.next().getPage()) ;
            }
            ));
        }

        if (!pagination.isFirst()) {
            content.set(0, 0, ClickableItem.of(ItemUtils.previosPage, e 
                    -> {
                content.getHost().open(p, pagination.previous().getPage()) ;
               })
            );
        }

        pagination.addToIterator(content.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1, 0)).allowOverride(false));
        


        
        
        
    }
    
    
    
    
    
    

    
    
    
    
    
    
    
}